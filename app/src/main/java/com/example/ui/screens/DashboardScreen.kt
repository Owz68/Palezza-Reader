package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.NavigationDestination
import com.example.model.PdfDocumentItem
import com.example.ui.components.DocumentItemCard
import com.example.ui.components.StorageMeterCard
import com.example.ui.theme.PalezzaBleuElectrique
import com.example.ui.theme.PalezzaBleuNuit
import com.example.ui.theme.PalezzaGrisBordure
import com.example.ui.theme.PalezzaGrisSurface
import com.example.ui.theme.PalezzaPdfRouge
import com.example.ui.theme.PalezzaTexteMuted
import com.example.ui.theme.PalezzaTexteSecondaire
import com.example.viewmodel.PalezzaUiState
import com.example.viewmodel.PalezzaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: PalezzaUiState,
    viewModel: PalezzaViewModel,
    onOpenFilePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreatePdfDialog by remember { mutableStateOf(false) }
    var newDocTitle by remember { mutableStateOf("Nouvelle Note de Service") }
    var selectedTemplate by remember { mutableStateOf("NOTE") } // "NOTE", "INVOICE", "BLANK"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Brand Hero Card
        item {
            Spacer(modifier = Modifier.height(4.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("dashboard_hero_card"),
                colors = CardDefaults.cardColors(containerColor = PalezzaBleuNuit),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PalezzaBleuElectrique)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PALEZZA READER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PalezzaBleuElectrique,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tableau de Bord",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lecture, édition, numérisation et gestion PDF de haute précision.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF93C5FD),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.palezza_logo),
                        contentDescription = "Logo Palezza Reader",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }

        // Section Title: Actions Rapides
        item {
            Text(
                text = "Actions Rapides",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PalezzaBleuNuit
            )
        }

        // The 3 Primary Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. 'Ouvrir un PDF'
                DashboardPrimaryActionButton(
                    title = "Ouvrir un PDF",
                    subtitle = "Parcourir",
                    icon = Icons.Default.FolderOpen,
                    onClick = onOpenFilePicker,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_open_pdf")
                )

                // 2. 'Scanner un document'
                DashboardPrimaryActionButton(
                    title = "Scanner un document",
                    subtitle = "Numériser",
                    icon = Icons.Default.DocumentScanner,
                    onClick = { viewModel.navigateTo(NavigationDestination.SCANNER) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_scan_document")
                )

                // 3. 'Créer un PDF'
                DashboardPrimaryActionButton(
                    title = "Créer un PDF",
                    subtitle = "Nouveau",
                    icon = Icons.Default.NoteAdd,
                    onClick = { showCreatePdfDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_create_pdf")
                )
            }
        }

        // Storage Summary Component (Composant de résumé de stockage)
        item {
            StorageMeterCard(
                formattedUsed = state.formattedStorageUsed,
                formattedLimit = state.formattedStorageLimit,
                progress = state.storageProgress,
                isPremium = state.isPremium,
                onUpgradeClick = { viewModel.upgradeToPremium() }
            )
        }

        // Quick PDF Tools Suite
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Outils PDF Fréquents",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PalezzaBleuNuit
                    )
                    TextButton(onClick = { viewModel.navigateTo(NavigationDestination.TOOLS) }) {
                        Text(
                            text = "Tous les outils",
                            color = PalezzaBleuElectrique,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = PalezzaBleuElectrique,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tools = listOf(
                        Triple("Fusionner", Icons.Default.MergeType, PalezzaBleuElectrique),
                        Triple("Compresser", Icons.Default.Compress, Color(0xFF059669)),
                        Triple("Signer", Icons.Default.Draw, Color(0xFF7C3AED)),
                        Triple("Annoter", Icons.Default.NoteAlt, Color(0xFFEA580C)),
                        Triple("Sécuriser", Icons.Default.Shield, PalezzaBleuNuit)
                    )
                    items(tools) { (label, icon, color) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(PalezzaGrisSurface)
                                .border(1.dp, PalezzaGrisBordure, RoundedCornerShape(14.dp))
                                .clickable { viewModel.navigateTo(NavigationDestination.TOOLS) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = color,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = PalezzaBleuNuit
                            )
                        }
                    }
                }
            }
        }

        // Recent Documents Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Documents Récents",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PalezzaBleuNuit
                )
                TextButton(onClick = { viewModel.navigateTo(NavigationDestination.RECENTS) }) {
                    Text(
                        text = "Voir tout (${state.recentDocuments.size})",
                        color = PalezzaBleuElectrique,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        items(state.recentDocuments.take(3)) { doc ->
            DocumentItemCard(
                document = doc,
                onClick = { viewModel.openDocument(doc) },
                onToggleFavorite = { viewModel.toggleFavorite(doc.id) },
                onDelete = { viewModel.deleteDocument(doc.id) },
                onRename = { newTitle -> viewModel.renameDocument(doc.id, newTitle) }
            )
        }

        // Favorites Section
        if (state.favoriteDocuments.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Documents Favoris",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PalezzaBleuNuit
                        )
                    }
                    TextButton(onClick = { viewModel.navigateTo(NavigationDestination.FAVORITES) }) {
                        Text(
                            text = "Voir tout (${state.favoriteDocuments.size})",
                            color = PalezzaBleuElectrique,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            items(state.favoriteDocuments.take(2)) { doc ->
                DocumentItemCard(
                    document = doc,
                    onClick = { viewModel.openDocument(doc) },
                    onToggleFavorite = { viewModel.toggleFavorite(doc.id) },
                    onDelete = { viewModel.deleteDocument(doc.id) },
                    onRename = { newTitle -> viewModel.renameDocument(doc.id, newTitle) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }

    // Modal Dialog: Créer un PDF
    if (showCreatePdfDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePdfDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF2F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = PalezzaPdfRouge,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Créer un Document PDF",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PalezzaBleuNuit
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Choisissez le type de document et saisissez un titre :",
                        style = MaterialTheme.typography.bodySmall,
                        color = PalezzaTexteSecondaire
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newDocTitle,
                        onValueChange = { newDocTitle = it },
                        label = { Text("Titre du document") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PalezzaBleuElectrique,
                            unfocusedBorderColor = PalezzaGrisBordure
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Modèle de document :",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PalezzaBleuNuit
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val templates = listOf(
                        Pair("NOTE", "Note de Service"),
                        Pair("INVOICE", "Facture Pro"),
                        Pair("BLANK", "Page Vierge")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        templates.forEach { (type, label) ->
                            val isSel = selectedTemplate == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) PalezzaBleuNuit else PalezzaGrisSurface)
                                    .border(
                                        1.dp,
                                        if (isSel) PalezzaBleuNuit else PalezzaGrisBordure,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedTemplate = type }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color.White else PalezzaBleuNuit
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCreatePdfDialog = false
                        viewModel.createDocument(newDocTitle, selectedTemplate)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PalezzaBleuElectrique,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Générer le PDF", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePdfDialog = false }) {
                    Text("Annuler", color = PalezzaTexteSecondaire)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun DashboardPrimaryActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .border(1.2.dp, PalezzaBleuElectrique.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon container with Bleu Électrique accent styling
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PalezzaBleuElectrique.copy(alpha = 0.12f))
                    .border(1.dp, PalezzaBleuElectrique.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PalezzaBleuElectrique,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = PalezzaBleuNuit,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                minLines = 2,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = PalezzaTexteMuted,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
