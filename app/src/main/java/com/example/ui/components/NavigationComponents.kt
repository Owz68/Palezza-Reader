package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.NavigationDestination
import com.example.ui.theme.PalezzaBleuElectrique
import com.example.ui.theme.PalezzaBleuNuit
import com.example.ui.theme.PalezzaGrisBordure
import com.example.ui.theme.PalezzaGrisSurface
import com.example.ui.theme.PalezzaTexteMuted
import com.example.ui.theme.PalezzaTexteSecondaire

@Composable
fun PalezzaBottomBar(
    currentDestination: NavigationDestination,
    onNavigate: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Pair(NavigationDestination.DASHBOARD, Icons.Default.Home),
        Pair(NavigationDestination.DOCUMENTS, Icons.Default.Description),
        Pair(NavigationDestination.TOOLS, Icons.Default.Widgets),
        Pair(NavigationDestination.SCANNER, Icons.Default.CameraAlt),
        Pair(NavigationDestination.ACCOUNT, Icons.Default.AccountCircle)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp),
        color = Color.White
    ) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = PalezzaBleuNuit,
            modifier = Modifier.navigationBarsPadding(),
            tonalElevation = 0.dp
        ) {
            items.forEach { (destination, icon) ->
                val isSelected = currentDestination == destination

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(destination) },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = destination.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = destination.shortLabel,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = PalezzaBleuNuit,
                        indicatorColor = PalezzaBleuNuit,
                        unselectedIconColor = PalezzaTexteMuted,
                        unselectedTextColor = PalezzaTexteMuted
                    ),
                    modifier = Modifier.testTag("nav_item_${destination.id}")
                )
            }
        }
    }
}

@Composable
fun PalezzaDrawerContent(
    currentDestination: NavigationDestination,
    onNavigate: (NavigationDestination) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(320.dp),
        drawerContainerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.palezza_logo),
                    contentDescription = "Logo Palezza Reader",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Palezza Reader",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PalezzaBleuNuit
                    )
                    Text(
                        text = "Palezza Network • Suite Pro",
                        style = MaterialTheme.typography.labelSmall,
                        color = PalezzaBleuElectrique,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = PalezzaGrisBordure, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // The 10 App Spaces
            val spaces = listOf(
                Triple(NavigationDestination.DASHBOARD, Icons.Default.Home, "Tableau de bord"),
                Triple(NavigationDestination.DOCUMENTS, Icons.Default.Description, "Mes documents"),
                Triple(NavigationDestination.RECENTS, Icons.Default.History, "Fichiers récents"),
                Triple(NavigationDestination.FAVORITES, Icons.Default.Star, "Favoris"),
                Triple(NavigationDestination.FOLDERS, Icons.Default.Folder, "Mes dossiers"),
                Triple(NavigationDestination.TOOLS, Icons.Default.Widgets, "Outils PDF"),
                Triple(NavigationDestination.SCANNER, Icons.Default.CameraAlt, "Scanner"),
                Triple(NavigationDestination.SIGNATURES, Icons.Default.Draw, "Signatures"),
                Triple(NavigationDestination.SETTINGS, Icons.Default.Settings, "Paramètres"),
                Triple(NavigationDestination.ACCOUNT, Icons.Default.AccountCircle, "Mon compte Palezza")
            )

            spaces.forEach { (destination, icon, label) ->
                val isSelected = currentDestination == destination

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) Color.White else PalezzaBleuNuit,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        onNavigate(destination)
                        onCloseDrawer()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PalezzaBleuNuit,
                        selectedTextColor = Color.White,
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = PalezzaBleuNuit
                    ),
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("drawer_item_${destination.id}")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = PalezzaGrisBordure, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Footer
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Palezza Reader v1.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = PalezzaTexteMuted
                )
                Text(
                    text = "© 2026 Palezza Network. Tous droits réservés.",
                    style = MaterialTheme.typography.labelSmall,
                    color = PalezzaTexteMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
