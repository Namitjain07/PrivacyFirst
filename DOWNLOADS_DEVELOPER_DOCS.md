# Downloads Feature - Developer Documentation

## Architecture Overview

```
┌──────────────────────────────────────────────────────────────┐
│                      Presentation Layer                       │
│  ┌────────────────┐  ┌──────────────┐  ┌─────────────────┐  │
│  │ SettingsScreen │──│ Navigation   │──│ DownloadsScreen │  │
│  │    (Compose)   │  │   (NavHost)  │  │    (Compose)    │  │
│  └────────────────┘  └──────────────┘  └─────────────────┘  │
│                                              │                │
└──────────────────────────────────────────────┼────────────────┘
                                               │
┌──────────────────────────────────────────────┼────────────────┐
│                      Business Logic           │                │
│                  ┌────────────────────────────▼──────────┐    │
│                  │   DownloadManagerHelper               │    │
│                  │   - startDownload()                   │    │
│                  │   - updateDownloadStatus()            │    │
│                  │   - deleteDownload()                  │    │
│                  │   - openFile()                        │    │
│                  │   - shareFile()                       │    │
│                  │   - clearAllDownloads()               │    │
│                  └───────────────────┬───────────────────┘    │
│                                      │                         │
└──────────────────────────────────────┼─────────────────────────┘
                                       │
┌──────────────────────────────────────┼─────────────────────────┐
│                      Data Layer       │                         │
│  ┌────────────────┐  ┌───────────────▼──────┐  ┌────────────┐│
│  │  DownloadItem  │  │  SharedPreferences   │  │ FileSystem ││
│  │  (Data Class)  │  │    (JSON Storage)    │  │ (Downloads)││
│  └────────────────┘  └──────────────────────┘  └────────────┘│
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Key Components

### 1. DownloadItem (Data Model)
**Location:** `data/DownloadItem.kt`

```kotlin
data class DownloadItem(
    val id: Long,              // Unique identifier
    val filename: String,      // Display name
    val url: String,           // Source URL
    val mimeType: String?,     // MIME type (e.g., "application/pdf")
    val size: Long,            // Size in bytes
    val timestamp: Long,       // Download start time
    val status: DownloadStatus,// Current status
    val filePath: String?,     // Local file URI
    val downloadId: Long       // Android DownloadManager ID
)

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    CANCELLED
}
```

**Extension Function:**
```kotlin
fun Long.toReadableSize(): String
// Converts bytes to human-readable format (B, KB, MB, GB, TB)
```

### 2. DownloadManagerHelper (Business Logic)
**Location:** `utils/DownloadManagerHelper.kt`

**Key Properties:**
```kotlin
private val downloadManager: DownloadManager
private val prefs: SharedPreferences
private val _downloads: MutableStateFlow<List<DownloadItem>>
val downloads: StateFlow<List<DownloadItem>>
```

**Public Methods:**
```kotlin
// Start a new download
fun startDownload(
    url: String,
    filename: String? = null,
    mimeType: String? = null,
    showNotification: Boolean = true
): Long

// Delete a download (file and metadata)
fun deleteDownload(downloadItem: DownloadItem)

// Open file with default app
fun openFile(downloadItem: DownloadItem)

// Share file via Android share sheet
fun shareFile(downloadItem: DownloadItem)

// Clear all downloads
fun clearAllDownloads()
```

**Private Methods:**
```kotlin
// Update download status from DownloadManager
private fun updateDownloadStatus(downloadId: Long)

// Add new download to list
private fun addDownload(downloadItem: DownloadItem)

// Persist downloads to SharedPreferences
private fun saveDownloads()

// Load downloads from SharedPreferences
private fun loadDownloads()

// Register BroadcastReceiver for download completion
private fun registerDownloadReceiver()
```

### 3. DownloadsScreen (UI)
**Location:** `ui/screens/DownloadsScreen.kt`

**Main Composable:**
```kotlin
@Composable
fun DownloadsScreen(
    onBackClick: () -> Unit
)
```

**Sub-components:**
```kotlin
@Composable
fun DownloadItemCard(
    downloadItem: DownloadItem,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
)
```

**State Management:**
```kotlin
val downloadManager = remember { DownloadManagerHelper(context) }
val downloads by downloadManager.downloads.collectAsState()
var showDeleteDialog by remember { mutableStateOf(false) }
var selectedDownload by remember { mutableStateOf<DownloadItem?>(null) }
```

### 4. WebView Integration
**Location:** `ui/screens/WebViewScreen.kt`

**Integration Point:**
```kotlin
setDownloadListener(object : DownloadListener {
    override fun onDownloadStart(
        url: String?,
        userAgent: String?,
        contentDisposition: String?,
        mimetype: String?,
        contentLength: Long
    ) {
        // Security check
        if (securityLevel == SecurityLevel.HIGH) {
            // Show error and return
            return
        }
        
        // Extract filename
        val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
        
        // Start download with tracking
        val downloadId = downloadManagerHelper.startDownload(
            url = url,
            filename = filename,
            mimeType = mimetype,
            showNotification = true
        )
    }
})
```

## Data Flow

### Download Started (from WebView)
```
WebView Download Triggered
    ↓
WebViewScreen.DownloadListener
    ↓
DownloadManagerHelper.startDownload()
    ↓
Android DownloadManager.enqueue()
    ↓
