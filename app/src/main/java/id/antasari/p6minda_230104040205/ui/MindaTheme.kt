package id.antasari.p6minda_230104040205.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MindaColorScheme = lightColorScheme()

@Composable
fun MindaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MindaColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}