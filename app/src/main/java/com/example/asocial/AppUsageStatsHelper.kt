package com.example.asocial

import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.*

class AppUsageStatsHelper (private val context: Context){

    fun getTimeSpentOnAppForLast24hrs( packageName: String): Long {
        var totalTime = 0L

        if (packageName.isEmpty()) {
            return totalTime
        }

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return totalTime

        try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val start = calendar.timeInMillis
            val end = System.currentTimeMillis()

            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start,
                end
            )

            for (usageStats in stats) {
                if (usageStats.packageName == packageName) {
                    totalTime = usageStats.totalTimeInForeground
                    break
                }
            }
        } catch (e: SecurityException) {
            // Handle SecurityException (e.g., permission not granted)
            e.printStackTrace()
        } catch (e: Exception) {
            // Handle other exceptions
            e.printStackTrace()
        }
        return totalTime
    }

    fun getTimeSpentOnAppForToday(packageName: String): Long {
        var totalTime = 0L

        if (packageName.isEmpty()) {
            return totalTime
        }

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return totalTime

        try {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val start = calendar.timeInMillis
            val end = System.currentTimeMillis()

            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start,
                end
            )

            for (usageStats in stats) {
                if (usageStats.packageName == packageName) {
                    totalTime = usageStats.totalTimeInForeground
                    break
                }
            }
        } catch (e: SecurityException) {
            // Handle SecurityException (e.g., permission not granted)
            e.printStackTrace()
        } catch (e: Exception) {
            // Handle other exceptions
            e.printStackTrace()
        }
        return totalTime
    }



    fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }
}
