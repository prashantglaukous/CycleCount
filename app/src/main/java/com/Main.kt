package com

import com.glaukous.views.login.LoginResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder

fun main() {
    val str= "{\"key\": 200,\"message\": \"User available\",\"isSuccess\": true, \"data\": \"{\"Token\":\"eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNobWFjLXNoYTI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiTmF6aW0iLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9naXZlbm5hbWUiOiI0IiwiaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNS8wNS9pZGVudGl0eS9jbGFpbXMvZW1haWxhZGRyZXNzIjoibmF6aW1AZ2xhdWtvdXMuY29tIiwiZXhwIjoxNjc2NTc2MzE1LCJpc3MiOiJodHRwczovL215d2ViYXBpLmNvbSIsImF1ZCI6Imh0dHBzOi8vbXl3ZWJhcGkuY29tIn0.UCz4BYsjOb8SnMiZzWXZwl7klHg6lTHQ1-JUEeWDOmc\",\"RefreshToken\":\"Glaukoustech.PTL.API.Infrastructure.RefreshToken\",\"UserID\":3,\"Name\":\"Nazim\",\"Password\":\"nazim\",\"Email\":\"nazim@glaukous.com\",\"ProjectID\":\"naidu\",\"PickerID\":4}\"}"
   // println(str.replace("\"{","{").replace("}\"","}"))

    println(GsonBuilder().create().fromJson(str.replace("\"{","{").replace("}\"","}"),LoginResponse::class.java))
}