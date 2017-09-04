package com.derus.audiobooks

import android.app.ProgressDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class DetailActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * help to toggle between play and pause.
     */
    private var mMyMediaPlayer: MyMediaPlayer? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mPlayPauseButton: ImageButton? = null
    private var mSeekbar:SeekBar? = null
    private var mTimer: TextView? = null
    private var seekBarHandler:SeekBarHandler? = null
    private val api = ApiService()
    private var str: String = ""
    private var file: File? = null
    private var directory: File? = null
    private var urlFile: String = ""

    private var audiobook = Audiobook()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val url = intent.getStringExtra("EXTRA_URL")
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE_URL")
        val title = intent.getStringExtra("EXTRA_TITLE")
        val author = intent.getStringExtra("EXTRA_AUTHOR")

        Picasso.with(this).load(imageUrl).into(detail_image)

        str = File.separator + title + File.separator + title + ".mp3"
        file = File(applicationContext!!.getExternalFilesDir(null), str)
        directory = File(applicationContext!!.getExternalFilesDir(null), File.separator + title)
        //createDirectory(directory.toString())

        audiobook = getAudiobook(url)

        if (file!!.exists())
        mMyMediaPlayer = MyMediaPlayer(this, file!!, play_pause_btn, progressbar, tv_progress)


        mPlayPauseButton = play_pause_btn
        mPlayPauseButton?.setOnClickListener(this)

        mTimer = tv_progress

        song_title.setText(title)
        song_artist.setText(author)
        setTitle(title)

    }

    fun createDirectory(directory: String) {
        val directoryFile = File(directory)
        if (!directoryFile.exists())
            directoryFile.mkdirs()
    }

    fun getAudiobook(url: String): Audiobook{
        var audiobook = Audiobook()
        api.getAudiobook(url).enqueue(object: Callback<Audiobook>{
            override fun onResponse(call: Call<Audiobook>?, response: Response<Audiobook>?) {
                if (response != null) {
                    audiobook = response.body()!!
                    urlFile = audiobook.media.get(1).url
                }
            }

            override fun onFailure(call: Call<Audiobook>?, t: Throwable?) {

            }

        })
        return audiobook
    }

    fun downloadAudiobookMedia(pdfFile: File, url: String) {

        val progress = ProgressDialog(this)
        progress.setMessage("Pobieranie pliku..")

        val call = api.downloadFile(url)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.isSuccessful!!) {
                    val isFileDownloaded = writeResponseBodyToDisk(response.body()!!, pdfFile)
                    if (isFileDownloaded){
                        Toast.makeText(applicationContext, "Pobrano", Toast.LENGTH_SHORT).show()
                    }

                    mMyMediaPlayer = MyMediaPlayer(applicationContext, file!!, play_pause_btn, progressbar, tv_progress)

                    invalidateOptionsMenu()
                    progress.dismiss()
                } else {
                    Toast.makeText(applicationContext, "Błąd pobierania", Toast.LENGTH_SHORT).show()
                    progress.dismiss()
                    deleteFiles(file!!)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                progress.dismiss()
                Toast.makeText(applicationContext, "Błąd pobierania", Toast.LENGTH_SHORT).show()
                deleteFiles(file!!)
            }
        })

        progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Anuluj", DialogInterface.OnClickListener {
            dialog, which ->
            progress.dismiss()
            call.cancel()
        })
        progress.show()
    }

    fun downloadMp3File(file: File, url: String){
        if (urlFile.length > 0 && !file.exists()){
            createDirectory(directory.toString())
            downloadAudiobookMedia(file, url)
        }
    }

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

    override fun onClick(v: View?) {
        if (v?.id == R.id.play_pause_btn) {
            togglePlayback()
        }
    }

    fun togglePlayback() {
        if (file!!.exists()) {
            if (mMyMediaPlayer != null) {
                if (mMyMediaPlayer!!.isPlaying()) {
                    mMyMediaPlayer!!.pauseAudio()
                } else {
                    mMyMediaPlayer!!.createMediaPlayerIfNeeded()
                    mMyMediaPlayer!!.playAudio()
                }
            }
        }else {
            Toast.makeText(this, "Pobierz plik audio!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mMyMediaPlayer?.getMediaPlayer() != null && mMyMediaPlayer!!.isPlaying()) {
            mMyMediaPlayer!!.pauseAudio()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mMyMediaPlayer?.getMediaPlayer() != null)
        mMyMediaPlayer!!.relaxResources(true)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.detail, menu)

        if (file!!.exists()){
            menu?.findItem(R.id.action_download)?.setIcon(R.drawable.ic_delete_file)
        }else{
            menu?.findItem(R.id.action_download)?.setIcon(R.drawable.ic_download_file)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        if(id == R.id.action_download){
            if (file?.exists()!! && !mMyMediaPlayer!!.isPlaying()){
                deleteFiles(directory!!)
                mMyMediaPlayer?.relaxResources(true)
                mMyMediaPlayer?.resetProgress()
                invalidateOptionsMenu()
            }else{
                downloadMp3File(file!!, urlFile)
                //invalidateOptionsMenu()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun deleteFiles(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles())
                deleteFiles(child)

        fileOrDirectory.delete()
    }
}
