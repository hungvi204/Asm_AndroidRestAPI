package com.example.asm2.services;


import com.example.asm2.model.Distributor;
import com.example.asm2.model.Fruit;
import com.example.asm2.model.Order;
import com.example.asm2.model.Page;
import com.example.asm2.model.Response;
import com.example.asm2.model.Users;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiServices {
    //Sử dụng máy ảo android studio thì localhost thay thành ip 10.0.0.2
    //Đối với máy thật ta sử dụng ip của máy
    //Base_URL là url của api
    public static String BASE_URL = "http://10.0.2.2:3000/distributors/";
    public static String BASE_URL_USER = "http://10.0.2.2:3000/api/";

    public static String BASE_URL_FRUIT = "http://10.0.2.2:3000/fruit/";
    public static String BASE_URL_ORDER = "http://10.0.2.2:3000/order/";


    //Annotations @GET cho method GET và url
    //Base_URL + @GET("get-list-distributor") = http://10.0.2.2:3000/distributors/get-list-distributor
    @GET("get-list-distributor")
    Call<Response<ArrayList<Distributor>>> getListDistributor();
    //Call<Giá trị trả về của api>
    @GET("search-distributor")
    Call<Response<ArrayList<Distributor>>> searchDistributor(@Query("key") String key);

    @POST("add-distributor")
    Call<Response<Distributor>> addDistributor(@Body Distributor distributor);

    //param url sẽ bỏ vào {}
    @DELETE("delete-distributor-by-id/{id}")
    Call<Response<Distributor>> deleteDistributorById(@Path("id") String id);

    @PUT("update-distributor-by-id/{id}")
    Call<Response<Distributor>> updateDistributorById(@Path("id") String id, @Body Distributor distributor);

    @Multipart
    @POST("register-send-email")
    Call<Response<Users>> register(@Part("username")RequestBody username,
                                   @Part("password")RequestBody password,
                                   @Part("email")RequestBody email,
                                   @Part("name")RequestBody name,
                                   @Part MultipartBody.Part avatar);

    @POST("login")
    Call<Response<Users>> login(@Body Users users);

    @GET("get-list-fruits")
    Call<Response<ArrayList<Fruit>>> getListFruit();
    //@Header("Authorization") là token mà ta cần truyền lên để lấy dữ liệu

    @GET("get-page-fruit")
    Call<Response<Page<ArrayList<Fruit>>>> getPageFruit(@QueryMap Map<String, String> stringMap);
//    Call<Response<Page<ArrayList<Fruit>>>> getPageFruit(@QueryMap Map<String, String> stringMap);

    @POST("add-order")
    Call<Response<Response<Order>>> order(@Body Order order);
}
