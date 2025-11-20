# Downloads UI Guide

## Navigation Flow

```
Settings Activity
    └─> Storage Section
        └─> Downloads Item
            └─> Downloads Screen
                ├─> View all downloads
                ├─> Open files
                ├─> Share files
                └─> Delete files
```

## UI Components

### 1. Settings Integration
```
┌─────────────────────────────────────────┐
│  Settings                          [←]  │
├─────────────────────────────────────────┤
│                                         │
│  🛡️ Security                            │
│  🔑 Password Management                 │
│                                         │
│  📁 Storage                             │
│  ┌─────────────────────────────────┐   │
│  │  📥  Downloads                  │   │
│  │  View and manage downloaded     │   │
│  │  files                      [>] │   │
│  └─────────────────────────────────┘   │
│                                         │
│  🔒 Privacy & Legal                     │
└─────────────────────────────────────────┘
```

### 2. Downloads Screen - Empty State
```
┌─────────────────────────────────────────┐
│  Downloads                    [←]  [🗑️] │
├─────────────────────────────────────────┤
│                                         │
│                                         │
│              📥                         │
│                                         │
│        No downloads yet                 │
│                                         │
│   Files you download will appear here  │
│                                         │
│                                         │
└─────────────────────────────────────────┘
```

### 3. Downloads Screen - With Files
```
┌─────────────────────────────────────────┐
│  Downloads                    [←]  [🗑️] │
├─────────────────────────────────────────┤
│  ℹ️ Total Downloads: 5                  │
│     Completed: 4                        │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 📄  document.pdf            [⋮] │   │
│  │     ✓ Completed  2.5 MB  2h ago │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🖼️  image.jpg               [⋮] │   │
│  │     ✓ Completed  1.2 MB  5h ago │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🎥  video.mp4               [⋮] │   │
│  │     ⏳ Downloading  0 B  Just now│   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🎵  audio.mp3               [⋮] │   │
│  │     ✓ Completed  3.8 MB  1d ago │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 📄  report.pdf              [⋮] │   │
│  │     ❌ Failed  0 B  2d ago      │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### 4. File Actions Menu
```
┌─────────────────────────────────────────┐
│  📄  document.pdf            [⋮] ◄───┐  │
│     ✓ Completed  2.5 MB  2h ago      │  │
│                                      │  │
│                    ┌─────────────────┤  │
│                    │  🔗 Open        │  │
│                    │  📤 Share       │  │
│                    │  🗑️ Delete      │  │
│                    └─────────────────┘  │
└─────────────────────────────────────────┘
```

### 5. Delete Confirmation Dialog
```
     ┌─────────────────────────┐
     │      🗑️                  │
     │   Delete Download       │
     │                         │
     │  Are you sure you want  │
     │  to delete "file.pdf"?  │
     │  This will remove the   │
     │  file from your device. │
     │                         │
     │  [Cancel]  [Delete]     │
     └─────────────────────────┘
```

## File Type Icons

- 📄 PDF files
- 🖼️ Image files (jpg, png, gif)
- 🎥 Video files (mp4, mkv, avi)
- 🎵 Audio files (mp3, wav, flac)
- 📁 Generic files
- ⏳ Downloading (in progress)
- ❌ Failed downloads

## Status Badges

- ✅ **Completed** - Green badge, download successful
- ⏳ **Downloading** - Blue badge, in progress
- ❌ **Failed** - Red badge, download failed
- ⏸️ **Pending** - Gray badge, queued
- 🚫 **Cancelled** - Gray badge, user cancelled

## Color Scheme

Follows Material Design 3 theme:
- Primary color for completed downloads
- Tertiary color for in-progress downloads
- Error color for failed downloads
- Surface variants for backgrounds
- Proper contrast ratios for accessibility

## Interactions

### Tap Actions
- **Tap on completed file**: Opens with default app
- **Tap on incomplete file**: Shows "Download not completed yet" toast
- **Tap menu (⋮)**: Opens action menu
- **Tap clear all (🗑️)**: Shows confirmation dialog

### Long Press
Not implemented (using menu button instead)

## Accessibility

- All icons have content descriptions
- Color not used as the only indicator (text + icons)
- Touch targets meet minimum size requirements
- Screen reader friendly
- High contrast mode compatible
