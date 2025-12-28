package id.antasari.p6minda_230104040205.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.antasari.p6minda_230104040205.data.DiaryEntry
import id.antasari.p6minda_230104040205.data.DiaryRepository
import id.antasari.p6minda_230104040205.data.MindaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// ============================================================================
// 1. INSIGHTS SCREEN (EXTRA) - Screen 7
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraInsightsScreen(onTryTemplates: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { MindaDatabase.getInstance(context) }
    val repo = remember { DiaryRepository(db.diaryDao()) }
    var entries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        entries = withContext(Dispatchers.IO) { repo.allEntries() }
    }

    val totalEntries = entries.size
    val distinctMoods = entries.map { it.mood }.toSet().size
    val datesSet = remember(entries) {
        entries.map { Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() }.toSet()
    }
    val currentStreak = calcCurrentStreak(datesSet)
    val longestStreak = calcLongestStreak(datesSet)

    val moodCounts = remember(entries) { entries.groupingBy { it.mood }.eachCount() }
    val totalForPercent = moodCounts.values.sum().coerceAtLeast(1)
    val trendData = moodCounts.entries.sortedBy { it.key }.map { (mood, count) ->
        mood to (count.toFloat() / totalForPercent.toFloat())
    }

    val today = LocalDate.now()
    val last7 = (0..6).map { today.minusDays((6 - it).toLong()) }
    val hasEntryOn = last7.associateWith { it in datesSet }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Insights (Extra)", fontWeight = FontWeight.SemiBold) }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp, bottom = 24.dp)
        ) {
            // Summary Card
            item {
                BorderedCard {
                    Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceBetween) {
                        SummaryColumn(totalEntries, "Entries")
                        SummaryColumn(distinctMoods, "Moods")
                        SummaryColumn(currentStreak, "Streak")
                    }
                }
            }

            // Streak Card
            item {
                BorderedCard {
                    Column(Modifier.padding(16.dp)) {
                        Text("Diary Streak", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            last7.forEach { DayCheck(it, hasEntryOn[it] == true, 34.dp) }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("🔥 Longest chain: $longestStreak", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Trends Card
            item {
                BorderedCard {
                    Column(Modifier.padding(12.dp)) {
                        Text("Trends", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        if (trendData.isEmpty()) {
                            Text("No mood data yet.", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.weight(1f), Alignment.Center) {
                                    DonutChart(trendData.map { it.second }, trendData.map { moodColor(it.first) }, 80.dp, 18.dp)
                                }
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    trendData.forEach { (mood, frac) ->
                                        LegendRowTight(moodColor(mood), moodLabel(mood), frac * 100f)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 2. CALENDAR SCREEN (EXTRA) - Screen 6
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraCalendarScreen() {
    val context = LocalContext.current
    val db = remember { MindaDatabase.getInstance(context) }
    val repo = remember { DiaryRepository(db.diaryDao()) }
    var groupedEntries by remember { mutableStateOf<Map<LocalDate, List<DiaryEntry>>>(emptyMap()) }

    LaunchedEffect(Unit) {
        groupedEntries = withContext(Dispatchers.IO) {
            repo.allEntries()
                .groupBy { Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() }
                .toSortedMap(compareByDescending { it })
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Calendar (Extra)", fontWeight = FontWeight.SemiBold) }) }
    ) { innerPadding ->
        if (groupedEntries.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                Text("No entries yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(Modifier.padding(innerPadding).fillMaxSize()) {
                groupedEntries.forEach { (date, entries) ->
                    item { Text(formatDateHeader(date), Modifier.padding(16.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                    items(entries) { entry ->
                        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Text("${entry.mood} ${entry.title}", fontWeight = FontWeight.Bold)
                            Text(entry.content.take(80), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            HorizontalDivider(Modifier.padding(top = 8.dp), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 3. SETTINGS SCREEN (EXTRA) - Screen 8
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraSettingsScreen(userName: String?) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings (Extra)", fontWeight = FontWeight.SemiBold) }) }
    ) { innerPadding ->
        LazyColumn(Modifier.padding(innerPadding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { SectionHeader("PERSONAL") }
            item { SettingsItem({ Icon(Icons.Filled.Person, null) }, "Your name: ${userName ?: "Anonymous"}") }
            item { SettingsItem({ Icon(Icons.Filled.Lock, null) }, "Password (PIN)") }
            item { SectionHeader("MY DATA") }
            item { SettingsItem({ Icon(Icons.Filled.Cloud, null) }, "Backup & Restore") }
            item { SettingsItem({ Icon(Icons.Filled.Delete, null) }, "Delete app data") }
        }
    }
}

// ============================================================================
// PRIVATE UI COMPONENTS
// ============================================================================

@Composable
private fun BorderedCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().border(0.8.dp, MaterialTheme.colorScheme.outline.copy(0.35f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        content = content
    )
}

@Composable
private fun SummaryColumn(number: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.widthIn(min = 80.dp)) {
        Text(number.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DayCheck(date: LocalDate, checked: Boolean, size: Dp) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(size).clip(RoundedCornerShape(10.dp))
                .background(if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
            Alignment.Center
        ) {
            if (checked) Icon(Icons.Filled.Check, null, tint = Color.White)
            else Text("+", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("${date.dayOfMonth}", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun DonutChart(fractions: List<Float>, colors: List<Color>, size: Dp, thickness: Dp) {
    Canvas(Modifier.size(size)) {
        var start = -90f
        fractions.forEachIndexed { i, p ->
            val sweep = p * 360f
            drawArc(colors.getOrElse(i) { Color.Gray }, start, sweep, false, style = Stroke(thickness.toPx(), cap = StrokeCap.Round))
            start += sweep
        }
    }
}

@Composable
private fun LegendRowTight(dotColor: Color, label: String, percent: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
        Spacer(Modifier.width(8.dp))
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text("${percent.roundToInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingsItem(leading: @Composable () -> Unit, label: String) {
    Row(
        Modifier.fillMaxWidth().border(0.8.dp, MaterialTheme.colorScheme.outline.copy(0.35f), RoundedCornerShape(12.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(24.dp), Alignment.Center) { leading() }
        Spacer(Modifier.width(12.dp))
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 8.dp))
}

// ============================================================================
// MOOD LOGIC (RESTORED FROM IMAGE)
// ============================================================================

@Composable
private fun moodColor(mood: String): Color = when (mood.lowercase()) {
    "😊", "happy" -> Color(0xFF4CAF50)
    "😌", "calm"  -> Color(0xFF42A5F5)
    "😢", "sad"   -> Color(0xFFEF5350)
    "😡", "angry" -> Color(0xFFFF7043)
    "😴", "tired" -> Color(0xFFAB47BC)
    "😎", "cool"  -> Color(0xFF26A69A)
    else          -> MaterialTheme.colorScheme.primary
}

private fun moodLabel(mood: String): String = when (mood.lowercase()) {
    "😊", "happy" -> "Happy"
    "😌", "calm"  -> "Calm"
    "😢", "sad"   -> "Sad"
    "😡", "angry" -> "Angry"
    "😴", "tired" -> "Tired"
    "😎", "cool"  -> "Cool"
    else          -> mood
}

// ============================================================================
// DATE UTILS
// ============================================================================

private fun formatDateHeader(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy"))

private fun calcCurrentStreak(dates: Set<LocalDate>): Int {
    var s = 0; var c = LocalDate.now()
    while (dates.contains(c)) { s++; c = c.minusDays(1) }
    return s
}

private fun calcLongestStreak(dates: Set<LocalDate>): Int {
    if (dates.isEmpty()) return 0
    val sorted = dates.sorted()
    var best = 1; var run = 1
    for (i in 1 until sorted.size) {
        if (sorted[i] == sorted[i-1].plusDays(1)) run++ else { best = maxOf(best, run); run = 1 }
    }
    return maxOf(best, run)
}