package dev.pankaj.cleanarchitecture.di


import android.content.Context
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pankaj.cleanarchitecture.App
import dev.pankaj.cleanarchitecture.BuildConfig
import dev.pankaj.cleanarchitecture.data.remote.api.ApiService
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module responsible for providing network-related dependencies.
 */
@Module
@Suppress("unused")
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val READ_TIMEOUT = 30
    private val WRITE_TIMEOUT = 30
    private val CONNECTION_TIMEOUT = 10
    private val CACHE_SIZE_BYTES = 10 * 1024 *1024L

    /**
     * Provides the application instance.
     *
     * @param app The application context.
     * @return The application instance.
     */
    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): App {
        return app as App
    }

    /**
     * Provides a Retrofit instance for network requests.
     *
     * @param client The OkHttpClient instance.
     * @return The Retrofit instance.
     */
    @Provides
    @Singleton
    fun provideRetrofit(client:OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    /**
     * Provides an OkHttpClient instance configured for network requests.
     *
     * @param headerInterceptor The interceptor for adding headers to requests.
     * @param cache The cache for storing network responses.
     * @return The OkHttpClient instance.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        headerInterceptor: Interceptor,
        cache: Cache
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient().newBuilder()
        okHttpClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.cache(cache)
        okHttpClientBuilder.addInterceptor(headerInterceptor)
        return okHttpClientBuilder.build()
    }

    /**
     * Provides an interceptor for adding headers to network requests.
     *
     * @return The header interceptor.
     */
    @Provides
    @Singleton
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor {
            val requestBuilder = it.request().newBuilder()
            // Add headers here if needed
            it.proceed(requestBuilder.build())
        }
    }

    /**
     * Provides a cache for storing network responses.
     *
     * @param context The application context.
     * @return The cache instance.
     */
    @Provides
    @Singleton
    internal fun provideCache(context: Context): Cache {
        val httpCacheDirectory = File(context.cacheDir.absolutePath, "HttpCache")
        return Cache(httpCacheDirectory, CACHE_SIZE_BYTES)
    }

    /**
     * Provides the application context.
     *
     * @param application The application instance.
     * @return The application context.
     */
    @Provides
    @Singleton
    fun provideContext(application: App): Context {
        return application.applicationContext
    }

    /**
     * Provides an instance of ApiService for making API requests.
     *
     * @param retrofit The Retrofit instance.
     * @return The ApiService instance.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
