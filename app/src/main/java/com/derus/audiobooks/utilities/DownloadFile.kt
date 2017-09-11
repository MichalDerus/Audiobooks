package com.derus.audiobooks.utilities

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import com.derus.audiobooks.R
import com.derus.audiobooks.listener.OnDownloadFileListener
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class DownloadFile(val context: Context, val file: File, val url: String, val listener: OnDownloadFileListener) : AsyncTask<Void, Int, Boolean>() {
    lateinit var progressDialog: ProgressDialog

    override fun onPreExecute() {
        super.onPreExecute()
        progressDialog = ProgressDialog(context)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setIndeterminate(false)
        progressDialog.setCancelable(false)
        progressDialog.setMax(100)
        progressDialog.setMessage(context.getString(R.string.downloading_file))
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.action_cancel), { _, _ ->
            progressDialog.dismiss()
            cancel(true)
            //Utils.deleteFiles(file)

        })
        progressDialog.show()

    }

    override fun doInBackground(vararg strings: Void?): Boolean {

        var count: Int
        try {
            val url = URL(url)
            val urlConnection = url.openConnection()
            urlConnection.connect()
            val lenghtOfFile = urlConnection.getContentLength()
            val input = BufferedInputStream(url.openStream())
            val output = FileOutputStream(file)
            val data = ByteArray(1024)
            var total: Long = 0

            while (true) {
                if (isCancelled) {
                    Utils.deleteFiles(file)
                    output.close()
                    input.close()
                    break
                }
                count = input.read(data)
                if (count == -1)
                    break
                total += count.toLong()
                publishProgress((total * 100 / lenghtOfFile).toInt())
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()
        } catch (e: Exception) {
        }

        return true
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)

        progressDialog.setProgress(values[0]!!)
    }

    override fun onPostExecute(value: Boolean) {
        progressDialog.dismiss()
        listener.onDownloadFinish()

    }
}