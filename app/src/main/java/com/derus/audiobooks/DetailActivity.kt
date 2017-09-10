package com.derus.audiobooks

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class DetailActivity : AppCompatActivity(), View.OnClickListener, OnDownloadFileListener {

    private var mMyMediaPlayer: MyMediaPlayer? = null
    private val api = ApiService()
    private var str: String = ""
    private var file: File? = null
    private var directory: File? = null
    private var urlFile: String = ""
    lateinit var  extraUrl: String
    private var downloadFile: DownloadFile? = null

    private var audiobook = Audiobook()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        extraUrl = intent.getStringExtra("EXTRA_URL")
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE_URL")
        val title = intent.getStringExtra("EXTRA_TITLE")
        val author = intent.getStringExtra("EXTRA_AUTHOR")

        Picasso.with(this).load(imageUrl).into(detail_image)

        str = File.separator + title + File.separator + title + ".mp3"
        file = File(applicationContext!!.getExternalFilesDir(null), str)
        directory = File(applicationContext!!.getExternalFilesDir(null), File.separator + title)

        audiobook = getAudiobook(extraUrl)

        if (file!!.exists()) {
            mMyMediaPlayer = MyMediaPlayer(this, file!!, play_pause_btn, progressbar, tv_progress)
            tv_progress.setText("00:00:00 / " + Utils.getDurationFromFile(file!!))
        }

        play_pause_btn.setOnClickListener(this)

        song_title.setText(title)
        song_artist.setText(author)
        setTitle(title)

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

    override fun onClick(v: View?) {
        if (v?.id == R.id.play_pause_btn) {
            togglePlayback()
        }
    }

    override fun onDownloadFinish() {
        invalidateOptionsMenu()
        tv_progress.setText("00:00:00 / " + Utils.getDurationFromFile(file!!))
        mMyMediaPlayer = MyMediaPlayer(this, file!!, play_pause_btn, progressbar, tv_progress)
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
            if (file?.exists()!!){
                if (mMyMediaPlayer?.getMediaPlayer() != null && !mMyMediaPlayer!!.isPlaying()){
                    Utils.deleteFiles(directory!!)
                    mMyMediaPlayer?.relaxResources(true)
                    mMyMediaPlayer?.resetProgress()
                    invalidateOptionsMenu()
                }else{
            /*        Utils.deleteFiles(directory!!)
                    invalidateOptionsMenu()
                    tv_progress.setText(R.string.timer_format)*/
                }
            }else{
                downloadMp3File(file!!, urlFile)
            }
        }

        return super.onOptionsItemSelected(item)
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
            downloadMp3File(file!!, urlFile)
        }
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

    fun downloadMp3File(file: File, url: String){
        if (urlFile.length > 0 && !file.exists()){
            createDirectory(directory.toString())
            downloadFile = DownloadFile(this, file, url, this)
            downloadFile?.execute()
        }else{
            getAudiobook(extraUrl)
        }
    }

    fun createDirectory(directory: String) {
        val directoryFile = File(directory)
        if (!directoryFile.exists())
            directoryFile.mkdirs()
    }

/*    fun downloadAudiobookMedia(pdfFile: File, url: String) {

        val progress = ProgressDialog(this)
        progress.setMessage(getString(R.string.downloading_file))

        val call = api.downloadFile(url)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.isSuccessful!!) {

                    val isFileDownloaded = Utils.writeResponseBodyToDisk(response.body()!!, pdfFile)
                    if (isFileDownloaded){
                        toast(getString(R.string.downloaded))
                    }

                    mMyMediaPlayer = MyMediaPlayer(applicationContext, file!!, play_pause_btn, progressbar, tv_progress)
                    tv_progress.setText("00:00:00 / " + Utils.getDurationFromFile(file!!))
                    invalidateOptionsMenu()
                    progress.dismiss()
                } else {
                    toast(getString(R.string.loading_data_error))
                    progress.dismiss()
                    Utils.deleteFiles(file!!)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                progress.dismiss()
                toast(getString(R.string.loading_data_error))
                Utils.deleteFiles(file!!)
            }
        })

        progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.action_cancel), {
            _, _ ->
            progress.dismiss()
            call.cancel()
        })
        progress.show()
    }*/
}
