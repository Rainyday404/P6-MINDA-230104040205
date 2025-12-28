package id.antasari.p6minda_230104040205.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.antasari.p6minda_230104040205.data.DiaryEntry
import id.antasari.p6minda_230104040205.data.DiaryRepository
import id.antasari.p6minda_230104040205.data.MindaDatabase
import id.antasari.p6minda_230104040205.util.formatTimestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    diaryId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Menggunakan Singleton Database
    val repository = remember {
        DiaryRepository(MindaDatabase.getInstance(context).diaryDao())
    }

    var diary by remember { mutableStateOf<DiaryEntry?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load data saat screen dibuka atau diaryId berubah
    LaunchedEffect(diaryId) {
        val data = withContext(Dispatchers.IO) {
            repository.getById(diaryId.toInt())
        }
        diary = data
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your entry",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Tombol Edit langsung di TopBar (opsional, mengikuti UI sebelumnya)
                    IconButton(
                        onClick = { onEdit(diaryId) },
                        enabled = (diary != null)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }

                    // Menu Dropdown untuk Hapus
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Hapus") },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                            )
                        }
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
                .verticalScroll(rememberScrollState())
        ) {
            if (diary == null) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                diary?.let { item ->
                    // Mood + Title (Menggabungkan mood emoji dan judul)
                    Text(
                        text = "${item.mood} ${item.title}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Timestamp
                    Text(
                        text = formatTimestamp(item.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Isi Catatan
                    Text(
                        text = item.content,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reflection Prompts (Bagian statis sesuai desain awal)
                    Text(
                        text = "Reflection prompts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• What did you learn about yourself today?\n" +
                                "• What are you grateful for right now?\n" +
                                "• Is there something you want to let go?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete entry?") },
            text = { Text("This action cannot be undone. Are you sure you want to delete this diary entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            diary?.let { repository.remove(it) }
                            withContext(Dispatchers.Main) {
                                showDeleteDialog = false
                                onBack() // Kembali ke Home setelah hapus
                            }
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}