package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CodeTemplateRepository
import com.example.ui.AppViewModel
import com.example.ui.CodeCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeExplorerScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject.collectAsState()
    val category by viewModel.selectedCodeCategory.collectAsState()
    val fileIndex by viewModel.selectedCodeFileIndex.collectAsState()
    val context = LocalContext.current

    val codeFiles = remember(project, category) {
        when (category) {
            CodeCategory.FRONTEND -> CodeTemplateRepository.getFrontendFiles(project)
            CodeCategory.BACKEND -> CodeTemplateRepository.getBackendFiles(project)
            CodeCategory.ANDROID -> CodeTemplateRepository.getAndroidTemplateFiles(project)
            CodeCategory.DEPLOYMENT -> CodeTemplateRepository.getDeploymentFiles(project)
        }
    }

    val activeFile = codeFiles.getOrNull(fileIndex) ?: codeFiles.firstOrNull()

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Category Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CodeCategory.values().forEach { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { viewModel.setCodeCategory(cat) },
                    label = { Text(cat.label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    leadingIcon = {
                        val icon = when (cat) {
                            CodeCategory.FRONTEND -> Icons.Default.Web
                            CodeCategory.BACKEND -> Icons.Default.Dns
                            CodeCategory.ANDROID -> Icons.Default.Android
                            CodeCategory.DEPLOYMENT -> Icons.Default.CloudSync
                        }
                        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Sub-files selector (if category has multiple files)
        if (codeFiles.size > 1) {
            SecondaryScrollableTabRow(
                selectedTabIndex = fileIndex.coerceIn(0, codeFiles.lastIndex),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                codeFiles.forEachIndexed { idx, file ->
                    Tab(
                        selected = fileIndex == idx,
                        onClick = { viewModel.setCodeFileIndex(idx) },
                        text = {
                            Text(file.filename, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    )
                }
            }
        }

        // Active File Viewer
        if (activeFile != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF090D16)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E293B))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("code_viewer_card")
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header with file info & copy button
                    Surface(
                        color = Color(0xFF111827),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = activeFile.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = activeFile.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }

                            FilledTonalButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText(activeFile.filename, activeFile.content)
                                    clipboard.setPrimaryClip(clip)
                                    viewModel.showMessage("${activeFile.filename} panoya kopyalandı!")
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp).testTag("copy_code_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kopyala", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Divider(color = Color(0xFF1F2937))

                    // Code Body with Syntax Container
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .verticalScroll(verticalScroll)
                            .horizontalScroll(horizontalScroll)
                    ) {
                        SelectionContainer {
                            Text(
                                text = activeFile.content,
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
