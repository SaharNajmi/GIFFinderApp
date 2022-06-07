package com.example.giffinderapp.service

import com.example.giffinderapp.model.GiphyRandom
import com.example.giffinderapp.model.GiphySearch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("gifs/random?api_key=4e0IO3RUtjsR5SkGyJfQE7x9qJECNF3a")
    suspend fun getRandomGif(): Response<GiphyRandom>

    @GET("gifs/search")
    suspend fun searchGifs(
        @Query("q") query: String?,
        @Query("api_key") api_key: String? = "4e0IO3RUtjsR5SkGyJfQE7x9qJECNF3a"
    ): Response<GiphySearch>
}