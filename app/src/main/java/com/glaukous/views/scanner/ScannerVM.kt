package com.glaukous.views.scanner

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.networkcalls.Repository
import com.glaukous.networkcalls.RetrofitApi
import com.glaukous.pref.PreferenceFile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScannerVM @Inject constructor(
    private val dataStoreUtil: DataStoreUtil,
    private val repository: Repository,
    private val apiInterface: RetrofitApi,
    private val preferencesUtils: PreferenceFile
) : ViewModel() {

    fun navigateToInput(view: View, barcode: String, quantity: Int) {
        view.findNavController().navigate(
            ScannerDirections.actionScannerToInput(
                barcode = barcode,
                quantity = quantity
            )
        )

    }
}