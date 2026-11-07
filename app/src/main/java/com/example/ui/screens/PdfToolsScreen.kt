package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavigationDestination
import com.example.ui.theme.PalezzaBleuElectrique
import com.example.ui.theme.PalezzaBleuNuit
import com.example.ui.theme.PalezzaGrisBordure
import com.example.ui.theme.PalezzaGrisSurface
import com.example.ui.theme.PalezzaTexteMuted
import com.example.ui.theme.PalezzaTexteSecondaire
import com.example.viewmodel.PalezzaUiState
import com.example.viewmodel.PalezzaViewModel

data class PdfToolItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val phase: String,
    val destination: NavigationDestination? = null
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PdfToolsScreen(
    state: PalezzaUiState,
    viewModel: PalezzaViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val tools = listOf(
        PdfToolItem(
            id = "merge",
            title = "Fusionner des PDF",
            description = "Assembler plusieurs fichiers PDF en un seul document ordonné.",
            icon = Icons.Default.MergeType,
            color = Color(0xFF2563EB),
            phase = "Phase 2"
        ),
        PdfToolItem(
            id = "split",
            title = "Séparer / Diviser",
            description = "Extraire des plages de pages ou scinder un document volumineux.",
            icon = Icons.Default.Splitscreen,
            color = Color(0xFF7C3AED),
            phase = "Phase 2"
        ),
        PdfToolItem(
            id = "compress",
            title = "Compresser un PDF",
            description = "Réduire la taille du fichier tout en conservant une bonne lisibilité.",
            icon = Icons.Default.Compress,
            color = Color(0xFF059669),
            phase = "Phase 2"
        ),
        PdfToolItem(
            id = "image_to_pdf",
            title = "Images en PDF",
            description = "Créer un document PDF professionnel à partir de photos ou scans.",
            icon = Icons.Default.Image,
            color = Color(0xFFEA580C),
            phase = "Phase 2"
        ),
        PdfToolItem(
            id = "signature",
            title = "Signature Électronique",
            description = "Dessiner ou importer votre signature et l'apposer sur vos contrats.",
            icon = Icons.Default.Draw,
            color = Color(0xFFD97706),
            phase = "Phase 3",
            destination = NavigationDestination.SIGNATURES
        ),
        PdfToolItem(
            id = "security",
            title = "Protéger par Mot de passe",
            description = "Sécuriser vos PDF sensibles avec chiffrement de lecture.",
            icon = Icons.Default.Lock,
            color = Color(0xFF475569),
            phase = "Phase 2"
        ),
        PdfToolItem(
            id = "ai_assistant",
            title = "Assistant IA Documentaire",
            description = "Résumer, expliquer un passage et extraire les montants et dates clés.",
            icon = Icons.Default.AutoAwesome,
            color = PalezzaBleuElectrique,
            phase = "Phase 5"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Boîte à Outils PDF",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PalezzaBleuNuit
        )
        Text(
            text = "Tous les outils essentiels pour éditer, convertir et organiser vos documents",
            style = MaterialTheme.typography.bodySmall,
            color = PalezzaTexteSecondaire
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tool Cards
        tools.forEach { tool ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, PalezzaGrisBordure, RoundedCornerShape(16.dp))
                    .clickable {
                        if (tool.destination != null) {
                            viewModel.navigateTo(tool.destination)
                        } else if (state.documents.isNotEmpty()) {
                            viewModel.openDocument(state.documents.first())
                        }
                    }
                    .testTag("tool_card_${tool.id}"),
                colors = CardDefaults.cardColors(containerColor = PalezzaGrisSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tool.color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.title,
                            tint = tool.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = tool.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = PalezzaBleuNuit
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .border(1.dp, PalezzaGrisBordure, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tool.phase,
                                    fontSize = 10.sp,
                                    color = tool.color,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = tool.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = PalezzaTexteSecondaire,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
