package io.github.mmolosay.thecolor.presentation.home.input

import io.github.mmolosay.thecolor.presentation.home.input.ColorInputFieldUiData.TrailingButton
import io.github.mmolosay.thecolor.presentation.home.input.ColorInputFieldUiData.ViewData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ColorInputFieldViewModel(
    private val viewData: ViewData,
    private val processText: (String) -> String,
) {

    private val _uiDataFlow = MutableStateFlow(makeInitialUiData())
    val uiDataFlow = _uiDataFlow.asStateFlow()

    private fun onTextChange(text: String) {
        _uiDataFlow.update {
            it.smartCopy(text = processText(text))
        }
    }

    private fun onTrailingButtonClick() {
        _uiDataFlow.update {
            it.smartCopy(text = "")
        }
    }

    // seems like a better solution than "uiDataFlow = _uiDataFlow.map {..}"
    private fun ColorInputFieldUiData.smartCopy(
        text: String,
    ) =
        copy(
            text = text,
            trailingButton = trailingButton(text, viewData.trailingIconContentDesc),
        )

    private fun trailingButton(text: String, iconContentDesc: String): TrailingButton =
        if (showTrailingButton(text)) {
            TrailingButton.Visible(
                onClick = ::onTrailingButtonClick,
                iconContentDesc = iconContentDesc,
            )
        } else {
            TrailingButton.Hidden
        }

    private fun showTrailingButton(text: String): Boolean =
        text.isNotEmpty()

    private fun makeInitialUiData() =
        ColorInputFieldUiData(
            text = "",
            onTextChange = ::onTextChange,
            processText = processText,
            label = viewData.label,
            placeholder = viewData.placeholder,
            prefix = viewData.prefix,
            trailingButton = TrailingButton.Hidden,
        )
}