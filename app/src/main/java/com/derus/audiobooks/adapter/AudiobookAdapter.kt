package com.derus.audiobooks.adapter

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.derus.audiobooks.R
import com.derus.audiobooks.listener.OnBookClickListener
import com.derus.audiobooks.model.AudiobookResponse
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_list.view.*


class AudiobookAdapter(val context: Context, val pref: SharedPreferences, val resultsList: ArrayList<AudiobookResponse>, val listener: OnBookClickListener) : RecyclerView.Adapter<AudiobookAdapter.ViewHolder>(),
        SectionTitleProvider, Filterable {

    var filteredList: ArrayList<AudiobookResponse> = resultsList
    var audiobookFilter: AudiobookFilter? = null

    override fun getFilter(): Filter {
        if (audiobookFilter == null) {
            audiobookFilter = AudiobookFilter()
        }
        return audiobookFilter!!
    }

    override fun getSectionTitle(position: Int): String {
        val orderBy = pref.getString(context.getString(R.string.settings_order_by_key), context.getString(R.string.settings_order_by_default))
        when (orderBy) {
            context.getString(R.string.settings_order_by_title_value) -> {
                return filteredList.get(position).title.substring(0, 1)
            }
            context.getString(R.string.settings_order_by_author_value) -> {
                return filteredList.get(position).author.substring(0, 1)
            }
            else -> return filteredList.get(position).title.substring(0, 1)
        }
        //return filteredList.get(position).title.substring(0, 1)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindAudiobooks(filteredList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(v, listener)
    }

    override fun getItemCount(): Int =
            filteredList.size

    class ViewHolder(itemView: View, val listener: OnBookClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bindAudiobooks(book: AudiobookResponse) {
            itemView.text_item_book_title.text = book.title
            itemView.text_item_book_author.text = book.author
            val path: String = "https://wolnelektury.pl/media/" + book.cover
            Picasso.with(itemView.context).load(path).into(itemView.image_item_book)
            super.itemView.setOnClickListener { listener.OnBookClick(book.href, path, book.title, book.author) }
        }
    }

    inner class AudiobookFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResult = FilterResults()

            if (constraint != null && constraint.length > 0) {
                val tempList = ArrayList<AudiobookResponse>()
                for (book: AudiobookResponse in resultsList) {
                    if (book.title.toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            book.author.toLowerCase().contains(constraint.toString().toLowerCase()))
                        tempList.add(book)
                }
                filterResult.count = tempList.size
                filterResult.values = tempList

            } else {
                filterResult.count = resultsList.size
                filterResult.values = resultsList
            }
            return filterResult
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredList = results?.values as ArrayList<AudiobookResponse>
            notifyDataSetChanged()
        }

    }
}