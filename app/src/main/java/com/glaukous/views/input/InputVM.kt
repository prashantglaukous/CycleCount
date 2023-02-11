package com.glaukous.views.input

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.glaukous.R
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.networkcalls.Repository
import com.glaukous.networkcalls.RetrofitApi
import com.glaukous.pref.PreferenceFile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InputVM @Inject constructor(
    private val dataStoreUtil: DataStoreUtil,
    private val repository: Repository,
    private val apiInterface: RetrofitApi,
    private val preferencesUtils: PreferenceFile
) : ViewModel() {

    val barcode = ObservableField("")
    val quantity = ObservableField(1)
    fun onClick(view: View) {
        when (view.id) {
            R.id.btnCancel, R.id.btnDone -> view.findNavController().navigate(InputDirections.actionInputToHome("Input"))

            R.id.ivMinus -> {
                if (quantity.get()!! > 1 && quantity.get() != null) {
                    quantity.set(quantity.get()!!.minus(1))
                }
            }

            R.id.ivPlus -> view.findNavController().navigate(
                InputDirections.actionInputToScanner(
                    barcode = barcode.get(),
                    quantity = quantity.get() ?: 0
                )
            )
        }
    }
}
