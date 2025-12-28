package id.antasari.p6minda_230104040205.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    userName: String,
    onBack: () -> Unit,
    onResetName: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "User Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hello, $userName!",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // TOMBOL RESET NAMA
        Button(
            onClick = onResetName,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Reset Username", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TOMBOL KEMBALI
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Back to Home")
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Minda App Info",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Version 1.0.0 (Practicum P6)",
            style = MaterialTheme.typography.bodySmall
        )
    }
}