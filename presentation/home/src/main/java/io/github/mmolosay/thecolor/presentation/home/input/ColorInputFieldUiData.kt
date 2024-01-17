package io.github.mmolosay.thecolor.presentation.home.input

/**
 * All data that is required to correctly display this UI component.
 * Designed in a way so it's convenient for UI to consume: related values are grouped into subclasses.
 */
data class ColorInputFieldUiData(
    val text: String,
    val onTextChange: (String) -> Unit,
    val processText: (String) -> String,
    val label: String,
    val placeholder: String,
    val prefix: String,
    val trailingButton: TrailingButton,
) {

    sealed interface TrailingButton {
        data object Hidden : TrailingButton
        data class Visible(
            val onClick: () -> Unit,
            val iconContentDesc: String,
        ) : TrailingButton
    }

    /**
     * Part of to-be [ColorInputFieldUiData].
     * Created by `View`, since string resources are tied to platform-specific
     * components (like `Context`), which should be avoided in `ViewModel`s.
     */
    data class ViewData(
        val label: String,
        val placeholder: String,
        val prefix: String,
        val trailingIconContentDesc: String,
    )
}