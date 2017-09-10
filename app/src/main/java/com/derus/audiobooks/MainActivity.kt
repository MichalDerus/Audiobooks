package com.derus.audiobooks

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MainActivity : AppCompatActivity(), OnBookClickListener, SearchView.OnQueryTextListener{

    lateinit var audiobookAdapter: AudiobookAdapter
    val CHECKED_TITLE = 0
    val CHECKED_AUTHOR = 1
    lateinit var sortTitleValue: String
    lateinit var sortAuthorValue: String
    lateinit var sortKey: String
    val list = ArrayList<AudiobookResponse>()

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

        sortTitleValue = getString(R.string.settings_order_by_title_value)
        sortAuthorValue = getString(R.string.settings_order_by_author_value)
        sortKey = getString(R.string.settings_order_by_key)


        recyclerView.layoutManager = LinearLayoutManager(this)
        val api = ApiService()
        //val list = ArrayList<AudiobookResponse>()
        audiobookAdapter = AudiobookAdapter(this, getPreferences(Context.MODE_PRIVATE), list, this)
        recyclerView.adapter = audiobookAdapter
        recyclerView.setHasFixedSize(true)
        fastscroll.setRecyclerView(recyclerView)

        api.getAudiobooksList().enqueue(object : Callback<ArrayList<AudiobookResponse>> {
            override fun onFailure(call: Call<ArrayList<AudiobookResponse>>?, t: Throwable?) {
                download_data_error.visibility = View.VISIBLE
            }

            override fun onResponse(call: Call<ArrayList<AudiobookResponse>>?, response: Response<ArrayList<AudiobookResponse>>?) {
                if (response != null && response.isSuccessful) {
                    list.addAll(response.body()!!)
                    /*list.sortBy {
                        when(sortBy){
                            sortTitleValue -> it.title
                            sortAuthorValue -> it.author
                            else -> it.title
                        }
                    }*/
                    sortBy(list, getPref(sortKey))
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
        when (id) {
            R.id.action_delete_all -> {
                alert(getString(R.string.message_delete_all_books)) {
                    positiveButton(getString(R.string.action_yes)) {
                        val deletedItems = deleteFiles(File(application.getExternalFilesDir(null).toString()))
                        val message = resources.getQuantityString(R.plurals.numberOfBooksDeleted, deletedItems, deletedItems)
                        toast(message)
                    }
                    negativeButton(getString(R.string.action_no)) { }
                }.show()
            }
            R.id.action_sort_by -> {
                showSortDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showSortDialog() {
        val options = resources.getStringArray(R.array.settings_order_by_labels)
        var position = CHECKED_TITLE
        when(getPref(getString(R.string.settings_order_by_key))){
            sortAuthorValue -> position = CHECKED_AUTHOR
            sortTitleValue -> position = CHECKED_TITLE
        }
        val simpleAlert = AlertDialog.Builder(this)
        simpleAlert.setTitle(getString(R.string.settings_order_by_label))
        simpleAlert.setSingleChoiceItems(options, position, {
            dialog, which ->
            dialog.dismiss()
            when (which){
                CHECKED_TITLE -> setPref(sortTitleValue)
                CHECKED_AUTHOR -> setPref(sortAuthorValue)
            }
            sortBy(list, getPref(sortKey))
        })
        simpleAlert.create().show()
    }

    fun sortBy(list: ArrayList<AudiobookResponse>, sortBy: String){
        list.sortBy {
            when(sortBy){
                sortTitleValue -> it.title
                sortAuthorValue -> it.author
                else -> it.title
            }
        }
        recyclerView.adapter.notifyDataSetChanged()
    }

    fun getPref(key: String): String{
        val preference = getPreferences(Context.MODE_PRIVATE)
        return preference.getString(key, getString(R.string.settings_order_by_default))
    }

    fun setPref(value: String){
        val preference = getPreferences(Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString(sortKey, value)
        editor.apply()
    }

    fun deleteFiles(fileOrDirectory: File): Int {
        var count = 0
        for (files in fileOrDirectory.listFiles()) {
            if (files.isDirectory) {
                for (child in files.listFiles())
                    child.delete()
            }
            count++
            files.delete()
        }
        return count
    }
}
