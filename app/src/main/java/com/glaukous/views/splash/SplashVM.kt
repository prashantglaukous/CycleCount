package com.glaukous.views.splash

import android.view.View
import androidx.lifecycle.ViewModel
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.networkcalls.Repository
import com.glaukous.pref.PreferenceFile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil
) : ViewModel() {
    init {

    }

    fun onClick(view: View) {
        when (view.id) {

        }
    }

}