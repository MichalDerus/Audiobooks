package com.derus.audiobooks

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Michal on 31.08.2017.
 */
class ApiService {
    private val apiInterface: ApiInterface

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://wolnelektury.pl/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        apiInterface = retrofit.create(ApiInterface::class.java)
    }

    fun getAudiobooksList(): Call<ArrayList<AudiobookResponse>> {
        return apiInterface.getAllBook()
    }

    fun getAudiobook(url: String): Call<Audiobook> {
        return apiInterface.getAudiobook(url)

    }

    fun downloadFile(url: String) : Call<ResponseBody>{
        return apiInterface.downloadFileWithDynamicUrl(url)
    }
}