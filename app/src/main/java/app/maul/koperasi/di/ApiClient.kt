package app.maul.koperasi.di

import app.maul.koperasi.api.RajaOngkirApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Base URL untuk API Raja Ongkir Anda
    private const val RAJAONGKIR_BASE_URL = "https://api-sandbox.collaborator.komerce.id/"

    // Base URL untuk API User Anda (contoh)
    // private const val USER_API_BASE_URL = "https://api.yourotherdomain.com/"

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("x-api-key", "qAUHt48xc92ad9c69ed75fd0GZoBN07T")
                    .method(original.method, original.body)

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }
    // Instance Retrofit untuk Raja Ongkir
    val rajaOngkirService: RajaOngkirApiService by lazy {
        Retrofit.Builder()
            .baseUrl(RAJAONGKIR_BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RajaOngkirApiService::class.java)
    }

    // Anda bisa menambahkan instance untuk API User di sini
    /*
    val userService: YourUserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(USER_API_BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YourUserApiService::class.java)
    }
    */
}