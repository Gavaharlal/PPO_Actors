package model


class SearchEngineResponse(
    val searchEngine: SearchEngine,
    val results: List<SearchResult>
) {

    override fun toString(): String {

        return "\n$searchEngine: {\n${results.joinToString(separator = "") { "\t$it\n" }}}"
    }
}