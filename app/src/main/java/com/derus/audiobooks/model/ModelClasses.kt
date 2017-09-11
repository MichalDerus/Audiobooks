package com.derus.audiobooks.model

import android.widget.ImageView
import android.widget.TextView


data class AudiobookResponse(
        var author: String, // Hans Christian Andersen
        var title: String, // Brzydkie kaczątko
        var cover: String, // book/cover/brzydkie-kaczatko.jpg
        var href: String // http://wolnelektury.pl/api/books/brzydkie-kaczatko/
)

data class Audiobook(
        var title: String, // Brzydkie kaczątko
        var media: List<Media>,
        var cover: String// http://wolnelektury.pl/media/book/cover/brzydkie-kaczatko.jpg
) {
    constructor() : this("", emptyList(), "")
}

data class Media(
        var url: String, // http://wolnelektury.pl/media/book/daisy.zip/hans-christian-andersen-brzydkie-kaczatko.daisy.zip
        var director: String, //
        var type: String, // daisy
        var name: String, // Hans Christian Andersen, Brzydkie kaczątko
        var artist: String//
)

data class ChildItem(
        var book: AudiobookResponse)

data class ChildHolder(
        internal var image: ImageView,
        internal var title: TextView,
        internal var author: TextView)

data class GroupItem(
        var title: String,
        var items: ArrayList<ChildItem>) {
    constructor() : this("", ArrayList())
}

data class GroupHolder(
        internal var title: TextView)
