package io.github.mmolosay.thecolor.presentation.home.input

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ColorInputViewModel @Inject constructor() : ViewModel() {

    private val _uiDataFlow = MutableStateFlow(makeInitialViewModelData())
    val uiDataFlow = _uiDataFlow.asStateFlow()

    private fun onInputChange(value: String) {
        _uiDataFlow.update {
            it.smartCopy(input = value)
        }
    }

    private fun onTrailingButtonClick() {
        _uiDataFlow.update {
            it.smartCopy(input = "")
        }
    }

    // seems like a better solution than "uiDataFlow = _uiDataFlow.map {..}"
    private fun ColorInputFieldUiData.ViewModelData.smartCopy(
        input: String,
    ) =
        copy(
            text = input.take(MAX_SYMBOLS_IN_HEX_COLOR),
            showTrailingButton = input.isNotEmpty(),
        )

    private fun makeInitialViewModelData() =
        ColorInputFieldUiData.ViewModelData(
            text = "",
            onTextChange = ::onInputChange,
            showTrailingButton = false,
            onTrailingButtonClick = ::onTrailingButtonClick,
        )

    private companion object {
        const val MAX_SYMBOLS_IN_HEX_COLOR = 6
    }
}