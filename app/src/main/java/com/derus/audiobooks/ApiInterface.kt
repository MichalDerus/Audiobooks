package com.derus.audiobooks

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.*

/**
 * Created by Michal on 31.08.2017.
 */
interface ApiInterface{
    @GET("audiobooks")
    fun getAllBook(): Call<ArrayList<AudiobookResponse>>

    @GET()
    fun getAudiobook(@Url url: String): Call<Audiobook>

    @GET
    fun downloadFileWithDynamicUrl(@Url fileUrl: String): Call<ResponseBody>
}