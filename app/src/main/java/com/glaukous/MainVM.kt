package com.glaukous

import android.view.KeyEvent
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.glaukous.MainActivity.Companion.context
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.networkcalls.Repository
import com.glaukous.pref.PreferenceFile
import com.glaukous.views.home.Home
import com.glaukous.views.input.Input
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil
) : ViewModel() {

    lateinit var navController: NavController
    val showAppBar = ObservableField(true)
    val isHome = ObservableField(false)
    var keyEvent = 0

    init {

    }

    fun onClick(view: View) {

        when (view.id) {
            R.id.ivBack -> if (keyEvent != KeyEvent.KEYCODE_ENTER) {
                if (navController.backQueue.size <= 2) {
                    (context.get() as MainActivity).finishAffinity()
                } else {
                    navController.popBackStack()
                    (context.get() as MainActivity).barcodes = ""
                    keyEvent = 0
                }
            }else{
                when (navController.currentDestination?.id) {
                    R.id.home -> {
                        if (Home.barcode != null) {
                            Home.barcode?.barcode(
                                ((context.get() as MainActivity).barcodes.split("\n")
                                    .first())
                            )
                            (context.get() as MainActivity).barcodes = ""
                            keyEvent = 0
                        }

                    }

                    R.id.input -> {
                        if (Input.inputCode != null) {
                            Input.inputCode?.barcode(
                                ((context.get() as MainActivity).barcodes.split("\n")
                                    .first())
                            )
                            (context.get() as MainActivity).barcodes = ""
                            keyEvent = 0
                        }
                    }

                    else -> {
                        (context.get() as MainActivity).barcodes = ""
                    }
                }
            }
        }
    }
}
