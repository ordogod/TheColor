package com.ordolabs.feature_home.ui.fragment.color.input.page

import androidx.core.widget.doOnTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ordolabs.feature_home.R
import com.ordolabs.feature_home.databinding.ColorInputHexFragmentBinding
import com.ordolabs.thecolor.model.color.ColorPrototype
import com.ordolabs.thecolor.util.ext.getText
import com.ordolabs.thecolor.util.ext.getTextString
import com.ordolabs.thecolor.util.ext.setTextPreservingSelection
import com.ordolabs.thecolor.util.struct.Resource
import kotlinx.coroutines.flow.Flow

class ColorInputHexFragment :
    BaseColorInputFragment<ColorPrototype.Hex>(R.layout.color_input_hex_fragment) {

    private val binding: ColorInputHexFragmentBinding by viewBinding()

    override fun setViews() {
        setInputTextWatcher()
    }

    private fun setInputTextWatcher() =
        binding.inputHex.editText?.doOnTextChanged { _, _, _, _ ->
            outputOnInputChanges()
        }

    // region BaseColorInputFragment

    override fun assemblePrototype(): ColorPrototype.Hex {
        val input = binding.inputHex.getTextString()
        return ColorPrototype.Hex(value = input)
    }

    override fun populateViews(color: ColorPrototype.Hex): Unit =
        binding.run {
            inputHex.editText?.setTextPreservingSelection(color.value)
        }

    override fun clearViews(): Unit =
        binding.run {
            inputHex.getText()?.clear()
        }

    override fun getColorInputFlow(): Flow<Resource<ColorPrototype.Hex>> =
        colorInputVM.colorHex

    // endregion

    companion object {

        fun newInstance() =
            ColorInputHexFragment()
    }
}