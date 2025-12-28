package id.antasari.p6minda_230104040205.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEntryScreen(
    entryId: Int,
    onBack: () -> Unit,
    onSaved: (Int) -> Unit
) {
    val context = LocalContext.current
    val db = remember { MindaDatabase.getInstance(context) }
    val repo = remember { DiaryRepository(db.diaryDao()) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // State Input
    var titleText by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😊") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember {
        mutableStateOf(LocalTime.now().withSecond(0).withNano(0))
    }

    // State Dialog
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Formatter
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("d MMM, yyyy", Locale.getDefault())
    }
    val timeFormatter12h = remember {
        DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    }

    fun combinedMillis(): Long {
        val dt = LocalDateTime.of(selectedDate, selectedTime)
        return dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    LaunchedEffect(entryId) {
        if (entryId > 0) {
            val loaded = withContext(Dispatchers.IO) { repo.getById(entryId) }
            if (loaded != null) {
                titleText = loaded.title
                contentText = loaded.content
                // Perbaikan Warning: Menggunakan ifBlank
                selectedMood = loaded.mood.ifBlank { "😊" }

                val zdt = Instant.ofEpochMilli(loaded.timestamp).atZone(ZoneId.systemDefault())
                selectedDate = zdt.toLocalDate()
                selectedTime = zdt.toLocalTime().withSecond(0).withNano(0)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (entryId > 0) "Edit Entry" else "New Entry",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Perbaikan Warning: Menggunakan AutoMirrored
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pemilih Tanggal & Jam
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = selectedDate.format(dateFormatter),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable { showDatePicker = true },
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = "  •  ", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = selectedTime.format(timeFormatter12h),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable { showTimePicker = true },
                    color = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Title / Headline") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = contentText,
                onValueChange = { contentText = it },
                label = { Text("Content / Reflection") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp),
                minLines = 8
            )

            Text(
                text = "How are you feeling?",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            MoodGrid(
                selectedMood = selectedMood,
                onMoodSelected = { selectedMood = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            if (entryId > 0) {
                                val existing = repo.getById(entryId)
                                if (existing != null) {
                                    repo.edit(existing.copy(
                                        title = titleText,
                                        content = contentText,
                                        mood = selectedMood,
                                        timestamp = combinedMillis()
                                    ))
                                }
                            } else {
                                repo.add(DiaryEntry(
                                    id = 0,
                                    title = titleText,
                                    content = contentText,
                                    mood = selectedMood,
                                    timestamp = combinedMillis()
                                ))
                            }
                            withContext(Dispatchers.Main) { onSaved(entryId) }
                        }
                    },
                    enabled = titleText.isNotBlank() && contentText.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
        }
    }

    // Dialog Pemilih Tanggal
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // Dialog Pemilih Jam
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            text = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodGrid(selectedMood: String, onMoodSelected: (String) -> Unit) {
    val moods = listOf(
        "😊" to "Happy", "😌" to "Calm", "😔" to "Sad",
        "😡" to "Angry", "😴" to "Tired", "😎" to "Cool"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        moods.chunked(3).forEach { rowMoods ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowMoods.forEach { (emoji, label) ->
                    FilterChip(
                        selected = selectedMood == emoji,
                        onClick = { onMoodSelected(emoji) },
                        label = { Text("$emoji $label") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}