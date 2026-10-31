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

data class AnalysisSourceDto(
    @SerializedName("name")
    val name: String = "",

    @SerializedName("domain")
    val domain: String? = null,

    @SerializedName("match_percentage")
    val matchPercentage: Int = 80,

    @SerializedName("url")
    val url: String? = null,

    @SerializedName("details")
    val details: String? = null
)

data class ReportMetricDto(
    @SerializedName("label")
    val label: String = "",

    @SerializedName("percentage")
    val percentage: Int = 50
)

data class AnalysisResultDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("title")
    val title: String = "",

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("trust_score")
    val trustScore: Int = 50,

    @SerializedName("verdict")
    val verdict: String = "Needs Further Verification",

    @SerializedName("summary")
    val summary: String = "",

    @SerializedName("reasons")
    val reasons: List<String> = emptyList(),

    @SerializedName("supporting_sources")
    val supportingSources: List<AnalysisSourceDto> = emptyList(),

    @SerializedName("metrics")
    val metrics: List<ReportMetricDto> = emptyList(),

    @SerializedName("supporting_evidence")
    val supportingEvidence: String? = null,

    @SerializedName("caveat")
    val caveat: String? = null,

    @SerializedName("source_credibility")
    val sourceCredibility: String? = null,

    @SerializedName("domain")
    val domain: String? = null,

    @SerializedName("scan_type")
    val scanType: String = "news",

    @SerializedName("timestamp")
    val timestamp: String? = null,

    @SerializedName("formatted_date")
    val formattedDate: String? = null
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
