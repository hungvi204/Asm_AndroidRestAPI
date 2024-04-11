package com.example.asm2.services;

import static com.example.asm2.services.ApiServices.BASE_URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequest {
    //Biến interface ApiServices
    private ApiServices requestInterface;

    public HttpRequest(){
        requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiServices.class);
    }

    public ApiServices callAPI(){
        //Get Retrofit
        return requestInterface;
    }
}
