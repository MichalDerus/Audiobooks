package com.derus.audiobooks

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity(), OnBookClickListener {

    override fun OnBookClick(url: String, imageUrl: String, title: String, author: String) {
        //Toast.makeText(applicationContext, url, Toast.LENGTH_SHORT).show()
        if (url.isEmpty()) {
            Toast.makeText(applicationContext, "Nie ma url!", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("EXTRA_URL", url)
            intent.putExtra("EXTRA_IMAGE_URL", imageUrl)
            intent.putExtra("EXTRA_TITLE", title)
            intent.putExtra("EXTRA_AUTHOR", author)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val recyclerView = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val api = ApiService()
        val list = ArrayList<AudiobookResponse>()
        recyclerView.adapter = AudiobookAdapter(list, this)
        recyclerView.setHasFixedSize(true)
        fastscroll.setRecyclerView(recyclerView)

        api.getAudiobooksList().enqueue(object: Callback<ArrayList<AudiobookResponse>> {
            override fun onFailure(call: Call<ArrayList<AudiobookResponse>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<AudiobookResponse>>?, response: Response<ArrayList<AudiobookResponse>>?) {
                if (response != null && response.isSuccessful) {
                    list.addAll(response.body()!!)
                    list.sortBy { it.title }
                    recyclerView.adapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        if(id == R.id.action_delete_all){
            //deleteFiles(File(this.getExternalFilesDir(null).toString()))
            //Log.i("aaa", this.getExternalFilesDir(null).toString())

            alert("Czy chcesz usunąć wszystkie pobrane pliki?") {
                positiveButton("Tak") { deleteFiles(File(application.getExternalFilesDir(null).toString())) }
                negativeButton("Nie") {  }
            }.show()
        }
        return super.onOptionsItemSelected(item)
    }

    fun deleteFiles(fileOrDirectory: File) {
        for (files in fileOrDirectory.listFiles()){
            if (files.isDirectory) {
                for (child in files.listFiles())
                    child.delete()
            }
            files.delete()
        }
    }
}
