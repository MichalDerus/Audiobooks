package com.derus.audiobooks

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File




class MainActivity : AppCompatActivity(), OnBookClickListener, SearchView.OnQueryTextListener {

    lateinit var audiobookAdapter: AudiobookAdapter

    override fun onQueryTextSubmit(query: String?): Boolean = true

    override fun onQueryTextChange(newText: String?): Boolean {
        audiobookAdapter.filter.filter(newText)
        return true
    }

    override fun OnBookClick(url: String, imageUrl: String, title: String, author: String) {
        //Toast.makeText(applicationContext, url, Toast.LENGTH_SHORT).show()
        if (url.isEmpty()) {
            Toast.makeText(this, "Nie ma url!", Toast.LENGTH_SHORT).show()
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

        recyclerView.layoutManager = LinearLayoutManager(this)
        val api = ApiService()
        val list = ArrayList<AudiobookResponse>()
        audiobookAdapter = AudiobookAdapter(list, this)
        recyclerView.adapter = audiobookAdapter
        recyclerView.setHasFixedSize(true)
        fastscroll.setRecyclerView(recyclerView)

        api.getAudiobooksList().enqueue(object: Callback<ArrayList<AudiobookResponse>> {
            override fun onFailure(call: Call<ArrayList<AudiobookResponse>>?, t: Throwable?) {
                download_data_error.visibility = View.VISIBLE
            }

            override fun onResponse(call: Call<ArrayList<AudiobookResponse>>?, response: Response<ArrayList<AudiobookResponse>>?) {
                if (response != null && response.isSuccessful) {
                    list.addAll(response.body()!!)
                    list.sortBy { it.title }
                    recyclerView.adapter.notifyDataSetChanged()
                    download_data_error.visibility = View.INVISIBLE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)

        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu!!.findItem(R.id.search).actionView as? SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView?.setSubmitButtonEnabled(false)
        searchView?.setOnQueryTextListener(this)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        if(id == R.id.action_delete_all){
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
