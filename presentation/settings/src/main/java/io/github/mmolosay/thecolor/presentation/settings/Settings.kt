package io.github.mmolosay.thecolor.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/*
 * Work in progress
 */

@Composable
fun Settings(
    viewModel: SettingsViewModel,
    navigateToHome: () -> Unit,
) {
    Settings(
        data = viewModel.dataFlow.collectAsStateWithLifecycle().value,
        navEvent = viewModel.navEventFlow.collectAsStateWithLifecycle().value,
        navigateToHome = navigateToHome,
    )
}

@Composable
fun Settings(
    data: SettingsData,
    navEvent: SettingsNavEvent?,
    navigateToHome: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            Text(text = "Settings")
            Button(onClick = data.goToHome) {
                Text(text = "Go home")
            }
        }
    }

    LaunchedEffect(navEvent) {
        val event = navEvent ?: return@LaunchedEffect
        when (event) {
            is SettingsNavEvent.GoToHome -> navigateToHome()
        }
        event.onConsumed()
    }
}