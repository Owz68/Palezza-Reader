package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PalezzaBleuElectrique
import com.example.ui.theme.PalezzaBleuNuit
import com.example.ui.theme.PalezzaGrisBordure
import com.example.ui.theme.PalezzaGrisSurface
import com.example.viewmodel.PalezzaUiState
import com.example.viewmodel.PalezzaViewModel

@Composable
fun PdfReaderScreen(
    state: PalezzaUiState,
    viewModel: PalezzaViewModel,
    modifier: Modifier = Modifier
) {
    val reader = state.readerState
    val doc = reader.activeDocument ?: return

    BackHandler {
        viewModel.closeDocument()
    }

    var panX by remember { mutableFloatStateOf(0f) }
    var panY by remember { mutableFloatStateOf(0f) }

    val isBookmarked = reader.bookmarks.contains(reader.currentPage)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0))
            .testTag("pdf_reader_screen")
    ) {
        // Main PDF Page Display with Pinch-to-zoom and Pan
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        panX += pan.x
                        panY += pan.y
                        if (zoom != 1.0f) {
                            val newZoom = (reader.zoomLevel * zoom).coerceIn(0.75f, 3.5f)
                            viewModel.setZoom(newZoom)
                        }
                    }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { viewModel.toggleFullScreen() }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (reader.isLoading && state.renderedCurrentPageBitmap == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PalezzaBleuElectrique)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Rendu haute précision de la page...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PalezzaBleuNuit,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (state.renderedCurrentPageBitmap != null) {
                Image(
                    bitmap = state.renderedCurrentPageBitmap.asImageBitmap(),
                    contentDescription = "Page ${reader.currentPage} sur ${reader.totalPages}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = if (reader.isFullScreen) 16.dp else 72.dp)
                        .graphicsLayer(
                            scaleX = reader.zoomLevel,
                            scaleY = reader.zoomLevel,
                            rotationZ = reader.rotationDegrees.toFloat(),
                            translationX = panX,
                            translationY = panY
                        )
                        .shadow(12.dp, RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        // Top Controls Bar (Hidden in Fullscreen)
        AnimatedVisibility(
            visible = !reader.isFullScreen,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .shadow(4.dp),
                color = PalezzaBleuNuit
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = { viewModel.closeDocument() },
                            modifier = Modifier.testTag("reader_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Fermer le lecteur",
                                tint = Color.White
                            )
                        }

                        Column(modifier = Modifier.padding(start = 4.dp)) {
                            Text(
                                text = doc.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Page ${reader.currentPage} sur ${reader.totalPages}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PalezzaBleuElectrique
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Zoom Out
                        IconButton(
                            onClick = { viewModel.zoomOut() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomOut,
                                contentDescription = "Zoom arrière",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Zoom In
                        IconButton(
                            onClick = { viewModel.zoomIn() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "Zoom avant",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Rotate Clockwise
                        IconButton(
                            onClick = { viewModel.rotateClockwise() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RotateRight,
                                contentDescription = "Pivoter la page",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Thumbnails Grid Toggle
                        IconButton(
                            onClick = { viewModel.toggleThumbnails() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Miniatures des pages",
                                tint = if (reader.showThumbnails) PalezzaBleuElectrique else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Bookmark
                        IconButton(
                            onClick = { viewModel.toggleBookmark(reader.currentPage) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Marque-page",
                                tint = if (isBookmarked) Color(0xFFFBBF24) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Fullscreen
                        IconButton(
                            onClick = { viewModel.toggleFullScreen() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Mode plein écran",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Exit Fullscreen floating trigger
        if (reader.isFullScreen) {
            IconButton(
                onClick = { viewModel.toggleFullScreen() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Icon(
                    imageVector = Icons.Default.FullscreenExit,
                    contentDescription = "Quitter le plein écran",
                    tint = Color.White
                )
            }
        }

        // Thumbnails Strip (When toggled)
        AnimatedVisibility(
            visible = reader.showThumbnails && !reader.isFullScreen,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .shadow(8.dp),
                color = PalezzaBleuNuit.copy(alpha = 0.95f)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Aperçu des pages",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { viewModel.toggleThumbnails() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items((1..reader.totalPages).toList()) { pageNum ->
                            val isCurrent = pageNum == reader.currentPage
                            val thumb = state.pageThumbnails[pageNum]

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (isCurrent) 2.dp else 1.dp,
                                        color = if (isCurrent) PalezzaBleuElectrique else Color(0xFF334155),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(if (isCurrent) Color(0xFF1E293B) else Color(0xFF0F172A))
                                    .clickable { viewModel.goToPage(pageNum) }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp, 68.dp)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (thumb != null) {
                                        Image(
                                            bitmap = thumb.asImageBitmap(),
                                            contentDescription = "Miniature page $pageNum",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(
                                            text = "$pageNum",
                                            fontWeight = FontWeight.Bold,
                                            color = PalezzaBleuNuit,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Text(
                                    text = "p. $pageNum",
                                    fontSize = 10.sp,
                                    color = if (isCurrent) PalezzaBleuElectrique else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar with Page Slider (Hidden in Fullscreen)
        AnimatedVisibility(
            visible = !reader.isFullScreen,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .shadow(12.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Page
                    IconButton(
                        onClick = { viewModel.previousPage() },
                        enabled = reader.currentPage > 1,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PalezzaGrisSurface)
                            .testTag("reader_prev_page_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Page précédente",
                            tint = if (reader.currentPage > 1) PalezzaBleuNuit else Color(0xFFCBD5E1)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Page Slider
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Slider(
                            value = reader.currentPage.toFloat(),
                            onValueChange = { viewModel.goToPage(it.toInt()) },
                            valueRange = 1f..reader.totalPages.coerceAtLeast(1).toFloat(),
                            steps = (reader.totalPages - 2).coerceAtLeast(0),
                            colors = SliderDefaults.colors(
                                thumbColor = PalezzaBleuElectrique,
                                activeTrackColor = PalezzaBleuElectrique,
                                inactiveTrackColor = Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier.testTag("reader_page_slider")
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Next Page
                    IconButton(
                        onClick = { viewModel.nextPage() },
                        enabled = reader.currentPage < reader.totalPages,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PalezzaGrisSurface)
                            .testTag("reader_next_page_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Page suivante",
                            tint = if (reader.currentPage < reader.totalPages) PalezzaBleuNuit else Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }
    }
}
