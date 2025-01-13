package io.github.mmolosay.thecolor.presentation.input.impl

import io.github.mmolosay.thecolor.domain.repository.UserPreferencesRepository
import io.github.mmolosay.thecolor.presentation.input.impl.ColorInputViewModel.DataState
import io.github.mmolosay.thecolor.testing.MainDispatcherExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import io.github.mmolosay.thecolor.domain.model.ColorInputType as DomainColorInputType

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
class ColorInputViewModelTest {

    val testDispatcher = UnconfinedTestDispatcher()

    val mediator: ColorInputMediator = mockk()
    val userPreferencesRepository: UserPreferencesRepository = mockk()

    lateinit var sut: ColorInputViewModel

    @Test
    fun `initial data is set on initialization`() {
        every {
            userPreferencesRepository.flowOfColorInputType()
        } returns flowOf(DomainColorInputType.Hex)

        createSut()

        data.selectedInputType shouldBe DomainColorInputType.Hex
    }

    @Test
    fun `preferred input type is an initially selected one`() {
        every {
            userPreferencesRepository.flowOfColorInputType()
        } returns flowOf(DomainColorInputType.Rgb)

        createSut()

        data.selectedInputType shouldBe DomainColorInputType.Rgb
    }

    @Test
    fun `preferred input type is first in the ordered list of input types`() {
        every {
            userPreferencesRepository.flowOfColorInputType()
        } returns flowOf(DomainColorInputType.Rgb)

        createSut()

        data.orderedInputTypes.first() shouldBe DomainColorInputType.Rgb
    }

    @Test
    fun `changing input type to RGB updates data with RGB view type`() {
        every {
            userPreferencesRepository.flowOfColorInputType()
        } returns flowOf(DomainColorInputType.Hex)
        createSut()

        data.onInputTypeChange(DomainColorInputType.Rgb)

        data.selectedInputType shouldBe DomainColorInputType.Rgb
    }

    fun createSut() =
        ColorInputViewModel(
            coroutineScope = CoroutineScope(context = testDispatcher),
            eventStore = mockk(),
            mediator = mediator,
            hexViewModelFactory = { _, _, _ -> mockk() },
            rgbViewModelFactory = { _, _, _ -> mockk() },
            userPreferencesRepository = userPreferencesRepository,
            defaultDispatcher = testDispatcher,
        ).also {
            sut = it
        }

    val data: ColorInputData
        get() = sut.dataStateFlow.value.shouldBeInstanceOf<DataState.Ready>().data
}