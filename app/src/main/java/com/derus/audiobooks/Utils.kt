package com.derus.audiobooks

import android.media.MediaMetadataRetriever
import okhttp3.ResponseBody
import java.io.*


class Utils{
    companion object {
        fun writeResponseBodyToDisk(body: ResponseBody, file: File): Boolean {
            try {
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    val fileReader = ByteArray(4096)
                    var fileSizeDownloaded: Long = 0
                    inputStream = body.byteStream()
                    outputStream = FileOutputStream(file)
                    while (true) {
                        val read = inputStream!!.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        outputStream.write(fileReader, 0, read)
                        fileSizeDownloaded += read.toLong()
                    }
                    outputStream.flush()
                    return true
                } catch (e: IOException) {
                    return false
                } finally {
                    if (inputStream != null) {
                        inputStream.close()
                    }
                    if (outputStream != null) {
                        outputStream.close()
                    }
                }
            } catch (e: IOException) {
                return false
            }

        }

        fun getTimeString(millis: Long): String {
            val buf = StringBuffer()

            val hours = (millis / (1000 * 60 * 60)).toInt()
            val minutes = (millis % (1000 * 60 * 60) / (1000 * 60)).toInt()
            val seconds = (millis % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

            buf
                    .append(String.format("%02d", hours))
                    .append(":")
                    .append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds))

            return buf.toString()
        }

        fun getDurationFromFile(file: File): String{
            var duration = getTimeString(0)
            if (file.exists()) {
                val data = MediaMetadataRetriever()
                data.setDataSource(file.absolutePath)
                duration = getTimeString(data.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong())
                data.release()
            }
            return duration
        }

        fun deleteFiles(fileOrDirectory: File) {
            if (fileOrDirectory.isDirectory)
                for (child in fileOrDirectory.listFiles())
                    deleteFiles(child)

            fileOrDirectory.delete()
        }
    }
}