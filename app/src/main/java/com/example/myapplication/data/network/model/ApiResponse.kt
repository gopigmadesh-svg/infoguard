package com.example.myapplication.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Standard API envelope returned by TruthLens backend endpoints.
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T? = null
)

