package com.interview.juno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.interview.juno.emitter.Resource
import com.interview.juno.emitter.Resource.Companion.loading
import kotlinx.coroutines.Dispatchers

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getUsers(selectedDate: String) = liveData(Dispatchers.IO) {
        emit(loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getData(selectedDate)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message ="Error Occurred!"))
        }
    }
}