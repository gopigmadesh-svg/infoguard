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

data class AnalyzeImageRequest(
    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("image")
    val image: String,

    @SerializedName("mime_type")
    val mimeType: String = "image/jpeg",

    @SerializedName("claim")
    val claim: String? = null,

    @SerializedName("ocr_text")
    val ocrText: String? = null
)

data class AnalyzeUrlRequest(
    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("url")
    val url: String,

    @SerializedName("scan_type")
    val scanType: String = "url"
)

data class PageSignalDto(
    @SerializedName("icon_emoji")
    val iconEmoji: String = "🌐",

    @SerializedName("title")
    val title: String = "",

    @SerializedName("subtitle")
    val subtitle: String = "",

    @SerializedName("status")
    val status: String = "verified"
)

data class ImageSignalDto(
    @SerializedName("label")
    val label: String = "",

    @SerializedName("status")
    val status: String = "normal",

    @SerializedName("description")
    val description: String = ""
)

data class SubClaimDto(
    @SerializedName("statement")
    val statement: String = "",

    @SerializedName("status")
    val status: String = "VERIFIED", // VERIFIED, FALSE, MISLEADING, UNVERIFIED

    @SerializedName("details")
    val details: String? = null,

    @SerializedName("evidence_sources")
    val evidenceSources: List<String> = emptyList()
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
    val details: String? = null,

    @SerializedName("published_date")
    val publishedDate: String? = null,

    @SerializedName("stance")
    val stance: String? = "supports"
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
    val trustScore: Int? = null,

    @SerializedName("is_insufficient_evidence")
    val isInsufficientEvidence: Boolean = false,

    @SerializedName("verdict")
    val verdict: String = "Needs Further Verification",

    @SerializedName("risk_level")
    val riskLevel: String? = null,

    @SerializedName("risk_explanation")
    val riskExplanation: String? = null,

    @SerializedName("summary")
    val summary: String = "",

    @SerializedName("reasons")
    val reasons: List<String> = emptyList(),

    @SerializedName("evidence_supporting_score")
    val evidenceSupportingScore: String? = null,

    @SerializedName("sub_claims")
    val subClaims: List<SubClaimDto> = emptyList(),

    @SerializedName("supporting_sources")
    val supportingSources: List<AnalysisSourceDto> = emptyList(),

    @SerializedName("contradicting_sources")
    val contradictingSources: List<AnalysisSourceDto> = emptyList(),

    @SerializedName("metrics")
    val metrics: List<ReportMetricDto> = emptyList(),

    @SerializedName("supporting_evidence")
    val supportingEvidence: String? = null,

    @SerializedName("contradicting_evidence")
    val contradictingEvidence: String? = null,

    @SerializedName("verification_limitations")
    val verificationLimitations: String? = null,

    @SerializedName("caveat")
    val caveat: String? = null,

    @SerializedName("source_credibility")
    val sourceCredibility: String? = null,

    @SerializedName("domain")
    val domain: String? = null,

    @SerializedName("scan_type")
    val scanType: String = "news",

    @SerializedName("image_url")
    val imageUrl: String? = null,

    @SerializedName("image_signals")
    val imageSignals: List<ImageSignalDto> = emptyList(),

    @SerializedName("page_signals")
    val pageSignals: List<PageSignalDto> = emptyList(),

    @SerializedName("is_ssl_verified")
    val isSslVerified: Boolean = true,

    @SerializedName("effective_url")
    val effectiveUrl: String? = null,

    @SerializedName("timestamp")
    val timestamp: String? = null,

    @SerializedName("formatted_date")
    val formattedDate: String? = null,

    @SerializedName("search_time")
    val searchTime: String? = null
)

data class RecentScanDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("scan_type")
    val scanType: String,

    @SerializedName("trust_score")
    val trustScore: Int? = null,

    @SerializedName("is_insufficient_evidence")
    val isInsufficientEvidence: Boolean = false,

    @SerializedName("verdict")
    val verdict: String,

    @SerializedName("source_credibility")
    val sourceCredibility: String? = null,

    @SerializedName("created_at")
    val createdAt: String
)
