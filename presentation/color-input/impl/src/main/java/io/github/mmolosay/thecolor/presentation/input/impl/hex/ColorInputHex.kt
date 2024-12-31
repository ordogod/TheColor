package io.github.mmolosay.thecolor.presentation.input.impl.hex

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.mmolosay.thecolor.presentation.design.TheColorTheme
import io.github.mmolosay.thecolor.presentation.input.impl.UiComponents.ProcessColorSubmissionResultAsSideEffect
import io.github.mmolosay.thecolor.presentation.input.impl.UiComponents.TextField
import io.github.mmolosay.thecolor.presentation.input.impl.field.TextFieldData
import io.github.mmolosay.thecolor.presentation.input.impl.field.TextFieldData.Text
import io.github.mmolosay.thecolor.presentation.input.impl.field.TextFieldData.TrailingButton
import io.github.mmolosay.thecolor.presentation.input.impl.field.TextFieldUiStrings
import io.github.mmolosay.thecolor.presentation.input.impl.model.DataState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColorInputHex(
    viewModel: ColorInputHexViewModel,
) {
    val context = LocalContext.current
    val strings = remember(context) { ColorInputHexUiStrings(context) }
    val state = viewModel.dataStateFlow.collectAsStateWithLifecycle().value
    val colorSubmissionResult =
        viewModel.colorSubmissionResultFlow.collectAsStateWithLifecycle().value

    val transition = updateTransition(
        targetState = state,
        label = "data state cross-fade",
    )
    val animationSpec = tween<Float>(
        durationMillis = 500,
        easing = FastOutSlowInEasing,
    )
    transition.Crossfade(
        animationSpec = animationSpec,
        contentKey = { it::class }, // don't animate when 'DataState' type stays the same
    ) { state ->
        when (state) {
            is DataState.BeingInitialized ->
                ColorInputHexLoading()
            is DataState.Ready -> {
                ColorInputHex(
                    data = state.data,
                    strings = strings,
                )
            }
        }
    }

    ProcessColorSubmissionResultAsSideEffect(
        result = colorSubmissionResult,
    )
}

@Composable
fun ColorInputHex(
    data: ColorInputHexData,
    strings: ColorInputHexUiStrings,
) {
    var value by remember {
        val text = data.textField.text.string
        val value = TextFieldValue(
            text = text,
            selection = TextRange(index = text.length), // cursor at the end of the text
        )
        mutableStateOf(value)
    }

    TextField(
        modifier = Modifier
            .defaultMinSize(minWidth = 180.dp)
            .fillMaxWidth(0.5f),
        data = data.textField,
        strings = strings.textField,
        value = value,
        onValueChange = { new -> value = new },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Characters,
        ),
        keyboardActions = KeyboardActions(
            onDone = { data.submitColor() },
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TheColorTheme {
        ColorInputHex(
            data = previewData(),
            strings = previewUiStrings(),
        )
    }
}

private fun previewData() =
    ColorInputHexData(
        textField = TextFieldData(
            text = Text(""),
            onTextChange = {},
            filterUserInput = { Text(it) },
            trailingButton = TrailingButton.Visible(
                onClick = {},
            ),
            shouldSelectAllTextOnFocus = false,
        ),
        submitColor = {},
    )

private fun previewUiStrings() =
    ColorInputHexUiStrings(
        textField = TextFieldUiStrings(
            label = "HEX",
            placeholder = "000000",
            prefix = "#",
            trailingIconContentDesc = "Clear text",
        ),
    )