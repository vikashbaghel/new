package com.app.rupyz.generic.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@SuppressLint("NewApi")
fun Uri.getPath(context: Context): String? {
    // check here to KITKAT or new version
    val isKitKat = true
    var selection: String? = null
    var selectionArgs: Array<String>? = null

    if (isExternalStorageDocument(this)) {
        val docId = DocumentsContract.getDocumentId(this)
        val split = docId.split(":").toTypedArray()
        val type = split[0]

        val fullPath = getPathFromExtSD(split)
        return if (fullPath.isNotEmpty()) {
            fullPath
        } else {
            null
        }
    }

    if (isDownloadsDocument(this)) {
        var id: String?
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(this, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val fileName = cursor.getString(0)
                val path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
                if (!TextUtils.isEmpty(path)) {
                    return path
                }
            }
        } finally {
            cursor?.close()
        }
        id = DocumentsContract.getDocumentId(this)
        if (!TextUtils.isEmpty(id)) {
            if (id.startsWith("raw:")) {
                return id.replaceFirst("raw:".toRegex(), "")
            }
            val contentUriPrefixesToTry = arrayOf("content://downloads/public_downloads", "content://downloads/my_downloads")
            for (contentUriPrefix in contentUriPrefixesToTry) {
                return try {
                    val contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), id.toLong())
                    getDataColumn(context, contentUri, null, null)
                } catch (e: NumberFormatException) {
                    //In Android 8 and Android P the id is not a number
                    this.path!!.replaceFirst("^/document/raw:".toRegex(), "").replaceFirst("^raw:".toRegex(), "")
                }
            }
        }
    }

    if (isMediaDocument(this)) {
        val docId = DocumentsContract.getDocumentId(this)
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
        selection = "_id=?"
        selectionArgs = arrayOf(split[1])


        return getDataColumn(context, contentUri, selection, selectionArgs)
    }

    if (isGoogleDriveUri(this)) {
        return getDriveFilePath(this, context)
    }

    if (isWhatsAppFile(this)) {
        return getFilePathForWhatsApp(this, context)
    }


    if ("content" == this.scheme) {

        if (isGooglePhotosUri(this)) {
            return this.lastPathSegment
        }
        if (isGoogleDriveUri(this)) {
            return getDriveFilePath(this, context)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            // return getFilePathFromURI(context,uri);
            return copyFileToInternalStorage(this, context, "userfiles")
            // return getRealPathFromURI(context,uri);
        } else {
            return getDataColumn(context, this, null, null)
        }

    }
    if ("file" == this.scheme) {
        return this.path
    }


    return null
}

private fun fileExists(filePath: String): Boolean {
    val file = File(filePath)
    return file.exists()
}

private fun getPathFromExtSD(pathData: Array<String>): String {
    val type = pathData[0]
    val relativePath = "/${pathData[1]}"
    var fullPath = ""

    // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
    // something like "71F8-2C0A", some kind of unique id per storage
    // don't know any API that can get the root path of that storage based on its id.
    //
    // so no "primary" type, but let the check here for other devices
    if ("primary" == type) {
        fullPath = "${Environment.getExternalStorageDirectory()}$relativePath"
        if (fileExists(fullPath)) {
            return fullPath
        }
    }

    // Environment.isExternalStorageRemovable() is `true` for external and internal storage
    // so we cannot relay on it.
    //
    // instead, for each possible path, check if file exists
    // we'll start with secondary storage as this could be our (physically) removable sd card
    fullPath = "${System.getenv("SECONDARY_STORAGE")}$relativePath"
    if (fileExists(fullPath)) {
        return fullPath
    }

    fullPath = "${System.getenv("EXTERNAL_STORAGE")}$relativePath"
    if (fileExists(fullPath)) {
        return fullPath
    }

    return fullPath
}

private fun getDriveFilePath(uri: Uri, context: Context): String {
    val returnUri = uri
    val returnCursor = context.contentResolver.query(returnUri, null, null, null, null)
    val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    val size = returnCursor.getLong(sizeIndex).toString()
    val file = File(context.cacheDir, name)
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        var read: Int
        val maxBufferSize = 1 * 1024 * 1024
        val bytesAvailable = inputStream!!.available()

        //int bufferSize = 1024;
        val bufferSize = Math.min(bytesAvailable, maxBufferSize)

        val buffers = ByteArray(bufferSize)
        while (inputStream.read(buffers).also { read = it } != -1) {
            outputStream.write(buffers, 0, read)
        }
        Log.e("File Size", "Size ${file.length()}")
        inputStream.close()
        outputStream.close()
        Log.e("File Path", "Path ${file.path}")
        Log.e("File Size", "Size ${file.length()}")
    } catch (e: Exception) {
        Log.e("Exception", e.message!!)
    }
    return file.path
}

private fun copyFileToInternalStorage(uri: Uri, context: Context, newDirName: String): String {
    val returnUri = uri

    val returnCursor = context.contentResolver.query(returnUri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)

    val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    val size = returnCursor.getLong(sizeIndex).toString()

    val output: File = if (!newDirName.isEmpty()) {
        val dir = File("${context.filesDir}/$newDirName")
        if (!dir.exists()) {
            dir.mkdir()
        }
        File("${context.filesDir}/$newDirName/$name")
    } else {
        File("${context.filesDir}/$name")
    }
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(output)
        var read: Int
        val bufferSize = 1024
        val buffers = ByteArray(bufferSize)
        while (inputStream!!.read(buffers).also { read = it } != -1) {
            outputStream.write(buffers, 0, read)
        }
        inputStream.close()
        outputStream.close()
    } catch (e: Exception) {
        Log.e("Exception", e.message!!)
    }
    return output.path
}

private fun getFilePathForWhatsApp(uri: Uri, context: Context): String {
    return copyFileToInternalStorage(uri, context, "whatsapp")
}

private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index: Int = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

private fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

private fun isWhatsAppFile(uri: Uri): Boolean {
    return "com.whatsapp.provider.media" == uri.authority
}

private fun isGoogleDriveUri(uri: Uri): Boolean {
    return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
}