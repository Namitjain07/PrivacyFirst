package com.secure.privacyfirst.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.secure.privacyfirst.data.DownloadItem
import com.secure.privacyfirst.data.DownloadStatus
import com.secure.privacyfirst.data.toReadableSize
import com.secure.privacyfirst.utils.DownloadManagerHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val downloadManager = remember { DownloadManagerHelper(context) }
    val downloads by downloadManager.downloads.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedDownload by remember { mutableStateOf<DownloadItem?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Downloads",
                        style = MaterialTheme.typography.headlineSmall
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (downloads.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearAllDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteSweep,
                                contentDescription = "Clear all downloads"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        if (downloads.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No downloads yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Files you download will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Total Downloads: ${downloads.size}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Completed: ${downloads.count { it.status == DownloadStatus.COMPLETED }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(downloads) { download ->
                    DownloadItemCard(
                        downloadItem = download,
                        onOpen = {
                            if (download.status == DownloadStatus.COMPLETED) {
                                downloadManager.openFile(download)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Download not completed yet",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onShare = {
                            if (download.status == DownloadStatus.COMPLETED) {
                                downloadManager.shareFile(download)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Cannot share incomplete download",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onDelete = {
                            selectedDownload = download
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog && selectedDownload != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null
                )
            },
            title = {
                Text(text = "Delete Download")
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${selectedDownload?.filename}\"? This will remove the file from your device."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDownload?.let { downloadManager.deleteDownload(it) }
                        showDeleteDialog = false
                        selectedDownload = null
                        Toast.makeText(context, "Download deleted", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Clear all confirmation dialog
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null
                )
            },
            title = {
                Text(text = "Clear All Downloads")
            },
            text = {
                Text(
                    text = "Are you sure you want to clear all ${downloads.size} downloads? This will remove all downloaded files from your device."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        downloadManager.clearAllDownloads()
                        showClearAllDialog = false
                        Toast.makeText(context, "All downloads cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadItemCard(
    downloadItem: DownloadItem,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = downloadItem.status == DownloadStatus.COMPLETED,
                    onClick = onOpen
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // File icon based on mime type
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (downloadItem.status) {
                        DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                        DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.tertiaryContainer
                        DownloadStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = when {
                                downloadItem.mimeType?.startsWith("image/") == true -> Icons.Outlined.Image
                                downloadItem.mimeType?.startsWith("video/") == true -> Icons.Outlined.VideoFile
                                downloadItem.mimeType?.startsWith("audio/") == true -> Icons.Outlined.AudioFile
                                downloadItem.mimeType?.contains("pdf") == true -> Icons.Outlined.PictureAsPdf
                                downloadItem.status == DownloadStatus.DOWNLOADING -> Icons.Outlined.FileDownload
                                downloadItem.status == DownloadStatus.FAILED -> Icons.Outlined.ErrorOutline
                                else -> Icons.Outlined.InsertDriveFile
                            },
                            contentDescription = null,
                            tint = when (downloadItem.status) {
                                DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.onPrimaryContainer
                                DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.onTertiaryContainer
                                DownloadStatus.FAILED -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = downloadItem.filename,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (downloadItem.status) {
                                DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                DownloadStatus.FAILED -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Text(
                                text = when (downloadItem.status) {
                                    DownloadStatus.COMPLETED -> "Completed"
                                    DownloadStatus.DOWNLOADING -> "Downloading"
                                    DownloadStatus.FAILED -> "Failed"
                                    DownloadStatus.CANCELLED -> "Cancelled"
                                    DownloadStatus.PENDING -> "Pending"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = when (downloadItem.status) {
                                    DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                                    DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.tertiary
                                    DownloadStatus.FAILED -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = downloadItem.size.toReadableSize(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = formatTimestamp(downloadItem.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // More options button
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (downloadItem.status == DownloadStatus.COMPLETED) {
                        DropdownMenuItem(
                            text = { Text("Open") },
                            onClick = {
                                expanded = false
                                onOpen()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.OpenInNew,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share") },
                            onClick = {
                                expanded = false
                                onShare()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
