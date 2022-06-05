import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.io.File


class UtilsFile {
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return EXTERNAL_STORAGE_DOCUMENTS_PATH == uri.getAuthority()
    }

    private fun isPublicDocument(uri: Uri): Boolean {
        return PUBLIC_DOWNLOAD_PATH == uri.getAuthority()
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return DOWNLOAD_DOCUMENTS_PATH == uri.getAuthority()
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return MEDIA_DOCUMENTS_PATH == uri.getAuthority()
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return MEDIA_DOCUMENTS_PATH == uri.getAuthority()
    }

    private fun isPhotoContentUri(uri: Uri): Boolean {
        return PHOTO_CONTENTS_PATH == uri.getAuthority()
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        //String column = "_data" REMOVED IN FAVOR OF NULL FOR ALL
        //String projection = arrayOf(column) REMOVED IN FAVOR OF PROJECTION FOR ALL
        try {
            cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                return cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            Log.e("PathUtils", "Error getting uri for cursor to read file: " + e.message)
        } finally {
            assert(cursor != null)
            cursor?.close()
        }
        return null
    }

    fun getFullPathFromContentUri(context: Context, uri: Uri): String? {
        val isKitKat = true
        var filePath = ""
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } //non-primary e.g sd card
                else {
                    //getExternalMediaDirs() added in API 21
                    val extenal: Array<File> = context.getExternalMediaDirs()
                    for (f in extenal) {
                        filePath = f.getAbsolutePath()
                        if (filePath.contains(type)) {
                            val endIndex = filePath.indexOf("Android")
                            filePath = filePath.substring(0, endIndex) + split[1]
                        }
                    }
                    filePath
                }
            } else if (isDownloadsDocument(uri)) {
                val fileName = getDataColumn(context, uri, null, null)
                var uriToReturn: String? = null
                if (fileName != null) {
                    uriToReturn = Uri.withAppendedPath(
                        Uri.parse(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath),
                        fileName
                    ).toString()
                }
                return uriToReturn
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                var cursor: Cursor? = null
                val column = "_data"
                val projection = arrayOf(
                    column
                )
                try {
                    cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs,
                            null)
                    if (cursor != null && cursor.moveToFirst()) {
                        val column_index: Int = cursor.getColumnIndexOrThrow(column)
                        return cursor.getString(column_index)
                    }
                } finally {
                    if (cursor != null) cursor.close()
                }
                return null
            }
        } else if ("content".equals(uri.getScheme(), ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
            return uri.getPath()
        } else if (isPublicDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri: Uri = ContentUris.withAppendedId(
                Uri.parse(PUBLIC_DOWNLOAD_PATH), id.toLong())
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            @SuppressLint("Recycle") val cursor: Cursor =
                context.getContentResolver().query(contentUri, projection, null, null, null)!!
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                return cursor.getString(column_index)
            }
        }
        return null
    }

    companion object {
        private const val PUBLIC_DOWNLOAD_PATH = "content://downloads/public_downloads"
        private const val EXTERNAL_STORAGE_DOCUMENTS_PATH = "com.android.externalstorage.documents"
        private const val DOWNLOAD_DOCUMENTS_PATH = "com.android.providers.downloads.documents"
        private const val MEDIA_DOCUMENTS_PATH = "com.android.providers.media.documents"
        private const val PHOTO_CONTENTS_PATH = "com.google.android.apps.photos.content"
    }
}