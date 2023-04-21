package server

import model.SearchEngine

class StubServer(
    private val searchEngine: SearchEngine,
    private val responseNumber: Int,
    private val delay: Int = 0
) : SearchService{

    override fun engine(): SearchEngine = searchEngine

    override fun search(query: String): String {
        val urlsAndTitles = ArrayList<Pair<String, String>>()
        try {
            Thread.sleep(delay * 1000L)

            for (i in 0 until responseNumber) {
                urlsAndTitles.add(
                    Pair(
                        generateUrl(i, query),
                        generateTitle(i, query)
                    )
                )
            }

        } catch (ignored: InterruptedException) {
            return "[]"
        }

        return formJson(urlsAndTitles)
    }


    private fun formJson(urlsAndTitles: List<Pair<String, String>>): String {
        var urlsAndTitlesJson = ""
        for (i in urlsAndTitles.indices) {
            urlsAndTitlesJson += "{\"url\": \"${urlsAndTitles[i].first}\", \"title\": \"${urlsAndTitles[i].second}\"}"
            if (i != urlsAndTitles.size - 1) {
                urlsAndTitlesJson += ",";
            }
        }
        return "[$urlsAndTitlesJson]"
    }


    private fun generateUrl(index: Int, query: String): String {
        return "${searchEngine.link()}/text=${query}&hash=${generateQueryRef(query)}"
    }

    private fun generateTitle(index: Int, query: String): String {
        return query + "_$index"
    }


    private fun generateQueryRef(query: String): String {
        var queryRef = ""
        for (i in query.indices) {
            queryRef += (0..9).random().toString()
        }
        return queryRef
    }

}