package com.glaukous.views.splash

import android.view.View
import androidx.lifecycle.ViewModel
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.datastore.LOGIN_DATA
import com.glaukous.networkcalls.Repository
import com.glaukous.pref.PreferenceFile
import com.glaukous.pref.token
import com.glaukous.views.login.LoginData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil
) : ViewModel() {

    fun retrieveData(data: (LoginData?, String?) -> Unit) {
        dataStore.readObject(LOGIN_DATA, LoginData::class.java) {
            data(it, preferenceFile.retrieveKey(token))
        }
    }

    fun onClick(view: View) {
        when (view.id) {

        }
    }

}