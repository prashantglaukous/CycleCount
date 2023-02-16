package com.glaukous.extensions

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.glaukous.MainActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToast(stringId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, getString(stringId), duration).show()
}

fun String.showToast() = Toast.makeText(MainActivity.context.get(), this, Toast.LENGTH_SHORT).show()
fun String.showToast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_SHORT).show()

inline fun <reified Generic> jsonElementToData(
    responseData: JsonElement?,
    response: (Generic) -> Unit
) {
    try {
        response(GsonBuilder().create().fromJson(responseData, Generic::class.java))
    } catch (e: java.lang.IllegalStateException) {
        Log.e("ContextExtension","jsonStringToData Error: ${e.message}")
    }
}
inline fun <reified Generic> jsonStringToData(
    responseData: String?,
    response: (Generic) -> Unit
) {
    try {
        response(Gson().fromJson(responseData, Generic::class.java))
    } catch (e: java.lang.IllegalStateException) {
        Log.e("ContextExtension","jsonStringToData Error: ${e.message}")
    }
}
