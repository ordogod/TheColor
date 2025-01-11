package io.github.mmolosay.thecolor.presentation.settings.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mmolosay.thecolor.presentation.design.ColorScheme
import io.github.mmolosay.thecolor.presentation.design.TheColorTheme
import io.github.mmolosay.thecolor.presentation.design.toMaterialColorScheme
import io.github.mmolosay.thecolor.presentation.settings.ui.ItemUiComponents.AnimatedTextValue
import io.github.mmolosay.thecolor.presentation.settings.ui.ItemUiComponents.Description
import io.github.mmolosay.thecolor.presentation.settings.ui.ItemUiComponents.TextValue
import io.github.mmolosay.thecolor.presentation.settings.ui.ItemUiComponents.Title
import io.github.mmolosay.thecolor.presentation.settings.ui.UiComponents.DefaultItemContentPadding
import io.github.mmolosay.thecolor.presentation.settings.ui.UiComponents.DefaultItemValueSpacing
import kotlin.math.sqrt

@Composable
internal fun AppUiColorScheme(
    title: String,
    description: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
    ) {
        Row(
            modifier = modifier
                .padding(DefaultItemContentPadding)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Title(text = title)
                Description(text = description)
            }

            Spacer(modifier = Modifier.width(DefaultItemValueSpacing))
            Box(
                modifier = Modifier.align(Alignment.CenterVertically),
            ) {
                AnimatedTextValue(
                    targetValue = value,
                ) { targetValue ->
                    TextValue(
                        text = targetValue,
                    )
                }
            }
        }
    }
}

@Composable
internal fun AppUiColorSchemeSelection(
    options: List<AppUiColorSchemeOption>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.selectableGroup(),
    ) {
        options.forEach { option ->
            Option(option)
        }
    }
}

@Composable
private fun Option(
    option: AppUiColorSchemeOption,
) {
    Row(
        modifier = Modifier
            .selectable(
                selected = option.isSelected,
                onClick = option.onSelect,
                role = Role.RadioButton,
            )
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OptionPreview(colorScheme = option.colorScheme)

        Spacer(Modifier.width(12.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = option.name,
        )

        RadioButton(
            selected = option.isSelected,
            onClick = option.onSelect,
        )
    }
}

@Composable
private fun OptionPreview(
    colorScheme: ColorScheme,
) {
    val context = LocalContext.current
    val materialColorScheme = colorScheme.toMaterialColorScheme(context)
    val outer = materialColorScheme.primary
    val inner = materialColorScheme.background
    Canvas(
        modifier = Modifier.size(24.dp),
    ) {
        val outerDiameter = size.minDimension
        val outerRadius = outerDiameter / 2
        drawCircle(
            color = outer,
            radius = outerRadius,
        )
        val innerDiameter = outerDiameter / sqrt(2f) // looks good
        val innerRadius = innerDiameter / 2
        drawCircle(
            color = inner,
            radius = innerRadius,
        )
    }
}

internal data class AppUiColorSchemeOption(
    val name: String,
    val isSelected: Boolean,
    val onSelect: () -> Unit,
    val colorScheme: ColorScheme,
)

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
private fun AppUiColorSchemePreview() {
    TheColorTheme {
        AppUiColorScheme(
            title = "UI theme",
            description = "A color scheme of the application.",
            value = "Auto",
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun AppUiColorSchemeSelectionPreviewLight() {
    TheColorTheme(colorScheme = ColorScheme.Light) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppUiColorSchemeSelection(
                options = previewOptions(),
            )
        }
    }
}

@Preview
@Composable
private fun AppUiColorSchemeSelectionPreviewDark() {
    TheColorTheme(colorScheme = ColorScheme.Dark) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppUiColorSchemeSelection(
                options = previewOptions(),
            )
        }
    }
}

private fun previewOptions() =
    listOf(
        AppUiColorSchemeOption(
            name = "Auto (follows system)",
            isSelected = false,
            onSelect = {},
            colorScheme = ColorScheme.Light,
        ),
        AppUiColorSchemeOption(
            name = "Light",
            isSelected = true,
            onSelect = {},
            colorScheme = ColorScheme.Light,
        ),
        AppUiColorSchemeOption(
            name = "Dark",
            isSelected = false,
            onSelect = {},
            colorScheme = ColorScheme.Dark,
        ),
        AppUiColorSchemeOption(
            name = "Jungle",
            isSelected = false,
            onSelect = {},
            colorScheme = ColorScheme.Jungle,
        ),
        AppUiColorSchemeOption(
            name = "Midnight",
            isSelected = false,
            onSelect = {},
            colorScheme = ColorScheme.Midnight,
        ),
    )