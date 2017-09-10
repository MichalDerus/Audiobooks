package com.derus.audiobooks

import android.media.MediaPlayer
import android.os.AsyncTask
import android.widget.SeekBar
import android.widget.TextView


class SeekBarHandler(val seekbar: SeekBar?, var mediaPlayer: MediaPlayer?, var isViewOn: Boolean, val timer: TextView): AsyncTask<Void, Void, Boolean>() {

    override fun onPreExecute() {
        super.onPreExecute()
        seekbar?.max = mediaPlayer?.duration!!
    }

    override fun onProgressUpdate(vararg values: Void?) {
        super.onProgressUpdate(*values)
        val time = mediaPlayer?.getCurrentPosition()!!
        val maxTime = seekbar?.max
        seekbar?.setProgress(time)

        timer.setText(Utils.getTimeString(time.toLong()) + " / " + Utils.getTimeString(maxTime!!.toLong()))
    }

    override fun onCancelled() {
        super.onCancelled()
        setViewOnOff(false)
    }

    fun setViewOnOff(isOn:Boolean) {
        isViewOn = isOn
    }

    fun refreshMediaPlayer(mediaPlayer: MediaPlayer?) {
        this.mediaPlayer = mediaPlayer
    }

    override fun doInBackground(vararg params: Void?): Boolean {
        while (mediaPlayer?.isPlaying() == true && isViewOn == true) {
            try {
                Thread.sleep(200)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            publishProgress()
        }
        return true
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
    }

}