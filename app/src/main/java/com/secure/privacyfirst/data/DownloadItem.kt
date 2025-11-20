package com.secure.privacyfirst.data

data class DownloadItem(
    val id: Long = System.currentTimeMillis(),
    val filename: String,
    val url: String,
    val mimeType: String? = null,
    val size: Long = 0L, // Size in bytes
    val timestamp: Long = System.currentTimeMillis(),
    val status: DownloadStatus = DownloadStatus.PENDING,
    val filePath: String? = null,
    val downloadId: Long = -1L // Android DownloadManager ID
)

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    CANCELLED
}

fun Long.toReadableSize(): String {
    if (this <= 0) return "Unknown"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(this.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.2f %s", this / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
