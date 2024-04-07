package io.github.mmolosay.thecolor.presentation.home

import io.github.mmolosay.thecolor.presentation.toCompose

fun HomeUiData(
    data: HomeData,
    viewData: HomeUiData.ViewData,
) =
    HomeUiData(
        topBar = TopBar(data, viewData),
        headline = viewData.headline,
        proceedButton = ProceedButton(data, viewData),
        showColorCenter = ShowColorCenter(data.colorUsedToProceed),
        navEvent = data.navEvent,
    )

private fun TopBar(
    data: HomeData,
    viewData: HomeUiData.ViewData,
) =
    HomeUiData.TopBar(
        settingsAction = HomeUiData.TopBar.SettingsAction(
            onClick = data.goToSettings,
            iconContentDescription = viewData.settingsIconContentDesc,
        )
    )

private fun ShowColorCenter(data: HomeData.ColorData?) =
    when (data != null) {
        true -> HomeUiData.ShowColorCenter.Yes(
            backgroundColor = data.color.toCompose(),
            useLightContentColors = data.isDark,
        )
        false -> HomeUiData.ShowColorCenter.No
    }

private fun ProceedButton(
    data: HomeData,
    viewData: HomeUiData.ViewData,
) =
    HomeUiData.ProceedButton(
        onClick = data.canProceed.actionOrNoop(),
        enabled = data.canProceed is HomeData.CanProceed.Yes,
        text = viewData.proceedButtonText,
    )

private fun HomeData.CanProceed.actionOrNoop(): () -> Unit =
    when (this) {
        is HomeData.CanProceed.Yes -> action
        is HomeData.CanProceed.No -> {
            {} // disabled button isn't clickable, thus no-op won't ever be invoked
        }
    }