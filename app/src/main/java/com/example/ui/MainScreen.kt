package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.BuildConsoleScreen
import com.example.ui.screens.CodeExplorerScreen
import com.example.ui.screens.ConfiguratorScreen
import com.example.ui.screens.LiveWebViewScreen
import com.example.ui.screens.SavedProjectsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Web to APK Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "URL'den Android APK & Kaynak Kod Üretici",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { viewModel.setTab(StudioTab.COMPILER) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFF16A34A),
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("top_direct_download_apk_btn")
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "APK İndir",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "APK İndir",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = { viewModel.shareProjectDetails(context) },
                        modifier = Modifier.testTag("top_share_button")
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = "Projeyi Paylaş")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                tonalElevation = 4.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                StudioTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = {
                            val icon = when (tab) {
                                StudioTab.STUDIO -> if (isSelected) Icons.Filled.Tune else Icons.Outlined.Tune
                                StudioTab.PREVIEW -> if (isSelected) Icons.Filled.Smartphone else Icons.Outlined.Smartphone
                                StudioTab.COMPILER -> if (isSelected) Icons.Filled.BuildCircle else Icons.Outlined.BuildCircle
                                StudioTab.CODE_EXPLORER -> if (isSelected) Icons.Filled.Code else Icons.Outlined.Code
                                StudioTab.PROJECTS -> if (isSelected) Icons.Filled.Folder else Icons.Outlined.Folder
                            }
                            Icon(icon, contentDescription = tab.label)
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "tab_transition"
        ) { targetTab ->
            when (targetTab) {
                StudioTab.STUDIO -> ConfiguratorScreen(viewModel = viewModel)
                StudioTab.PREVIEW -> LiveWebViewScreen(viewModel = viewModel)
                StudioTab.COMPILER -> BuildConsoleScreen(viewModel = viewModel)
                StudioTab.CODE_EXPLORER -> CodeExplorerScreen(viewModel = viewModel)
                StudioTab.PROJECTS -> SavedProjectsScreen(viewModel = viewModel)
            }
        }
    }
}
