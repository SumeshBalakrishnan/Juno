package com.interview.juno

import com.interview.juno.network.ApiHelper

class MainRepository (private val apiHelper: ApiHelper) {
    suspend fun getData(selectedDate: String) = apiHelper.getNasaView(selectedDate)
}