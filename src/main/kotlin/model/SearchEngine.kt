package model

enum class SearchEngine {
    BING {
        override fun link(): String = "bing.com"
        override fun toString(): String = "bing"
    },
    GOOGLE {
        override fun link(): String = "google.com"
        override fun toString(): String = "google"
    },
    YANDEX {
        override fun link(): String = "yandex.ru"
        override fun toString(): String = "yandex"
    };

    abstract fun link(): String
}