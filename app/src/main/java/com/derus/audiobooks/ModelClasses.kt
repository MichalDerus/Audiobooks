package com.derus.audiobooks

/**
 * Created by Michal on 31.08.2017.
 */

data class AudiobookResponse(
		var author: String,// Hans Christian Andersen
		var title: String,// Brzydkie kaczątko
		var cover: String,// book/cover/brzydkie-kaczatko.jpg
		var href: String // http://wolnelektury.pl/api/books/brzydkie-kaczatko/
)

data class AudiobooksResultsList(val list: List<AudiobookResponse>)

data class Audio(val audiobook: Audiobook?)

data class Audiobook(
		var title: String,// Brzydkie kaczątko
		var media: List<Media>,
		var cover: String// http://wolnelektury.pl/media/book/cover/brzydkie-kaczatko.jpg
){
	constructor(): this("", emptyList(), "")
}

data class Media(
		var url: String,// http://wolnelektury.pl/media/book/daisy.zip/hans-christian-andersen-brzydkie-kaczatko.daisy.zip
		var director: String,//
		var type: String,// daisy
		var name: String,// Hans Christian Andersen, Brzydkie kaczątko
		var artist: String//
)