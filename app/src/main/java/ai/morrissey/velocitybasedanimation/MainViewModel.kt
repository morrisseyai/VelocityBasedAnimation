package ai.morrissey.velocitybasedanimation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val _newsArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsArticles: StateFlow<List<NewsArticle>> get() = _newsArticles

    init {
        loadArticles()
    }

    fun loadArticles() {
        _newsArticles.value = mockArticles
    }
}

data class NewsArticle(
    val headline: String,
    val subhead: String,
    val articleUrl: String,
    val previewImageUrl: String
)


val mockArticle = NewsArticle(
    headline = "Headline about an enormously exciting piece of breaking news",
    subhead = "Here's more detail about the breaking news",
    articleUrl = "",
    previewImageUrl = ""
)

val mockArticles = (0..500).map { mockArticle }
