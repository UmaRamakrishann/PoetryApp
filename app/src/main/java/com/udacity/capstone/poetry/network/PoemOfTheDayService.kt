package com.udacity.capstone.poetry.network

import com.udacity.capstone.poetry.util.PoetryConstants
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

/**
 * A retrofit service to fetch the poem of the day
 * https://api.poems.one/
 */
interface PoemOfTheDayService {
    @GET("pod")
    suspend fun getPoemOfTheDay(
    ): String
}

object PoemOfTheDay {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(PoetryConstants.POD_BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val poem = retrofit.create(PoemOfTheDayService::class.java)
}