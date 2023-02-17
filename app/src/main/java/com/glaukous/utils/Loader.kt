package com.glaukous.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.glaukous.databinding.ProgressLayoutBinding

object Loader {

    private var customDialog: AlertDialog? = null

    fun Context.showProgress(msg:String="") {
        hideProgress()
        val customAlertBuilder = AlertDialog.Builder(this)
        val customAlertView =
            ProgressLayoutBinding.inflate(LayoutInflater.from(this), null, false)
        customAlertBuilder.setView(customAlertView.root)
        customAlertBuilder.setCancelable(false)
        customDialog = customAlertBuilder.create()
        if(msg.isNotEmpty()){
            customAlertView.tvStatusLoading.text=msg
        }

        customDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog!!.show()
    }

    /*fun Context.showProgressWithAction(
        retryTimer: Boolean,
        time: Long,
        action: (Boolean, String?) -> Unit
    ) {
        val customAlertBuilder = AlertDialog.Builder(this)
        val customAlertView =
            ProgressLayoutBinding.inflate(LayoutInflater.from(this), null, false)
        customAlertBuilder.setView(customAlertView.root)
        customAlertBuilder.setCancelable(false)
        customDialog = customAlertBuilder.create()

        if (retryTimer) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (time > 2) {
                    customAlertView.tvTimer.visibility= View.VISIBLE
                    val timer = object :CountDownTimer(time * 1000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            customAlertView.tvTimer.text=(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)% 60).toString()
                        }
                        override fun onFinish() {
                            action(true, null)
                        }
                    }
                    timer.start()
                } else {
                    action(false, "Please try after some time!")
                }
            },10000)
        } else {
            hideProgress()
        }

        customDialog!!.window!!.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        customDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog!!.show()
    }*/

    fun hideProgress() {
        if (customDialog != null && customDialog?.isShowing!!) {
            customDialog?.dismiss()
        }
    }
}