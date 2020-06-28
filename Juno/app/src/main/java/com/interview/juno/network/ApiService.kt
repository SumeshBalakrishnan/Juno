package com.interview.juno.network

import com.interview.juno.model.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/planetary/apod?api_key=0JX9fJXVUeETq03k4Xfjj9kFWv0p5KWiLAnPdTpu")
    suspend fun getNasaData(@Query("date") selectedDate : String): Response
}