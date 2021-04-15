package com.udacity.capstone.poetry.network

import com.squareup.moshi.JsonClass
import com.udacity.capstone.poetry.database.DatabasePoem
import com.udacity.capstone.poetry.domain.Poem

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

@JsonClass(generateAdapter = true)
data class NetworkPoemContainer(val poems: List<NetworkPoem>)

@JsonClass(generateAdapter = true)
data class NetworkPoem(
    val author: String,
    val title: String,
    val lines: String,
)

fun NetworkPoemContainer.asDatabaseModel(): Array<DatabasePoem> {
    return poems.map {
        DatabasePoem(
            author = it.author,
            title = it.title,
            text = it.lines,
            isFavorite = false
        )
    }.toTypedArray()
}

fun NetworkPoemContainer.asDomainModel(): List<Poem> {
    return poems.map {
        Poem(
            author = it.author,
            title = it.title,
            text = it.lines,
            isFavorite = false
        )
    }
}






