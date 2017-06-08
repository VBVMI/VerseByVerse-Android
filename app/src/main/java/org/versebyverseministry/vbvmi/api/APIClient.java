package org.versebyverseministry.vbvmi.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.versebyverseministry.vbvmi.database.DBFlowExclusionStrategy;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by thomascarey on 8/06/17.
 */

public class APIClient {


    private static Retrofit retrofit = null;

    static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder().setExclusionStrategies(new DBFlowExclusionStrategy()).create();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.versebyverseministry.org")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return retrofit;
    }

}

