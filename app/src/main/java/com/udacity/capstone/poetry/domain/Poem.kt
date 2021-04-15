package com.udacity.capstone.poetry.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Poem(
    val author: String,
    val title: String,
   val text: String,
    var isFavorite: Boolean
): Parcelable