package app.maul.koperasi.factory


import app.maul.koperasi.api.ApiService
import app.maul.koperasi.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


//    private const val BASE_URL = "https://koperasi.simagang.my.id/"
    const val BASE_URL = Constant.BASE_URL

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .connectTimeout(30,TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun setupRetrofitGithub(okHttp : OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    fun apiEndPoint(retrofit : Retrofit): ApiService = retrofit.create(ApiService::class.java)



}