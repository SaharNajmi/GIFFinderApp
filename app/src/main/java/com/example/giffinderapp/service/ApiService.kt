package com.example.giffinderapp.service

import com.example.giffinderapp.model.Gif
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("gifs/random?api_key=4e0IO3RUtjsR5SkGyJfQE7x9qJECNF3a")
    suspend fun getRandomGif(): Response<Gif>
}