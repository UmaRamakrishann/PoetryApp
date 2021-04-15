package com.udacity.capstone.poetry.database

import androidx.room.Entity
import com.udacity.capstone.poetry.domain.Poem

enum class PoemsDisplayFilter(val value: String) { BROWSE_ALL_POEMS("all"), BROWSE_FAVORITES("favorites"), POEM_OF_THE_DAY("pod") }

@Entity(primaryKeys = arrayOf("author", "title"))
data class DatabasePoem constructor(
    val author: String,
    val title: String,
    val text: String,
    var isFavorite: Boolean
)


fun List<DatabasePoem>.asDomainModel(): List<Poem> {
    return map {
        Poem(
            author = it.author,
            title = it.title,
            text = it.text,
            isFavorite = it.isFavorite
        )
    }
}

fun fromPoem(it:Poem):DatabasePoem {
    return DatabasePoem(
        author = it.author,
        title = it.title,
        text = it.text,
        isFavorite = it.isFavorite
    )

}







