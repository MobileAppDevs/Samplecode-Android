package com.nisha.mvvmstructure.di

import android.content.Context
import com.google.gson.GsonBuilder
import com.nisha.mvvmstructure.App
import com.nisha.mvvmstructure.data.remote.WebService
import com.nisha.mvvmstructure.utils.NetworkConstants.BASE_URL
import com.nisha.mvvmstructure.utils.NetworkConstants.CACHE_SIZE_BYTES
import com.nisha.mvvmstructure.utils.NetworkConstants.CONNECTION_TIMEOUT
import com.nisha.mvvmstructure.utils.NetworkConstants.READ_TIMEOUT
import com.nisha.mvvmstructure.utils.NetworkConstants.WRITE_TIMEOUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    /**
     * create singleton app instance & provide access everywhere
     * */

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): App {
        return app as App
    }

    /**
     * provide single instance of retrofit
     * */

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    /**
     * provides single instance of okHttp client with timeouts, interceptors & cache
     * */

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
     * provide instance of interceptor with additional headers
     * */

    @Provides
    @Singleton
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor {
            val requestBuilder = it.request().newBuilder()
            //TODO : add with your headers
            it.proceed(requestBuilder.build())
        }
    }


    @Provides
    @Singleton
    internal fun provideCache(context: Context): Cache {
        val httpCacheDirectory = File(context.cacheDir.absolutePath, "HttpCache")
        return Cache(httpCacheDirectory, CACHE_SIZE_BYTES)
    }

    /**
     * provide single instance of application context
     * */

    @Provides
    @Singleton
    fun provideContext(application: App): Context {
        return application.applicationContext
    }

    /**
     * provide single instance of api service
     * */

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): WebService {
        return retrofit.create(WebService::class.java)
    }

}