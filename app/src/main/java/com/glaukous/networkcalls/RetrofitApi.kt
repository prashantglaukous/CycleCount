package com.glaukous.networkcalls

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApi {

    @GET(LOGIN_API)
    suspend fun login(
        @Query("EmailID") userId: String,
        @Query("Password") password: String
    ): Response<JsonElement>

    @GET(GetCycleCountByPicker)
    suspend fun getCycleCountByPicker(
        @Header("Authorization") auth: String
    ): Response<JsonElement>

    @POST(VerifyItem)
    suspend fun verifyItem(
        @Header("Authorization") auth: String,
        @Query("ItemBarCode") ItemBarCode: String,
    ): Response<JsonElement>

    @POST(SubmitCount)
    suspend fun submitCount(
        @Header("Authorization") auth: String,
        @Body body: RequestBody,
    ): Response<ResponseBody>

}