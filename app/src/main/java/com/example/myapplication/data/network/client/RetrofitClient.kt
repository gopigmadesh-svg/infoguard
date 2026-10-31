package com.example.myapplication.data.network.client

import com.example.myapplication.data.network.api.TruthLensApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton providing configured OkHttpClient, Gson, and Retrofit API Service instance.
 */
object RetrofitClient {

    val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("X-Tunnel-Skip-Anti-Abuse", "true")
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Volatile
    private var cachedService: TruthLensApiService? = null

    val apiService: TruthLensApiService
        get() {
            return cachedService ?: synchronized(this) {
                cachedService ?: createService(NetworkConfig.currentBaseUrl).also { cachedService = it }
            }
        }

    fun updateBaseUrl(newBaseUrl: String) {
        NetworkConfig.setCustomBaseUrl(newBaseUrl)
        synchronized(this) {
            cachedService = createService(NetworkConfig.currentBaseUrl)
        }
    }

    private fun createService(baseUrl: String): TruthLensApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(TruthLensApiService::class.java)
    }
}

