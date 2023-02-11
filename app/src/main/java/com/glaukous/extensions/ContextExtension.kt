package com.glaukous.extensions

import android.content.Context
import android.widget.Toast
import com.glaukous.MainActivity

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToast(stringId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, getString(stringId), duration).show()
}

fun String.showToast() = Toast.makeText(MainActivity.context.get(), this, Toast.LENGTH_SHORT).show()
fun String.showToast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_SHORT).show()