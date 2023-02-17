package com.glaukous.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.navigation.findNavController
import com.glaukous.MainActivity
import com.glaukous.R
import com.glaukous.databinding.*
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.pref.PreferenceFile
import com.glaukous.sockethelper.SocketHelper

fun View.showAlertFullScreen(
    @LayoutRes layout: Int,
    cancelable: Boolean = false,
    viewSend: (View, Dialog) -> Unit
) {
    try {
        val dialog = Dialog(this.context, R.style.DialogFullScreen)
        val view = LayoutInflater.from(this.context).inflate(layout, null)
        dialog.setContentView(view)
        dialog.setCancelable(cancelable)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        viewSend(view, dialog)
        dialog.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.showConfirmDialog(message:String, data:()->Unit){
    showAlertWrap(R.layout.confirm_alert, cancelable = true, cancelableOnTouchOutside = true){view, dialog->
        val binding  = ConfirmAlertBinding.bind(view)
        binding.tvText.text= message
        binding.btNo.setOnClickListener {
            dialog.dismiss()
        }
        binding.btYes.setOnClickListener {
            data()
            dialog.dismiss()
        }
    }
}
var dialog:Dialog?=null
fun View.showAlertWrap(
    @LayoutRes layout: Int,
    cancelable: Boolean = false,
    cancelableOnTouchOutside: Boolean = false,
    viewSend: (View, Dialog) -> Unit
) {
    try {
        dialog?.dismiss()
        dialog = Dialog(this.context)
        val view = LayoutInflater.from(this.context).inflate(layout, null)
        dialog?.let {dialog->
            dialog.setContentView(view)
            dialog.setCancelable(cancelable)
            dialog.setCanceledOnTouchOutside(cancelableOnTouchOutside)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            viewSend(view, dialog)
            dialog.show()
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


/**Session Expired Alert*/

fun Context.sessionExpired(preferenceFile: PreferenceFile, dataStoreUtil: DataStoreUtil) = try {
    (this as MainActivity).binding.root.let { view->
        view.showAlertWrap(
            R.layout.common_alert,
            cancelable=false, cancelableOnTouchOutside = false
        ) { view2, dialog ->
            val binding = CommonAlertBinding.bind(view2)
            binding.tvTerms.text = getString(R.string.session_expired)
            binding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                dataStoreUtil.clearDataStore {  }
                SocketHelper.getInstance().disconnectSocket()
                (this as Activity).findNavController(R.id.mainContainer).popBackStack(R.id.navGraph, true)
                this.findNavController(R.id.mainContainer).navigate(R.id.login)
            }
        }
    }
} catch (e: Exception) {
    e.printStackTrace()
}



