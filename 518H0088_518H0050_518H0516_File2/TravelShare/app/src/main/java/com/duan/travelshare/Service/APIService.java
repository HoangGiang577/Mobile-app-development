package com.duan.travelshare.Service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAfsOMsHE:APA91bG-MyNQ3njruHpyltnPMZEBWrA1KqLKWsqkBmuA8nlr3iT4HI115YMkqA-wr9APhO6Da8EUzA4d8PfWj8T4sHTwWKI66xDSZe1Ga2FW_0JKJZ_s8uG11jH8pYmuOFnB3q5RseoR" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body Sender body);
}

