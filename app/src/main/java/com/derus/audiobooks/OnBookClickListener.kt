package com.derus.audiobooks

/**
 * Created by Michal on 31.08.2017.
 */

interface OnBookClickListener{
    fun OnBookClick(url: String, imageUrl: String, title: String, author: String)
}