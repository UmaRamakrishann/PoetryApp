package com.udacity.capstone.poetry.util

import com.udacity.capstone.poetry.network.NetworkPoem
import com.udacity.capstone.poetry.network.NetworkPoemContainer
import com.udacity.capstone.poetry.network.PoemOfTheDay
import com.udacity.capstone.poetry.network.Poetry
import org.json.JSONArray
import org.json.JSONObject

suspend fun fetchAllPoems(): NetworkPoemContainer {
    val response = Poetry.poems.getAuthors()
    val jsonResponse = JSONObject(response)
    val authorsJsonArray = jsonResponse.getJSONArray("authors")
    val poetryList = ArrayList<NetworkPoem>()
    for (i in 0 until authorsJsonArray.length()) {
        val authorParam = authorsJsonArray.getString(i)
        val poemResponse = Poetry.poems.getPoemsForAuthor(authorParam)
        val poemJsonArray = JSONArray(poemResponse)

        if (poemJsonArray != null) {
            val numberPoems = minOf(5, poemJsonArray.length())
            for (j in 0 until numberPoems) {
                val poemJsonObject = JSONObject(poemJsonArray.getString(j))
                val author = poemJsonObject.getString("author");
                val title = poemJsonObject.getString("title");
                val linesArray = poemJsonObject.getJSONArray("lines")
                val lines = StringBuilder()
                for (k in 0 until linesArray.length()) {
                    lines.append(linesArray.getString(k))
                    lines.append("\n")
                }
                val poem = NetworkPoem(
                    author = author, title = title, lines = lines.toString()
                )
                poetryList.add(poem)
            }
        }
    }
    return NetworkPoemContainer(poetryList)
}

suspend fun fetchPoemOfTheDay(): NetworkPoemContainer {
    val poetryList = ArrayList<NetworkPoem>()
    val response = PoemOfTheDay.poem.getPoemOfTheDay()
    val jsonReponse = JSONObject(response)
    val contents = jsonReponse.getJSONObject("contents")
    val poems = contents.getJSONArray("poems")
    for (j in 0 until poems.length()) {
        val poemsElement = JSONObject(poems.getString(j))
        val pod = poemsElement.getJSONObject("poem")
        val author = pod.getString("author")
        val title = pod.getString("title")
        val lines = pod.getString("poem")
        val networkPod = NetworkPoem(
            author = author, title = title, lines = lines
        )
        poetryList.add(networkPod)
    }
    return NetworkPoemContainer(poetryList)
}


