package app.maul.koperasi.network

import android.content.Context
import app.maul.koperasi.preference.Preferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val context : Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = Preferences.getToken(context)

        val requestBuilder = chain.request().newBuilder()
        if (token.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}