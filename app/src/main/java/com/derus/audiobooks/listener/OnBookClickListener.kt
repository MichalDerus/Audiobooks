package com.derus.audiobooks.listener

interface OnBookClickListener {
    fun OnBookClick(url: String, imageUrl: String, title: String, author: String)
}