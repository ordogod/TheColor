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
import androidx.compose.foundation.lazy.LazyListLayoutInfo
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs

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
        val coroutineScope = rememberCoroutineScope()
        val density = LocalDensity.current
        val indexOfSelectedOption = options.indexOfFirst { it.isSelected }
        val selectedOption = options[indexOfSelectedOption]

        Text(
            text = selectedOption.name,
        )

        Spacer(Modifier.height(16.dp))
        Box(
            contentAlignment = Alignment.Center,
        ) {
            SelectedItemIndicator()

            val listState = rememberLazyListState()
            val layoutInfo = listState.layoutInfo
            val horizontalContentPadding = layoutInfo.calculateHorizontalContentPadding(density)
            val flingBehavior = rememberSnapFlingBehavior(listState, SnapPosition.Center)
            var indexOfSnappedListItem by remember { mutableStateOf<Int?>(null) }
            val isUiCalculated = (horizontalContentPadding != null)
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
                // so waiting for it to be calculated and set first
                if (horizontalContentPadding == null) return@LaunchedEffect
                listState.scrollToItem(index = indexOfSelectedOption)
            }

            LaunchedEffect(listState, isUiCalculated) {
                if (!isUiCalculated) return@LaunchedEffect
                snapshotFlow { listState.isScrollInProgress }
                    .distinctUntilChanged()
                    .collect { isScrollInProgress ->
                        if (isScrollInProgress) return@collect
                        val viewportCenter = listState.layoutInfo.viewportSize.width / 2
                        val itemClosestToCenter =
                            listState.layoutInfo.visibleItemsInfo.minByOrNull { itemInfo ->
                                val contentPadding = listState.layoutInfo.beforeContentPadding
                                val itemCenter =
                                    contentPadding + itemInfo.offset + (itemInfo.size / 2)
                                val distanceToCenter = abs(itemCenter - viewportCenter)
                                distanceToCenter
                            }
                        val indexOfCenteredItem = itemClosestToCenter?.index
                        indexOfSnappedListItem = indexOfCenteredItem
                    }
            }

            LaunchedEffect(indexOfSnappedListItem) {
                // { i -> i } mapping because all items of the list are options
                val indexOfOption = indexOfSnappedListItem ?: return@LaunchedEffect
                val option = options[indexOfOption]
                option.onSelect()
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun LazyListLayoutInfo.calculateHorizontalContentPadding(
    density: Density,
): Dp? {
    val viewportSize = this.viewportSize
    val firstVisibleItemWidth = this.visibleItemsInfo.firstOrNull()?.size
    return remember(viewportSize, firstVisibleItemWidth) calc@{
        if (viewportSize == IntSize.Zero) return@calc null // not composed / measured yet
        if (firstVisibleItemWidth == null) return@calc null // not composed / measured yet
        if (firstVisibleItemWidth == 0) return@calc null // not measured yet
        // assuming first and last items in list have the same size, and thus both
        // start and end paddings are the same
        val horizontalContentPaddingPx = (viewportSize.width / 2) - (firstVisibleItemWidth / 2)
        with(density) { horizontalContentPaddingPx.toDp() }
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
        val width = 80.dp
        val height = width * 1.32f
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