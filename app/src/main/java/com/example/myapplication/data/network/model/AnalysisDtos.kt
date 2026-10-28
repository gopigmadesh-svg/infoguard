package com.example.myapplication.data.network.model

import com.google.gson.annotations.SerializedName

data class AnalyzeClaimRequest(
    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("content")
    val content: String,

    @SerializedName("scan_type")
    val scanType: String = "news"
)

data class AnalysisResultDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("title")
    val title: String,

    @SerializedName("trust_score")
    val trustScore: Int,

    @SerializedName("verdict")
    val verdict: String,

    @SerializedName("summary")
    val summary: String,

    @SerializedName("reasons")
    val reasons: List<String> = emptyList(),

    @SerializedName("source_credibility")
    val sourceCredibility: String? = null,

    @SerializedName("scan_type")
    val scanType: String = "news",

    @SerializedName("timestamp")
    val timestamp: String? = null
)

data class RecentScanDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("scan_type")
    val scanType: String,

    @SerializedName("trust_score")
    val trustScore: Int,

    @SerializedName("verdict")
    val verdict: String,

    @SerializedName("source_credibility")
    val sourceCredibility: String? = null,

    @SerializedName("created_at")
    val createdAt: String
)

