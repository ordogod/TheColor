package io.github.mmolosay.thecolor.presentation.settings.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

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
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val indexOfSelectedOption = options.indexOfFirst { it.isSelected }
        val selectedOption = options.first { it.isSelected }
        Text(
            text = selectedOption.name,
        )

        Spacer(Modifier.height(16.dp))
        Box(
            contentAlignment = Alignment.Center,
        ) {
            SelectedItemIndicator()

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
            val coroutineScope = rememberCoroutineScope()
            val flingBehavior = rememberSnapFlingBehavior(listState, SnapPosition.Center)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                state = listState,
                contentPadding = PaddingValues(horizontal = horizontalContentPadding ?: 0.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                flingBehavior = flingBehavior,
            ) {
                itemsIndexed(options) { index, option ->
                    Option(
                        option = option,
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                                option.onSelect()
                            }
                        },
                    )
                }
            }

            LaunchedEffect(horizontalContentPadding) {
                // seems like changing 'contentPadding' affects scrolling,
                //  so waiting for it to be calculated and set first
                if (horizontalContentPadding == null) return@LaunchedEffect
                listState.scrollToItem(index = indexOfSelectedOption)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SelectedItemIndicator() {
    val borderWidth = 2.dp
    val contentPadding = 6.dp
    val cornerSize = OptionItemUi.CornerSize + contentPadding + borderWidth
    Box(
        modifier = Modifier
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(cornerSize),
            )
            .padding(all = borderWidth)
            .padding(all = contentPadding)
            .size(OptionItemUi.Size),
    )
}



@Composable
private fun Option(
    option: AppUiColorSchemeOption,
    onClick: () -> Unit,
) {
    val color = option.currentColorScheme.toMaterialColorScheme(LocalContext.current).surface
    Surface(
        modifier = Modifier.size(OptionItemUi.Size),
        selected = option.isSelected,
        onClick = onClick,
        shape = RoundedCornerShape(OptionItemUi.CornerSize),
        color = color,
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.20f),
        ),
    ) {}
}

private object OptionItemUi {
    val Size = run {
        val goldenRatio = 1.618f
        val width = 80.dp
        val height = width * goldenRatio
        DpSize(width, height)
    }
    val CornerSize = 8.dp
}

internal data class AppUiColorSchemeOption(
    val name: String,
    val currentColorScheme: ColorScheme,
    val mayResolveInDifferentColorSchemes: Boolean,
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
private fun AppUiColorSchemeSelectionPreview() {
    TheColorTheme {
        val options = listOf(
            AppUiColorSchemeOption(
                name = "Auto (follows system)",
                currentColorScheme = ColorScheme.Light,
                mayResolveInDifferentColorSchemes = true,
                isSelected = false,
                onSelect = {},
            ),
            AppUiColorSchemeOption(
                name = "Light",
                currentColorScheme = ColorScheme.Light,
                mayResolveInDifferentColorSchemes = false,
                isSelected = true,
                onSelect = {},
            ),
            AppUiColorSchemeOption(
                name = "Dark",
                currentColorScheme = ColorScheme.Dark,
                mayResolveInDifferentColorSchemes = false,
                isSelected = false,
                onSelect = {},
            ),
            AppUiColorSchemeOption(
                name = "Jungle",
                currentColorScheme = ColorScheme.Jungle,
                mayResolveInDifferentColorSchemes = false,
                isSelected = false,
                onSelect = {},
            ),
            AppUiColorSchemeOption(
                name = "Midnight",
                currentColorScheme = ColorScheme.Midnight,
                mayResolveInDifferentColorSchemes = false,
                isSelected = false,
                onSelect = {},
            ),
        )
        AppUiColorSchemeSelection(
            options = options,
        )
    }
}