package com.derus.audiobooks.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnGroupClickListener
import com.derus.audiobooks.DetailActivity
import com.derus.audiobooks.R
import com.derus.audiobooks.adapter.AuthorsListAdapter
import com.derus.audiobooks.listener.OnBookClickListener
import com.derus.audiobooks.model.AudiobookResponse
import com.derus.audiobooks.model.ChildItem
import com.derus.audiobooks.model.GroupItem
import com.derus.audiobooks.rest.ApiService
import com.derus.audiobooks.utilities.AnimatedExpandableListView
import kotlinx.android.synthetic.main.fragment_list_authors.*
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthorsListFragment : Fragment(), OnBookClickListener {
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

    var listView: AnimatedExpandableListView? = null
    var adapter: AuthorsListAdapter? = null
    var listOfItems = ArrayList<GroupItem>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater!!.inflate(R.layout.fragment_list_authors, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val api = ApiService()
        listView = list as AnimatedExpandableListView
        adapter = AuthorsListAdapter(activity, this)

        api.getAudiobooksList().enqueue(object : Callback<ArrayList<AudiobookResponse>> {
            override fun onFailure(call: Call<ArrayList<AudiobookResponse>>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ArrayList<AudiobookResponse>>?, response: Response<ArrayList<AudiobookResponse>>?) {
                if (response != null && response.isSuccessful) {
                    listOfItems = createGroupItemList(response.body()!!)
                    adapter!!.setData(listOfItems)
                    listView?.setAdapter(adapter)
                }
            }
        })

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView!!.setOnGroupClickListener(object : OnGroupClickListener {

            override fun onGroupClick(parent: ExpandableListView, v: View, groupPosition: Int, id: Long): Boolean {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView!!.isGroupExpanded(groupPosition)) {
                    listView!!.collapseGroupWithAnimation(groupPosition)
                } else {
                    listView!!.expandGroupWithAnimation(groupPosition)
                }
                return true
            }

        })
    }

    fun createGroupItemList(list: ArrayList<AudiobookResponse>): ArrayList<GroupItem> {
        val author = HashMap<String, ArrayList<AudiobookResponse>>()
        var bookOfAuthor: ArrayList<AudiobookResponse>

        for (item: AudiobookResponse in list) {
            val authorName = item.author

            bookOfAuthor = if (author.contains(authorName)) {
                author.get(authorName)!!
            } else {
                ArrayList()
            }

            bookOfAuthor.add(item)
            author.put(authorName, bookOfAuthor)
        }
        val books = ArrayList<GroupItem>()
        for (entry in author) {
            val item = GroupItem()
            item.title = entry.key
            for (element in entry.value) {
                val child = ChildItem(element)
                item.items.add(child)
            }
            books.add(item)
        }
        books.sortBy { it.title }
        return books
    }
}
