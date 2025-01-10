package io.github.mmolosay.thecolor.presentation.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.mmolosay.thecolor.presentation.design.TheColorTheme
import io.github.mmolosay.thecolor.presentation.settings.ui.ItemUiComponents.Description
import io.github.mmolosay.thecolor.presentation.settings.ui.ItemUiComponents.Title
import io.github.mmolosay.thecolor.presentation.settings.ui.UiComponents.DefaultItemContentPadding
import io.github.mmolosay.thecolor.presentation.settings.ui.UiComponents.DefaultItemValueSpacing

@Composable
internal fun DynamicUiColors(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface {
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
            Switch(
                modifier = Modifier.align(Alignment.CenterVertically),
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
private fun DynamicUiColorsPreview() {
    TheColorTheme {
        DynamicUiColors(
            title = "Dynamic colors for UI theme",
            description = "Color scheme will be based on your wallpaper.",
            checked = true,
            onCheckedChange = {},
        )
    }
}