package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.StorageMeterCard
import com.example.ui.theme.PalezzaBleuElectrique
import com.example.ui.theme.PalezzaBleuNuit
import com.example.ui.theme.PalezzaGrisBordure
import com.example.ui.theme.PalezzaGrisSurface
import com.example.ui.theme.PalezzaTexteMuted
import com.example.ui.theme.PalezzaTexteSecondaire
import com.example.viewmodel.PalezzaUiState
import com.example.viewmodel.PalezzaViewModel

@Composable
fun AccountScreen(
    state: PalezzaUiState,
    viewModel: PalezzaViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // User Profile Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("account_profile_card"),
            colors = CardDefaults.cardColors(containerColor = PalezzaBleuNuit)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(PalezzaBleuElectrique),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "P",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Utilisateur Palezza",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (state.isPremium) Color(0xFFF59E0B) else Color(0xFF334155))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (state.isPremium) "PREMIUM" else "GRATUIT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Palezza ID : PLZ-2026-8842",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF93C5FD),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Brazzaville, Congo",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Storage Quota
        StorageMeterCard(
            formattedUsed = state.formattedStorageUsed,
            formattedLimit = state.formattedStorageLimit,
            progress = state.storageProgress,
            isPremium = state.isPremium,
            onUpgradeClick = { viewModel.upgradeToPremium() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Subscription Plans (Gratuit vs Premium)
        Text(
            text = "Formules & Abonnements",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PalezzaBleuNuit
        )
        Text(
            text = "Étudié pour le Congo-Brazzaville avec compatibilité Mobile Money",
            style = MaterialTheme.typography.bodySmall,
            color = PalezzaTexteSecondaire
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Premium Offer Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, if (state.isPremium) Color(0xFF10B981) else PalezzaBleuElectrique, RoundedCornerShape(20.dp))
                .testTag("premium_offer_card"),
            colors = CardDefaults.cardColors(containerColor = PalezzaGrisSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Formule Palezza Premium",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PalezzaBleuNuit
                        )
                        Text(
                            text = "2 000 FCFA / mois ou 20 000 FCFA / an",
                            style = MaterialTheme.typography.bodySmall,
                            color = PalezzaBleuElectrique,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Sans engagement",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val features = listOf(
                    "Lecture et annotations professionnelles illimitées",
                    "Stockage Cloud étendu à 10 Go",
                    "Édition avancée et conversion multiformats",
                    "Reconnaissance de texte OCR étendue",
                    "Assistant IA documentaire (résumés et extractions)",
                    "Synchronisation multi-appareils (Android, iOS, PC, Web)"
                )

                features.forEach { feat ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feat,
                            style = MaterialTheme.typography.bodySmall,
                            color = PalezzaBleuNuit,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!state.isPremium) {
                    Button(
                        onClick = { viewModel.upgradeToPremium() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PalezzaBleuElectrique,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("activate_premium_btn")
                    ) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Activer l'offre Premium (Essai gratuit)", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFECFDF5))
                            .padding(10.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Votre abonnement Premium est actif.",
                            color = Color(0xFF065F46),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Cloud & Connected Devices
        Text(
            text = "Appareils Connectés",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PalezzaBleuNuit
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PalezzaGrisSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = PalezzaBleuElectrique,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cet appareil (Android)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PalezzaBleuNuit
                    )
                    Text(
                        text = "Session locale active • Mode Offline-first",
                        style = MaterialTheme.typography.bodySmall,
                        color = PalezzaTexteMuted,
                        fontSize = 11.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("En ligne", color = Color(0xFF166534), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
