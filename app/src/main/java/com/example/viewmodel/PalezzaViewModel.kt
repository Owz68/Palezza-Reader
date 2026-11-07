package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.NavigationDestination
import com.example.model.PdfDocumentItem
import com.example.model.ReaderUiState
import com.example.model.ReaderViewMode
import com.example.pdf.PdfEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class PalezzaUiState(
    val currentDestination: NavigationDestination = NavigationDestination.DASHBOARD,
    val documents: List<PdfDocumentItem> = emptyList(),
    val searchQuery: String = "",
    val activeFilterTab: String = "Tous", // "Tous", "Récents", "Favoris", "Dossiers"
    val selectedFolder: String? = null,
    val sortBy: String = "Date", // "Date", "Nom", "Taille"
    val readerState: ReaderUiState = ReaderUiState(),
    val renderedCurrentPageBitmap: Bitmap? = null,
    val pageThumbnails: Map<Int, Bitmap> = emptyMap(),
    val isPremium: Boolean = false,
    val storageLimitBytes: Long = 2L * 1024 * 1024 * 1024, // 2 Go Gratuit
    val userNotification: String? = null
) {
    val totalStorageUsedBytes: Long
        get() = documents.sumOf { it.fileSizeBytes }

    val formattedStorageUsed: String
        get() {
            val usedMo = totalStorageUsedBytes.toDouble() / (1024 * 1024)
            return String.format("%.1f Mo", usedMo)
        }

    val formattedStorageLimit: String
        get() = if (isPremium) "10 Go" else "2 Go"

    val storageProgress: Float
        get() = (totalStorageUsedBytes.toFloat() / storageLimitBytes.toFloat()).coerceIn(0.01f, 1.0f)

    val recentDocuments: List<PdfDocumentItem>
        get() = documents.sortedByDescending { it.lastOpenedTimestamp }

    val favoriteDocuments: List<PdfDocumentItem>
        get() = documents.filter { it.isFavorite }

    val folders: List<String>
        get() = documents.map { it.folder }.distinct().sorted()

    val filteredDocuments: List<PdfDocumentItem>
        get() {
            var result = when (activeFilterTab) {
                "Récents" -> recentDocuments
                "Favoris" -> favoriteDocuments
                "Dossiers" -> if (selectedFolder != null) documents.filter { it.folder == selectedFolder } else documents
                else -> documents
            }

            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim().lowercase()
                result = result.filter {
                    it.title.lowercase().contains(query) ||
                    it.folder.lowercase().contains(query) ||
                    it.tags.any { tag -> tag.lowercase().contains(query) }
                }
            }

            return when (sortBy) {
                "Nom" -> result.sortedBy { it.title.lowercase() }
                "Taille" -> result.sortedByDescending { it.fileSizeBytes }
                else -> result.sortedByDescending { it.lastOpenedTimestamp }
            }
        }
}

class PalezzaViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PalezzaUiState())
    val uiState: StateFlow<PalezzaUiState> = _uiState.asStateFlow()

    init {
        loadInitialDocuments()
    }

    private fun loadInitialDocuments() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val bundled = PdfEngine.initializeBundledDocuments(context)
            _uiState.update { it.copy(documents = bundled) }
        }
    }

    fun navigateTo(destination: NavigationDestination) {
        _uiState.update { it.copy(currentDestination = destination) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFilterTab(tab: String) {
        _uiState.update { it.copy(activeFilterTab = tab, selectedFolder = null) }
    }

    fun selectFolder(folder: String?) {
        _uiState.update { it.copy(selectedFolder = folder, activeFilterTab = "Dossiers") }
    }

    fun setSortBy(sortBy: String) {
        _uiState.update { it.copy(sortBy = sortBy) }
    }

    fun toggleFavorite(documentId: String) {
        _uiState.update { current ->
            val updated = current.documents.map { doc ->
                if (doc.id == documentId) doc.copy(isFavorite = !doc.isFavorite) else doc
            }
            val activeDoc = current.readerState.activeDocument
            val updatedActive = if (activeDoc?.id == documentId) activeDoc.copy(isFavorite = !activeDoc.isFavorite) else activeDoc
            current.copy(
                documents = updated,
                readerState = current.readerState.copy(activeDocument = updatedActive)
            )
        }
    }

    fun deleteDocument(documentId: String) {
        val docToDelete = _uiState.value.documents.find { it.id == documentId }
        docToDelete?.let { doc ->
            try {
                val file = File(doc.filePath)
                if (file.exists() && !doc.isAsset) {
                    file.delete()
                }
            } catch (_: Exception) {}
        }
        _uiState.update { current ->
            current.copy(
                documents = current.documents.filterNot { it.id == documentId },
                userNotification = "Document supprimé de la bibliothèque"
            )
        }
    }

    fun renameDocument(documentId: String, newTitle: String) {
        if (newTitle.isBlank()) return
        _uiState.update { current ->
            val updated = current.documents.map { doc ->
                if (doc.id == documentId) doc.copy(title = newTitle.trim()) else doc
            }
            current.copy(documents = updated, userNotification = "Document renommé en « $newTitle »")
        }
    }

    /**
     * Imports a PDF chosen by user through the system picker.
     */
    fun importDocument(uri: Uri) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            _uiState.update { it.copy(userNotification = "Importation du document en cours...") }
            val item = PdfEngine.importPdfFromUri(context, uri)
            if (item != null) {
                _uiState.update { current ->
                    current.copy(
                        documents = listOf(item) + current.documents,
                        userNotification = "Document « ${item.title} » importé avec succès !"
                    )
                }
                openDocument(item)
            } else {
                _uiState.update { it.copy(userNotification = "Erreur lors de l'importation du PDF") }
            }
        }
    }

    /**
     * Creates a new PDF document with chosen template and registers it.
     */
    fun createDocument(title: String, templateType: String = "NOTE") {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            _uiState.update { it.copy(userNotification = "Création du document PDF en cours...") }
            val item = PdfEngine.createNewPdfDocument(context, title, templateType)
            if (item != null) {
                _uiState.update { current ->
                    current.copy(
                        documents = listOf(item) + current.documents,
                        userNotification = "Document « ${item.title} » créé avec succès !"
                    )
                }
                openDocument(item)
            } else {
                _uiState.update { it.copy(userNotification = "Erreur lors de la création du document") }
            }
        }
    }

    /**
     * Opens a document in the real Reader.
     */
    fun openDocument(doc: PdfDocumentItem) {
        val totalPages = PdfEngine.getPageCount(doc.filePath).coerceAtLeast(1)
        val initialPage = doc.lastPageRead.coerceIn(1, totalPages)

        _uiState.update { current ->
            // Update lastOpened
            val updatedList = current.documents.map {
                if (it.id == doc.id) it.copy(lastOpenedTimestamp = System.currentTimeMillis()) else it
            }

            current.copy(
                documents = updatedList,
                renderedCurrentPageBitmap = null,
                readerState = ReaderUiState(
                    activeDocument = doc,
                    currentPage = initialPage,
                    totalPages = totalPages,
                    zoomLevel = 1.0f,
                    rotationDegrees = 0,
                    viewMode = ReaderViewMode.PAGE_BY_PAGE,
                    isFullScreen = false,
                    showThumbnails = false,
                    isLoading = true
                )
            )
        }

        renderCurrentPage()
    }

    fun closeDocument() {
        val currentDoc = _uiState.value.readerState.activeDocument
        val currentPage = _uiState.value.readerState.currentPage
        if (currentDoc != null) {
            _uiState.update { current ->
                val updatedDocs = current.documents.map {
                    if (it.id == currentDoc.id) it.copy(lastPageRead = currentPage) else it
                }
                current.copy(
                    documents = updatedDocs,
                    renderedCurrentPageBitmap = null,
                    pageThumbnails = emptyMap(),
                    readerState = ReaderUiState(activeDocument = null)
                )
            }
        } else {
            _uiState.update { it.copy(readerState = ReaderUiState(activeDocument = null)) }
        }
    }

    fun nextPage() {
        val state = _uiState.value.readerState
        if (state.currentPage < state.totalPages) {
            goToPage(state.currentPage + 1)
        }
    }

    fun previousPage() {
        val state = _uiState.value.readerState
        if (state.currentPage > 1) {
            goToPage(state.currentPage - 1)
        }
    }

    fun goToPage(pageNumber: Int) {
        val state = _uiState.value.readerState
        val target = pageNumber.coerceIn(1, state.totalPages)
        if (target != state.currentPage) {
            _uiState.update { it.copy(readerState = it.readerState.copy(currentPage = target, isLoading = true)) }
            renderCurrentPage()
        }
    }

    fun setZoom(zoom: Float) {
        _uiState.update { it.copy(readerState = it.readerState.copy(zoomLevel = zoom.coerceIn(0.75f, 3.5f))) }
    }

    fun zoomIn() {
        _uiState.update {
            val newZoom = (it.readerState.zoomLevel + 0.25f).coerceAtMost(3.0f)
            it.copy(readerState = it.readerState.copy(zoomLevel = newZoom))
        }
    }

    fun zoomOut() {
        _uiState.update {
            val newZoom = (it.readerState.zoomLevel - 0.25f).coerceAtLeast(0.75f)
            it.copy(readerState = it.readerState.copy(zoomLevel = newZoom))
        }
    }

    fun resetZoom() {
        _uiState.update { it.copy(readerState = it.readerState.copy(zoomLevel = 1.0f)) }
    }

    fun rotateClockwise() {
        _uiState.update {
            val newRot = (it.readerState.rotationDegrees + 90) % 360
            it.copy(readerState = it.readerState.copy(rotationDegrees = newRot))
        }
    }

    fun toggleFullScreen() {
        _uiState.update { it.copy(readerState = it.readerState.copy(isFullScreen = !it.readerState.isFullScreen)) }
    }

    fun toggleThumbnails() {
        val nextState = !_uiState.value.readerState.showThumbnails
        _uiState.update { it.copy(readerState = it.readerState.copy(showThumbnails = nextState)) }
        if (nextState) {
            loadThumbnails()
        }
    }

    fun toggleViewMode() {
        val next = if (_uiState.value.readerState.viewMode == ReaderViewMode.PAGE_BY_PAGE)
            ReaderViewMode.CONTINUOUS else ReaderViewMode.PAGE_BY_PAGE
        _uiState.update { it.copy(readerState = it.readerState.copy(viewMode = next)) }
    }

    fun toggleBookmark(page: Int) {
        _uiState.update { current ->
            val set = current.readerState.bookmarks.toMutableSet()
            val added: Boolean
            if (set.contains(page)) {
                set.remove(page)
                added = false
            } else {
                set.add(page)
                added = true
            }
            current.copy(
                readerState = current.readerState.copy(bookmarks = set),
                userNotification = if (added) "Page $page ajoutée aux marque-pages" else "Marque-page retiré"
            )
        }
    }

    fun clearNotification() {
        _uiState.update { it.copy(userNotification = null) }
    }

    fun upgradeToPremium() {
        _uiState.update {
            it.copy(
                isPremium = true,
                storageLimitBytes = 10L * 1024 * 1024 * 1024,
                userNotification = "Compte Palezza Premium activé (Stockage étendu à 10 Go)"
            )
        }
    }

    private fun renderCurrentPage() {
        val state = _uiState.value.readerState
        val doc = state.activeDocument ?: return

        viewModelScope.launch {
            val pageIndex = state.currentPage - 1
            val bitmap = PdfEngine.renderPage(doc.filePath, pageIndex, scaleFactor = 2.2f)
            _uiState.update {
                it.copy(
                    renderedCurrentPageBitmap = bitmap,
                    readerState = it.readerState.copy(isLoading = false)
                )
            }
        }
    }

    private fun loadThumbnails() {
        val doc = _uiState.value.readerState.activeDocument ?: return
        val total = _uiState.value.readerState.totalPages

        viewModelScope.launch(Dispatchers.IO) {
            val map = mutableMapOf<Int, Bitmap>()
            for (p in 0 until total.coerceAtMost(12)) {
                val bmp = PdfEngine.renderPage(doc.filePath, p, scaleFactor = 0.5f)
                if (bmp != null) {
                    map[p + 1] = bmp
                }
            }
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(pageThumbnails = map) }
            }
        }
    }
}
