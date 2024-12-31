package io.github.mmolosay.thecolor.data

import io.github.mmolosay.thecolor.data.remote.mapper.ColorMapper
import io.github.mmolosay.thecolor.domain.model.Color
import io.github.mmolosay.thecolor.domain.usecase.ColorConverter
import io.kotest.assertions.withClue
import io.kotest.matchers.floats.shouldBeLessThan
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.abs

@RunWith(Enclosed::class)
object GetColorLightnessUseCaseImplTests {

    abstract class BaseClass {
        val colorConverter: ColorConverter = ColorConverter()
        val colorMapper: ColorMapper = ColorMapper()

        val sut = GetColorLightnessUseCaseImpl(
            colorConverter = colorConverter,
            colorMapper = colorMapper,
        )

        infix fun Float.shouldBeInEqualityRangeWith(other: Float) {
            val delta = abs(this - other)
            val threshold = 0.0001f
            withClue("$this should be in equality range with $other") {
                delta shouldBeLessThan threshold
            }
        }
    }

    @RunWith(Parameterized::class)
    class HslLightnessTests(
        val color: Color,
        val expectedLightness: Float,
    ) : BaseClass() {

        @Test
        fun `color lightness in HSL is as expected`() {
            val lightness = with(sut) { color.hslLightness() }

            lightness shouldBeInEqualityRangeWith expectedLightness
        }

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data() = listOf(
                /* #0  */ Color.Hex(0xFFFFFF) shouldHaveLightness 1.0000f,
                /* #1  */ Color.Hex(0x000000) shouldHaveLightness 0.0000f,
                /* #2  */ Color.Hex(0xF0F8FF) shouldHaveLightness 0.9706f,
                /* #3  */ Color.Hex(0x123456) shouldHaveLightness 0.2039f,
                /* #4  */ Color.Hex(0x1A803F) shouldHaveLightness 0.3020f,
                /* #5  */ Color.Hex(0xF54021) shouldHaveLightness 0.5451f,
                /* #6  */ Color.Hex(0xF3A505) shouldHaveLightness 0.4863f,
                /* #7  */ Color.Hex(0xC0E904) shouldHaveLightness 0.4647f,
            )

            infix fun Color.shouldHaveLightness(expectedLightness: Float): Array<Any> =
                arrayOf(this, expectedLightness)
        }
    }

    @RunWith(Parameterized::class)
    class LabLightnessTests(
        val color: Color,
        val expectedLightness: Float,
    ) : BaseClass() {

        @Test
        fun `color lightness in LAB is as expected`() {
            val lightness = with(sut) { color.labLightness() }

            lightness shouldBeInEqualityRangeWith expectedLightness
        }

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data() = listOf(
                /* #0  */ Color.Hex(0xFFFFFF) shouldHaveLightness 1.0000f,
                /* #1  */ Color.Hex(0x000000) shouldHaveLightness 0.0000f,
                /* #2  */ Color.Hex(0xF0F8FF) shouldHaveLightness 0.9718f,
                /* #3  */ Color.Hex(0x123456) shouldHaveLightness 0.2104f,
                /* #4  */ Color.Hex(0x1A803F) shouldHaveLightness 0.4700f,
                /* #5  */ Color.Hex(0xF54021) shouldHaveLightness 0.5527f,
                /* #6  */ Color.Hex(0xF3A505) shouldHaveLightness 0.7353f,
                /* #7  */ Color.Hex(0xC0E904) shouldHaveLightness 0.8675f,
            )

            infix fun Color.shouldHaveLightness(expectedLightness: Float): Array<Any> =
                arrayOf(this, expectedLightness)
        }
    }
}