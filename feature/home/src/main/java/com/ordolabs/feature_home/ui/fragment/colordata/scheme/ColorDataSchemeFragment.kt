package com.ordolabs.feature_home.ui.fragment.colordata.scheme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ordolabs.feature_home.R
import com.ordolabs.feature_home.databinding.ColorDataSchemeFragmentBinding
import com.ordolabs.feature_home.ui.fragment.colordata.base.BaseColorDataFragment
import com.ordolabs.thecolor.model.ColorSchemePresentation
import com.ordolabs.thecolor.util.InflaterUtil.cloneInViewContext

class ColorDataSchemeFragment :
    BaseColorDataFragment<ColorSchemePresentation>() {

    private val binding: ColorDataSchemeFragmentBinding by viewBinding(CreateMethod.BIND)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inherit container view group theme
        return inflater.cloneInViewContext(container)
            .inflate(R.layout.color_data_scheme_fragment, container, false)
    }

    override fun collectViewModelsData() {
        // impl me
    }

    override fun setViews() {
        // impl me
    }

    override fun populateViews(data: ColorSchemePresentation) {
        binding.text.text = "It works!"
    }

    companion object {
        fun newInstance() =
            ColorDataSchemeFragment()
    }
}