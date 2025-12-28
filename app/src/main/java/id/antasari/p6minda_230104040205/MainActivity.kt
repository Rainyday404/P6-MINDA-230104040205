package id.antasari.p6minda_230104040205

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.antasari.p6minda_230104040205.data.UserPrefsRepository
import id.antasari.p6minda_230104040205.ui.BottomNavBar
import id.antasari.p6minda_230104040205.ui.MindaTheme
import id.antasari.p6minda_230104040205.ui.navigation.AppNavHost
import id.antasari.p6minda_230104040205.ui.navigation.Routes
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val userPrefs = remember { UserPrefsRepository(this@MainActivity) }
                    val scope = rememberCoroutineScope()

                    val userName by userPrefs.userNameFlow.collectAsState(initial = null)

                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    Scaffold(
                        bottomBar = {
                            if (shouldShowBottomBar(currentRoute)) {
                                BottomNavBar(navController = navController)
                            }
                        },
                        floatingActionButton = {
                            if (shouldShowBottomBar(currentRoute)) {
                                FloatingActionButton(
                                    onClick = { navController.navigate(Routes.NEW) },
                                    modifier = Modifier.offset(y = 40.dp),
                                    containerColor = Color(0xFF6750A4),
                                    contentColor = Color.White
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "New entry")
                                }
                            }
                        },
                        floatingActionButtonPosition = FabPosition.Center
                    ) { innerPadding ->
                        // Perbaikan: Mengirim 'repo' dan 'scope' sesuai error di IDE kamu
                        AppNavHost(
                            navController = navController,
                            storedName = userName,
                            repo = userPrefs,
                            scope = scope,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    private fun shouldShowBottomBar(route: String?): Boolean =
        route in setOf(Routes.HOME, Routes.CALENDAR, Routes.INSIGHTS, Routes.SETTINGS)
}