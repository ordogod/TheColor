package com.ordolabs.feature_home.ui.fragment.color.data.page.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ordolabs.feature_home.R
import com.ordolabs.feature_home.databinding.ColorDataPageFragmentBinding
import com.ordolabs.feature_home.ui.adapter.pager.ColorDataPagerAdapter
import com.ordolabs.feature_home.ui.fragment.BaseFragment
import com.ordolabs.feature_home.ui.fragment.color.data.ColorThemedView
import com.ordolabs.feature_home.viewmodel.color.data.ColorDataViewModel
import com.ordolabs.thecolor.model.color.Color
import com.ordolabs.thecolor.model.color.isDark
import com.ordolabs.thecolor.util.ext.getNextFor
import com.ordolabs.thecolor.util.ext.parentViewModels
import com.ordolabs.thecolor.util.ext.setFragmentOrGet
import com.ordolabs.thecolor.R as RApp

abstract class BaseColorDataPage :
    BaseFragment(),
    ColorThemedView {

    // region Abstract

    abstract val page: ColorDataPagerAdapter.Page

    abstract fun makeColorDataFragmentNewInstance(): Fragment
    abstract fun getChangePageBtnText(): String

    // endregion

    private val binding: ColorDataPageFragmentBinding by viewBinding(CreateMethod.BIND)
    private val colorDataVM: ColorDataViewModel by parentViewModels()

    override val color: Color?
        get() = (parentFragment as? ColorThemedView)?.color

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* ViewPager2 inflates its fragments without container,
        * thus parent view group theme can not be used :< */
        val themeOverlay = if (color?.isDark() == true) {
            RApp.style.ThemeOverlay_TheColor_Dark
        } else {
            RApp.style.ThemeOverlay_TheColor_Light
        }
        val themedContext = ContextThemeWrapper(context, themeOverlay)
        return inflater
            .cloneInContext(themedContext)
            .inflate(R.layout.color_data_page_fragment, container, false)
    }

    override fun collectViewModelsData() {
        // nothing is here
    }

    override fun setViews() {
        setColorDataFragment()
        setChangePageBtn()
    }

    private fun setColorDataFragment() {
        setFragmentOrGet { makeColorDataFragmentNewInstance() }
    }

    private fun setChangePageBtn() =
        binding.changePageBtn.let { button ->
            button.setOnClickListener {
                val dest = getNextFor(page)
                colorDataVM.changeDataPage(dest)
            }
            button.text = getChangePageBtnText()
        }
}