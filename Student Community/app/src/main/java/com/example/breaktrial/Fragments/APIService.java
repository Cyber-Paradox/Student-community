package com.example.breaktrial.Fragments;

import com.example.breaktrial.Notifications.MyResponse;
import com.example.breaktrial.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAj1yiLT4:APA91bFdutM-hHZpJBKR9SeJpil3WSesMtlymPh6aHwtHaqx7-bLCTRHLTQoEPJqtXK0dBBDoo7flnaqjRjqSLOkiKgMt3a_kXHRtZM00kh1qdiXy9OVQD_fCPPaiFLfAzVNDt3MJaRs"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
