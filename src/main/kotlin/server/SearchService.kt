package server

import model.SearchEngine

interface SearchService {
    fun search(query: String): String

    fun engine(): SearchEngine
}