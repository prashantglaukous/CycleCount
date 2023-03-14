package com.glaukous.views.input

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.glaukous.MainActivity
import com.glaukous.MainVM
import com.glaukous.R
import com.glaukous.databinding.InputBinding
import com.glaukous.interfaces.Barcode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class Input : Fragment(), Barcode {
    private var _binding: InputBinding? = null
    private var binding = _binding
    private val viewModel by viewModels<InputVM>()
    private val args by navArgs<InputArgs>()
    private val mainVM by activityViewModels<MainVM>()
    private val hitResult = MutableLiveData<Boolean>()

    companion object {
        var inputCode: Barcode? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = InputBinding.inflate(layoutInflater, container, false)
        binding?.viewModel = viewModel
        inputCode = this
        if (!args.isADifferentCode) {
            viewModel.barcode.set(args.barcode.takeIf { it.isNotEmpty() } ?: args.newBarCode)
            viewModel.quantity.set(args.quantity)
            viewModel.itemCode.set(args.itemCode)
            viewModel.date.set(args.date)
            viewModel.floor.set(args.floor)
            viewModel.cycleCountId.set(args.cycleCountId)
        } else {
            hitResult.value = args.isADifferentCode
        }

        hitResult.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.quantity.set(args.quantity)
                viewModel.itemCode.set(args.itemCode)
                viewModel.cycleCountId.set(args.cycleCountId)
                viewModel.barcode.set(args.barcode)
                viewModel.floor.set(args.floor)
                viewModel.submitCount(binding?.root!!, args, verify = false, "")
            }
        }
        decreasePress()
        return binding?.root
    }

    private fun decreasePress() {

        binding?.ivMinus?.setOnClickListener {
            if (viewModel.quantity.get()!! > 1 && viewModel.quantity.get() != null) {
                viewModel.quantity.set(viewModel.quantity.get()!!.minus(1))
            }
        }
    }


    private var count = 0
    private var barCodeData: String = ""
    override fun barcode(barcode: String) {
        if (barcode.isNotEmpty()) {
            this.barCodeData = barcode
        }
        ++count
        val increasedBy = 3.takeIf {
            args.itemCode.trim().startsWith("NBR") || args.itemCode.trim().startsWith("IBR")
        } ?: 1/*.takeIf { barCodeData.trim().equals(args.barcode, true) } ?: 0*/

//        if (count / 2 == 1) {
        try {
            if (barCodeData.isNotEmpty() && barCodeData.equals(viewModel.barcode.get(), true)) {
                updateButton()
                viewModel.quantity.set(
                    viewModel.quantity.get()
                        ?.plus(increasedBy) ?: 0
                )
            } else {
                updateButton()
//                "Code doesn't match".showToast(requireContext())
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.verifyItemCode(barcode) { code, count ->
                        viewModel.submitCount(
                            view = binding?.root!!,
                            args = args,
                            verify = true,
                            barCodeData = code,
                            count = count
                        )
                    }

                }
            }

        } catch (_: Exception) {
        }
//        }
    }

    private fun updateButton() {
        Handler(Looper.getMainLooper()).postDelayed({
            mainVM.keyEvent = 0
            count = 0
        }, 200)
    }

    override fun navigateToScanner() {
        super.navigateToScanner()
        if (findNavController().currentDestination?.id == R.id.input) {
            findNavController()
                .navigate(
                    InputDirections.actionInputToScanner(
                        barcode = viewModel.barcode.get(),
                        quantity = viewModel.quantity.get() ?: 0,
                        floor = viewModel.floor.get(),
                        date = viewModel.date.get(),
                        cycleCountId = viewModel.cycleCountId.get() ?: 0,
                        itemCode = viewModel.itemCode.get()?:""
                    )
                )
        }
    }

    override fun onResume() {
        super.onResume()
        MainActivity.navigator.apply {
            showBack(show = true)
        }
    }
}