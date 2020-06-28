package com.interview.juno.network

class ApiHelper (private val apiService: ApiService) {

    suspend fun getNasaView(selectedDate: String) = apiService.getNasaData(selectedDate)
}