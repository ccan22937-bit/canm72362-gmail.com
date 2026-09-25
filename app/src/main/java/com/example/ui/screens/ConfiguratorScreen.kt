package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppProject
import com.example.ui.AppViewModel
import com.example.ui.StudioTab

data class PresetItem(
    val name: String,
    val url: String,
    val colorHex: String,
    val iconEmoji: String,
    val desc: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguratorScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject.collectAsState()
    val scrollState = rememberScrollState()

    val presets = remember {
        listOf(
            PresetItem("Harita Rehberi", "https://www.openstreetmap.org", "#059669", "🗺️", "OpenStreetMap Açık Kaynak Harita"),
            PresetItem("Google Haritalar", "https://maps.google.com", "#2563EB", "📍", "Google Maps Mobil Web Arayüzü"),
            PresetItem("Wikipedia Mobil", "https://m.wikipedia.org", "#475569", "📚", "Çevrimiçi Ansiklopedi Portalı"),
            PresetItem("Leaflet Demo", "https://leafletjs.com/examples/quick-start/example.html", "#16A34A", "🧭", "İnteraktif JavaScript Harita Demosu"),
            PresetItem("Yemek & Sipariş", "https://getir.com", "#7C3AED", "🍔", "Hızlı Teslimat & E-Ticaret")
        )
    }

    var showAdvancedSettings by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                    )
                )
            ),
            modifier = Modifier.fillMaxWidth().testTag("hero_banner")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Web'den Android APK Üretici",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Web sitenizin linkini girin; tam ekran WebView, JavaScript, GPS/Harita ve zoom desteği olan Android APK'ya dönüştürün.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Quick Presets Selector
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Hızlı Şablonlar & Örnekler",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.take(3).forEach { preset ->
                    SuggestionChip(
                        onClick = {
                            viewModel.loadPreset(preset.name, preset.url, preset.colorHex)
                        },
                        label = {
                            Text("${preset.iconEmoji} ${preset.name}", fontSize = 12.sp)
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Configuration Form Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("config_form_card")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Uygulama Yapılandırması",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Target URL Input
                OutlinedTextField(
                    value = project.url,
                    onValueChange = { viewModel.updateUrl(it) },
                    label = { Text("Dönüştürülecek Web Sitesi URL'si") },
                    placeholder = { Text("https://www.openstreetmap.org") },
                    leadingIcon = {
                        Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (project.url.isNotBlank()) {
                            IconButton(onClick = { viewModel.updateUrl("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Temizle")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("url_input")
                )

                // App Name Input
                OutlinedTextField(
                    value = project.appName,
                    onValueChange = { viewModel.updateAppName(it) },
                    label = { Text("Uygulama Adı (App Name)") },
                    placeholder = { Text("Harita Rehberi") },
                    leadingIcon = {
                        Icon(Icons.Default.Smartphone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("app_name_input")
                )

                // Package Name Input
                OutlinedTextField(
                    value = project.packageName,
                    onValueChange = { viewModel.updatePackageName(it) },
                    label = { Text("Paket Adı (Package Name / ApplicationId)") },
                    placeholder = { Text("com.ornek.mapapp") },
                    leadingIcon = {
                        Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.fillMaxWidth().testTag("package_name_input")
                )

                // Version and Orientation Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = project.versionName,
                        onValueChange = { viewModel.updateVersion(it) },
                        label = { Text("Sürüm (v1.0.0)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = project.orientation,
                        onValueChange = { viewModel.setOrientation(it) },
                        label = { Text("Ekran Yönü") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Advanced WebView Toggles
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAdvancedSettings = !showAdvancedSettings },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "WebView Özellikleri & İzinler",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(
                                imageVector = if (showAdvancedSettings) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }

                        AnimatedVisibility(visible = showAdvancedSettings) {
                            Column(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SwitchRow(
                                    title = "JavaScript Desteği",
                                    subtitle = "Haritalar ve modern web uygulamaları için zorunlu",
                                    checked = project.enableJavaScript,
                                    onCheckedChange = { viewModel.toggleJs(it) }
                                )
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                SwitchRow(
                                    title = "GPS / Konum Servisi (Geolocation)",
                                    subtitle = "Harita sitelerinde anlık kullanıcı konumunu göster",
                                    checked = project.enableGeolocation,
                                    onCheckedChange = { viewModel.toggleGeolocation(it) }
                                )
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                SwitchRow(
                                    title = "Pinch-to-Zoom & Yakınlaştırma",
                                    subtitle = "Çift parmakla yakınlaştırma ve harita gezinmesi",
                                    checked = project.enableZoom,
                                    onCheckedChange = { viewModel.toggleZoom(it) }
                                )
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                SwitchRow(
                                    title = "Pull-to-Refresh (Yenileme)",
                                    subtitle = "Yukarı kaydırarak sayfayı yeniden yükle",
                                    checked = project.enablePullToRefresh,
                                    onCheckedChange = { viewModel.togglePullRefresh(it) }
                                )
                            }
                        }
                    }
                }

                // Action Buttons
                Button(
                    onClick = { viewModel.startBuildPipeline() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Uygulamayı Oluştur (APK Derle)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.setTab(StudioTab.PREVIEW) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("preview_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Canlı Test Et", fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.setTab(StudioTab.CODE_EXPLORER) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("code_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kaynak Kodlar", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag("switch_${title.take(6)}")
        )
    }
}
