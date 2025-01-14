package io.github.mmolosay.thecolor.presentation.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.mmolosay.debounce.debounced
import io.github.mmolosay.thecolor.domain.model.UserPreferences.asSingletonSet
import io.github.mmolosay.thecolor.domain.model.UserPreferences.isSingleton
import io.github.mmolosay.thecolor.domain.model.UserPreferences.single
import io.github.mmolosay.thecolor.presentation.design.ColorScheme
import io.github.mmolosay.thecolor.presentation.design.DayNightColorSchemeResolver
import io.github.mmolosay.thecolor.presentation.design.Material3DynamicColorsAvailability.areDynamicColorsAvailable
import io.github.mmolosay.thecolor.presentation.design.TheColorTheme
import io.github.mmolosay.thecolor.presentation.design.asColorSchemeResolver
import io.github.mmolosay.thecolor.presentation.design.systemBrightness
import io.github.mmolosay.thecolor.presentation.impl.onlyBottom
import io.github.mmolosay.thecolor.presentation.impl.withoutBottom
import io.github.mmolosay.thecolor.presentation.settings.SettingsData
import io.github.mmolosay.thecolor.presentation.settings.SettingsData.UiColorSchemeOption
import io.github.mmolosay.thecolor.presentation.settings.SettingsUiStrings
import io.github.mmolosay.thecolor.presentation.settings.SettingsViewModel
import io.github.mmolosay.thecolor.presentation.settings.SettingsViewModel.DataState
import kotlin.time.Duration.Companion.milliseconds
import io.github.mmolosay.thecolor.domain.model.ColorInputType as DomainColorInputType
import io.github.mmolosay.thecolor.domain.model.UserPreferences.UiColorScheme as DomainUiColorScheme
import io.github.mmolosay.thecolor.domain.model.UserPreferences.UiColorSchemeSet as DomainUiColorSchemeSet

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navigateBack: () -> Unit,
) {
    val dataState by viewModel.dataStateFlow.collectAsStateWithLifecycle()
    SettingsScreen(
        dataState = dataState,
        navigateBack = navigateBack,
    )
}

