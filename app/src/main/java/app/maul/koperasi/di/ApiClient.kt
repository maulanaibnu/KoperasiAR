package app.maul.koperasi.di

import app.maul.koperasi.api.RajaOngkirApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val RAJAONGKIR_BASE_URL = "https://api-sandbox.collaborator.komerce.id/"
    private const val API_KEY = "qAUHt48xc92ad9c69ed75fd0GZoBN07T"

    private fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("x-api-key", API_KEY)
                    .method(original.method, original.body)

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    val rajaOngkirService: RajaOngkirApiService by lazy {
        Retrofit.Builder()
            .baseUrl(RAJAONGKIR_BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RajaOngkirApiService::class.java)
    }
}
