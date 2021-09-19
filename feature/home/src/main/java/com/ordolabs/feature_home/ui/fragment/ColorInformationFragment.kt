package com.ordolabs.feature_home.ui.fragment

import androidx.core.view.isInvisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.get
import com.github.michaelbull.result.runCatching
import com.ordolabs.feature_home.R
import com.ordolabs.feature_home.databinding.FragmentColorInformationBinding
import com.ordolabs.feature_home.viewmodel.ColorInformationViewModel
import com.ordolabs.feature_home.viewmodel.ColorInputViewModel
import com.ordolabs.thecolor.model.ColorInformationPresentation
import com.ordolabs.thecolor.util.ColorUtil
import com.ordolabs.thecolor.util.ext.showToast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ColorInformationFragment : BaseFragment(R.layout.fragment_color_information) {

    private val binding: FragmentColorInformationBinding by viewBinding()
    private val colorInputVM: ColorInputViewModel by sharedViewModel()
    private val colorInformationVM: ColorInformationViewModel by viewModel()

    override fun collectViewModelsData() {
        collectColorPreview()
        collectProcceedCommand()
        collectColorInformation()
        collectCoroutineException()
    }

    override fun setViews() {
        // nothing is here
    }

    private fun populateInformationViews(info: ColorInformationPresentation) = binding.run {
        name.text = info.name
    }

    private fun toggleVisibility(visible: Boolean) {
        view?.isInvisible = !visible
    }

    private fun collectColorInformation() =
        colorInformationVM.information.collectOnLifecycle { resource ->
            resource.ifSuccess { information ->
                toggleVisibility(visible = true)
                populateInformationViews(information)
            }
        }

    private fun collectColorPreview() =
        colorInputVM.colorPreview.collectOnLifecycle { resource ->
            resource.fold(
                onEmpty = ::onColorPreviewEmpty,
                onSuccess = ::onColorPreviewSuccess
            )
        }

    private fun onColorPreviewEmpty() {
        toggleVisibility(visible = false)
    }

    private fun onColorPreviewSuccess(color: ColorUtil.Color) {
        val info = colorInformationVM.information.value.getOrNull()
        val visible = (info?.hexValue == color.hex)
        toggleVisibility(visible)
    }

    private fun collectProcceedCommand() =
        colorInputVM.procceedCommand.collectOnLifecycle { resource ->
            resource.ifSuccess { color ->
                colorInformationVM.fetchColorInformation(color)
            }
        }

    private fun collectCoroutineException() =
        colorInformationVM.coroutineExceptionMessageRes.collectOnLifecycle { idres ->
            val text = Result.runCatching { getString(idres) }.get() ?: return@collectOnLifecycle
            showToast(text)
        }

    companion object {

        fun newInstance() = ColorInformationFragment()
    }
}