package com.mrghost077.revenantrx.network;


import com.mrghost077.revenantrx.models.LoginRequest;
import com.mrghost077.revenantrx.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServices {

    @POST("/api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
