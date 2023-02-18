package com.glaukous.views.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.glaukous.MainActivity
import com.glaukous.MainVM
import com.glaukous.R
import com.glaukous.databinding.HomeBinding
import com.glaukous.interfaces.Barcode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Home : Fragment(), Barcode {
    private var _binding: HomeBinding? = null
    private var binding = _binding
    private val viewModel by viewModels<HomeVM>()
    private val mainVM by activityViewModels<MainVM>()
//    private val args by navArgs<HomeArgs>()


    companion object {
        var barcode: Barcode? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = HomeBinding.inflate(layoutInflater, container, false)
        binding?.viewModel = viewModel
        barcode = this
        MainActivity.navigator.apply {
            showAppBar(show = true)
            showBack(show = false)
        }
        binding?.swipe?.setOnRefreshListener {
            viewModel.getCycleCountByPicker()
            binding?.swipe?.isRefreshing = false
        }
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCycleCountByPicker()
        MainActivity.navigator.apply {
            showBack(show = false)
        }
    }

    private var entry = 0
    override fun barcode(barcode: String) {
        Log.e("TAG", "barcode: $barcode", )
        entry++
        if (barcode.isNotEmpty() && entry / 2 == 0) {
            updateButton()
            viewModel.verifyItemCode(barcode.trim(), binding?.root)
        }
    }

    private fun updateButton() {
        Handler(Looper.getMainLooper()).postDelayed({
            entry = 0
            mainVM.keyEvent = 0
        }, 200)
    }

    override fun navigateToScanner() {
        super.navigateToScanner()
        Log.e("TAG", "navigateToScanner: ", )
        if (findNavController().currentDestination?.id == R.id.home  && mainVM.keyEvent != KeyEvent.KEYCODE_ENTER) {
            findNavController()
                .navigate(
                    HomeDirections.actionHomeToScanner(
                        null,
                        floor = viewModel.floor.get(),
                        date = viewModel.date.get(),
                        cycleCountId = viewModel.cycleCountId.get() ?: 0
                    )
                )
        }
    }
}