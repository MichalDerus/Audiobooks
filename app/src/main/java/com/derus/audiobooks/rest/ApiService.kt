package com.derus.audiobooks.rest

import com.derus.audiobooks.model.Audiobook
import com.derus.audiobooks.model.AudiobookResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiService {
    private val apiInterface: ApiInterface

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://wolnelektury.pl/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        apiInterface = retrofit.create(ApiInterface::class.java)
    }

    fun getAudiobooksList(): Call<ArrayList<AudiobookResponse>> = apiInterface.getAllBook()

    fun getAudiobook(url: String): Call<Audiobook> = apiInterface.getAudiobook(url)

    fun downloadFile(url: String): Call<ResponseBody> = apiInterface.downloadFileWithDynamicUrl(url)
}