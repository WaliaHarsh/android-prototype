package com.example.asocial


import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import java.util.ArrayList

class ApkInfoExtractor(private val context: Context) {

    fun getAllInstalledApkInfo(): MutableSet<String> {
        val apkPackageName = mutableSetOf<String>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        val resolveInfoList: List<ResolveInfo> = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfoList) {
            val activityInfo = resolveInfo.activityInfo
//            if (!isSystemPackage(resolveInfo)) {
//                apkPackageName.add(activityInfo.applicationInfo.packageName)
//            }
            apkPackageName.add(activityInfo.applicationInfo.packageName)
        }
        return apkPackageName
    }

    private fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
        return resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    fun getAppIconByPackageName(apkTempPackageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(apkTempPackageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ContextCompat.getDrawable(context, R.mipmap.ic_launcher)
        }
    }

    fun getAppName(apkPackageName: String): String {
        var name = ""
        val packageManager: PackageManager = context.packageManager
        try {
            val applicationInfo: ApplicationInfo? = packageManager.getApplicationInfo(apkPackageName, 0)
            if (applicationInfo != null) {
                name = packageManager.getApplicationLabel(applicationInfo).toString()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return name
    }
}

