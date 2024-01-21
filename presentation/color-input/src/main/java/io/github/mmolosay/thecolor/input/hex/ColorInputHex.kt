package io.github.mmolosay.thecolor.input.hex

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.mmolosay.thecolor.presentation.design.TheColorTheme
import io.github.mmolosay.thecolor.input.UiComponents.TextField
import io.github.mmolosay.thecolor.input.field.TextFieldUiData
import io.github.mmolosay.thecolor.input.field.TextFieldUiData.Text
import io.github.mmolosay.thecolor.input.field.TextFieldUiData.TrailingButton

@Composable
fun ColorInputHex(
    vm: ColorInputHexViewModel,
) {
    val uiData = vm.uiDataFlow.collectAsStateWithLifecycle().value
    ColorInputHex(
        uiData = uiData,
    )
}

@Composable
fun ColorInputHex(
    uiData: ColorInputHexUiData,
) {
    var value by remember { mutableStateOf(TextFieldValue(text = uiData.textField.text.string)) }
    TextField(
        modifier = Modifier
            .wrapContentSize(align = Alignment.TopCenter) // ComposeView propagates min=max constraints
            .fillMaxWidth(0.5f),
        uiData = uiData.textField,
        value = value,
        updateValue = { new -> value = new },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Characters,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TheColorTheme {
        ColorInputHex(
            uiData = previewUiData(),
        )
    }
}

private fun previewUiData() =
    ColorInputHexUiData(
        textField = TextFieldUiData(
            text = Text(""),
            onTextChange = {},
            filterUserInput = { Text(it) },
            label = "HEX",
            placeholder = "000000",
            prefix = "#",
            trailingButton = TrailingButton.Visible(onClick = {}, iconContentDesc = ""),
        )
    )