package id.antasari.p6minda_230104040205.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.antasari.p6minda_230104040205.data.DiaryEntry
import id.antasari.p6minda_230104040205.data.DiaryRepository
import id.antasari.p6minda_230104040205.data.MindaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen() {
    val context = LocalContext.current
    val db = remember { MindaDatabase.getInstance(context) }
    val repo = remember { DiaryRepository(db.diaryDao()) }

    var entries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }

    // Mengambil data dari database saat layar dibuka
    LaunchedEffect(Unit) {
        val all = withContext(Dispatchers.IO) {
            repo.allEntries()
        }
        entries = all
    }

    // Perhitungan Statistik
    val totalEntries = entries.size
    val nowMillis = System.currentTimeMillis()
    val weekAgoMillis = nowMillis - 7L * 24 * 60 * 60 * 1000
    val last7Count = entries.count { it.timestamp >= weekAgoMillis }

    // Perhitungan Mood
    val moodCounts = remember(entries) {
        entries.groupingBy { it.mood }.eachCount()
    }
    val maxCount = moodCounts.values.maxOrNull() ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Insights",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Ringkasan Jurnal
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Journal Summary",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$totalEntries entries total",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "$last7Count in the last 7 days",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Mood Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (moodCounts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No mood data yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    moodCounts.entries
                        .sortedByDescending { it.value }
                        .forEach { (mood, count) ->
                            val fraction = if (maxCount > 0) count.toFloat() / maxCount.toFloat() else 0f

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Emoji Mood
                                Text(text = mood, style = MaterialTheme.typography.titleLarge)

                                Spacer(Modifier.width(12.dp))

                                // Progress Bar Mood
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(fraction)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    )
                                }

                                // Angka Jumlah
                                Text(
                                    text = count.toString(),
                                    modifier = Modifier.padding(start = 12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                }
            }
        }
    }
}