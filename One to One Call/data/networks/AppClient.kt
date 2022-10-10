package com.ongraph.androisample.data.networks

import android.annotation.SuppressLint
import android.os.Build
import androidx.viewbinding.BuildConfig
import com.google.gson.GsonBuilder
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.ongraph.androisample.common.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {
    fun getRtcService(): RtcTokenService {
        return getRetrofitBuilder().create(RtcTokenService::class.java)
    }

    private fun getOkHttpClient() : OkHttpClient{
        return getOkHttpBuilder()
            .writeTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private fun getRetrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }

    private fun getOkHttpBuilder(): OkHttpClient.Builder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY;
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(AuthInterceptor())
            builder.addInterceptor(logging)
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(OkHttpProfilerInterceptor())
            }
            val client = builder.build()
            client.newBuilder()
        } else {
            getUnsafeOkHttpClient()
        }

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder =
        try {
            val trustAllCerts: Array<TrustManager> = arrayOf(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {

                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) = Unit

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(OkHttpProfilerInterceptor())
            }
            builder.sslSocketFactory(
                sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}
