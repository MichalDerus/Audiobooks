package com.derus.audiobooks.rest

import com.derus.audiobooks.model.Audiobook
import com.derus.audiobooks.model.AudiobookResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.*

interface ApiInterface {
    @GET("audiobooks")
    fun getAllBook(): Call<ArrayList<AudiobookResponse>>

    @GET()
    fun getAudiobook(@Url url: String): Call<Audiobook>

    @GET
    fun downloadFileWithDynamicUrl(@Url fileUrl: String): Call<ResponseBody>
}