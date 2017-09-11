package com.derus.audiobooks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.derus.audiobooks.*
import com.derus.audiobooks.listener.OnBookClickListener
import com.derus.audiobooks.model.ChildHolder
import com.derus.audiobooks.model.ChildItem
import com.derus.audiobooks.model.GroupHolder
import com.derus.audiobooks.model.GroupItem
import com.derus.audiobooks.utilities.AnimatedExpandableListView
import com.squareup.picasso.Picasso

class AuthorsListAdapter(var context: Context, var listener: OnBookClickListener) : AnimatedExpandableListView.AnimatedExpandableListAdapter() {
    val inflater: LayoutInflater

    private var items: List<GroupItem>? = null

    init {
        inflater = LayoutInflater.from(context)
    }

    fun setData(items: List<GroupItem>) {
        this.items = items
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ChildItem =
            items!![groupPosition].items[childPosition]

    override fun getChildId(groupPosition: Int, childPosition: Int): Long =
            childPosition.toLong()

    override fun getRealChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var conView = convertView
        val holder: ChildHolder
        val item = getChild(groupPosition, childPosition)
        if (conView == null) {
            conView = inflater.inflate(R.layout.item_list, parent, false)
            val image = conView!!.findViewById(R.id.image_item_book) as ImageView
            val title = conView.findViewById(R.id.text_item_book_title) as TextView
            val author = conView.findViewById(R.id.text_item_book_author) as TextView
            holder = ChildHolder(image, title, author)
            conView.setTag(holder)
        } else {
            holder = conView.getTag() as ChildHolder
        }

        holder.title.text = item.book.title
        holder.author.text = item.book.author

        val path: String = "https://wolnelektury.pl/media/" + item.book.cover
        Picasso.with(context).load(path).into(holder.image)

        conView.setOnClickListener(View.OnClickListener {
            listener.OnBookClick(item.book.href, path, item.book.title, item.book.author)
        })

        return conView
    }

    override fun getRealChildrenCount(groupPosition: Int): Int =
            items!![groupPosition].items.size

    override fun getGroup(groupPosition: Int): GroupItem = items!![groupPosition]

    override fun getGroupCount(): Int = items!!.size

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var conView = convertView
        val holder: GroupHolder
        val item = getGroup(groupPosition)
        if (conView == null) {
            conView = inflater.inflate(R.layout.group_item, parent, false)
            val title = conView!!.findViewById(R.id.textTitle) as TextView
            holder = GroupHolder(title)
            conView.setTag(holder)
        } else {
            holder = conView.getTag() as GroupHolder
        }

        holder.title.text = item.title

        return conView
    }

    override fun hasStableIds(): Boolean = true

    override fun isChildSelectable(arg0: Int, arg1: Int): Boolean = true
}
