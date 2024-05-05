package io.github.mmolosay.thecolor.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mmolosay.thecolor.domain.failure.HttpFailure
import io.github.mmolosay.thecolor.domain.model.Color
import io.github.mmolosay.thecolor.domain.usecase.GetColorDetailsUseCase
import io.github.mmolosay.thecolor.presentation.ColorCenterCommand
import io.github.mmolosay.thecolor.presentation.ColorCenterCommandProvider
import io.github.mmolosay.thecolor.presentation.ColorToColorIntUseCase
import io.github.mmolosay.thecolor.presentation.details.ColorDetailsData.ExactMatch
import io.github.mmolosay.thecolor.utils.rethrowCancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import io.github.mmolosay.thecolor.domain.model.ColorDetails as DomainColorDetails

@HiltViewModel
class ColorDetailsViewModel @Inject constructor(
    private val commandProvider: ColorCenterCommandProvider,
    private val getColorDetails: GetColorDetailsUseCase,
    private val createData: CreateColorDetailsDataUseCase,
    private val createError: CreateColorDetailsErrorUseCase,
    @Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _dataStateFlow =
        MutableStateFlow<State>(State.Idle) // TODO: inject initial state as in ColorSchemeViewModel for better testing
    val dataStateFlow = _dataStateFlow.asStateFlow()

    init {
        collectColorCenterCommands()
    }

    private fun collectColorCenterCommands() =
        viewModelScope.launch { // TODO: not main dispatcher?
            commandProvider.commandFlow.collect { command ->
                when (command) {
                    is ColorCenterCommand.FetchData -> getColorDetails(color = command.color)
                }
            }
        }

    private fun getColorDetails(color: Color) {
        _dataStateFlow.value = State.Loading
        viewModelScope.launch(ioDispatcher) {
            getColorDetails.invoke(color)
                .rethrowCancellationException()
                .onSuccess { details ->
                    val data = createData(details)
                    _dataStateFlow.value = State.Ready(data)
                }
                .onFailure { exception ->
                    val error = createError(exception)
                    _dataStateFlow.value = State.Error(error)
                }
        }
    }


    /** Depicts possible states of color details data. */
    sealed interface State {
        data object Idle : State
        data object Loading : State
        data class Ready(val data: ColorDetailsData) : State
        data class Error(val error: ColorDetailsError) : State
    }
}

@Singleton
/* private but Dagger */
class CreateColorDetailsDataUseCase @Inject constructor(
    private val colorToColorInt: ColorToColorIntUseCase,
) {

    operator fun invoke(details: DomainColorDetails) =
        ColorDetailsData(
            colorName = details.colorName,
            hex = ColorDetailsData.Hex(value = details.colorHexString.withNumberSign),
            rgb = ColorDetailsData.Rgb(
                r = details.colorTranslations.rgb.standard.r.toString(),
                g = details.colorTranslations.rgb.standard.g.toString(),
                b = details.colorTranslations.rgb.standard.b.toString(),
            ),
            hsl = ColorDetailsData.Hsl(
                h = details.colorTranslations.hsl.standard.h.toString(),
                s = details.colorTranslations.hsl.standard.s.toString(),
                l = details.colorTranslations.hsl.standard.l.toString(),
            ),
            hsv = ColorDetailsData.Hsv(
                h = details.colorTranslations.hsv.standard.h.toString(),
                s = details.colorTranslations.hsv.standard.s.toString(),
                v = details.colorTranslations.hsv.standard.v.toString(),
            ),
            cmyk = ColorDetailsData.Cmyk(
                c = details.colorTranslations.cmyk.standard.c.toString(),
                m = details.colorTranslations.cmyk.standard.m.toString(),
                y = details.colorTranslations.cmyk.standard.y.toString(),
                k = details.colorTranslations.cmyk.standard.k.toString(),
            ),
            exactMatch = ExactMatch(details),
        )

    private fun ExactMatch(details: DomainColorDetails): ExactMatch =
        if (details.matchesExact) {
            ExactMatch.Yes
        } else {
            ExactMatch.No(
                exactValue = details.exact.hexStringWithNumberSign,
                exactColor = with(colorToColorInt) { details.exact.color.toColorInt() },
                onExactClick = { /* TODO */ },
                deviation = details.distanceFromExact.toString(),
            )
        }
}

@Singleton
/* private but Dagger */
class CreateColorDetailsErrorUseCase @Inject constructor() {

    operator fun invoke(exception: Throwable): ColorDetailsError {
        val errorType = exception.asErrorTypeOrNull()
        return ColorDetailsError(type = errorType)
    }

    private fun Throwable.asErrorTypeOrNull(): ColorDetailsError.Type? =
        when (this) {
            is HttpFailure.UnknownHost -> ColorDetailsError.Type.NoConnection
            is HttpFailure.Timeout -> ColorDetailsError.Type.Timeout
            is HttpFailure.ErrorResponse -> ColorDetailsError.Type.ErrorResponse
            else -> null
        }
}