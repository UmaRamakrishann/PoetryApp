package com.udacity.capstone.poetry.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.capstone.poetry.database.PoetryDatabase
import com.udacity.capstone.poetry.database.asDomainModel
import com.udacity.capstone.poetry.database.fromPoem
import com.udacity.capstone.poetry.domain.Poem
import com.udacity.capstone.poetry.network.asDatabaseModel
import com.udacity.capstone.poetry.network.asDomainModel
import com.udacity.capstone.poetry.util.fetchAllPoems
import com.udacity.capstone.poetry.util.fetchPoemOfTheDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PoetryRepository(private val database: PoetryDatabase) {

    companion object {
        const val TAG = "PoetryRepository"
    }

    fun getPoems(): LiveData<List<Poem>> {
        return Transformations.map(
            database.poemDao.getPoems()
        ) {
            it.asDomainModel()
        }
    }

    suspend fun upsertPoem(selectedPoem: Poem) {
        database.poemDao.upsert(fromPoem(selectedPoem))
    }

    fun getFavoritePoems(): LiveData<List<Poem>> {
        return Transformations.map(
            database.poemDao.getFavoritePoems()
        ) {
            it.asDomainModel()
        }
    }

    suspend fun refreshPoems() {
        withContext(Dispatchers.IO) {
            try {
                val poetryList = fetchAllPoems()
                database.poemDao.insertAll(*poetryList.asDatabaseModel())
            } catch (e: Exception) {
                Log.i(TAG, "No Internet")
            }
        }
    }

    suspend fun getPoemOfTheDay(): List<Poem> {
        var podList: List<Poem> = mutableListOf()
        withContext(Dispatchers.IO) {
            try {
                val pod = fetchPoemOfTheDay()
                podList = pod.asDomainModel()
            } catch (e: Exception) {
                Log.i(TAG, "No Internet")
            }
        }
        return podList
    }
}
