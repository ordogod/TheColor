package com.ordolabs.feature_home.ui.fragment.color.data.scheme

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ordolabs.feature_home.ui.fragment.color.data.ColorDataObtainFragment
import com.ordolabs.feature_home.ui.fragment.color.data.base.BaseColorDataFragment
import com.ordolabs.feature_home.ui.fragment.color.data.scheme.editor.ColorSchemeEditorFragment
import com.ordolabs.feature_home.ui.fragment.color.data.scheme.editor.ColorSchemeEditorParent
import com.ordolabs.feature_home.ui.fragment.color.data.scheme.editor.ColorSchemeEditorView
import com.ordolabs.feature_home.util.FeatureHomeUtil.featureHomeComponent
import com.ordolabs.feature_home.viewmodel.colordata.scheme.ColorSchemeObtainViewModel
import com.ordolabs.thecolor.model.color.data.ColorScheme
import com.ordolabs.thecolor.model.color.data.ColorSchemeRequest
import com.ordolabs.thecolor.util.struct.Resource
import kotlinx.coroutines.flow.Flow
import com.ordolabs.thecolor.R as RApp

class ColorSchemeObtainFragment :
    ColorDataObtainFragment<ColorScheme>(),
    ColorSchemeEditorParent {

    private val schemeObtainVM: ColorSchemeObtainViewModel by viewModels {
        featureHomeComponent.viewModelFactory
    }

    // region Set views

    override fun setViews() {
        super.setViews()
        view?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = resources.getDimensionPixelSize(RApp.dimen.offset_16)
        }
    }

    // endregion

    // region ColorDataObtainFragment

    override fun getColorDataFlow(): Flow<Resource<ColorScheme>> =
        schemeObtainVM.scheme

    override fun obtainColorData() {
        val request = assembleSchemeRequest() ?: return
        schemeObtainVM.getColorScheme(request)
    }

    override fun makeColorDataFragment(): BaseColorDataFragment<ColorScheme> =
        ColorSchemeEditorFragment.newInstance()

    override fun makeContentShimmerFragment(): Fragment =
        ColorSchemeShimmerFragment.newInstance()

    private fun assembleSchemeRequest(): ColorSchemeRequest? {
        val config = getColorSchemeEditorView()?.appliedConfig ?: return null
        return assebleSchemeRequest(config)
    }

    // endregion

    // region ColorSchemeEditorParent

    override fun dispatchColorSchemeConfig(config: ColorSchemeRequest.Config) {
        obtainColorScheme(config)
    }

    // endregion

    private fun getColorSchemeEditorView(): ColorSchemeEditorView? =
        dataView as? ColorSchemeEditorView

    private fun obtainColorScheme(config: ColorSchemeRequest.Config) {
        val request = assebleSchemeRequest(config) ?: return
        schemeObtainVM.getColorScheme(request)
    }

    private fun assebleSchemeRequest(config: ColorSchemeRequest.Config): ColorSchemeRequest? {
        val seed = color ?: return null
        return ColorSchemeRequest(seed, config)
    }

    companion object {
        fun newInstance() =
            ColorSchemeObtainFragment()
    }
}