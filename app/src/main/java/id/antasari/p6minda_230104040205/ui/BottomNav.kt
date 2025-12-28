package id.antasari.p6minda_230104040205.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
// Pastikan import Routes ini sesuai dengan letak file Routes.kt kamu
import id.antasari.p6minda_230104040205.ui.navigation.Routes

// 1. Model data untuk item navigasi
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

// 2. Daftar item menggunakan konstanta Routes agar konsisten
private val bottomItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Filled.Home),
    BottomNavItem(Routes.CALENDAR, "Calendar", Icons.Filled.CalendarMonth),
    BottomNavItem(Routes.INSIGHTS, "Insights", Icons.Filled.BarChart),
    BottomNavItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings)
)

// 3. Komponen Utama Bottom Navigation Bar
@Composable
fun BottomNavBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Destructuring untuk mengambil item berdasarkan urutan list
        val (homeItem, calendarItem, insightsItem, settingsItem) = bottomItems

        // SISI KIRI (Home & Calendar)
        BottomNavButton(
            item = homeItem,
            selected = currentRoute == homeItem.route,
            onClick = { navigateToTab(navController, homeItem.route, currentRoute) },
            modifier = Modifier.weight(1f).padding(start = 10.dp)
        )
        BottomNavButton(
            item = calendarItem,
            selected = currentRoute == calendarItem.route,
            onClick = { navigateToTab(navController, calendarItem.route, currentRoute) },
            modifier = Modifier.weight(1f).padding(start = 10.dp)
        )

        // SLOT TENGAH (Ruang kosong untuk Floating Action Button)
        Spacer(modifier = Modifier.weight(1f))

        // SISI KANAN (Insights & Settings)
        BottomNavButton(
            item = insightsItem,
            selected = currentRoute == insightsItem.route,
            onClick = { navigateToTab(navController, insightsItem.route, currentRoute) },
            modifier = Modifier.weight(1f).padding(end = 10.dp)
        )
        BottomNavButton(
            item = settingsItem,
            selected = currentRoute == settingsItem.route,
            onClick = { navigateToTab(navController, settingsItem.route, currentRoute) },
            modifier = Modifier.weight(1f).padding(end = 10.dp)
        )
    }
}

// 4. Tombol Custom untuk Navigasi
@Composable
private fun BottomNavButton(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = Color(0xFF6750A4) // Ungu MD3
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant
    val color = if (selected) activeColor else inactiveColor

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .clickable(onClick = onClick)
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = color
            )
            Text(
                text = item.label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = color
            )
        }
    }
}

// 5. Fungsi Helper: Navigasi cerdas (menghindari penumpukan stack)
private fun navigateToTab(navController: NavController, route: String, currentRoute: String?) {
    if (currentRoute != route) {
        navController.navigate(route) {
            // Kembali ke Home saat menekan back untuk navigasi yang bersih
            popUpTo(Routes.HOME) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

// 6. Fungsi Helper: Cek visibilitas Bottom Bar
fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return bottomItems.any { it.route == currentRoute }
}