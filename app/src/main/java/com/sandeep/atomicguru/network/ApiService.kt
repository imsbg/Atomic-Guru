package com.sandeep.atomicguru.network

import com.sandeep.atomicguru.data.PromotionResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// The base URL must point to the directory on GitHub, not the file itself.
private const val BASE_URL = "https://raw.githubusercontent.com/imsbg/Atomic-Guru/master/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @GET("pr_img.json")
    suspend fun getPromotions(): PromotionResponse
}

// Singleton object to create the service
object PromotionApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}