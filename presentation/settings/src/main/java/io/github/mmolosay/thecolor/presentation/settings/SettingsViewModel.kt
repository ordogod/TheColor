package io.github.mmolosay.thecolor.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mmolosay.thecolor.domain.model.UserPreferences.asSingletonSet
import io.github.mmolosay.thecolor.domain.repository.UserPreferencesRepository
import io.github.mmolosay.thecolor.domain.usecase.ResetUserPreferencesToDefaultUseCase
import io.github.mmolosay.thecolor.presentation.design.toPresentation
import io.github.mmolosay.thecolor.presentation.settings.SettingsData.UiColorSchemeOption
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import io.github.mmolosay.thecolor.domain.model.ColorInputType as DomainColorInputType
import io.github.mmolosay.thecolor.domain.model.UserPreferences.AutoProceedWithRandomizedColors as DomainAutoProceedWithRandomizedColors
import io.github.mmolosay.thecolor.domain.model.UserPreferences.DynamicUiColors as DomainDynamicUiColors
import io.github.mmolosay.thecolor.domain.model.UserPreferences.ResumeFromLastSearchedColorOnStartup as DomainShouldResumeFromLastSearchedColorOnStartup
import io.github.mmolosay.thecolor.domain.model.UserPreferences.SelectAllTextOnTextFieldFocus as DomainSelectAllTextOnTextFieldFocus
import io.github.mmolosay.thecolor.domain.model.UserPreferences.SmartBackspace as DomainSmartBackspace
import io.github.mmolosay.thecolor.domain.model.UserPreferences.UiColorScheme as DomainUiColorScheme
import io.github.mmolosay.thecolor.domain.model.UserPreferences.UiColorSchemeSet as DomainUiColorSchemeSet

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val resetUserPreferencesToDefault: ResetUserPreferencesToDefaultUseCase,
    @Named("defaultDispatcher") private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    val dataStateFlow: StateFlow<DataState> =
        combine(
            listOf(
                userPreferencesRepository.flowOfColorInputType(),
                userPreferencesRepository.flowOfAppUiColorSchemeSet(),
                userPreferencesRepository.flowOfDynamicUiColors(),
                userPreferencesRepository.flowOfResumeFromLastSearchedColorOnStartup(),
                userPreferencesRepository.flowOfSmartBackspace(),
                userPreferencesRepository.flowOfSelectAllTextOnTextFieldFocus(),
                userPreferencesRepository.flowOfAutoProceedWithRandomizedColors(),
            ),
            transform = ::createData,
        )
            .map { data -> DataState.Ready(data) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DataState.Loading,
            )

    private fun resetPreferencesToDefault() {
        viewModelScope.launch(defaultDispatcher) {
            resetUserPreferencesToDefault()
        }
    }

    private fun updatePreferredColorInputType(value: DomainColorInputType) {
        viewModelScope.launch(defaultDispatcher) {
            userPreferencesRepository.setColorInputType(value)
        }
    }

    private fun updateAppUiColorSchemeSet(value: DomainUiColorSchemeSet) {
        viewModelScope.launch(defaultDispatcher) {
            userPreferencesRepository.setAppUiColorSchemeSet(value)
        }
    }

    private fun updateDynamicUiColorsEnablement(value: Boolean) {
        viewModelScope.launch(defaultDispatcher) {
            val domainModel = DomainDynamicUiColors(value)
            userPreferencesRepository.setDynamicUiColors(domainModel)
        }
    }

    private fun updateResumeFromLastSearchedColorOnStartupEnablement(value: Boolean) {
        viewModelScope.launch(defaultDispatcher) {
            val domainModel = DomainShouldResumeFromLastSearchedColorOnStartup(value)
            userPreferencesRepository.setResumeFromLastSearchedColorOnStartup(domainModel)
        }
    }

    private fun updateSmartBackspaceEnablement(value: Boolean) {
        viewModelScope.launch(defaultDispatcher) {
            val domainModel = DomainSmartBackspace(value)
            userPreferencesRepository.setSmartBackspace(domainModel)
        }
    }

    private fun updateSelectAllTextOnTextFieldFocusEnablement(value: Boolean) {
        viewModelScope.launch(defaultDispatcher) {
            val domainModel = DomainSelectAllTextOnTextFieldFocus(value)
            userPreferencesRepository.setSelectAllTextOnTextFieldFocus(domainModel)
        }
    }

    private fun updateAutoProceedWithRandomizedColorsEnablement(value: Boolean) {
        viewModelScope.launch(defaultDispatcher) {
            val domainModel = DomainAutoProceedWithRandomizedColors(value)
            userPreferencesRepository.setAutoProceedWithRandomizedColors(domainModel)
        }
    }

    // that's the only way to combine() more than 5 flows of different types
    private fun createData(
        userSettings: Array<Any>,
    ): SettingsData {
        val iterator = userSettings.iterator()
        return createData(
            preferredColorInputType = iterator.next() as DomainColorInputType,
            appUiColorSchemeSet = iterator.next() as DomainUiColorSchemeSet,
            dynamicUiColors = iterator.next() as DomainDynamicUiColors,
            shouldResumeFromLastSearchedColorOnStartup = iterator.next()as DomainShouldResumeFromLastSearchedColorOnStartup,
            smartBackspace = iterator.next() as DomainSmartBackspace,
            selectAllTextOnTextFieldFocus = iterator.next() as DomainSelectAllTextOnTextFieldFocus,
            autoProceedWithRandomizedColors = iterator.next() as DomainAutoProceedWithRandomizedColors,
        )
    }

    private fun createData(
        preferredColorInputType: DomainColorInputType,
        appUiColorSchemeSet: DomainUiColorSchemeSet,
        dynamicUiColors: DomainDynamicUiColors,
        shouldResumeFromLastSearchedColorOnStartup: DomainShouldResumeFromLastSearchedColorOnStartup,
        smartBackspace: DomainSmartBackspace,
        selectAllTextOnTextFieldFocus: DomainSelectAllTextOnTextFieldFocus,
        autoProceedWithRandomizedColors: DomainAutoProceedWithRandomizedColors,
    ): SettingsData {
        return SettingsData(
            resetPreferencesToDefault = ::resetPreferencesToDefault,

            preferredColorInputType = preferredColorInputType,
            changePreferredColorInputType = ::updatePreferredColorInputType,

            appUiColorSchemeSet = appUiColorSchemeSet,
            appUiColorSchemeOptions = appUiColorSchemeOptions(),
            changeAppUiColorSchemeSet = ::updateAppUiColorSchemeSet,

            isDynamicUiColorsEnabled = dynamicUiColors.enabled,
            changeDynamicUiColorsEnablement = ::updateDynamicUiColorsEnablement,

            isResumeFromLastSearchedColorOnStartupEnabled = shouldResumeFromLastSearchedColorOnStartup.enabled,
            changeResumeFromLastSearchedColorOnStartupEnablement = ::updateResumeFromLastSearchedColorOnStartupEnablement,

            isSmartBackspaceEnabled = smartBackspace.enabled,
            changeSmartBackspaceEnablement = ::updateSmartBackspaceEnablement,

            isSelectAllTextOnTextFieldFocusEnabled = selectAllTextOnTextFieldFocus.enabled,
            changeSelectAllTextOnTextFieldFocusEnablement = ::updateSelectAllTextOnTextFieldFocusEnablement,

            isAutoProceedWithRandomizedColorsEnabled = autoProceedWithRandomizedColors.enabled,
            changeAutoProceedWithRandomizedColorsEnablement = ::updateAutoProceedWithRandomizedColorsEnablement,
        )
    }

    private fun appUiColorSchemeOptions(): List<UiColorSchemeOption> =
        buildList {
            fun makeOption(colorSchemeSet: DomainUiColorSchemeSet) =
                UiColorSchemeOption(
                    colorSchemeSet = colorSchemeSet,
                    colorSchemeResolver = colorSchemeSet.toPresentation(),
                )

            makeOption(DomainUiColorSchemeSet.DayNight)
                .also { add(it) }
            makeOption(DomainUiColorScheme.Light.asSingletonSet())
                .also { add(it) }
            makeOption(DomainUiColorScheme.Dark.asSingletonSet())
                .also { add(it) }
            makeOption(DomainUiColorScheme.Jungle.asSingletonSet())
                .also { add(it) }
            makeOption(DomainUiColorScheme.Midnight.asSingletonSet())
                .also { add(it) }
        }

    sealed interface DataState {
        data object Loading : DataState
        data class Ready(val data: SettingsData) : DataState
    }
}