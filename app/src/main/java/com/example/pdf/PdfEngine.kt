package com.example.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import com.example.model.PdfDocumentItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object PdfEngine {
    private const val TAG = "PdfEngine"
    private val renderMutex = Mutex()

    /**
     * Initializes the bundled official documents from assets into internal storage.
     */
    suspend fun initializeBundledDocuments(context: Context): List<PdfDocumentItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<PdfDocumentItem>()

        // 1. Official Cahier des Charges provided by user
        val cahierFile = File(context.filesDir, "Cahier_des_charges_Palezza_Reader.pdf")
        if (!cahierFile.exists() || cahierFile.length() == 0L) {
            try {
                context.assets.open("cahier_des_charges_palezza.pdf").use { input ->
                    FileOutputStream(cahierFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error copying cahier des charges from assets", e)
            }
        }

        if (cahierFile.exists() && cahierFile.length() > 0) {
            val pages = getPageCount(cahierFile.absolutePath)
            list.add(
                PdfDocumentItem(
                    id = "doc_cahier_charges",
                    title = "Cahier des charges - Palezza Reader",
                    filePath = cahierFile.absolutePath,
                    fileSizeBytes = cahierFile.length(),
                    pageCount = pages,
                    lastOpenedTimestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                    lastPageRead = 1,
                    isFavorite = true,
                    folder = "Spécifications",
                    tags = listOf("Officiel", "Version 1.0", "Palezza Network")
                )
            )
        }

        // 2. Guide de démarrage rapide Palezza Reader (Généré programmatiquement comme vrai PDF)
        val guideFile = File(context.filesDir, "Guide_Demarrage_Palezza_Reader.pdf")
        if (!guideFile.exists()) {
            createSampleGuidePdf(guideFile)
        }
        if (guideFile.exists()) {
            list.add(
                PdfDocumentItem(
                    id = "doc_guide_demarrage",
                    title = "Guide de démarrage rapide - Palezza Reader",
                    filePath = guideFile.absolutePath,
                    fileSizeBytes = guideFile.length(),
                    pageCount = getPageCount(guideFile.absolutePath),
                    lastOpenedTimestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                    lastPageRead = 1,
                    isFavorite = false,
                    folder = "Documentation",
                    tags = listOf("Guide", "Bienvenue", "Tutoriel")
                )
            )
        }

        // 3. Contrat de partenariat type Palezza Network
        val contratFile = File(context.filesDir, "Contrat_Partenariat_Palezza_Network.pdf")
        if (!contratFile.exists()) {
            createSampleContractPdf(contratFile)
        }
        if (contratFile.exists()) {
            list.add(
                PdfDocumentItem(
                    id = "doc_contrat_partenariat",
                    title = "Contrat de partenariat - Palezza Network",
                    filePath = contratFile.absolutePath,
                    fileSizeBytes = contratFile.length(),
                    pageCount = getPageCount(contratFile.absolutePath),
                    lastOpenedTimestamp = System.currentTimeMillis() - 1000 * 60 * 360,
                    lastPageRead = 1,
                    isFavorite = true,
                    folder = "Contrats",
                    tags = listOf("Juridique", "Signature", "Partenariat")
                )
            )
        }

        list
    }

    /**
     * Obtains the page count of a real PDF using PdfRenderer.
     */
    fun getPageCount(filePath: String): Int {
        val file = File(filePath)
        if (!file.exists()) return 1
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        return try {
            pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(pfd)
            renderer.pageCount
        } catch (e: Exception) {
            Log.e(TAG, "Error getting page count for $filePath", e)
            1
        } finally {
            try { renderer?.close() } catch (_: Exception) {}
            try { pfd?.close() } catch (_: Exception) {}
        }
    }

    /**
     * Renders a specific 0-based page index to a crisp Bitmap.
     */
    suspend fun renderPage(
        filePath: String,
        pageIndex: Int,
        scaleFactor: Float = 2.0f
    ): Bitmap? = withContext(Dispatchers.IO) {
        renderMutex.withLock {
            val file = File(filePath)
            if (!file.exists()) return@withContext null

            var pfd: ParcelFileDescriptor? = null
            var renderer: PdfRenderer? = null
            var page: PdfRenderer.Page? = null

            try {
                pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                renderer = PdfRenderer(pfd)
                if (pageIndex < 0 || pageIndex >= renderer.pageCount) return@withContext null

                page = renderer.openPage(pageIndex)
                val targetWidth = (page.width * scaleFactor).toInt().coerceIn(300, 2400)
                val targetHeight = (page.height * scaleFactor).toInt().coerceIn(400, 3200)

                val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                // Draw white background
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmap
            } catch (e: Exception) {
                Log.e(TAG, "Error rendering page $pageIndex of $filePath", e)
                null
            } finally {
                try { page?.close() } catch (_: Exception) {}
                try { renderer?.close() } catch (_: Exception) {}
                try { pfd?.close() } catch (_: Exception) {}
            }
        }
    }

    /**
     * Copies a user-selected PDF Uri to app storage and registers it.
     */
    suspend fun importPdfFromUri(context: Context, uri: Uri): PdfDocumentItem? = withContext(Dispatchers.IO) {
        try {
            var fileName = "Document_${System.currentTimeMillis()}.pdf"
            var fileSize = 0L

            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        cursor.getString(nameIndex)?.let { fileName = it }
                    }
                    if (sizeIndex != -1) {
                        fileSize = cursor.getLong(sizeIndex)
                    }
                }
            }

            val sanitizedTitle = fileName.removeSuffix(".pdf").replace("_", " ")
            val destFile = File(context.filesDir, "imported_${UUID.randomUUID()}_$fileName")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }

            if (!destFile.exists() || destFile.length() == 0L) return@withContext null

            val pageCount = getPageCount(destFile.absolutePath)
            PdfDocumentItem(
                id = UUID.randomUUID().toString(),
                title = sanitizedTitle,
                filePath = destFile.absolutePath,
                fileSizeBytes = destFile.length(),
                pageCount = pageCount,
                lastOpenedTimestamp = System.currentTimeMillis(),
                lastPageRead = 1,
                isFavorite = false,
                folder = "Importés",
                tags = listOf("Importé", "PDF")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import PDF from Uri", e)
            null
        }
    }

    /**
     * Creates a genuine new PDF document on disk using Android's native PdfDocument.
     */
    suspend fun createNewPdfDocument(
        context: Context,
        title: String,
        templateType: String = "NOTE"
    ): PdfDocumentItem? = withContext(Dispatchers.IO) {
        try {
            val sanitized = title.trim().ifEmpty { "Nouveau Document" }
            val fileName = "Palezza_${sanitized.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            val destFile = File(context.filesDir, fileName)

            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawColor(Color.WHITE)

            // Header band in Bleu Nuit
            val paintHeader = Paint().apply { color = Color.parseColor("#0B192C") }
            canvas.drawRect(0f, 0f, 595f, 90f, paintHeader)

            val paintTitle = Paint().apply {
                color = Color.WHITE
                textSize = 20f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val paintBrand = Paint().apply {
                color = Color.parseColor("#0066FF")
                textSize = 11f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val paintBody = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 13f
                isAntiAlias = true
            }
            val paintMuted = Paint().apply {
                color = Color.parseColor("#64748B")
                textSize = 10f
                isAntiAlias = true
            }

            canvas.drawText(sanitized.uppercase(), 40f, 48f, paintTitle)
            canvas.drawText("PALEZZA READER — PALEZZA NETWORK", 40f, 70f, paintBrand)

            var y = 140f
            when (templateType) {
                "INVOICE" -> {
                    canvas.drawText("FACTURE N° FAC-" + System.currentTimeMillis().toString().takeLast(6), 40f, y, paintBody)
                    y += 30f
                    canvas.drawText("Émetteur : Palezza Network Brazzaville", 40f, y, paintBody)
                    y += 24f
                    canvas.drawText("Date d'émission : 7 novembre 2026", 40f, y, paintBody)
                    y += 36f
                    canvas.drawRect(40f, y, 555f, y + 36f, Paint().apply { color = Color.parseColor("#F1F5F9") })
                    canvas.drawText("Désignation des prestations", 55f, y + 24f, paintBody)
                    canvas.drawText("Total (FCFA)", 430f, y + 24f, paintBody)
                    y += 56f
                    canvas.drawText("1. Abonnement Suite Palezza Reader Pro", 55f, y, paintBody)
                    canvas.drawText("20 000 FCFA", 430f, y, paintBody)
                }
                "BLANK" -> {
                    canvas.drawText("Document vierge prêt pour la saisie et l'annotation.", 40f, y, paintBody)
                    y += 26f
                    canvas.drawText("Utilisez les outils d'annotation et de signature pour compléter ce document.", 40f, y, paintMuted)
                }
                else -> {
                    canvas.drawText("NOTE DE SERVICE OFFICIELLE", 40f, y, paintBody)
                    y += 28f
                    canvas.drawText("Objet : $sanitized", 40f, y, paintBody)
                    y += 24f
                    canvas.drawText("Date : 7 novembre 2026", 40f, y, paintBody)
                    y += 40f
                    canvas.drawText("Ce document professionnel a été créé avec succès dans Palezza Reader.", 40f, y, paintBody)
                    y += 20f
                    canvas.drawText("Il est prêt pour l'organisation de pages, l'annotation et l'archivage sécurisé.", 40f, y, paintMuted)
                }
            }

            // Footer
            canvas.drawLine(40f, 790f, 555f, 790f, Paint().apply { color = Color.parseColor("#E2E8F0") })
            canvas.drawText("Généré par Palezza Reader • Palezza Network Congo", 40f, 810f, paintMuted)

            document.finishPage(page)

            FileOutputStream(destFile).use { out ->
                document.writeTo(out)
            }
            document.close()

            PdfDocumentItem(
                id = UUID.randomUUID().toString(),
                title = sanitized,
                filePath = destFile.absolutePath,
                fileSizeBytes = destFile.length(),
                pageCount = 1,
                lastOpenedTimestamp = System.currentTimeMillis(),
                lastPageRead = 1,
                isFavorite = false,
                folder = "Créations",
                tags = listOf("Créé", "Palezza")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error creating new PDF", e)
            null
        }
    }

    /**
     * Creates a genuine sample Guide PDF using Android's native PdfDocument.
     */
    private fun createSampleGuidePdf(file: File) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 at 72 dpi
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        // Background
        canvas.drawColor(Color.WHITE)

        val paintPrimary = Paint().apply {
            color = Color.parseColor("#0B192C")
            textSize = 24f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintAccent = Paint().apply {
            color = Color.parseColor("#0066FF")
            textSize = 14f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintBody = Paint().apply {
            color = Color.parseColor("#334155")
            textSize = 12f
            isAntiAlias = true
        }

        // Header band
        val paintBand = Paint().apply { color = Color.parseColor("#0B192C") }
        canvas.drawRect(0f, 0f, 595f, 90f, paintBand)

        val paintHeaderTitle = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val paintHeaderSub = Paint().apply {
            color = Color.parseColor("#93C5FD")
            textSize = 11f
            isAntiAlias = true
        }

        canvas.drawText("PALEZZA READER", 40f, 45f, paintHeaderTitle)
        canvas.drawText("Guide officiel de démarrage rapide — Palezza Network", 40f, 68f, paintHeaderSub)

        var y = 130f
        canvas.drawText("Bienvenue sur Palezza Reader", 40f, y, paintPrimary)
        y += 24f
        canvas.drawText("Une solution moderne, accessible et conçue pour fonctionner en ligne comme hors connexion.", 40f, y, paintBody)
        y += 36f

        canvas.drawText("1. OUVRIR ET PARCOURIR UN DOCUMENT", 40f, y, paintAccent)
        y += 20f
        canvas.drawText("• Appuyez sur « Ouvrir un PDF » depuis le tableau de bord pour importer n'importe quel fichier.", 40f, y, paintBody)
        y += 18f
        canvas.drawText("• Utilisez les commandes de zoom (+ / -) et la rotation pour un confort de lecture optimal.", 40f, y, paintBody)
        y += 18f
        canvas.drawText("• Naviguez grâce au slider de pages ou ouvrez la grille de miniatures en un tap.", 40f, y, paintBody)
        y += 34f

        canvas.drawText("2. GESTION DES DOCUMENTS ET DOSSIERS", 40f, y, paintAccent)
        y += 20f
        canvas.drawText("• Classez vos documents dans des dossiers personnalisés (Contrats, Projets, Factures).", 40f, y, paintBody)
        y += 18f
        canvas.drawText("• Ajoutez vos documents importants aux Favoris avec l'icône étoile.", 40f, y, paintBody)
        y += 18f
        canvas.drawText("• Retrouvez instantanément vos derniers fichiers consultés dans la section Récents.", 40f, y, paintBody)
        y += 34f

        canvas.drawText("3. ARCHITECTURE ET SÉCURITÉ PALEZZA NETWORK", 40f, y, paintAccent)
        y += 20f
        canvas.drawText("• Vos documents restent strictement privés et stockés localement sur votre appareil.", 40f, y, paintBody)
        y += 18f
        canvas.drawText("• Prêt pour la synchronisation sécurisée Palezza Cloud et le compte Palezza ID.", 40f, y, paintBody)
        y += 60f

        // Footer
        val paintFooter = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 10f
            isAntiAlias = true
        }
        canvas.drawLine(40f, 780f, 555f, 780f, paintFooter)
        canvas.drawText("Palezza Reader v1.0 — Développé sous la marque Palezza Network — 2026", 40f, 805f, paintFooter)

        document.finishPage(page)

        try {
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error writing guide PDF", e)
        } finally {
            document.close()
        }
    }

    /**
     * Creates a genuine sample Contract PDF using Android's native PdfDocument.
     */
    private fun createSampleContractPdf(file: File) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        canvas.drawColor(Color.WHITE)

        val paintTitle = Paint().apply {
            color = Color.parseColor("#0B192C")
            textSize = 20f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val paintSubtitle = Paint().apply {
            color = Color.parseColor("#0066FF")
            textSize = 13f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val paintText = Paint().apply {
            color = Color.parseColor("#1E293B")
            textSize = 11f
            isAntiAlias = true
        }
        val paintBox = Paint().apply {
            color = Color.parseColor("#F1F5F9")
            style = Paint.Style.FILL
        }

        canvas.drawText("CONTRAT DE PARTENARIAT COMMERCIAL", 40f, 60f, paintTitle)
        canvas.drawText("PALEZZA NETWORK — RÉPUBLIQUE DU CONGO", 40f, 85f, paintSubtitle)

        var y = 130f
        canvas.drawRoundRect(40f, 110f, 555f, 210f, 12f, 12f, paintBox)
        canvas.drawText("Entre les soussignés :", 55f, y, paintSubtitle)
        y += 20f
        canvas.drawText("1. La société PALEZZA NETWORK, immatriculée au RCCM de Brazzaville.", 55f, y, paintText)
        y += 18f
        canvas.drawText("2. Le Partenaire Professionnel désigné ci-après.", 55f, y, paintText)

        y = 250f
        canvas.drawText("ARTICLE 1 — OBJET DU CONTRAT", 40f, y, paintSubtitle)
        y += 20f
        canvas.drawText("Le présent accord a pour objet de définir les conditions de distribution et d'utilisation", 40f, y, paintText)
        y += 16f
        canvas.drawText("des solutions logicielles de la suite Palezza, dont l'application Palezza Reader.", 40f, y, paintText)

        y += 40f
        canvas.drawText("ARTICLE 2 — SIGNATURE ET VALIDATION", 40f, y, paintSubtitle)
        y += 20f
        canvas.drawText("Le document peut faire l'objet d'une apposition de signature visuelle via le module", 40f, y, paintText)
        y += 16f
        canvas.drawText("de signature électronique de Palezza Reader avant transmission sécurisée.", 40f, y, paintText)

        // Signature blocks
        y = 660f
        canvas.drawRect(40f, y, 260f, y + 100f, paintBox)
        canvas.drawRect(335f, y, 555f, y + 100f, paintBox)
        canvas.drawText("Pour Palezza Network", 55f, y + 25f, paintText)
        canvas.drawText("Lu et approuvé", 55f, y + 80f, paintText)
        canvas.drawText("Pour le Partenaire", 350f, y + 25f, paintText)
        canvas.drawText("Signature manuscrite / visuelle", 350f, y + 80f, paintText)

        document.finishPage(page)

        try {
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error writing contract PDF", e)
        } finally {
            document.close()
        }
    }
}
