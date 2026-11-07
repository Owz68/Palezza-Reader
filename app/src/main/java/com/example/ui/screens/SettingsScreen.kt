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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.PalezzaBleuElectrique
import com.example.ui.theme.PalezzaBleuNuit
import com.example.ui.theme.PalezzaGrisBordure
import com.example.ui.theme.PalezzaGrisSurface
import com.example.ui.theme.PalezzaTexteMuted
import com.example.ui.theme.PalezzaTexteSecondaire
import com.example.viewmodel.PalezzaUiState
import com.example.viewmodel.PalezzaViewModel

@Composable
fun SettingsScreen(
    state: PalezzaUiState,
    viewModel: PalezzaViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var offlineSyncEnabled by remember { mutableStateOf(true) }
    var highQualityRender by remember { mutableStateOf(true) }
    var autoSaveAnnotations by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Paramètres de l'Application",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PalezzaBleuNuit
        )
        Text(
            text = "Personnalisez votre expérience de lecture et de gestion des documents",
            style = MaterialTheme.typography.bodySmall,
            color = PalezzaTexteSecondaire
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reader Preferences Section
        SettingsSectionHeader(title = "Préférences de Lecture")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PalezzaGrisSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SettingsToggleRow(
                    icon = Icons.Default.MenuBook,
                    title = "Rendu Haute Résolution",
                    subtitle = "Améliore la netteté du texte lors des zooms",
                    checked = highQualityRender,
                    onCheckedChange = { highQualityRender = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsToggleRow(
                    icon = Icons.Default.Sync,
                    title = "Sauvegarde Automatique de la Dernière Page",
                    subtitle = "Reprend automatiquement là où vous vous étiez arrêté",
                    checked = autoSaveAnnotations,
                    onCheckedChange = { autoSaveAnnotations = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Offline & Cloud Section
        SettingsSectionHeader(title = "Synchronisation & Données")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PalezzaGrisSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SettingsToggleRow(
                    icon = Icons.Default.Sync,
                    title = "Mode Hors-ligne Prioritaire",
                    subtitle = "Conserve tous vos fichiers et modifications localement",
                    checked = offlineSyncEnabled,
                    onCheckedChange = { offlineSyncEnabled = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = PalezzaBleuElectrique,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Cache des miniatures PDF",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = PalezzaBleuNuit
                            )
                            Text(
                                text = "Taille actuelle : 4.8 Mo",
                                style = MaterialTheme.typography.bodySmall,
                                color = PalezzaTexteMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, PalezzaGrisBordure, RoundedCornerShape(8.dp))
                            .clickable { /* Cleared */ }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Vider",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PalezzaBleuNuit
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About Palezza Network
        SettingsSectionHeader(title = "À Propos de Palezza Reader")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PalezzaBleuNuit)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.palezza_logo),
                        contentDescription = "Logo Palezza Reader",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Palezza Reader",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Version 1.0 (Phase 1) — 7 novembre 2026",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF93C5FD),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Projet porté par Palezza Network. Conçu pour proposer une alternative moderne, accessible et performante aux outils PDF traditionnels, adaptée aux téléphones, tablettes et ordinateurs.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "République du Congo • Brazzaville",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = PalezzaBleuNuit,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PalezzaBleuElectrique,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PalezzaBleuNuit
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = PalezzaTexteMuted,
                    fontSize = 11.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PalezzaBleuElectrique,
                uncheckedThumbColor = PalezzaGrisBordure,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}
