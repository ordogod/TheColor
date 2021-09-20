package com.ordolabs.thecolor.util

import androidx.annotation.ColorInt
import com.github.ajalt.colormath.RGB
import com.ordolabs.thecolor.model.InputHexPresentation
import com.ordolabs.thecolor.model.InputRgbPresentation
import android.graphics.Color as ColorAndroid

object ColorUtil {

    /**
     * "Abstract" color model. Actually, holds HEX color value __without number sign__.
     */
    data class Color(
        val hex: String
    ) {
        val hexWithNumberSign: String get() = '#' + this.hex

        override operator fun equals(other: Any?): Boolean {
            other ?: return false
            if (other is String) {
                return (other == hex || other == hexWithNumberSign)
            }
            if (other is Color) {
                return super.equals(other)
            }
            return false
        }

        override fun hashCode(): Int {
            return hex.hashCode()
        }

        companion object
    }

    fun Color.Companion.from(input: InputHexPresentation): Color? {
        input.value ?: return null
        val expanded = when (input.value.length) {
            3 -> input.value.map { it.toString().repeat(2) }.joinToString(separator = "")
            6 -> input.value
            else -> return null
        }
        return Color(hex = expanded)
    }

    fun Color.Companion.from(input: InputRgbPresentation): Color? {
        if (input.r == null || input.g == null || input.b == null) return null
        val rgb = RGB(input.r, input.g, input.b)
        val value = rgb.toHex(withNumberSign = false).uppercase()
        return Color(hex = value)
    }

    fun Color.toColorHex(): InputHexPresentation {
        return InputHexPresentation(value = this.hex)
    }

    fun Color.toColorRgb(): InputRgbPresentation {
        val color = RGB(this.hex)
        return InputRgbPresentation(
            r = color.r,
            g = color.g,
            b = color.b
        )
    }

    @ColorInt
    fun Color.toColorInt(): Int {
        return ColorAndroid.parseColor(this.hexWithNumberSign)
    }
}