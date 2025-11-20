package com.secure.privacyfirst.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.secure.privacyfirst.data.DownloadItem
import com.secure.privacyfirst.data.DownloadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class DownloadManagerHelper(private val context: Context) {
    
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val prefs = context.getSharedPreferences("downloads_prefs", Context.MODE_PRIVATE)
    
    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()
    
    companion object {
        private const val TAG = "DownloadManagerHelper"
        private const val KEY_DOWNLOADS = "downloads_list"
    }
    
    init {
        loadDownloads()
        registerDownloadReceiver()
    }
    
    private fun registerDownloadReceiver() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L) ?: return
                updateDownloadStatus(downloadId)
            }
        }
        
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }
    
    fun startDownload(
        url: String,
        filename: String? = null,
        mimeType: String? = null,
        showNotification: Boolean = true
    ): Long {
        try {
            val finalFilename = filename ?: URLUtil.guessFileName(url, null, mimeType)
            
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle(finalFilename)
                setDescription("Downloading...")
                setMimeType(mimeType)
                
                if (showNotification) {
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                } else {
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                }
                
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, finalFilename)
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }
            
            val downloadId = downloadManager.enqueue(request)
            
            // Track this download
            val downloadItem = DownloadItem(
                filename = finalFilename,
                url = url,
                mimeType = mimeType,
                status = DownloadStatus.DOWNLOADING,
                downloadId = downloadId
            )
            
            addDownload(downloadItem)
            
            Log.d(TAG, "Download started: $finalFilename (ID: $downloadId)")
            return downloadId
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start download: ${e.message}", e)
            return -1L
        }
    }
    
    private fun updateDownloadStatus(downloadId: Long) {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor: Cursor? = downloadManager.query(query)
        
        cursor?.use {
            if (it.moveToFirst()) {
                val status = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val bytesDownloaded = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val totalBytes = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val uriString = it.getString(it.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                
                val downloadStatus = when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.COMPLETED
                    DownloadManager.STATUS_FAILED -> DownloadStatus.FAILED
                    DownloadManager.STATUS_RUNNING -> DownloadStatus.DOWNLOADING
                    DownloadManager.STATUS_PAUSED -> DownloadStatus.PENDING
                    else -> DownloadStatus.PENDING
                }
                
                // Update the download in our list
                val currentDownloads = _downloads.value.toMutableList()
                val index = currentDownloads.indexOfFirst { it.downloadId == downloadId }
                
                if (index != -1) {
                    val updatedItem = currentDownloads[index].copy(
                        status = downloadStatus,
                        size = totalBytes,
                        filePath = uriString
                    )
                    currentDownloads[index] = updatedItem
                    _downloads.value = currentDownloads
                    saveDownloads()
                    
                    Log.d(TAG, "Download status updated: ${updatedItem.filename} - $downloadStatus")
                }
            }
        }
    }
    
    private fun addDownload(downloadItem: DownloadItem) {
        val currentDownloads = _downloads.value.toMutableList()
        currentDownloads.add(0, downloadItem) // Add to beginning
        _downloads.value = currentDownloads
        saveDownloads()
    }
    
    fun deleteDownload(downloadItem: DownloadItem) {
        // Remove from Android DownloadManager
        if (downloadItem.downloadId != -1L) {
            downloadManager.remove(downloadItem.downloadId)
        }
        
        // Delete the file
        downloadItem.filePath?.let { path ->
            try {
                val uri = Uri.parse(path)
                val file = File(uri.path ?: return@let)
                if (file.exists()) {
                    file.delete()
                    Log.d(TAG, "File deleted: ${file.absolutePath}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete file: ${e.message}", e)
            }
        }
        
        // Remove from our list
        val currentDownloads = _downloads.value.toMutableList()
        currentDownloads.removeAll { it.id == downloadItem.id }
        _downloads.value = currentDownloads
        saveDownloads()
    }
    
    fun openFile(downloadItem: DownloadItem) {
        downloadItem.filePath?.let { path ->
            try {
                val uri = Uri.parse(path)
                val file = File(uri.path ?: return)
                
                if (!file.exists()) {
                    Log.w(TAG, "File does not exist: ${file.absolutePath}")
                    return
                }
                
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(contentUri, downloadItem.mimeType ?: "*/*")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                
                context.startActivity(Intent.createChooser(intent, "Open with"))
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open file: ${e.message}", e)
            }
        }
    }
    
    fun shareFile(downloadItem: DownloadItem) {
        downloadItem.filePath?.let { path ->
            try {
                val uri = Uri.parse(path)
                val file = File(uri.path ?: return)
                
                if (!file.exists()) {
                    Log.w(TAG, "File does not exist: ${file.absolutePath}")
                    return
                }
                
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = downloadItem.mimeType ?: "*/*"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                
                context.startActivity(Intent.createChooser(intent, "Share via"))
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to share file: ${e.message}", e)
            }
        }
    }
    
    fun clearAllDownloads() {
        _downloads.value.forEach { downloadItem ->
            if (downloadItem.downloadId != -1L) {
                downloadManager.remove(downloadItem.downloadId)
            }
        }
        _downloads.value = emptyList()
        saveDownloads()
    }
    
    private fun saveDownloads() {
        try {
            val jsonArray = JSONArray()
            _downloads.value.forEach { download ->
                val jsonObject = JSONObject().apply {
                    put("id", download.id)
                    put("filename", download.filename)
                    put("url", download.url)
                    put("mimeType", download.mimeType)
                    put("size", download.size)
                    put("timestamp", download.timestamp)
                    put("status", download.status.name)
                    put("filePath", download.filePath)
                    put("downloadId", download.downloadId)
                }
                jsonArray.put(jsonObject)
            }
            
            prefs.edit().putString(KEY_DOWNLOADS, jsonArray.toString()).apply()
            Log.d(TAG, "Downloads saved: ${_downloads.value.size} items")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save downloads: ${e.message}", e)
        }
    }
    
    private fun loadDownloads() {
        try {
            val jsonString = prefs.getString(KEY_DOWNLOADS, null) ?: return
            val jsonArray = JSONArray(jsonString)
            val downloadsList = mutableListOf<DownloadItem>()
            
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val download = DownloadItem(
                    id = jsonObject.getLong("id"),
                    filename = jsonObject.getString("filename"),
                    url = jsonObject.getString("url"),
                    mimeType = jsonObject.optString("mimeType", null),
                    size = jsonObject.getLong("size"),
                    timestamp = jsonObject.getLong("timestamp"),
                    status = DownloadStatus.valueOf(jsonObject.getString("status")),
                    filePath = jsonObject.optString("filePath", null),
                    downloadId = jsonObject.getLong("downloadId")
                )
                downloadsList.add(download)
            }
            
            _downloads.value = downloadsList
            Log.d(TAG, "Downloads loaded: ${downloadsList.size} items")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load downloads: ${e.message}", e)
        }
    }
}
