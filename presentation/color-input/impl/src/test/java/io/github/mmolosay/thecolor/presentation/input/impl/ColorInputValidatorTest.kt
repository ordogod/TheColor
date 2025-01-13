package io.github.mmolosay.thecolor.presentation.input.impl

import io.github.mmolosay.thecolor.domain.model.Color
import io.github.mmolosay.thecolor.domain.model.ColorPrototype
import io.github.mmolosay.thecolor.domain.usecase.ColorFactory
import io.github.mmolosay.thecolor.presentation.input.api.ColorInput
import io.github.mmolosay.thecolor.presentation.input.api.ColorInputState
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class ColorInputValidatorTest {

    val colorInputMapper: ColorInputMapper = mockk()
    val colorFactory: ColorFactory = mockk()

    val sut: ColorInputValidator = ColorInputValidator(
        colorInputMapper = colorInputMapper,
        colorFactory = colorFactory,
    )

    @ParameterizedTest
    @MethodSource("data")
    fun `validating given 'ColorInput' produces expected 'ColorInputState'`(
        givenColorInput: ColorInput,
        expectedColorInputState: ColorInputState,
        sutDependencies: SutDependencies?,
    ) {
        if (sutDependencies != null) {
            every {
                with(colorInputMapper) { givenColorInput.toPrototype() }
            } returns sutDependencies.prototypeFromColorInputMapper
            every {
                colorFactory.from(prototype = sutDependencies.prototypeFromColorInputMapper)
            } returns sutDependencies.parsedColorFromColorFactory
        }

        val resultState = with(sut) { givenColorInput.validate() }

        resultState shouldBe expectedColorInputState
    }

    data class SutDependencies(
        val prototypeFromColorInputMapper: ColorPrototype,
        val parsedColorFromColorFactory: Color?,
    )

    companion object {

        @JvmStatic
        fun data() = listOf(
            /* #0 */
            TestCase(
                givenColorInput = ColorInput.Hex(""),
                expectedColorInputState = ColorInputState.Invalid(
                    isEmpty = true,
                    isCompleteFromUserPerspective = false,
                ),
            ).asArrayOfAnys(),
            /* #1 */
            TestCase(
                givenColorInput = ColorInput.Hex("0"),
                expectedColorInputState = ColorInputState.Invalid(
                    isEmpty = false,
                    isCompleteFromUserPerspective = false,
                ),
            ).asArrayOfAnys(),
            /* #2 */
            TestCase(
                givenColorInput = ColorInput.Hex("01"),
                expectedColorInputState = ColorInputState.Invalid(
                    isEmpty = false,
                    isCompleteFromUserPerspective = false,
                ),
            ).asArrayOfAnys(),
            /* #3 */
            run {
                val prototypeFromColorInputMapper: ColorPrototype = mockk()
                val parsedColorFromColorFactory: Color = mockk()
                TestCase(
                    givenColorInput = ColorInput.Hex("012"),
                    expectedColorInputState = ColorInputState.Valid(color = parsedColorFromColorFactory),
                    sutDependencies = SutDependencies(
                        prototypeFromColorInputMapper,
                        parsedColorFromColorFactory,
                    ),
                ).asArrayOfAnys()
            },
            /* #4 */
            run {
                val prototypeFromColorInputMapper: ColorPrototype = mockk()
                val parsedColorFromColorFactory: Color? = null
                TestCase(
                    givenColorInput = ColorInput.Hex("012xxx"),
                    expectedColorInputState = ColorInputState.Invalid(
                        isEmpty = false,
                        isCompleteFromUserPerspective = true,
                    ),
                    sutDependencies = SutDependencies(
                        prototypeFromColorInputMapper,
                        parsedColorFromColorFactory,
                    ),
                ).asArrayOfAnys()
            },
            /* #5 */
            run {
                val prototypeFromColorInputMapper: ColorPrototype = mockk()
                val parsedColorFromColorFactory: Color = mockk()
                TestCase(
                    givenColorInput = ColorInput.Hex("012345"),
                    expectedColorInputState = ColorInputState.Valid(color = parsedColorFromColorFactory),
                    sutDependencies = SutDependencies(
                        prototypeFromColorInputMapper,
                        parsedColorFromColorFactory,
                    ),
                ).asArrayOfAnys()
            },
        )

        data class TestCase(
            val givenColorInput: ColorInput,
            val expectedColorInputState: ColorInputState,
            val sutDependencies: SutDependencies? = null,
        )

        fun TestCase.asArrayOfAnys(): Array<Any?> =
            arrayOf(givenColorInput, expectedColorInputState, sutDependencies)
    }
}