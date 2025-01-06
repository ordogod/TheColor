package io.github.mmolosay.thecolor.presentation.errors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Reusable UI elements for a error View of varying appearance.
 */
/*
 * Notice how this object is not 'internal' as other similar objects across the app.
 * This is done to allow features that want to display custom-looking errors to reuse default elements.
 */
object ErrorUiComponents {

    @Composable
    fun ErrorLayout(
        modifier: Modifier = Modifier,
        message: @Composable () -> Unit,
        button: (@Composable () -> Unit)?,
    ) {
        Column(
            modifier = modifier.defaultMinSize(minHeight = 200.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            message()
            button?.invoke()
        }
    }

    @Composable
    fun Message(
        text: String,
    ) {
        Text(
            text = text,
        )
    }

    @Composable
    fun ActionButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Button(
            modifier = modifier,
            onClick = onClick,
        ) {
            Text(
                text = text,
            )
        }
    }
}