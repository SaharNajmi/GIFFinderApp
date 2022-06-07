package com.example.giffinderapp.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.giffinderapp.model.GiphySearch
import com.example.giffinderapp.service.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    val randGifUrl = MutableLiveData<String>()
    val gifs = MutableLiveData<List<GiphySearch.Data>>()

    init {
        getRandomGif()
    }

    fun getRandomGif() {
        viewModelScope.launch {
            val response = apiService.getRandomGif()
            if (response.isSuccessful)
                randGifUrl.value = response.body()!!.data.images.preview_gif.url
            else
                randGifUrl.value = "null"
        }
    }

    fun searchGif(textSearch: String) {
        viewModelScope.launch {
            val response = apiService.searchGifs(textSearch)
            if (response.isSuccessful)
                gifs.value = response.body()!!.data
        }
    }
}