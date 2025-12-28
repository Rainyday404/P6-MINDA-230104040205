package id.antasari.p6minda_230104040205.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// Perbaikan: Import versi AutoMirrored untuk menghilangkan warning
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
// Perbaikan: Menghapus import .sp yang tidak digunakan (Baris 30)
import id.antasari.p6minda_230104040205.R

/* ====== Helper visual ====== */

@Composable
private fun TopGradientHeader(
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    colorTop: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
    colorBottom: Color = Color.Transparent
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                Brush.verticalGradient(listOf(colorTop, colorBottom))
            )
    )
}

@Composable
private fun DotsIndicator(
    total: Int,
    current: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { i ->
            val w = if (i == current) 18.dp else 6.dp
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(w)
                    .background(
                        if (i == current) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

/* ================ 1. Welcome ================ */

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onLoginRestore: () -> Unit
) {
    Scaffold { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            TopGradientHeader()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Welcome to",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            buildAnnotatedString {
                                append("My ")
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append("Minda")
                                }
                                append("!")
                            },
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Get started") }
                Spacer(Modifier.height(10.dp))
                TextButton(onClick = onLoginRestore, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        "Log in and restore",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/* ================ 2. Ask name (CENTER) ================ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskNameScreen(
    onConfirm: (String) -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Perbaikan: Menggunakan Icons.AutoMirrored.Filled.ArrowBack
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {}
            )
        }
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            TopGradientHeader()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "What's your name?",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(18.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            placeholder = { Text("Your name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                DotsIndicator(total = 4, current = 1, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Skip") }
                    Button(
                        onClick = { onConfirm(name.trim()) },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Confirm") }
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

/* ================ 3. Hello Screen ================ */

@Composable
fun HelloScreen(
    userName: String,
    onNext: () -> Unit
) {
    Scaffold { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            TopGradientHeader()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .widthIn(max = 560.dp)
                    ) {
                        val title = buildAnnotatedString {
                            append("Welcome to your ")
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append("Minda")
                            }
                            append(", ")
                            append(userName)
                            append("!")
                        }
                        Text(
                            title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        InfoCardLeftAligned(
                            title = "Menyimpan Jejak Waktu Fana",
                            subtitle = "Cermin sunyi saksikan jiwamu mekar, dari embun jadi sungai.",
                            trailingIcon = Icons.Outlined.Book
                        )
                        Spacer(Modifier.height(10.dp))
                        InfoCardLeftAligned(
                            title = "Kekuatan Sunyi Rahasia Hati",
                            subtitle = "Istana rahasia hati; aman dari tatapan dunia luar.",
                            trailingIcon = Icons.Outlined.Lock
                        )
                        Spacer(Modifier.height(10.dp))
                        InfoCardLeftAligned(
                            title = "Merangkai Makna dari Riak Rasa",
                            subtitle = "Selami emosi, temukan benang takdir, menuju damai sejati.",
                            trailingIcon = Icons.Outlined.Mood
                        )
                    }
                }

                Column(Modifier.padding(bottom = 18.dp)) {
                    DotsIndicator(total = 4, current = 2, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onNext,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Next") }
                }
            }
        }
    }
}

@Composable
private fun InfoCardLeftAligned(
    title: String,
    subtitle: String,
    trailingIcon: ImageVector
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

/* ================ 4. Start Journaling Screen ================ */

@Composable
fun StartJournalingScreen(
    onGotIt: () -> Unit
) {
    Scaffold { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            TopGradientHeader()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.banner_diary),
                        contentDescription = "Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(12.dp)
                            .clip(MaterialTheme.shapes.large),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "You're all set!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = """
                        Waktu adalah emas,
                        setiap heningnya berharga,
                        Jangan biarkan benang pikiran terurai
                        tanpa peta.
                        
                        Mulailah menulis,
                        menjadi saksi atas dirimu,
                        Di lembaran rahasia
                        yang tak tersentuh siapa pun,
                        Di sini, perasaanmu bernaung,
                        di balik Minda yang tenang.
                        
                        by. MindaTeam
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.weight(1f))

                DotsIndicator(total = 4, current = 3)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onGotIt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Got it!")
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}