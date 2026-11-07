package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DocumentItemCard
import com.example.ui.theme.PalezzaBleuElectrique
import com.example.ui.theme.PalezzaBleuNuit
import com.example.ui.theme.PalezzaGrisBordure
import com.example.ui.theme.PalezzaGrisSurface
import com.example.ui.theme.PalezzaTexteMuted
import com.example.ui.theme.PalezzaTexteSecondaire
import com.example.viewmodel.PalezzaUiState
import com.example.viewmodel.PalezzaViewModel

@Composable
fun DocumentsLibraryScreen(
    state: PalezzaUiState,
    viewModel: PalezzaViewModel,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }
    val tabScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Rechercher dans les documents...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = PalezzaBleuNuit,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Effacer la recherche",
                            tint = PalezzaTexteMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PalezzaGrisSurface,
                unfocusedContainerColor = PalezzaGrisSurface,
                focusedBorderColor = PalezzaBleuElectrique,
                unfocusedBorderColor = PalezzaGrisBordure
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("library_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filters and Sort Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Filter tabs
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(tabScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf("Tous", "Récents", "Favoris", "Dossiers")
                tabs.forEach { tab ->
                    val isSelected = state.activeFilterTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PalezzaBleuNuit else PalezzaGrisSurface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) PalezzaBleuNuit else PalezzaGrisBordure,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setFilterTab(tab) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("filter_tab_$tab")
                    ) {
                        Text(
                            text = tab,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else PalezzaBleuNuit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sort button
            Box {
                IconButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PalezzaGrisSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Trier les documents",
                        tint = PalezzaBleuNuit,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Trier par Date") },
                        onClick = {
                            viewModel.setSortBy("Date")
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Trier par Nom") },
                        onClick = {
                            viewModel.setSortBy("Nom")
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Trier par Taille") },
                        onClick = {
                            viewModel.setSortBy("Taille")
                            showSortMenu = false
                        }
                    )
                }
            }
        }

        // Folders list if "Dossiers" is selected
        if (state.activeFilterTab == "Dossiers") {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "Tous les dossiers" pill
                val allFoldersSelected = state.selectedFolder == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (allFoldersSelected) PalezzaBleuElectrique else PalezzaGrisSurface)
                        .clickable { viewModel.selectFolder(null) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Tous les dossiers",
                        fontSize = 12.sp,
                        color = if (allFoldersSelected) Color.White else PalezzaBleuNuit,
                        fontWeight = FontWeight.Medium
                    )
                }

                state.folders.forEach { folder ->
                    val isFolderSelected = state.selectedFolder == folder
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isFolderSelected) PalezzaBleuElectrique else PalezzaGrisSurface)
                            .clickable { viewModel.selectFolder(folder) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = if (isFolderSelected) Color.White else PalezzaTexteSecondaire,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = folder,
                            fontSize = 12.sp,
                            color = if (isFolderSelected) Color.White else PalezzaBleuNuit,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Document count header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${state.filteredDocuments.size} document(s)",
                style = MaterialTheme.typography.labelMedium,
                color = PalezzaTexteSecondaire,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Tri : ${state.sortBy}",
                style = MaterialTheme.typography.labelSmall,
                color = PalezzaTexteMuted
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Documents List
        if (state.filteredDocuments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PalezzaGrisSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = PalezzaTexteMuted,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aucun document trouvé",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PalezzaBleuNuit
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Importez un PDF ou modifiez vos critères de recherche",
                        style = MaterialTheme.typography.bodySmall,
                        color = PalezzaTexteMuted
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.filteredDocuments) { doc ->
                    DocumentItemCard(
                        document = doc,
                        onClick = { viewModel.openDocument(doc) },
                        onToggleFavorite = { viewModel.toggleFavorite(doc.id) },
                        onDelete = { viewModel.deleteDocument(doc.id) },
                        onRename = { newTitle -> viewModel.renameDocument(doc.id, newTitle) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
