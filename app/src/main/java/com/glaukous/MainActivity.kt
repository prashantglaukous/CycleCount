package com.glaukous


import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.glaukous.databinding.ActivityMainBinding
import com.glaukous.extensions.showToast
import com.glaukous.interfaces.Navigator
import com.glaukous.views.home.Home.Companion.barcode
import com.glaukous.views.input.Input.Companion.inputCode
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator {

    lateinit var binding: ActivityMainBinding
    val mainVM: MainVM by viewModels()

    companion object {
        lateinit var context: WeakReference<Context>
        lateinit var navigator: Navigator
    }

    override fun onStart() {
        super.onStart()
        context = WeakReference(this)
    }

    override fun onRestart() {
        super.onRestart()
        context = WeakReference(this)
    }

    override fun onResume() {
        super.onResume()
        context = WeakReference(this)
        mainVM.isHome.set(mainVM.navController.currentDestination?.id == R.id.home)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = WeakReference(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navigator = this
        binding.model = mainVM
        initNavController()
        checkUpdate()

    }

    private fun initNavController() {

        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.mainContainer)

        val navHostFragment = fragment as NavHostFragment

        mainVM.navController = navHostFragment.navController

    }


    //override onBackPressed method
    /*@SuppressLint("RestrictedApi")
    override fun onBackPressed() {
    if (mainVM.navController.backQueue.size == 2) {
    showExit()
    } else
    super.onBackPressed()
    }*/


    /**Check app update from playStore*/
    private fun checkUpdate() {
        val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // If an in-app update is already running, resume the update.
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo, AppUpdateType.IMMEDIATE, this, 1234
                )
            }
        }
    }

    /***
     * Override result method
     * */
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1234 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Log.d("MainActivity", "onActivityResult" + "Result Ok")
                        //  handle user's approval }
                    }
                    Activity.RESULT_CANCELED -> {
                        Log.d("MainActivity", "onActivityResult" + "Result Cancelled")
                        //  handle user's rejection  }
                        finish()
                    }
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                        //if you want to request the update again just call checkUpdate()
                        Log.d("MainActivity", "onActivityResult" + "Update Failure")
                        //  handle update failure
                        finish()
                    }
                }
            }
        }
    }*/


    /*private fun showExit() {
        binding.root.showConfirmDialog(getString(R.string.exit_message)) {
            finishAffinity()
        }
    }*/


    override fun showAppBar(show: Boolean) {
        mainVM.showAppBar.set(show)
    }

    override fun showBack(show: Boolean) {
        mainVM.isHome.set(show)
    }

    var barcodes: String = ""
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        /*if (event.unicodeChar != 0) {
            Log.e("TAG", "dispatchKeyEvent2: $barcodes", )
            barcodes += event.unicodeChar.toChar().takeIf { true } ?: ""
        }*/
        when (event.action) {
            KeyEvent.ACTION_DOWN -> if (event.unicodeChar != 0) {
                mainVM.keyEvent = event.keyCode.takeIf { it != 0 } ?: 2
                Log.e("TAG", "dispatchKeyEvent2: $barcodes")
                barcodes += event.unicodeChar.toChar().takeIf { true } ?: ""
            }


            KeyEvent.KEYCODE_ENTER -> {

                Log.e("TAG", "dispatchKeyEvent: KEYCODE_ENTER")
                mainVM.keyEvent = event.keyCode.takeIf { it != 0 } ?: 2
                when (mainVM.navController.currentDestination?.id) {
                    R.id.home -> {
                        if (barcode != null && mainVM.isDataAvailableInHome.get() == true) {
                            barcode?.barcode(barcodes.trim())
                            barcodes = ""
                        } else {
                            "You don't have any task".showToast()
                        }

                    }

                    R.id.input -> {
                        if (inputCode != null && mainVM.isDataAvailableInHome.get() == true) {
                            inputCode?.barcode(barcodes.trim())
                            barcodes = ""
                        }
                    }

                    else -> {
                        barcodes = ""
                    }
                }
            }


            else -> {}
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val audioManager = this@MainActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                mainVM.keyEvent = keyCode
                if (mainVM.isDataAvailableInHome.get() == true) {
                    when (mainVM.navController.currentDestination?.id) {
                        R.id.home -> barcode?.navigateToScanner()
                        R.id.input -> inputCode?.navigateToScanner()
                        else -> audioManager.adjustVolume(
                            AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_SHOW_UI
                        )
                    }
                } else {
                    audioManager.adjustVolume(
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_SHOW_UI
                    )
                }
            }

            KeyEvent.KEYCODE_VOLUME_UP -> {
                mainVM.keyEvent = keyCode
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
            }

            KeyEvent.KEYCODE_BACK -> {
                mainVM.keyEvent = keyCode
                if (mainVM.navController.backQueue.size <= 2) {
                    finishAffinity()
                } else {
                    mainVM.navController.popBackStack()
                }
            }
            KeyEvent.KEYCODE_ENTER -> {
                mainVM.keyEvent = keyCode
                when (mainVM.navController.currentDestination?.id) {
                    R.id.home -> {
                        if (barcode != null && mainVM.isDataAvailableInHome.get() == true) {
                            barcode?.barcode(barcodes.trim())
                            barcodes = ""
                        } else {
                            "You don't have any task".showToast()
                        }

                    }
                    R.id.input -> {
                        if (inputCode != null) {
                            inputCode?.barcode(barcodes.trim())
                            barcodes = ""
                        }
                    }
                    else -> {
                        barcodes = ""
                    }
                }
            }
        }
        return true
    }
}