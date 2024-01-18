package com.smileapp.zodiac.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.smileapp.zodiac.view.fragment.ProfileFragment
import java.io.File
import java.io.FileNotFoundException

class InternalStorageContentProvider: ContentProvider() {
    var MIME_TYPES = HashMap<String, String>()
    val CONTENT_URI = Uri.parse("content://com.smileapp.zodiac/")
    override fun openFile(uri:Uri, mode:String):ParcelFileDescriptor{
        val f = File(context!!.filesDir,ProfileFragment().TEMP_PHOTO_FILE_NAME)
        if (f.exists()){
            return ParcelFileDescriptor.open(f,ParcelFileDescriptor.MODE_READ_WRITE)
        }
        throw FileNotFoundException(uri.path)
    }

    override fun onCreate(): Boolean {
        MIME_TYPES[".jpg"] = "image/jpeg"
        MIME_TYPES[".jpeg"] = "image/jpeg"
        try {
            val mFile = File(context?.filesDir,ProfileFragment().TEMP_PHOTO_FILE_NAME)
            if (!mFile.exists()){
                mFile.createNewFile()
                context?.contentResolver?.notifyChange(CONTENT_URI,null)
            }
            return (true)
        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?,
    ): Cursor? {
        return null
    }

    override fun getType(p0: Uri): String? {
        val path = p0.toString()

        for (extension in MIME_TYPES.keys){
            if (path.endsWith(extension)){
                return MIME_TYPES.toString()
            }

        }
        return (null)
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return 0
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }
}