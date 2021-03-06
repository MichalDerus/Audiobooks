package com.derus.audiobooks.utilities

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.support.v4.content.ContextCompat
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.derus.audiobooks.R
import java.io.File

class MyMediaPlayer(val context: Context, var file: File, val mPlayPauseButton: ImageButton, val mSeekbar: SeekBar, val mTimer: TextView) : MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {

    private var mMediaPlayer: MediaPlayer? = null
    private var seekBarHandler: SeekBarHandler? = null

    init {
        mSeekbar.setOnSeekBarChangeListener(this)
        createMediaPlayerIfNeeded()
    }


    fun getMediaPlayer(): MediaPlayer? = mMediaPlayer

    fun resetProgress() {
        mSeekbar.progress = 0
        mTimer.setText(R.string.timer_format)
    }


    fun createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            //mMediaPlayer = MediaPlayer.create(this, R.raw.simplerock)
            mMediaPlayer = MediaPlayer()
            mMediaPlayer?.setDataSource(context, Uri.parse(file.toString()))
            mMediaPlayer?.prepare()

            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mMediaPlayer?.setWakeMode(context,
                    PowerManager.PARTIAL_WAKE_LOCK)

            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mMediaPlayer?.setOnPreparedListener(this)
            mMediaPlayer?.setOnCompletionListener(this)
            mMediaPlayer?.setOnErrorListener(this)
            mMediaPlayer?.setOnSeekCompleteListener(this)
        }
    }

    fun playAudio() {
        mMediaPlayer?.start()
        seekBarHandler = SeekBarHandler(mSeekbar, mMediaPlayer, isViewOn = true, timer = mTimer)
        seekBarHandler?.execute()
        val pauseDrawabale = ContextCompat.getDrawable(context, android.R.drawable.ic_media_pause)
        mPlayPauseButton.setImageDrawable(pauseDrawabale)
    }

    fun pauseAudio() {
        seekBarHandler?.cancel(true)
        mMediaPlayer?.pause()
        val playDrawabale = ContextCompat.getDrawable(context, android.R.drawable.ic_media_play)
        mPlayPauseButton.setImageDrawable(playDrawabale)
    }

    fun isPlaying(): Boolean {
        if (mMediaPlayer != null)
            return mMediaPlayer!!.isPlaying
        else
            return false
    }

    /**
     * Releases resources used by the service for playback. This includes the
     * "foreground service" status, the wake locks and possibly the MediaPlayer.

     * @param releaseMediaPlayer Indicates whether the Media Player should also
     * *            be released or not
     */
    fun relaxResources(releaseMediaPlayer: Boolean) {
        seekBarHandler?.cancel(true)
        seekBarHandler = null
// stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mMediaPlayer != null) {
            mMediaPlayer?.reset()
            mMediaPlayer?.release()
            mMediaPlayer = null
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        relaxResources(true)
        val playDrawabale = ContextCompat.getDrawable(context, android.R.drawable.ic_media_play)
        mPlayPauseButton.setImageDrawable(playDrawabale)
        mSeekbar.progress = 0
        mTimer.setText(R.string.timer_format)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean = false

    override fun onPrepared(mp: MediaPlayer?) {
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            mMediaPlayer?.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}