package com.example.asm2.services;

import static com.example.asm2.services.ApiServices.BASE_URL_ORDER;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequest_Order {
    private ApiServices requestInterface;

    public HttpRequest_Order(){
        requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL_ORDER)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiServices.class);
    }

    public ApiServices callAPI(){
        //Get Retrofit
        return requestInterface;
    }
}
