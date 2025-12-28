package id.antasari.p6minda_230104040205.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import id.antasari.p6minda_230104040205.data.UserPrefsRepository
import id.antasari.p6minda_230104040205.ui.*
import id.antasari.p6minda_230104040205.ui.calendar.CalendarScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    navController: NavHostController,
    storedName: String?,
    // Gunakan flag onboarding jika ada di repository,
    // di sini kita asumsikan jika nama tidak kosong maka sudah onboarding
    repo: UserPrefsRepository,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    // 1. Penentuan StartDestination (Logika: Jika nama kosong, ke Welcome)
    val startDest = if (storedName.isNullOrBlank()) Routes.WELCOME else Routes.HOME

    NavHost(
        navController = navController,
        startDestination = startDest,
        modifier = modifier
    ) {
        // --- 1. ONBOARDING SECTION ---

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Routes.ASK_NAME) },
                onLoginRestore = { navController.navigate(Routes.ASK_NAME) }
            )
        }

        composable(Routes.ASK_NAME) {
            AskNameScreen(
                // Perbaikan error: Nama parameter harus 'onConfirm' dan 'onSkip'
                onConfirm = { typedName ->
                    scope.launch { repo.saveUserName(typedName) }
                    navController.navigate(Routes.HELLO)
                },
                onSkip = { navController.navigate(Routes.HELLO) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HELLO) {
            HelloScreen(
                userName = storedName ?: "",
                onNext = { navController.navigate(Routes.CTA) }
            )
        }

        composable(Routes.CTA) {
            StartJournalingScreen(
                onGotIt = {
                    // Berpindah ke Home dan hapus semua stack onboarding
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- 2. MAIN SECTION ---

        composable(Routes.HOME) {
            HomeScreen(
                userName = storedName,
                onAddEntry = { navController.navigate(Routes.NEW) },
                onOpenEntry = { id -> navController.navigate("detail/$id") }
            )
        }

        composable(Routes.CALENDAR) {
            CalendarScreen(onEdit = { id -> navController.navigate("edit/$id") })
        }

        composable(Routes.INSIGHTS) {
            InsightsScreen()
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                userName = storedName ?: "User",
                onBack = { navController.popBackStack() },
                onResetName = {
                    scope.launch {
                        repo.clear()
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        // --- 3. CRUD & EXTRA ---

        composable(Routes.TEST_ROOM) {
            TestRoomScreen()
        }

        composable(Routes.NEW) {
            NewEntryScreen(
                onBack = { navController.popBackStack() },
                onSaved = { id ->
                    navController.popBackStack()
                    navController.navigate("detail/$id")
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("id") ?: 0L
            NoteDetailScreen(
                diaryId = id,
                onBack = { navController.popBackStack() },
                onEdit = { eid -> navController.navigate("edit/$eid") }
            )
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("id") ?: 0L
            EditEntryScreen(
                entryId = id.toInt(),
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}