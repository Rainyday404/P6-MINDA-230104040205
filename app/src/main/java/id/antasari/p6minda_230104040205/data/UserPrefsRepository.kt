package id.antasari.p6minda_230104040205.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1) Extension DataStore di Context
private val Context.userPrefsDataStore by preferencesDataStore(name = "user_prefs")

class UserPrefsRepository(private val context: Context) {

    // 2) Definisi Key untuk Preferences
    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        // FLAG BARU: Untuk menandai apakah seluruh proses onboarding sudah selesai
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    // 3) Flow untuk Nama User
    val userNameFlow: Flow<String?> = context.userPrefsDataStore.data
        .map { prefs -> prefs[Keys.USER_NAME] }

    // 4) Flow untuk Status Onboarding (Default: false)
    val onboardingCompletedFlow: Flow<Boolean> = context.userPrefsDataStore.data
        .map { prefs -> prefs[Keys.ONBOARDING_COMPLETED] ?: false }

    // 5) Simpan Nama (Dipanggil di Onboarding Step-2)
    suspend fun saveUserName(name: String) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
        }
    }

    // 6) Set Status Onboarding Selesai (Dipanggil di Onboarding Step-4/Terakhir)
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    /**
     * Optional: Fungsi untuk menghapus semua data (misal untuk Reset/Debug)
     */
    suspend fun clear() {
        context.userPrefsDataStore.edit { it.clear() }
    }
}