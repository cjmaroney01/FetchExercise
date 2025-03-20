package com.colinmaroney.fetchexercise.repository

import com.colinmaroney.fetchexercise.data.HiringEntry
import com.colinmaroney.fetchexercise.service.HiringService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HiringRepo {
    companion object {
        const val baseUrl = "https://fetch-hiring.s3.amazonaws.com/"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(HiringService::class.java)

    suspend fun getHiringData(): List<HiringEntry> {
        return service.getHiringData()
    }
}