package com.example.myapplication.data.network.model

import com.google.gson.annotations.SerializedName

data class SavedArticleDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("summary")
    val summary: String? = null,

    @SerializedName("source")
    val source: String? = null,

    @SerializedName("url")
    val url: String? = null,

    @SerializedName("trust_score")
    val trustScore: Int = 70,

    @SerializedName("saved_at")
    val savedAt: String
)

data class SaveArticleRequest(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("summary")
    val summary: String? = null,

    @SerializedName("source")
    val source: String? = null,

    @SerializedName("url")
    val url: String? = null,

    @SerializedName("trust_score")
    val trustScore: Int = 70
)

data class RemoveArticleRequest(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("article_id")
    val articleId: Int
)

