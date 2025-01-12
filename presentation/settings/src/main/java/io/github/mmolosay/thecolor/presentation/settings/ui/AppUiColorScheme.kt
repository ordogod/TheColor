package io.github.mmolosay.thecolor.presentation.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.mmolosay.thecolor.presentation.design.TheColorTheme
import io.github.mmolosay.thecolor.presentation.settings.ui.ItemUiComponents.AnimatedTextValue
import io.github.mmolosay.thecolor.presentation.settings.ui.ItemUiComponents.Description
import io.github.mmolosay.thecolor.presentation.settings.ui.ItemUiComponents.TextValue
import io.github.mmolosay.thecolor.presentation.settings.ui.ItemUiComponents.Title
import io.github.mmolosay.thecolor.presentation.settings.ui.UiComponents.DefaultItemContentPadding
import io.github.mmolosay.thecolor.presentation.settings.ui.UiComponents.DefaultItemValueSpacing

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
internal fun NewAppUiColorSchemeSelection(
    options: List<AppUiColorSchemeOption>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val selectedOption = options.first { it.isSelected }
        Text(
            text = selectedOption.name,
        )

        Spacer(Modifier.height(16.dp))
        val listState = rememberLazyListState()
        val info = listState.layoutInfo
        val horizontalContentPadding = run {
            val size = info.viewportSize
            if (size == IntSize.Zero) return@run null
            val firstVisibleItemInfo = info.visibleItemsInfo.firstOrNull() ?: return@run null
            val firstItemWidth = firstVisibleItemInfo.size
            if (firstItemWidth == 0) return@run null
            val horizontalContentPaddingPx = (size.width / 2) - (firstItemWidth / 2)
            with(LocalDensity.current) { horizontalContentPaddingPx.toDp() }
        }
        val flingBehavior = rememberSnapFlingBehavior(listState, SnapPosition.Center)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(horizontal = horizontalContentPadding ?: 0.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            flingBehavior = flingBehavior,
        ) {
            item {
                TestItem(
                    ordinal = 1,
                )
            }
            item {
                TestItem(
                    ordinal = 2,
                )
            }
            item {
                TestItem(
                    ordinal = 3,
                )
            }
            item {
                TestItem(
                    ordinal = 4,
                )
            }
            item {
                TestItem(
                    ordinal = 5,
                )
            }
            item {
                TestItem(
                    ordinal = 6,
                )
            }
            item {
                TestItem(
                    ordinal = 7,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TestItem(
    ordinal: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(80.dp)
            .height(120.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(percent = 10),
            )
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = ordinal.toString(),
        )
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
        Text(text = option.name)

        Spacer(modifier = Modifier.weight(1f))
        RadioButton(
            selected = option.isSelected,
            onClick = option.onSelect,
        )
    }
}

internal data class AppUiColorSchemeOption(
    val name: String,
    val isSelected: Boolean,
    val onSelect: () -> Unit,
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

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
private fun NewAppUiColorSchemeSelectionPreview() {
    TheColorTheme {
        val options = listOf(
            AppUiColorSchemeOption(
                name = "Light",
                isSelected = true,
                onSelect = {},
            ),
            AppUiColorSchemeOption(
                name = "Dark",
                isSelected = false,
                onSelect = {},
            ),
            AppUiColorSchemeOption(
                name = "Auto (follows system)",
                isSelected = false,
                onSelect = {},
            ),
        )
        NewAppUiColorSchemeSelection(
            options = options,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
private fun AppUiColorSchemeSelectionPreview() {
    TheColorTheme {
        val options = listOf(
            AppUiColorSchemeOption(
                name = "Light",
                isSelected = true,
                onSelect = {},
            ),
            AppUiColorSchemeOption(
                name = "Dark",
                isSelected = false,
                onSelect = {},
            ),
            AppUiColorSchemeOption(
                name = "Auto (follows system)",
                isSelected = false,
                onSelect = {},
            ),
        )
        AppUiColorSchemeSelection(
            options = options,
        )
    }
}