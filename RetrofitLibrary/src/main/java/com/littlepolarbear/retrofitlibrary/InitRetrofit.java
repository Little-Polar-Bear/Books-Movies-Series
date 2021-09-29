package com.littlepolarbear.retrofitlibrary;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*A class designed to construct and init retrofit, so this library can interact with a
 * cloud/server service.*/
public class InitRetrofit {

    // json url basePath where the data is located.
    private final String jsonBaseUrl;

    public InitRetrofit(String jsonBaseUrl) {
        this.jsonBaseUrl = jsonBaseUrl;
    }

    /*Create the retrofit object that we can use with a interface.*/
    public JsonFileCallbacks createRetrofit() {
        // retrieving the api
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();

                    Request newRequest = originalRequest.newBuilder()
                            .addHeader("Accept", "application/json")
                            .build();
                    return chain.proceed(newRequest);
                })
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(jsonBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(JsonFileCallbacks.class);
    }
}
