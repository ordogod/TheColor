package com.ordolabs.feature_home.ui.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Point
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isInvisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ordolabs.feature_home.R
import com.ordolabs.feature_home.databinding.FragmentHomeBinding
import com.ordolabs.feature_home.di.featureHomeModule
import com.ordolabs.feature_home.ui.fragment.colorinput.ColorInputHostFragment
import com.ordolabs.feature_home.viewmodel.ColorInformationViewModel
import com.ordolabs.feature_home.viewmodel.ColorInputViewModel
import com.ordolabs.thecolor.util.AnimationUtils
import com.ordolabs.thecolor.util.ColorUtil
import com.ordolabs.thecolor.util.ColorUtil.toColorInt
import com.ordolabs.thecolor.util.InsetsUtil
import com.ordolabs.thecolor.util.ext.createCircularRevealAnimation
import com.ordolabs.thecolor.util.ext.getBottomVisibleInParent
import com.ordolabs.thecolor.util.ext.getColor
import com.ordolabs.thecolor.util.ext.getDistanceInParent
import com.ordolabs.thecolor.util.ext.hideSoftInput
import com.ordolabs.thecolor.util.ext.longAnimDuration
import com.ordolabs.thecolor.util.ext.mediumAnimDuration
import com.ordolabs.thecolor.util.ext.setColor
import com.ordolabs.thecolor.util.ext.setFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.context.loadKoinModules
import com.ordolabs.thecolor.R as RApp

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val binding: FragmentHomeBinding by viewBinding()
    private val colorInputVM: ColorInputViewModel by sharedViewModel()
    private val colorInformationVM: ColorInformationViewModel by sharedViewModel()

    private val defaultPreviewColor: Int by lazy {
        getPreviewColor()
    }

    private val defaultSheetColor: Int by lazy {
        getInfoSheetColor()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(featureHomeModule)
    }

    override fun collectViewModelsData() {
        collectColorPreview()
        collectProcceedCommand()
    }

    override fun setViews() {
        defaultPreviewColor // init
        defaultSheetColor // init
        setColorInputFragment()
        setColorInformationFragment()
    }

    private fun setColorInputFragment() {
        val fragment = ColorInputHostFragment.newInstance()
        setFragment(fragment)
    }

    private fun setColorInformationFragment() {
        val fragment = ColorInformationFragment.newInstance()
        setFragment(fragment, binding.infoFragmentContainer.id)
    }

    private fun updateColorPreview(@ColorInt color: Int) {
        val sheetColor = getInfoSheetColor()
        if (color == getPreviewColor()) return
        if (color != sheetColor && sheetColor != defaultSheetColor) {
            animInfoSheetHiding(color)
        } else {
            makePreviewColorChangingAnimation(color).start()
        }
    }

    private fun animInfoSheetShowing(@ColorInt color: Int) {
        AnimatorSet().apply {
            playSequentially(
                animPreviewHiding(),
                makeInfoSheetRevealAnimation(hide = false).apply {
                    doOnStart {
                        setInfoSheetColor(color)
                    }
                }
            )
        }.start()
    }

    private fun animInfoSheetHiding(@ColorInt color: Int) {
        AnimatorSet().apply {
            playSequentially(
                makeInfoSheetRevealAnimation(hide = true),
                animPreviewShowing(),
                makePreviewColorChangingAnimation(color)
            )
        }.start()
    }

    private fun animPreviewShowing() = AnimatorSet().apply {
        playTogether(
            makePreviewShowingAnimation(),
            makePreviewElevationAnimation(flatten = false)
        )
        duration = longAnimDuration
    }

    private fun animPreviewHiding() = AnimatorSet().apply {
        playTogether(
            makePreviewHidingAnimation(),
            makePreviewElevationAnimation(flatten = true)
        )
        duration = longAnimDuration
    }

    private fun makePreviewColorChangingAnimation(@ColorInt color: Int): Animator {
        val preview = binding.preview
        val updated = binding.previewUpdated
        return updated.createCircularRevealAnimation().apply {
            duration = mediumAnimDuration
            interpolator = FastOutSlowInInterpolator()
            doOnStart {
                updated.background.setColor(color)
                updated.isInvisible = false
            }
            doOnEnd {
                preview.background.setColor(color)
                preview.invalidate()
                updated.isInvisible = true
            }
        }
    }

    private fun makePreviewShowingAnimation(): Animator {
        val preview = binding.previewWrapper
        return ObjectAnimator
            .ofFloat(preview, "translationY", preview.translationY, 0f)
            .apply {
                interpolator = AnticipateOvershootInterpolator()
            }
    }

    private fun makePreviewHidingAnimation(): Animator {
        val sheet = binding.infoSheet
        val preview = binding.previewWrapper
        val distance = preview.getDistanceInParent(sheet, view)?.y ?: 0
        val addend = makeInfoSheetRevealCenter().y
        val radius = preview.height / 2
        val translation = distance.toFloat() + addend - radius
        return ObjectAnimator
            .ofFloat(preview, "translationY", 0f, translation)
            .apply {
                interpolator = AnticipateOvershootInterpolator()
            }
    }

    private fun makePreviewElevationAnimation(flatten: Boolean): Animator {
        val preview = binding.previewWrapper
        val elevation = resources.getDimension(R.dimen.home_preview_elevation)
        val animator = if (flatten) { // reverse() can't be used when is a part of AnimatorSet
            ValueAnimator.ofFloat(elevation, 0f)
        } else {
            ValueAnimator.ofFloat(0f, elevation)
        }
        return animator.apply {
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                preview.elevation = animatedValue as Float
            }
        }
    }

    private fun makeInfoSheetRevealAnimation(hide: Boolean): Animator {
        val sheet = binding.infoSheet
        val preview = binding.previewWrapper
        val center = makeInfoSheetRevealCenter()
        var sr = preview.width.toFloat() / 2
        var er = AnimationUtils.getCircularRevealMaxRadius(sheet, center)
        if (hide) er.let {
            er = sr
            sr = it
        }
        return sheet.createCircularRevealAnimation(center.x, center.y, sr, er).apply {
            duration = longAnimDuration
            interpolator = AccelerateDecelerateInterpolator()
            doOnStart {
                sheet.isInvisible = false
            }
            doOnEnd {
                sheet.isInvisible = hide
            }
        }
    }

    private fun makeInfoSheetRevealCenter(): Point {
        val sheet = binding.infoSheet
        val bottom = sheet.getBottomVisibleInParent(view) ?: sheet.height
        val padding = resources.getDimensionPixelSize(RApp.dimen.offset_32)
        val navbarHeight = InsetsUtil.getNavigationBarHeight(context) ?: 0
        val x = sheet.width / 2
        val y = bottom - navbarHeight - padding
        return Point(x, y)
    }

    private fun getPreviewColor(): Int {
        return binding.preview.background.getColor()
    }

    private fun getInfoSheetColor(): Int {
        return binding.infoSheet.backgroundTintList?.defaultColor ?: 0
    }

    private fun setInfoSheetColor(@ColorInt color: Int) {
        binding.infoSheet.backgroundTintList = ColorStateList.valueOf(color)
    }

    private fun collectColorPreview() =
        colorInputVM.colorPreview.collectOnLifecycle { resource ->
            resource.fold(
                onEmpty = ::onColorPreviewEmpty,
                onSuccess = ::onColorPreviewSuccess
            )
        }

    private fun onColorPreviewEmpty() {
        updateColorPreview(defaultPreviewColor)
    }

    private fun onColorPreviewSuccess(color: ColorUtil.Color) {
        updateColorPreview(color.toColorInt())
    }

    private fun collectProcceedCommand() =
        colorInputVM.procceedCommand.collectOnLifecycle { resource ->
            resource.fold(
                onSuccess = ::onProcceedCommandSuccess
            )
        }

    private fun onProcceedCommandSuccess(color: ColorUtil.Color) {
        hideSoftInput()
        val int = color.toColorInt()
        if (getInfoSheetColor() == int) return // sequentiall button clicks, do nothing
        animInfoSheetShowing(int)
    }

    override fun setSoftInputMode() {
        // https://yatmanwong.medium.com/android-how-to-pan-the-page-up-more-25fc5c542a97
    }

    companion object {

        // being created by NavHostFragment, thus no newInstance() method
    }
}