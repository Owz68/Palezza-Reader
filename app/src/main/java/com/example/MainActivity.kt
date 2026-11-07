package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.NavigationDestination
import com.example.ui.components.PalezzaBottomBar
import com.example.ui.components.PalezzaDrawerContent
import com.example.ui.components.PalezzaTopBar
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocumentsLibraryScreen
import com.example.ui.screens.PdfReaderScreen
import com.example.ui.screens.PdfToolsScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignaturesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.PalezzaViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: PalezzaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                PalezzaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PalezzaApp(viewModel: PalezzaViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // System PDF File Picker launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importDocument(uri)
        }
    }

    // Show notifications from ViewModel
    LaunchedEffect(state.userNotification) {
        state.userNotification?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearNotification()
        }
    }

    // If a document is active in reader, show full-bleed PDF reader
    if (state.readerState.activeDocument != null) {
        PdfReaderScreen(
            state = state,
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                PalezzaDrawerContent(
                    currentDestination = state.currentDestination,
                    onNavigate = { destination ->
                        when (destination) {
                            NavigationDestination.RECENTS -> {
                                viewModel.setFilterTab("Récents")
                                viewModel.navigateTo(NavigationDestination.DOCUMENTS)
                            }
                            NavigationDestination.FAVORITES -> {
                                viewModel.setFilterTab("Favoris")
                                viewModel.navigateTo(NavigationDestination.DOCUMENTS)
                            }
                            NavigationDestination.FOLDERS -> {
                                viewModel.setFilterTab("Dossiers")
                                viewModel.navigateTo(NavigationDestination.DOCUMENTS)
                            }
                            else -> viewModel.navigateTo(destination)
                        }
                    },
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    PalezzaTopBar(
                        title = state.currentDestination.title,
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        onAccountClick = {
                            viewModel.navigateTo(NavigationDestination.ACCOUNT)
                        },
                        onSearchClick = {
                            viewModel.navigateTo(NavigationDestination.DOCUMENTS)
                        }
                    )
                },
                bottomBar = {
                    PalezzaBottomBar(
                        currentDestination = state.currentDestination,
                        onNavigate = { destination ->
                            viewModel.navigateTo(destination)
                        }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = Color.White
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(Color.White)
                ) {
                    when (state.currentDestination) {
                        NavigationDestination.DASHBOARD -> {
                            DashboardScreen(
                                state = state,
                                viewModel = viewModel,
                                onOpenFilePicker = {
                                    pdfPickerLauncher.launch(arrayOf("application/pdf"))
                                }
                            )
                        }
                        NavigationDestination.DOCUMENTS,
                        NavigationDestination.RECENTS,
                        NavigationDestination.FAVORITES,
                        NavigationDestination.FOLDERS -> {
                            DocumentsLibraryScreen(
                                state = state,
                                viewModel = viewModel
                            )
                        }
                        NavigationDestination.TOOLS -> {
                            PdfToolsScreen(
                                state = state,
                                viewModel = viewModel
                            )
                        }
                        NavigationDestination.SCANNER -> {
                            ScannerScreen(
                                state = state,
                                viewModel = viewModel
                            )
                        }
                        NavigationDestination.SIGNATURES -> {
                            SignaturesScreen(
                                state = state,
                                viewModel = viewModel
                            )
                        }
                        NavigationDestination.ACCOUNT -> {
                            AccountScreen(
                                state = state,
                                viewModel = viewModel
                            )
                        }
                        NavigationDestination.SETTINGS -> {
                            SettingsScreen(
                                state = state,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
