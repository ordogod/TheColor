package io.github.mmolosay.thecolor.presentation.input

import io.github.mmolosay.thecolor.presentation.input.field.TextFieldData
import io.github.mmolosay.thecolor.presentation.input.field.TextFieldData.Text
import io.github.mmolosay.thecolor.presentation.input.field.TextFieldData.TrailingButton
import io.github.mmolosay.thecolor.presentation.input.field.TextFieldViewModel
import io.github.mmolosay.thecolor.presentation.input.field.TextFieldViewModel.Companion.updateWith
import io.github.mmolosay.thecolor.presentation.input.model.Update
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beOfType
import org.junit.Test

class TextFieldViewModelTest {

    lateinit var sut: TextFieldViewModel

    val dataUpdate: Update<TextFieldData>
        get() = requireNotNull(sut.dataUpdatesFlow.value)

    val data: TextFieldData
        get() = dataUpdate.data

    @Test
    fun `sut is created with null data`() {
        createSut()

        sut.dataUpdatesFlow.value shouldBe null
    }

    @Test
    fun `data is initialized when text is changed by companion`() {
        createSut()

        sut updateWith Text("initial")

        sut.dataUpdatesFlow.value shouldNotBe null
    }

    @Test
    fun `text is updated when test is changed by companion`() {
        createSut()
        sut updateWith Text("initial")

        sut updateWith Text("new")

        data.text shouldBe Text("new")
    }

    @Test
    fun `data update is not caused by user when text is changed by companion`() {
        createSut()

        sut updateWith Text("initial")

        dataUpdate.causedByUser shouldBe false
    }

    @Test
    fun `text is updated when text is changed from UI`() {
        createSut()
        sut updateWith Text("initial")

        data.onTextChange(Text("new"))

        data.text shouldBe Text("new")
    }

    @Test
    fun `data update is caused by user when text is changed from UI`() {
        createSut()
        sut updateWith Text("initial")

        data.onTextChange(Text("new"))

        dataUpdate.causedByUser shouldBe true
    }

    @Test
    fun `trailing button is visible on initialization when text is non-empty`() {
        createSut()

        sut updateWith Text("non-empty text")

        data.trailingButton should beOfType<TrailingButton.Visible>()
    }

    @Test
    fun `trailing button is hidden on initialization when text is empty`() {
        createSut()

        sut updateWith Text("")

        data.trailingButton should beOfType<TrailingButton.Hidden>()
    }

    @Test
    fun `trailing button is visible when text is changed from UI and text is non-empty`() {
        createSut()
        sut updateWith Text("initial")

        data.onTextChange(Text("non-empty text"))

        data.trailingButton should beOfType<TrailingButton.Visible>()
    }

    @Test
    fun `trailing button is hidden when text is changed from UI and text is empty`() {
        createSut()
        sut updateWith Text("initial")

        data.onTextChange(Text(""))

        data.trailingButton should beOfType<TrailingButton.Hidden>()
    }

    @Test
    fun `text is cleared on trailing button click`() {
        createSut()
        sut updateWith Text("initial non-empty text")

        (data.trailingButton as TrailingButton.Visible).onClick()

        data.text shouldBe Text("")
    }

    @Test
    fun `UiData update is caused by user when trailing button is clicked`() {
        createSut()
        sut updateWith Text("initial non-empty text")

        (data.trailingButton as TrailingButton.Visible).onClick()

        dataUpdate.causedByUser shouldBe true
    }

    fun createSut() =
        TextFieldViewModel(
            filterUserInput = { Text(it) },
        ).also {
            sut = it
        }
}