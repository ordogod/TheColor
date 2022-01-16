package com.ordolabs.feature_home.ui.fragment.colordata.scheme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ordolabs.feature_home.R
import com.ordolabs.feature_home.ui.fragment.BaseFragment
import com.ordolabs.thecolor.util.InflaterUtil.cloneInViewContext

class ColorSchemeShimmerFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inherit container view group theme
        return inflater.cloneInViewContext(container)
            .inflate(R.layout.color_scheme_shimmer_fragment, container, false)
    }

    override fun collectViewModelsData() {
        // nothing is here
    }

    override fun setViews() {
        // nothing is here
    }

    companion object {
        fun newInstance() =
            ColorSchemeShimmerFragment()
    }
}