@Composable
fun SettingsScreen(
    dataState: DataState,
    navigateBack: () -> Unit,
) {
    when (dataState) {
        is DataState.Loading -> {
            // should promptly change to 'Ready', don't show loading indicator to avoid flashing
            Box(
                modifier = Modifier.fillMaxSize(),
            )
        }
        is DataState.Ready -> {
            SettingsScreen(
                data = dataState.data,
                navigateBack = navigateBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    data: SettingsData,
    navigateBack: () -> Unit,
) {
    val strings = SettingsUiStrings(LocalContext.current)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showResetPreferencesToDefaultDialog by remember { mutableStateOf(false) }
    val dismissResetPreferencesToDefaultDialog: () -> Unit = {
        showResetPreferencesToDefaultDialog = false
    }

    if (showResetPreferencesToDefaultDialog) {
        ResetPreferencesToDefaultAlertDialog(
            onDismissRequest = dismissResetPreferencesToDefaultDialog,
            strings = strings,
            onConfirmClick = {
                data.resetPreferencesToDefault()
                dismissResetPreferencesToDefaultDialog()
            },
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                strings = strings,
                scrollBehavior = scrollBehavior,
                navigateBack = navigateBack,
                onResetPreferencesToDefaultClick = { showResetPreferencesToDefaultDialog = true },
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.withoutBottom(),
    ) { contentPadding ->
        Settings(
            modifier = Modifier
                .padding(contentPadding)
                // consuming 'contentPadding' as window insets isn't needed here
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            data = data,
            strings = strings,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    strings: SettingsUiStrings,
    scrollBehavior: TopAppBarScrollBehavior,
    navigateBack: () -> Unit,
    onResetPreferencesToDefaultClick: () -> Unit,
) {
    val debouncedNavigateBack = remember(navigateBack) {
        debounced(
            action = navigateBack,
            timeout = 1000.milliseconds,
        )
    }
    LargeTopAppBar(
        title = {
            Text(text = strings.topBarTitle)
        },
        navigationIcon = {
            IconButton(
                onClick = debouncedNavigateBack,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = strings.topBarGoBackIconDesc,
                )
            }
        },
        actions = {
            IconButton(
                onClick = onResetPreferencesToDefaultClick,
            ) {
                Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = strings.topBarResetPreferencesToDefaultIconDesc,
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(),
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    data: SettingsData,
    strings: SettingsUiStrings,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item("preferred color input type") {
            var showSelectionDialog by remember { mutableStateOf(false) }
            val options = DomainColorInputType.entries.map { colorInputType ->
                ColorInputTypeOption(
                    name = colorInputType.toUiString(strings),
                    isSelected = (data.preferredColorInputType == colorInputType),
                    onSelect = { data.changePreferredColorInputType(colorInputType) },
                )
            }
            PreferredColorInputType(
                title = strings.itemPreferredColorInputTypeTitle,
                description = strings.itemPreferredColorInputTypeDesc,
                value = data.preferredColorInputType.toUiString(strings),
                onClick = { showSelectionDialog = true },
            )
            if (showSelectionDialog) {
                val windowInsets = BottomSheetDefaults.windowInsets
                /*
                 * TODO: ModalBottomSheet invisible icons in dark status bar
                 *  https://issuetracker.google.com/issues/362539765
                 *  Fixed in androidx.compose.material3:material3:1.4.0-alpha03
                 *  (version at the moment of writing is 1.3.1)
                 */
                /*
                 * TODO: ModalBottomSheet adds light scrim to 3-button navigation bar
                 *  https://issuetracker.google.com/issues/374013416
                 *  Supposedly fixed in androidx.compose.material3:material3:1.4.0-alpha03
                 *  (version at the moment of writing is 1.3.1)
                 */
                ModalBottomSheet(
                    onDismissRequest = { showSelectionDialog = false },
                    contentWindowInsets = { windowInsets.withoutBottom() },
                ) {
                    val bottomWindowInsets = windowInsets.onlyBottom()
                    PreferredColorInputTypeSelection(
                        modifier = Modifier
                            .padding(bottomWindowInsets.asPaddingValues())
                            .consumeWindowInsets(bottomWindowInsets),
                        options = options,
                    )
                }
            }
        }

        item("app ui color scheme") {
            var showSelectionDialog by remember { mutableStateOf(false) }
            val options = data.appUiColorSchemeOptions.map { option ->
                AppUiColorSchemeOption(
                    name = option.colorSchemeSet.toVerboseUiString(strings),
                    currentColorScheme = option.colorSchemeResolver.resolve(
                        brightness = systemBrightness(),
                        useDynamicColorSchemes = true, // TODO: we shouldn't pass this parameter here
                    ),
                    mayResolveInDifferentColorSchemes = !option.colorSchemeSet.isSingleton(),
                    isSelected = (data.appUiColorSchemeSet == option.colorSchemeSet),
                    onSelect = { data.changeAppUiColorSchemeSet(option.colorSchemeSet) },
                )
            }
            AppUiColorScheme(
                title = strings.itemAppUiColorSchemeTitle,
                description = strings.itemAppUiColorSchemeDesc,
                value = data.appUiColorSchemeSet.toShortUiString(strings),
                onClick = { showSelectionDialog = true },
            )
            if (showSelectionDialog) {
                val windowInsets = BottomSheetDefaults.windowInsets
                /*
                 * TODO: ModalBottomSheet invisible icons in dark status bar
                 *  https://issuetracker.google.com/issues/362539765
                 *  Fixed in androidx.compose.material3:material3:1.4.0-alpha03
                 *  (version at the moment of writing is 1.3.1)
                 */
                /*
                 * TODO: ModalBottomSheet adds light scrim to 3-button navigation bar
                 *  https://issuetracker.google.com/issues/374013416
                 *  Supposedly fixed in androidx.compose.material3:material3:1.4.0-alpha03
                 *  (version at the moment of writing is 1.3.1)
                 */
                ModalBottomSheet(
                    onDismissRequest = { showSelectionDialog = false },
                    contentWindowInsets = { windowInsets.withoutBottom() },
                ) {
                    val bottomWindowInsets = windowInsets.onlyBottom()
                    AppUiColorSchemeSelection(
                        modifier = Modifier
                            .padding(bottomWindowInsets.asPaddingValues())
                            .consumeWindowInsets(bottomWindowInsets),
                        options = options,
                    )
                }
            }
        }

        if (areDynamicColorsAvailable()) {
            item("dynamic ui colors") {
                DynamicUiColors(
                    title = strings.itemDynamicUiColorsTitle,
                    description = strings.itemDynamicUiColorsDesc,
                    checked = data.isDynamicUiColorsEnabled,
                    onCheckedChange = data.changeDynamicUiColorsEnablement,
                )
            }
        }

        item("resume from last searched color") {
            ResumeFromLastSearchedColor(
                title = strings.itemResumeFromLastSearchedColorTitle,
                description = strings.itemResumeFromLastSearchedColorDesc,
                checked = data.isResumeFromLastSearchedColorOnStartupEnabled,
                onCheckedChange = data.changeResumeFromLastSearchedColorOnStartupEnablement,
            )
        }

        item("smart backspace") {
            SmartBackspace(
                title = strings.itemSmartBackspaceTitle,
                description = strings.itemSmartBackspaceDesc,
                checked = data.isSmartBackspaceEnabled,
                onCheckedChange = data.changeSmartBackspaceEnablement,
            )
        }

        item("select all text on text field focus") {
            SelectAllTextOnTextFieldFocus(
                title = strings.itemSelectAllTextOnTextFieldFocusTitle,
                description = strings.itemSelectAllTextOnTextFieldFocusDesc,
                checked = data.isSelectAllTextOnTextFieldFocusEnabled,
                onCheckedChange = data.changeSelectAllTextOnTextFieldFocusEnablement,
            )
        }

        item("auto proceed with randomized colors") {
            AutoProceedWithRandomizedColors(
                title = strings.itemAutoProceedWithRandomizedColorsTitle,
                description = strings.itemAutoProceedWithRandomizedColorsDesc,
                checked = data.isAutoProceedWithRandomizedColorsEnabled,
                onCheckedChange = data.changeAutoProceedWithRandomizedColorsEnablement,
            )
        }

        item("spacer for navigation bar") {
            val windowInsets = WindowInsets.systemBars.onlyBottom()
            // visually the spacer will seem bigger due to spacing Arrangement of LazyColumn()
            Spacer(
                modifier = Modifier
                    .padding(windowInsets.asPaddingValues())
                    .consumeWindowInsets(windowInsets)
            )
        }
    }
}

private fun DomainColorInputType.toUiString(
    strings: SettingsUiStrings,
): String =
    when (this) {
        DomainColorInputType.Hex -> strings.itemPreferredColorInputTypeValueHex
        DomainColorInputType.Rgb -> strings.itemPreferredColorInputTypeValueRgb
    }

private fun DomainUiColorSchemeSet.toShortUiString(
    strings: SettingsUiStrings,
): String =
    if (this.isSingleton()) {
        when (this.single()) {
            DomainUiColorScheme.Light -> strings.itemAppUiColorSchemeValueLight
            DomainUiColorScheme.Dark -> strings.itemAppUiColorSchemeValueDark
            DomainUiColorScheme.Jungle -> strings.itemAppUiColorSchemeValueJungle
            DomainUiColorScheme.Midnight -> strings.itemAppUiColorSchemeValueMidnight
        }
    } else {
        when (this) {
            DomainUiColorSchemeSet.DayNight -> strings.itemAppUiColorSchemeValueDayNightShort
            else -> error("Unsupported UI color scheme set")
        }
    }

private fun DomainUiColorSchemeSet.toVerboseUiString(
    strings: SettingsUiStrings,
): String =
    if (this.isSingleton()) {
        when (this.single()) {
            DomainUiColorScheme.Light -> strings.itemAppUiColorSchemeValueLight
            DomainUiColorScheme.Dark -> strings.itemAppUiColorSchemeValueDark
            DomainUiColorScheme.Jungle -> strings.itemAppUiColorSchemeValueJungle
            DomainUiColorScheme.Midnight -> strings.itemAppUiColorSchemeValueMidnight
        }
    } else {
        when (this) {
            DomainUiColorSchemeSet.DayNight -> strings.itemAppUiColorSchemeValueDayNightVerbose
            else -> error("Unsupported UI color scheme set")
        }
    }

@Preview
@Composable
private fun Preview() {
    TheColorTheme {
        SettingsScreen(
            data = previewData(),
            navigateBack = {},
        )
    }
}

private fun previewData() =
    SettingsData(
        resetPreferencesToDefault = {},

        preferredColorInputType = DomainColorInputType.Hex,
        changePreferredColorInputType = {},

        appUiColorSchemeSet = DomainUiColorSchemeSet.DayNight,
        appUiColorSchemeOptions = listOf(
            UiColorSchemeOption(
                colorSchemeSet = DomainUiColorSchemeSet.DayNight,
                colorSchemeResolver = DayNightColorSchemeResolver,
            ),
            UiColorSchemeOption(
                colorSchemeSet = DomainUiColorScheme.Light.asSingletonSet(),
                colorSchemeResolver = ColorScheme.Light.asColorSchemeResolver(),
            ),
            UiColorSchemeOption(
                colorSchemeSet = DomainUiColorScheme.Dark.asSingletonSet(),
                colorSchemeResolver = ColorScheme.Dark.asColorSchemeResolver(),
            ),
        ),
        changeAppUiColorSchemeSet = {},

        isDynamicUiColorsEnabled = true,
        changeDynamicUiColorsEnablement = {},

        isResumeFromLastSearchedColorOnStartupEnabled = true,
        changeResumeFromLastSearchedColorOnStartupEnablement = {},

        isSmartBackspaceEnabled = true,
        changeSmartBackspaceEnablement = {},

        isSelectAllTextOnTextFieldFocusEnabled = true,
        changeSelectAllTextOnTextFieldFocusEnablement = {},

        isAutoProceedWithRandomizedColorsEnabled = true,
        changeAutoProceedWithRandomizedColorsEnablement = {},
    )