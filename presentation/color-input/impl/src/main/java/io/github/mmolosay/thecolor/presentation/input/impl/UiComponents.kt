package io.github.mmolosay.thecolor.presentation.input.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import io.github.mmolosay.thecolor.presentation.impl.thenIf
import io.github.mmolosay.thecolor.presentation.input.impl.field.TextFieldData
import io.github.mmolosay.thecolor.presentation.input.impl.field.TextFieldData.TrailingButton
import io.github.mmolosay.thecolor.presentation.input.impl.field.TextFieldUiStrings
import io.github.mmolosay.thecolor.presentation.input.impl.model.ColorSubmissionResult

/**
 * Reusable UI components for Color Input Views.
 */
internal object UiComponents {

    @Composable
    fun Loading() =
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )

    @Composable
    fun TextField(
        data: TextFieldData,
        strings: TextFieldUiStrings,
        value: TextFieldValue,
        onValueChange: (TextFieldValue) -> Unit,
        keyboardOptions: KeyboardOptions,
        keyboardActions: KeyboardActions,
        modifier: Modifier = Modifier,
    ) {
        OutlinedTextField(
            modifier = modifier
                .thenIf(data.shouldSelectAllTextOnFocus) {
                    selectAllTextOnFocus(
                        value = value,
                        onValueChange = onValueChange,
                    )
                },
            value = value,
            onValueChange = { new ->
                val current = value
                if (current.text != new.text) {
                    // can't just pass new.text to ViewModel for filtering: TextFieldValue.selection will be lost
                    val filteredText = data.filterUserInput(new.text)
                    val filteredValue = new.copy(text = filteredText.string)
                    onValueChange(filteredValue)
                    data.onTextChange(filteredText)
                } else {
                    onValueChange(new)
                }
            },
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.SansSerif),
            label = { Label(text = strings.label) },
            placeholder = { Placeholder(text = strings.placeholder) },
            trailingIcon = {
                TrailingButton(
                    data = data.trailingButton,
                    iconContentDesc = strings.trailingIconContentDesc,
                )
            },
            prefix = if (strings.prefix != null)
                ({ Prefix(text = strings.prefix) })
            else null,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
        )
        // for when text is cleared with trailing button or set programmatically
        LaunchedEffect(data.text) {
            val old = value
            val newText = data.text.string
            val hadSelectionAtTheEnd = (old.selection.end == old.text.length)
            val isNewTextLongerThanOld = (newText.length > old.text.length)
            // if it was "123|" become "123456|" instead of "123|456"
            val newSelection = if (hadSelectionAtTheEnd && isNewTextLongerThanOld) {
                TextRange(index = newText.length)
            } else {
                old.selection
            }
            val new = old.copy(text = newText, selection = newSelection)
            onValueChange(new)
        }
    }

    @Composable
    fun ProcessColorSubmissionResultAsSideEffect(
        result: ColorSubmissionResult?,
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        LaunchedEffect(result) {
            result ?: return@LaunchedEffect
            // color input was rejected, thus user will probably want to correct it and needs keyboard
            if (result.wasAccepted.not()) return@LaunchedEffect
            // color input was accepted, thus user probably won't change it and doesn't need keyboard
            keyboardController?.hide()
            result.discard()
        }
    }

    fun Modifier.onBackspace(
        onBackspace: () -> Unit,
    ) = this.onKeyEvent { keyEvent ->
        if (keyEvent.key == Key.Backspace && keyEvent.type == KeyEventType.KeyUp) {
            onBackspace()
            return@onKeyEvent true
        }
        return@onKeyEvent false
    }

    @Composable
    private fun Label(text: String) =
        Text(
            text = text,
        )

    @Composable
    private fun Placeholder(text: String) =
        Text(
            text = text,
            color = LocalContentColor.current.copy(alpha = 0.45f),
            style = MaterialTheme.typography.bodyLarge
                .copy(fontFamily = FontFamily.SansSerif),
        )

    @Composable
    private fun TrailingButton(
        data: TrailingButton,
        iconContentDesc: String?,
    ) {
        // when uiData is Hidden, we want to have memoized Visible data for some time while "exit" animation is running
        var visibleData by remember { mutableStateOf<TrailingButton.Visible?>(null) }
        val resizingAlignment = Alignment.Center
        AnimatedVisibility(
            visible = data is TrailingButton.Visible,
            enter = fadeIn() + expandIn(expandFrom = resizingAlignment),
            exit = fadeOut() + shrinkOut(shrinkTowards = resizingAlignment),
        ) {
            val lastVisible = visibleData ?: return@AnimatedVisibility
            iconContentDesc ?: return@AnimatedVisibility
            ClearIconButton(
                onClick = lastVisible.onClick,
                iconContentDesc = iconContentDesc,
            )
        }
        LaunchedEffect(data) {
            visibleData = data as? TrailingButton.Visible ?: return@LaunchedEffect
        }
    }

    @Composable
    private fun ClearIconButton(
        onClick: () -> Unit,
        iconContentDesc: String,
    ) {
        IconButton(
            onClick = onClick,
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = iconContentDesc,
            )
        }
    }

    @Composable
    private fun Prefix(text: String) =
        Text(
            text = text,
        )

    private fun Modifier.selectAllTextOnFocus(
        value: TextFieldValue,
        onValueChange: (TextFieldValue) -> Unit,
    ) =
        onFocusChanged action@{
            if (!it.isFocused) return@action
            val text = value.text
            val newValue = value.copy(
                selection = TextRange(start = 0, end = text.length)
            )
            onValueChange(newValue)
        }
}