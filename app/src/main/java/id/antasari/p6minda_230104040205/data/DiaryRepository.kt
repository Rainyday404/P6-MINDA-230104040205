package id.antasari.p6minda_230104040205.data

import kotlinx.coroutines.flow.Flow

class DiaryRepository(
    private val dao: DiaryDao
) {

    // Flow untuk Calendar/Home (Real-time update)
    // Menggunakan Flow agar UI otomatis update saat data di DB berubah
    fun entriesFlow(): Flow<List<DiaryEntry>> = dao.observeAll()

    // READ semua entry (Versi suspend/sekali panggil)
    suspend fun allEntries(): List<DiaryEntry> = dao.getAll()

    // READ satu entry by id (Untuk NoteDetailScreen & EditEntryScreen)
    suspend fun getById(id: Int): DiaryEntry? = dao.getById(id)

    // CREATE entry baru (Dipakai seed otomatis atau input manual)
    suspend fun add(entry: DiaryEntry): Long = dao.insert(entry)

    // UPDATE entry (Dipakai tombol Save di EditEntryScreen)
    suspend fun edit(entry: DiaryEntry) = dao.update(entry)

    // DELETE entry (Dipakai Delete di NoteDetailScreen)
    suspend fun remove(entry: DiaryEntry) = dao.delete(entry)
}