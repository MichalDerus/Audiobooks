package com.derus.audiobooks.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.SearchView
import com.derus.audiobooks.DetailActivity
import com.derus.audiobooks.MainActivity
import com.derus.audiobooks.R
import com.derus.audiobooks.adapter.AudiobookAdapter
import com.derus.audiobooks.listener.OnBookClickListener
import com.derus.audiobooks.model.AudiobookResponse
import com.derus.audiobooks.rest.ApiService
import kotlinx.android.synthetic.main.fragment_list_audiobooks.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class AudiobooksListFragment() : Fragment(), OnBookClickListener, SearchView.OnQueryTextListener {
    lateinit var audiobookAdapter: AudiobookAdapter
    val CHECKED_TITLE = 0
    val CHECKED_AUTHOR = 1
    lateinit var sortTitleValue: String
    lateinit var sortAuthorValue: String
    lateinit var sortKey: String
    val list = ArrayList<AudiobookResponse>()

    override fun onQueryTextSubmit(query: String?): Boolean {
        audiobookAdapter.filter.filter(query)
        (activity as MainActivity).searchView.setQuery(query, false)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        audiobookAdapter.filter.filter(newText)
        (activity as MainActivity).searchView.setQuery(newText, false)
        return false
    }

    override fun OnBookClick(url: String, imageUrl: String, title: String, author: String) {
        if (url.isEmpty()) {
            toast("Brak URL")
        } else {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("EXTRA_URL", url)
            intent.putExtra("EXTRA_IMAGE_URL", imageUrl)
            intent.putExtra("EXTRA_TITLE", title)
            intent.putExtra("EXTRA_AUTHOR", author)
            startActivity(intent)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater!!.inflate(R.layout.fragment_list_audiobooks, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sortTitleValue = getString(R.string.settings_order_by_title_value)
        sortAuthorValue = getString(R.string.settings_order_by_author_value)
        sortKey = getString(R.string.settings_order_by_key)

        progress_audiobook.visibility = View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val api = ApiService()
        //val list = ArrayList<AudiobookResponse>()
        audiobookAdapter = AudiobookAdapter(activity, activity.getPreferences(Context.MODE_PRIVATE), list, this)
        recyclerView.adapter = audiobookAdapter
        recyclerView.setHasFixedSize(true)
        fastscroll.setRecyclerView(recyclerView)

        api.getAudiobooksList().enqueue(object : Callback<ArrayList<AudiobookResponse>> {
            override fun onFailure(call: Call<ArrayList<AudiobookResponse>>?, t: Throwable?) {
                download_data_error_audiobooks.visibility = View.VISIBLE
                progress_audiobook.visibility = View.INVISIBLE
            }

            override fun onResponse(call: Call<ArrayList<AudiobookResponse>>?, response: Response<ArrayList<AudiobookResponse>>?) {
                if (response != null && response.isSuccessful) {
                    for (item in response.body()!!){
                        item.title = item.title.replace("[()\\[\\]*]".toRegex(), "").replace("^[\\s]".toRegex(), "")
                        list.add(item)
                    }
                    sortBy(list, getPref(sortKey))
                    recyclerView.adapter.notifyDataSetChanged()
                    download_data_error_audiobooks.visibility = View.INVISIBLE
                    progress_audiobook.visibility = View.INVISIBLE
                }else{
                    download_data_error_audiobooks.visibility = View.VISIBLE
                    progress_audiobook.visibility = View.INVISIBLE
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        //inflater?.inflate(R.menu.main, menu)
        val item: MenuItem? = menu?.findItem(R.id.search)
        val searchView = SearchView((activity as MainActivity).supportActionBar?.themedContext)
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItemCompat.SHOW_AS_ACTION_IF_ROOM)
        MenuItemCompat.setActionView(item, searchView)
        searchView.setOnQueryTextListener(this)
        searchView.setIconifiedByDefault(false)
        searchView.setQuery((activity as MainActivity).searchView.query, false)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        when (id) {
            R.id.action_delete_all -> {
                alert(getString(R.string.message_delete_all_books)) {
                    positiveButton(getString(R.string.action_yes)) {
                        val deletedItems = deleteFiles(File(activity.application.getExternalFilesDir(null).toString()))
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
        when (getPref(getString(R.string.settings_order_by_key))) {
            sortAuthorValue -> position = CHECKED_AUTHOR
            sortTitleValue -> position = CHECKED_TITLE
        }
        val simpleAlert = AlertDialog.Builder(activity)
        simpleAlert.setTitle(getString(R.string.settings_order_by_label))
        simpleAlert.setSingleChoiceItems(options, position, { dialog, which ->
            dialog.dismiss()
            when (which) {
                CHECKED_TITLE -> setPref(sortTitleValue)
                CHECKED_AUTHOR -> setPref(sortAuthorValue)
            }
            sortBy(list, getPref(sortKey))
        })
        simpleAlert.create().show()
    }

    fun sortBy(list: ArrayList<AudiobookResponse>, sortBy: String) {
        list.sortBy {
            when (sortBy) {
                sortTitleValue -> it.title
                sortAuthorValue -> it.author
                else -> it.title
            }
        }
        recyclerView.adapter.notifyDataSetChanged()
    }

    fun getPref(key: String): String {
        val preference = activity.getPreferences(Context.MODE_PRIVATE)
        return preference.getString(key, getString(R.string.settings_order_by_default))
    }

    fun setPref(value: String) {
        val preference = activity.getPreferences(Context.MODE_PRIVATE)
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