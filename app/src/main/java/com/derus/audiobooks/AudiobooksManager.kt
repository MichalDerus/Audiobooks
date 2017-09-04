package com.derus.audiobooks

import retrofit2.Call

/**
 * Created by Michal on 31.08.2017.
 */
class AudiobooksManager(private val api: ApiService = ApiService()) {
    /*    fun getAudiobooksList(): ArrayList<AudiobookResponse>? {
            val call = api.getAudiobooksList()
            val list: ArrayList<AudiobookResponse> = ArrayList()
            call.enqueue(object: Callback<ArrayList<AudiobookResponse>>{
                override fun onResponse(call: Call<ArrayList<AudiobookResponse>>?, response: Response<ArrayList<AudiobookResponse>>?) {
                    if (response != null && response.isSuccessful) {
                        list.addAll(response.body()!!)
                    }
                }
                override fun onFailure(call: Call<ArrayList<AudiobookResponse>>?, t: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
            return list
        }*/

    fun getAudiobooksCall(): Call<ArrayList<AudiobookResponse>> {
        val call = api.getAudiobooksList()
        return call
    }
}