Create DownloadItem with DOWNLOADING status
    ↓
Update StateFlow
    ↓
Save to SharedPreferences
    ↓
UI updates automatically (via StateFlow)
```

### Download Completed
```
Android DownloadManager completion
    ↓
BroadcastReceiver receives intent
    ↓
DownloadManagerHelper.updateDownloadStatus()
    ↓
Query DownloadManager for status
    ↓
Update DownloadItem with COMPLETED status
    ↓
Update StateFlow
    ↓
Save to SharedPreferences
    ↓
UI updates automatically
```

### File Operations
```
User taps "Open" or "Share"
    ↓
DownloadsScreen calls helper method
    ↓
DownloadManagerHelper.openFile() or shareFile()
    ↓
Create FileProvider URI
    ↓
Launch Intent with URI
    ↓
Android handles app selection
```

## Storage Strategy

### Metadata Storage (SharedPreferences)
```json
{
  "downloads_list": [
    {
      "id": 1700000000000,
      "filename": "document.pdf",
      "url": "https://example.com/doc.pdf",
      "mimeType": "application/pdf",
      "size": 2621440,
      "timestamp": 1700000000000,
      "status": "COMPLETED",
      "filePath": "file:///storage/emulated/0/Download/document.pdf",
      "downloadId": 12345
    }
  ]
}
```

### File Storage
- Location: `/storage/emulated/0/Download/` (public Downloads folder)
- Access: Via FileProvider URIs for security
- Permissions: Scoped storage on Android 13+, legacy permissions on older versions

## Security Considerations

### 1. Security Level Integration
```kotlin
when (securityLevel) {
    SecurityLevel.HIGH -> {
        // Downloads disabled
        showError("Downloads are disabled in High Security mode")
    }
    SecurityLevel.MEDIUM, SecurityLevel.LOW -> {
        // Downloads allowed
        startDownload(...)
    }
}
```

### 2. FileProvider Security
- Uses `androidx.core.content.FileProvider`
- Grants temporary URI permissions
- Prevents direct file path exposure
- Configured in `file_paths.xml`

### 3. Permission Handling
```xml
<!-- Only for Android 12 and below -->
<uses-permission 
    android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission 
    android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

## Error Handling

### Download Errors
```kotlin
try {
    val downloadId = downloadManager.enqueue(request)
    // Track download
} catch (e: Exception) {
    Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
    Log.e(TAG, "Download error", e)
}
```

### File Access Errors
```kotlin
try {
    val uri = FileProvider.getUriForFile(context, authority, file)
    // Open or share file
} catch (e: Exception) {
    Log.e(TAG, "Failed to access file: ${e.message}", e)
}
```

### JSON Parsing Errors
```kotlin
try {
    val jsonArray = JSONArray(jsonString)
    // Parse downloads
} catch (e: Exception) {
    Log.e(TAG, "Failed to parse downloads: ${e.message}", e)
}
```

## Testing Guide

### Unit Testing
```kotlin
// Test DownloadItem data class
@Test
fun testDownloadItemCreation() {
    val item = DownloadItem(
        filename = "test.pdf",
        url = "https://example.com/test.pdf",
        status = DownloadStatus.COMPLETED
    )
    assertEquals("test.pdf", item.filename)
    assertEquals(DownloadStatus.COMPLETED, item.status)
}

// Test size formatting
@Test
fun testReadableSize() {
    assertEquals("1.00 KB", 1024L.toReadableSize())
    assertEquals("1.00 MB", (1024L * 1024).toReadableSize())
}
```

### Integration Testing
```kotlin
// Test download flow
@Test
fun testDownloadFlow() {
    val helper = DownloadManagerHelper(context)
    val downloadId = helper.startDownload(
        url = "https://example.com/test.pdf",
        filename = "test.pdf"
    )
    assertTrue(downloadId > 0)
}
```

### UI Testing
```kotlin
@Test
fun testDownloadsScreen() {
    composeTestRule.setContent {
        DownloadsScreen(onBackClick = {})
    }
    
    // Verify empty state
    composeTestRule
        .onNodeWithText("No downloads yet")
        .assertIsDisplayed()
}
```

## Performance Considerations

1. **StateFlow vs LiveData**: Using StateFlow for reactive UI updates
2. **JSON vs Room**: SharedPreferences with JSON for simplicity (suitable for moderate data)
3. **Lazy Loading**: LazyColumn for efficient list rendering
4. **Background Operations**: DownloadManager handles downloads in background

## Migration Path (If Needed)

### To Room Database
```kotlin
@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: Long,
    val filename: String,
    val url: String,
    // ... other fields
)

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>
    
    @Insert
    suspend fun insert(download: DownloadEntity)
    
    @Delete
    suspend fun delete(download: DownloadEntity)
}
```

## Debugging Tips

1. **Enable logging**: Look for TAG "DownloadManagerHelper" in logcat
2. **Check file paths**: Verify files exist at reported paths
3. **Inspect SharedPreferences**: Use Device File Explorer
4. **Monitor DownloadManager**: Query DownloadManager for status
5. **Test on real device**: Emulator may have different behavior

## API References

- [DownloadManager](https://developer.android.com/reference/android/app/DownloadManager)
- [FileProvider](https://developer.android.com/reference/androidx/core/content/FileProvider)
- [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
