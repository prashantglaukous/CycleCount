package com.glaukous.networkcalls

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApi {

//    @FormUrlEncoded
    @GET(LOGIN_API)
    suspend fun login(
        @Query("EmailID") userId: String,
        @Query("Password") password: String
    ): Response<JsonElement>

    @GET(GetCycleCountByPicker)
    suspend fun getCycleCountByPicker(
        @Header("Authorization") auth: String
    ): Response<JsonElement>

}