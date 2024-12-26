package io.github.mmolosay.thecolor.presentation.input.impl

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.mmolosay.thecolor.presentation.design.TheColorTheme
import io.github.mmolosay.thecolor.presentation.input.impl.ColorInputViewModel.DataState
import io.github.mmolosay.thecolor.presentation.input.impl.field.TextFieldData
import io.github.mmolosay.thecolor.presentation.input.impl.field.TextFieldUiStrings
import io.github.mmolosay.thecolor.presentation.input.impl.hex.ColorInputHex
import io.github.mmolosay.thecolor.presentation.input.impl.hex.ColorInputHexData
import io.github.mmolosay.thecolor.presentation.input.impl.hex.ColorInputHexUiStrings
import io.github.mmolosay.thecolor.presentation.input.impl.rgb.ColorInputRgb
import io.github.mmolosay.thecolor.presentation.input.impl.rgb.ColorInputRgbData
import io.github.mmolosay.thecolor.presentation.input.impl.rgb.ColorInputRgbUiStrings
import io.github.mmolosay.thecolor.utils.doNothing
import io.github.mmolosay.thecolor.domain.model.ColorInputType as DomainColorInputType

@Composable
fun ColorInput(
    viewModel: ColorInputViewModel,
) {
    val context = LocalContext.current
    val strings = remember(context) { ColorInputUiStrings(context) }
    val dataState = viewModel.dataStateFlow.collectAsStateWithLifecycle().value
    when (dataState) {
        is DataState.Loading -> {
            // should promptly change to 'Ready', don't show loading indicator to avoid flashing
            doNothing()
        }
        is DataState.Ready -> {
            ColorInput(
                data = dataState.data,
                strings = strings,
                hexInput = {
                    ColorInputHex(viewModel = viewModel.hexViewModel)
                },
                rgbInput = {
                    ColorInputRgb(viewModel = viewModel.rgbViewModel)
                },
            )
        }
    }
}

@Composable
fun ColorInput(
    data: ColorInputData,
    strings: ColorInputUiStrings,
    hexInput: @Composable () -> Unit,
    rgbInput: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Crossfade(
            targetState = data.selectedInputType,
            label = "Input type cross-fade",
        ) { type ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
            ) {
                when (type) {
                    DomainColorInputType.Hex -> hexInput()
                    DomainColorInputType.Rgb -> rgbInput()
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        InputSelector(
            data = data,
            strings = strings,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputSelector(
    data: ColorInputData,
    strings: ColorInputUiStrings,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentEnforcement provides false,
        ) {
            data.orderedInputTypes.forEach { type ->
                val isSelected = (type == data.selectedInputType)
                val contentColor = LocalContentColor.current
                val colors = FilterChipDefaults.filterChipColors(
                    labelColor = contentColor.copy(alpha = 0.60f),
                    // selectedLabelColor as default
                )
                val border = FilterChipDefaults.filterChipBorder(
                    enabled = true, // constant
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.60f),
                    // selectedBorderColor doesn't matter because it has 0 width
                )
                FilterChip(
                    selected = isSelected,
                    onClick = { data.onInputTypeChange(type) },
                    label = {
                        val labelText = type.label(strings)
                        ChipLabel(text = labelText)
                    },
                    colors = colors,
                    border = border,
                )
            }
        }
    }
}

@Composable
private fun ChipLabel(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelMedium,
    )
}

private fun DomainColorInputType.label(strings: ColorInputUiStrings): String =
    when (this) {
        DomainColorInputType.Hex -> strings.hexLabel
        DomainColorInputType.Rgb -> strings.rgbLabel
    }

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TheColorTheme {
        ColorInput(
            data = previewData(),
            strings = previewUiStrings(),
            hexInput = {
                ColorInputHex(
                    data = previewHexData(),
                    strings = previewHexUiStrings(),
                )
            },
            rgbInput = {
                ColorInputRgb(
                    data = previewRgbData(),
                    strings = previewRgbUiStrings(),
                )
            },
        )
    }
}

private fun previewData() =
    ColorInputData(
        selectedInputType = DomainColorInputType.Hex,
        orderedInputTypes = listOf(
            DomainColorInputType.Hex,
            DomainColorInputType.Rgb,
        ),
        onInputTypeChange = {},
    )

private fun previewUiStrings() =
    ColorInputUiStrings(
        hexLabel = "HEX",
        rgbLabel = "RGB",
    )

private fun previewHexData() =
    ColorInputHexData(
        textField = TextFieldData(
            text = TextFieldData.Text(""),
            onTextChange = {},
            filterUserInput = { TextFieldData.Text(it) },
            trailingButton = TextFieldData.TrailingButton.Visible(
                onClick = {},
            ),
            shouldSelectAllTextOnFocus = false,
        ),
        submitColor = {},
    )

private fun previewHexUiStrings() =
    ColorInputHexUiStrings(
        textField = TextFieldUiStrings(
            label = "HEX",
            placeholder = "000000",
            prefix = "#",
            trailingIconContentDesc = "Clear text",
        ),
    )

private fun previewRgbData() =
    ColorInputRgbData(
        rTextField = TextFieldData(
            text = TextFieldData.Text(""),
            onTextChange = {},
            filterUserInput = { TextFieldData.Text(it) },
            trailingButton = TextFieldData.TrailingButton.Hidden,
            shouldSelectAllTextOnFocus = false,
        ),
        gTextField = TextFieldData(
            text = TextFieldData.Text(""),
            onTextChange = {},
            filterUserInput = { TextFieldData.Text(it) },
            trailingButton = TextFieldData.TrailingButton.Hidden,
            shouldSelectAllTextOnFocus = false,
        ),
        bTextField = TextFieldData(
            text = TextFieldData.Text(""),
            onTextChange = {},
            filterUserInput = { TextFieldData.Text(it) },
            trailingButton = TextFieldData.TrailingButton.Hidden,
            shouldSelectAllTextOnFocus = false,
        ),
        submitColor = {},
        isSmartBackspaceEnabled = true,
    )

private fun previewRgbUiStrings() =
    ColorInputRgbUiStrings(
        rTextField = TextFieldUiStrings(
            label = "R",
            placeholder = "0",
            prefix = null,
            trailingIconContentDesc = null,
        ),
        gTextField = TextFieldUiStrings(
            label = "G",
            placeholder = "0",
            prefix = null,
            trailingIconContentDesc = null,
        ),
        bTextField = TextFieldUiStrings(
            label = "B",
            placeholder = "0",
            prefix = null,
            trailingIconContentDesc = null,
        ),
    )