package com.bawa.inr.livechat;

import com.bawa.inr.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bawa.inr.Constants.API_BASE_URL;

public class UploadMediaRetrofit {

    public static Retrofit retrofit;

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging);

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Constants.SOCKET_CHAT_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        httpClient.connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS).writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.MILLISECONDS).readTimeout(Constants.READ_TIMEOUT, TimeUnit.MILLISECONDS);
        retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
