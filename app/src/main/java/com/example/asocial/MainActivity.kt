package com.example.asocial

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import android.app.AppOpsManager
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            checkPermission()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
        checkPermission()
    }

    private fun checkPermission() {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(), packageName
        )
        if (mode != AppOpsManager.MODE_ALLOWED) {
            requestUsageAccessPermission()
        }
    }

    private fun requestUsageAccessPermission() {
        Toast.makeText(
            this,
            "Permission required to access app usage stats",
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        requestPermissionLauncher.launch(intent)
    }
}

@Composable
fun MainScreen() {
    val apkInfoExtractor = ApkInfoExtractor(LocalContext.current)
    val appUsageStatsHelper = AppUsageStatsHelper(LocalContext.current)

    val installedApks = remember { mutableStateListOf<String>() }

    // Load installed apps
    installedApks.addAll(apkInfoExtractor.getAllInstalledApkInfo())

    // Sort the installed apps based on time spent
    installedApks.sortByDescending { packageName ->
        appUsageStatsHelper.getTimeSpentOnAppForToday(packageName)
    }

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Installed Apps")
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(installedApks) { packageName ->
                    val appName = apkInfoExtractor.getAppName(packageName)
                    val appIcon = apkInfoExtractor.getAppIconByPackageName(packageName)
                    val timeSpent =
                        appUsageStatsHelper.getTimeSpentOnAppForToday(packageName)
                    AppItem(appName, appIcon, timeSpent)
                }
            }
        }
    }
}

@Composable
fun AppItem(appName: String, appIcon: Drawable?, timeSpent: Long) {
    val appUsageStatsHelper = AppUsageStatsHelper(LocalContext.current)
    val imageBitmap = remember(appIcon) {
        appIcon?.toBitmap()?.asImageBitmap()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        imageBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = appName)
            Text(text = "Time spent: ${appUsageStatsHelper.formatTime(timeSpent)}")
        }
    }
}

fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return this.bitmap
    }

    val bitmap = Bitmap.createBitmap(
        intrinsicWidth.coerceAtLeast(1),
        intrinsicHeight.coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}


