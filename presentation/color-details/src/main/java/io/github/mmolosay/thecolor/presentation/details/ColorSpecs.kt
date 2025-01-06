package io.github.mmolosay.thecolor.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.mmolosay.thecolor.presentation.api.ColorInt
import io.github.mmolosay.thecolor.presentation.design.LocalColorsOnTintedSurface
import io.github.mmolosay.thecolor.presentation.design.TheColorTheme
import io.github.mmolosay.thecolor.presentation.design.colorsOnDarkSurface
import io.github.mmolosay.thecolor.presentation.design.colorsOnLightSurface
import io.github.mmolosay.thecolor.presentation.design.colorsOnTintedSurface
import io.github.mmolosay.thecolor.presentation.details.viewmodel.ColorDetailsData
import io.github.mmolosay.thecolor.presentation.impl.toCompose
import io.github.mmolosay.thecolor.presentation.design.R as DesignR

@Composable
internal fun ColorSpecs(
    colorName: String,
    exactMatch: ColorDetailsData.ExactMatch,
    initialColorData: ColorDetailsData.InitialColorData?,
    strings: ColorDetailsUiStrings,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Name(
            label = strings.nameLabel,
            name = colorName,
        )

        ExactMatch(
            exactMatch = exactMatch,
            initialColorData = initialColorData,
            strings = strings,
        )

        if (exactMatch is ColorDetailsData.ExactMatch.No) {
            ExactValue(
                exactMatch = exactMatch,
                strings = strings,
            )
            Deviation(
                label = strings.deviationLabel,
                value = exactMatch.deviation,
            )
        }
    }
}

@Composable
private fun Name(
    label: String,
    name: String,
) {
    Column {
        Label(text = label)
        Value(text = name)
    }
}

@Composable
private fun ExactMatch(
    exactMatch: ColorDetailsData.ExactMatch,
    initialColorData: ColorDetailsData.InitialColorData?,
    strings: ColorDetailsUiStrings,
) {
    val value = when (exactMatch) {
        is ColorDetailsData.ExactMatch.Yes -> strings.exactMatchYes
        is ColorDetailsData.ExactMatch.No -> strings.exactMatchNo
    }
    val goBackToInitialColorButton: (@Composable () -> Unit)? =
        if (initialColorData != null) {
            {
                GoBackToInitialColorButton(
                    onClick = initialColorData.goToInitialColor,
                    text = strings.goBackToInitialColorButtonText,
                    initialColor = initialColorData.initialColor.toCompose(),
                )
            }
        } else null
    ExactMatch(
        label = strings.exactMatchLabel,
        value = value,
        goBackToInitialColorButton = goBackToInitialColorButton,
    )
}

@Composable
private fun ExactMatch(
    label: String,
    value: String,
    goBackToInitialColorButton: (@Composable () -> Unit)?,
) {
    Row {
        Column {
            Label(text = label)
            Value(text = value)
        }
        if (goBackToInitialColorButton != null) {
            Spacer(modifier = Modifier.weight(1f))
            goBackToInitialColorButton()
        }
    }
}

@Composable
private fun GoBackToInitialColorButton(
    onClick: () -> Unit,
    text: String,
    initialColor: Color,
) {
    val colors = ButtonDefaults.outlinedButtonColors(
        contentColor = colorsOnTintedSurface.accent,
    )
    val border = ButtonDefaults.outlinedButtonBorder.copy(
        brush = SolidColor(colorsOnTintedSurface.muted),
    )
    OutlinedButton(
        onClick = onClick,
        colors = colors,
        border = border,
    ) {
        Text(
            text = text,
        )
        Spacer(modifier = Modifier.width(4.dp))
        ColorPreview(
            modifier = Modifier.padding(top = 1.dp),
            color = initialColor,
        )
    }
}

@Composable
private fun ExactValue(
    exactMatch: ColorDetailsData.ExactMatch.No,
    strings: ColorDetailsUiStrings,
) {
    ExactValue(
        label = strings.exactValueLabel,
        exactColorValue = exactMatch.exactValue,
        goToExactColor = exactMatch.goToExactColor,
        exactColor = exactMatch.exactColor.toCompose(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExactValue(
    label: String,
    exactColorValue: String,
    goToExactColor: () -> Unit,
    exactColor: Color,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Label(text = label)
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentEnforcement provides false,
            ) {
                val colors = IconButtonDefaults.iconButtonColors(
                    contentColor = colorsOnTintedSurface.accent,
                )
                IconButton(
                    onClick = goToExactColor,
                    modifier = Modifier.size(20.dp),
                    colors = colors,
                ) {
                    Icon(
                        modifier = Modifier.padding(all = 4.dp),
                        painter = painterResource(DesignR.drawable.ic_open_in_new),
                        contentDescription = stringResource(R.string.color_details_exact_value_icon_content_desc),
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Value(text = exactColorValue)
            ColorPreview(color = exactColor)
        }
    }
}

@Composable
private fun ColorPreview(
    color: Color,
    modifier: Modifier = Modifier,
) =
    Box(
        modifier = modifier
            .size(13.dp)
            .clip(CircleShape)
            .background(color)
    )

@Composable
private fun Deviation(
    label: String,
    value: String,
) {
    Column {
        Label(text = label)
        Value(text = value)
    }
}

@Composable
private fun Label(
    text: String,
) =
    Text(
        text = text,
        color = colorsOnTintedSurface.muted,
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 0.1666.em,
        )
    )

@Composable
private fun Value(
    text: String,
) =
    Text(
        text = text,
        color = colorsOnTintedSurface.accent,
        style = MaterialTheme.typography.bodyLarge,
    )

@Preview(showBackground = true)
@Composable
private fun PreviewLight() {
    TheColorTheme {
        CompositionLocalProvider(
            LocalColorsOnTintedSurface provides colorsOnDarkSurface(),
        ) {
            ColorSpecsWithPreviewData(
                modifier = Modifier.background(Color(0xFF_126B40)),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDark() {
    TheColorTheme {
        CompositionLocalProvider(
            LocalColorsOnTintedSurface provides colorsOnLightSurface(),
        ) {
            ColorSpecsWithPreviewData(
                modifier = Modifier.background(Color(0xFF_F0F8FF)),
            )
        }
    }
}

@Composable
private fun ColorSpecsWithPreviewData(
    modifier: Modifier,
) {
    ColorSpecs(
        modifier = modifier,
        colorName = "Jewel",
        exactMatch = ColorDetailsData.ExactMatch.No(
            exactValue = "#126B40",
            exactColor = ColorInt(0x126B40),
            goToExactColor = {},
            deviation = "1366",
        ),
        initialColorData = ColorDetailsData.InitialColorData(
            initialColor = ColorInt(0x1A803F),
            goToInitialColor = {},
        ),
        strings = ColorDetailsUiStrings(
            hexLabel = "_",
            rgbLabel = "_",
            hslLabel = "_",
            hsvLabel = "_",
            cmykLabel = "_",
            nameLabel = "NAME",
            exactMatchLabel = "EXACT MATCH",
            exactMatchYes = "Yes",
            exactMatchNo = "No",
            goBackToInitialColorButtonText = "Go back to",
            exactValueLabel = "EXACT VALUE",
            deviationLabel = "DEVIATION",
        ),
    )
}