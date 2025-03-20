package com.colinmaroney.fetchexercise.service

import com.colinmaroney.fetchexercise.data.HiringEntry
import retrofit2.http.GET

interface HiringService {
    @GET("/hiring.json")
    suspend fun getHiringData(): List<HiringEntry>
}