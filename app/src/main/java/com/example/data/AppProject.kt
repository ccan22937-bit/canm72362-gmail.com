package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_projects")
data class AppProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appName: String,
    val packageName: String,
    val url: String,
    val versionName: String = "1.0.0",
    val versionCode: Int = 1,
    val themeColorHex: String = "#4F46E5",
    val enableJavaScript: Boolean = true,
    val enableGeolocation: Boolean = true,
    val enableZoom: Boolean = true,
    val enableDomStorage: Boolean = true,
    val enablePullToRefresh: Boolean = true,
    val orientation: String = "Auto", // Auto, Portrait, Landscape
    val userAgentType: String = "Mobile", // Mobile, Desktop
    val lastBuiltAt: Long? = null,
    val apkSizeMb: Double = 4.8,
    val sha256Checksum: String = "A7:B9:4C:12:98:EE:7F:32:10:FA:BC:44:D8:E1:90:3A"
)
