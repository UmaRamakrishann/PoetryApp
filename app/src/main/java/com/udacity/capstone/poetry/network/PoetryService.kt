package com.udacity.capstone.poetry.network

import com.udacity.capstone.poetry.util.PoetryConstants
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * A retrofit service to fetch the poems from
 * https://poetrydb.org/index.html
 */
interface PoetryService {

    @GET("author")
    suspend fun getAuthors(
    ): String

    @GET("author/{authorName}/author,title,lines,linecount")
    suspend fun getPoemsForAuthor(
        @Path("authorName") authorName: String,
    ): String

}

object Poetry {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(PoetryConstants.POETRY_BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val poems = retrofit.create(PoetryService::class.java)
}
