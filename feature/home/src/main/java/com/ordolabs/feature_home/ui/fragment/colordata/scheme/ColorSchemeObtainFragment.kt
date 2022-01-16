package com.ordolabs.feature_home.ui.fragment.colordata.scheme

import androidx.fragment.app.Fragment
import com.ordolabs.feature_home.ui.fragment.colordata.ColorDataObtainFragment
import com.ordolabs.feature_home.ui.fragment.colordata.base.BaseColorDataFragment
import com.ordolabs.feature_home.viewmodel.colordata.scheme.ColorSchemeObtainViewModel
import com.ordolabs.feature_home.viewmodel.colordata.scheme.ColorSchemeSettingsViewModel
import com.ordolabs.thecolor.model.color.data.ColorScheme
import com.ordolabs.thecolor.util.struct.Resource
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class ColorSchemeObtainFragment :
    ColorDataObtainFragment<ColorScheme>() {

    private val colorSchemeObtainVM: ColorSchemeObtainViewModel by sharedViewModel()
    private val colorSchemeSettingsVM: ColorSchemeSettingsViewModel by sharedViewModel {
        parametersOf(color)
    }

    // region ColorDataObtainFragment

    override fun getColorDataFlow(): Flow<Resource<ColorScheme>> =
        colorSchemeObtainVM.scheme

    override fun obtainColorData() {
        val request = colorSchemeSettingsVM.assembleColorSchemeRequest()
        colorSchemeObtainVM.getColorScheme(request)
    }

    override fun makeColorDataFragment(): BaseColorDataFragment<ColorScheme> =
        ColorSchemeConfigureFragment.newInstance()

    override fun makeContentShimmerFragment(): Fragment =
        ColorSchemeShimmerFragment.newInstance()

    // endregion

    companion object {
        fun newInstance() =
            ColorSchemeObtainFragment()
    }
}