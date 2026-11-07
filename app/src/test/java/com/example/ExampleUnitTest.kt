package com.example

import com.example.model.NavigationDestination
import com.example.model.PdfDocumentItem
import com.example.viewmodel.PalezzaUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPdfDocumentItemFormattedSize() {
        val docKo = PdfDocumentItem(
            id = "1",
            title = "Guide.pdf",
            filePath = "/path/guide.pdf",
            fileSizeBytes = 45 * 1024L,
            pageCount = 3,
            lastOpenedTimestamp = 1000L
        )
        assertEquals("45 Ko", docKo.formattedSize)

        val docMo = PdfDocumentItem(
            id = "2",
            title = "Contrat.pdf",
            filePath = "/path/contrat.pdf",
            fileSizeBytes = (2.5 * 1024 * 1024).toLong(),
            pageCount = 12,
            lastOpenedTimestamp = 2000L
        )
        assertEquals("2.5 Mo", docMo.formattedSize)
    }

    @Test
    fun testPalezzaUiStateStorageCalculation() {
        val docs = listOf(
            PdfDocumentItem("1", "Doc 1", "/p1", 1024 * 1024L, 2, 100L),
            PdfDocumentItem("2", "Doc 2", "/p2", 2 * 1024 * 1024L, 5, 200L)
        )
        val state = PalezzaUiState(documents = docs, isPremium = false)
        assertEquals(3 * 1024 * 1024L, state.totalStorageUsedBytes)
        assertEquals("3.0 Mo", state.formattedStorageUsed)
        assertEquals("2 Go", state.formattedStorageLimit)
    }

    @Test
    fun testNavigationDestinationsCount() {
        // Must contain all 10 spaces
        val destinations = NavigationDestination.entries
        assertEquals(10, destinations.size)
        assertTrue(destinations.any { it == NavigationDestination.DASHBOARD })
        assertTrue(destinations.any { it == NavigationDestination.DOCUMENTS })
        assertTrue(destinations.any { it == NavigationDestination.RECENTS })
        assertTrue(destinations.any { it == NavigationDestination.FAVORITES })
        assertTrue(destinations.any { it == NavigationDestination.FOLDERS })
        assertTrue(destinations.any { it == NavigationDestination.TOOLS })
        assertTrue(destinations.any { it == NavigationDestination.SCANNER })
        assertTrue(destinations.any { it == NavigationDestination.SIGNATURES })
        assertTrue(destinations.any { it == NavigationDestination.SETTINGS })
        assertTrue(destinations.any { it == NavigationDestination.ACCOUNT })
    }
}
