package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.PalezzaBleuElectrique
import com.example.ui.theme.PalezzaBleuNuit
import com.example.ui.theme.PalezzaGrisBordure
import com.example.ui.theme.PalezzaGrisSurface
import com.example.ui.theme.PalezzaTexteMuted
import com.example.ui.theme.PalezzaTexteSecondaire

@Composable
fun StorageMeterCard(
    formattedUsed: String,
    formattedLimit: String,
    progress: Float,
    isPremium: Boolean,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedDetails by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, PalezzaGrisBordure, RoundedCornerShape(20.dp))
            .testTag("storage_meter_card"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Icon + Title + Plan Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PalezzaBleuNuit),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = PalezzaBleuElectrique,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Résumé du Stockage",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PalezzaBleuNuit
                        )
                        Text(
                            text = if (isPremium) "Palezza Cloud • 10 Go" else "Stockage Local • 2 Go",
                            style = MaterialTheme.typography.labelSmall,
                            color = PalezzaTexteMuted
                        )
                    }
                }

                // Plan Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isPremium) Color(0xFFFEF3C7) else PalezzaGrisSurface)
                        .border(
                            1.dp,
                            if (isPremium) Color(0xFFFDE68A) else PalezzaGrisBordure,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isPremium) "PREMIUM" else "GRATUIT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPremium) Color(0xFFB45309) else PalezzaBleuNuit
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Usage Metric Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedUsed,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalezzaBleuNuit
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "utilisés sur $formattedLimit",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = PalezzaTexteSecondaire
                    )
                }

                val percentage = (progress * 100).toInt().coerceAtLeast(1)
                Text(
                    text = "$percentage%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PalezzaBleuElectrique
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar (Bleu Électrique on light track)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PalezzaBleuElectrique,
                trackColor = PalezzaGrisSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Storage Categories Breakdown Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // PDF Docs
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PalezzaGrisSurface)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PalezzaBleuElectrique)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("PDF", fontSize = 10.sp, color = PalezzaTexteMuted)
                        Text(formattedUsed, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PalezzaBleuNuit)
                    }
                }

                // Cache
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PalezzaGrisSurface)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PalezzaBleuNuit)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Cache", fontSize = 10.sp, color = PalezzaTexteMuted)
                        Text("4,8 Mo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PalezzaBleuNuit)
                    }
                }

                // Libre
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PalezzaGrisSurface)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Disponible", fontSize = 10.sp, color = PalezzaTexteMuted)
                        Text(if (isPremium) "9,9 Go" else "1,9 Go", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PalezzaBleuNuit)
                    }
                }
            }

            // Expandable details toggle
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedDetails = !expandedDetails }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (expandedDetails) "Masquer les détails" else "Détails de l'espace disque",
                    fontSize = 12.sp,
                    color = PalezzaBleuElectrique,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (expandedDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = PalezzaBleuElectrique,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = expandedDetails) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PalezzaGrisSurface)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "• Emplacement : Stockage interne sécurisé de l'application",
                        fontSize = 11.sp,
                        color = PalezzaTexteSecondaire
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Sauvegarde locale automatique (Offline-First)",
                        fontSize = 11.sp,
                        color = PalezzaTexteSecondaire
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Prêt pour la synchronisation Cloudflare R2 & Supabase",
                        fontSize = 11.sp,
                        color = PalezzaTexteSecondaire
                    )
                }
            }

            // Upgrade Banner if not premium
            if (!isPremium) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onUpgradeClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PalezzaBleuNuit,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("upgrade_premium_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Étendre à 10 Go avec Palezza Premium",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
