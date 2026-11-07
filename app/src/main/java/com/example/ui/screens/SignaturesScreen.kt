package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.viewmodel.PalezzaUiState
import com.example.viewmodel.PalezzaViewModel

@Composable
fun SignaturesScreen(
    state: PalezzaUiState,
    viewModel: PalezzaViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val signaturePoints = remember { mutableStateListOf<Offset>() }
    var savedSignaturesCount by remember { mutableStateOf(1) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Signature Électronique Visuelle",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PalezzaBleuNuit
        )
        Text(
            text = "Créez et enregistrez votre signature manuscrite pour vos contrats et documents",
            style = MaterialTheme.typography.bodySmall,
            color = PalezzaTexteSecondaire
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Signature Pad
        Text(
            text = "Dessinez votre signature ci-dessous :",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = PalezzaBleuNuit
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PalezzaGrisSurface)
                .border(1.5.dp, PalezzaGrisBordure, RoundedCornerShape(20.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        signaturePoints.add(change.position)
                    }
                }
                .testTag("signature_canvas_pad")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Baseline guide
                drawLine(
                    color = Color(0xFFCBD5E1),
                    start = Offset(40f, size.height * 0.72f),
                    end = Offset(size.width - 40f, size.height * 0.72f),
                    strokeWidth = 1.5f
                )

                // Drawn strokes
                for (i in 0 until signaturePoints.size - 1) {
                    val p1 = signaturePoints[i]
                    val p2 = signaturePoints[i + 1]
                    if ((p1 - p2).getDistance() < 60f) {
                        drawLine(
                            color = Color(0xFF0F172A),
                            start = p1,
                            end = p2,
                            strokeWidth = 4.5f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            if (signaturePoints.isEmpty()) {
                Text(
                    text = "Glissez votre doigt ici pour signer",
                    color = PalezzaTexteMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Signature Pad Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    signaturePoints.clear()
                    saveMessage = null
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Effacer", color = PalezzaBleuNuit)
            }

            Button(
                onClick = {
                    if (signaturePoints.isNotEmpty()) {
                        savedSignaturesCount++
                        saveMessage = "Signature enregistrée dans votre bibliothèque privée !"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PalezzaBleuElectrique,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = signaturePoints.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Enregistrer")
            }
        }

        if (saveMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFECFDF5))
                    .padding(10.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = saveMessage!!,
                    fontSize = 12.sp,
                    color = Color(0xFF065F46),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Saved Signatures Library
        Text(
            text = "Bibliothèque de Signatures ($savedSignaturesCount)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PalezzaBleuNuit
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PalezzaGrisSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, PalezzaGrisBordure, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Draw,
                            contentDescription = null,
                            tint = PalezzaBleuElectrique,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Signature Principale",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PalezzaBleuNuit
                        )
                        Text(
                            text = "Créée le 7 nov. 2026 • Prête à l'apposition",
                            style = MaterialTheme.typography.bodySmall,
                            color = PalezzaTexteMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PalezzaBleuNuit)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Active", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Legal Compliance Disclaimer Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Information Légale",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "L'apposition visuelle d'une signature ne constitue pas automatiquement une signature électronique certifiée au sens légal. Pour les actes notariés ou juridiques stricts, vérifiez la valeur probante requise.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB45309),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
