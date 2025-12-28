package id.antasari.p6minda_230104040205.ui.calendar

import android.app.Application
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import id.antasari.p6minda_230104040205.data.DiaryEntry
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onEdit: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val vm: CalendarViewModel = viewModel(factory = CalendarViewModel.provideFactory(app))
    val diaryByDate = vm.diaryByDate.collectAsStateWithLifecycle(emptyMap()).value

    val today = remember { LocalDate.now() }
    var visibleMonth by remember { mutableStateOf(YearMonth.of(today.year, today.month)) }
    var selectedDate by remember { mutableStateOf(today) }
    var showPicker by remember { mutableStateOf(false) }

    fun goPrevMonth() {
        visibleMonth = visibleMonth.minusMonths(1)
        selectedDate = visibleMonth.atDay(1)
    }
    fun goNextMonth() {
        visibleMonth = visibleMonth.plusMonths(1)
        selectedDate = visibleMonth.atDay(1)
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // === Konstanta visual ===
    val pagePadding = 16.dp
    val gridSpacing = 2.dp
    val cellHeight = 40.dp
    val portraitGridHeight = 220.dp

    val currentYear = LocalDate.now().year
    val monthName = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() }
    val headerText = if (visibleMonth.year == currentYear) monthName else "$monthName ${visibleMonth.year}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showPicker = true }
                    ) {
                        // Gaya judul disamakan dengan judul "Minda" di Home
                        Text(
                            text = headerText,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "▼",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = pagePadding)
        ) {
            // ===== Baris Hari: Sun..Sat =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gridSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
                ).forEach { dow ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // ===== Grid Kalender =====
            val cells = remember(visibleMonth) { buildMonthCells(visibleMonth) }

            Row(modifier = Modifier.fillMaxWidth()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    verticalArrangement = Arrangement.spacedBy(gridSpacing),
                    horizontalArrangement = Arrangement.spacedBy(gridSpacing),
                    modifier = Modifier
                        .then(if (isLandscape) Modifier.weight(1f) else Modifier.height(portraitGridHeight))
                        .pointerInput(visibleMonth) {
                            var acc = 0f
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { _, drag -> acc += drag },
                                onDragEnd = {
                                    val t = 60f
                                    if (acc > t) goPrevMonth()
                                    if (acc < -t) goNextMonth()
                                    acc = 0f
                                }
                            )
                        }
                ) {
                    items(cells) { c ->
                        DayCell(
                            date = c.date,
                            inCurrentMonth = c.inCurrentMonth,
                            selected = c.date == selectedDate,
                            hasDiary = diaryByDate[c.date]?.isNotEmpty() == true,
                            cellHeight = cellHeight,
                            onClick = {
                                selectedDate = c.date
                                visibleMonth = YearMonth.from(c.date)
                            }
                        )
                    }
                }

                if (isLandscape) {
                    Spacer(Modifier.width(16.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        DiaryListForDate(
                            date = selectedDate,
                            entries = diaryByDate[selectedDate].orEmpty(),
                            onEdit = onEdit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            if (!isLandscape) {
                Spacer(Modifier.height(6.dp))
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    DiaryListForDate(
                        date = selectedDate,
                        entries = diaryByDate[selectedDate].orEmpty(),
                        onEdit = onEdit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        if (showPicker) {
            DayMonthYearPickerDialog(
                initial = selectedDate,
                onDismiss = { showPicker = false },
                onConfirm = { picked ->
                    selectedDate = picked
                    visibleMonth = YearMonth.from(picked)
                    showPicker = false
                }
            )
        }
    }
}

/* ========================== Kalender Helpers ========================== */

data class DayCellData(val date: LocalDate, val inCurrentMonth: Boolean)

private fun buildMonthCells(ym: YearMonth): List<DayCellData> {
    val first = ym.atDay(1)
    val sundayIndex = first.dayOfWeek.value % 7
    val start = first.minusDays(sundayIndex.toLong())
    return (0 until 42).map { i ->
        val d = start.plusDays(i.toLong())
        DayCellData(d, d.month == ym.month)
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    inCurrentMonth: Boolean,
    selected: Boolean,
    hasDiary: Boolean,
    cellHeight: Dp,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val textColor = if (inCurrentMonth) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cellHeight)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                Modifier
                    .padding(top = 1.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(primary)
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Text(
                text = date.dayOfMonth.toString(),
                modifier = Modifier.padding(top = 2.dp),
                color = textColor,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }

        if (hasDiary) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-6).dp)
                    .clip(CircleShape)
                    .background(if (selected) onPrimary else primary)
            )
        }
    }
}

/* ========================== Picker Dialog ========================== */

@Composable
private fun DayMonthYearPickerDialog(
    initial: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    var pickedYear by remember { mutableIntStateOf(initial.year) }
    var pickedMonth by remember { mutableIntStateOf(initial.monthValue) }
    var pickedDay by remember { mutableIntStateOf(initial.dayOfMonth) }

    fun clampDay() {
        val max = YearMonth.of(pickedYear, pickedMonth).lengthOfMonth()
        if (pickedDay > max) pickedDay = max
        if (pickedDay < 1) pickedDay = 1
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                clampDay()
                onConfirm(LocalDate.of(pickedYear, pickedMonth, pickedDay))
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Select date") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kolom Tanggal
                PickerColumn(
                    value = pickedDay.toString(),
                    onUp = { pickedDay += 1; clampDay() },
                    onDown = { pickedDay -= 1; clampDay() },
                    minWidth = 64.dp
                )
                // Kolom Bulan
                PickerColumn(
                    value = Month.of(pickedMonth).getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    onUp = { pickedMonth = if (pickedMonth >= 12) 1 else pickedMonth + 1; clampDay() },
                    onDown = { pickedMonth = if (pickedMonth <= 1) 12 else pickedMonth - 1; clampDay() },
                    minWidth = 72.dp
                )
                // Kolom Tahun
                PickerColumn(
                    value = pickedYear.toString(),
                    onUp = { pickedYear += 1; clampDay() },
                    onDown = { pickedYear -= 1; clampDay() },
                    minWidth = 84.dp
                )
            }
        }
    )
}

@Composable
private fun PickerColumn(
    value: String,
    onUp: () -> Unit,
    onDown: () -> Unit,
    minWidth: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = minWidth)
    ) {
        Text("^^", fontSize = 18.sp, modifier = Modifier.clickable { onUp() })
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("vv", fontSize = 18.sp, modifier = Modifier.clickable { onDown() })
    }
}

/* ========================== List Diary ========================== */

@Composable
private fun DiaryListForDate(
    date: LocalDate,
    entries: List<DiaryEntry>,
    onEdit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(64.dp)
        ) {
            Text(date.dayOfMonth.toString(), style = MaterialTheme.typography.headlineSmall)
            Text(
                date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                date.year.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(Modifier.weight(1f)) {
            if (entries.isEmpty()) {
                val label = date.format(DateTimeFormatter.ofPattern("d MMM"))
                Text(
                    "No diary on $label",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(entries) { e -> DiaryCardItem(e, onEdit) }
                }
            }
        }
    }
}

@Composable
private fun DiaryCardItem(e: DiaryEntry, onEdit: (Int) -> Unit) {
    val timeStr = Instant.ofEpochMilli(e.timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
        .format(DateTimeFormatter.ofPattern("h:mm a"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onEdit(e.id) }
            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 12.dp)
    ) {
        Text(
            timeStr,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            e.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(Modifier.height(6.dp))
        if (!e.content.isNullOrBlank()) {
            Text(
                e.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}