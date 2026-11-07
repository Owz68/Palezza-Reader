package com.example.model

data class PdfDocumentItem(
    val id: String,
    val title: String,
    val filePath: String,
    val fileSizeBytes: Long,
    val pageCount: Int,
    val lastOpenedTimestamp: Long,
    val lastPageRead: Int = 1,
    val isFavorite: Boolean = false,
    val folder: String = "Général",
    val tags: List<String> = emptyList(),
    val isAsset: Boolean = false
) {
    val formattedSize: String
        get() {
            return when {
                fileSizeBytes >= 1024 * 1024 -> String.format("%.1f Mo", fileSizeBytes.toDouble() / (1024 * 1024))
                fileSizeBytes >= 1024 -> String.format("%.0f Ko", fileSizeBytes.toDouble() / 1024)
                else -> "$fileSizeBytes octets"
            }
        }
}

enum class NavigationDestination(
    val id: String,
    val title: String,
    val shortLabel: String
) {
    DASHBOARD("dashboard", "Tableau de bord", "Accueil"),
    DOCUMENTS("documents", "Mes documents", "Documents"),
    RECENTS("recents", "Fichiers récents", "Récents"),
    FAVORITES("favorites", "Favoris", "Favoris"),
    FOLDERS("folders", "Mes dossiers", "Dossiers"),
    TOOLS("tools", "Outils PDF", "Outils"),
    SCANNER("scanner", "Scanner", "Scanner"),
    SIGNATURES("signatures", "Signatures", "Signatures"),
    SETTINGS("settings", "Paramètres", "Réglages"),
    ACCOUNT("account", "Mon compte Palezza", "Compte")
}

enum class ReaderViewMode(val label: String) {
    PAGE_BY_PAGE("Page par page"),
    CONTINUOUS("Défilement continu")
}

data class ReaderUiState(
    val activeDocument: PdfDocumentItem? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val zoomLevel: Float = 1.0f,
    val rotationDegrees: Int = 0,
    val viewMode: ReaderViewMode = ReaderViewMode.PAGE_BY_PAGE,
    val isFullScreen: Boolean = false,
    val showThumbnails: Boolean = false,
    val bookmarks: Set<Int> = emptySet(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
