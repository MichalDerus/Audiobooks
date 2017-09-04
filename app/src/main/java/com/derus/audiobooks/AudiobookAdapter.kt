package com.derus.audiobooks

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_list.view.*
import java.util.*

/**
 * Created by Michal on 31.08.2017.
 */
class AudiobookAdapter(val resultsList: ArrayList<AudiobookResponse>, val listener: OnBookClickListener) : RecyclerView.Adapter<AudiobookAdapter.ViewHolder>(), SectionTitleProvider {
    override fun getSectionTitle(position: Int): String {
        return resultsList.get(position).title.substring(0,1)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindAudiobooks(resultsList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AudiobookAdapter.ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(v, listener)
    }

    override fun getItemCount(): Int {
        return resultsList.size
    }

    class ViewHolder(itemView: View, val listener: OnBookClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bindAudiobooks(book: AudiobookResponse) {
                itemView.text_item_book_title.text = book.title
                itemView.text_item_book_author.text = book.author
                val path: String = "https://wolnelektury.pl/media/" + book.cover
                Picasso.with(itemView.context).load(path).into(itemView.image_item_book)
                super.itemView.setOnClickListener { listener.OnBookClick(book.href, path, book.title, book.author)}
        }
    }
}