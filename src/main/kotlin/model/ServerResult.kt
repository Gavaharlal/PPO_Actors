package model

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("url") val url: String,
    @SerializedName("title") val title: String
) {
    override fun toString(): String {
        return "$url: $title"
    }
}