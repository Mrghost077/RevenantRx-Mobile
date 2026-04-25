package com.mrghost077.revenantrx.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.mrghost077.revenantrx.BuildConfig;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
