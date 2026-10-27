package com.example.raksha.ui.screens

import androidx.compose.ui.res.stringResource
import com.example.raksha.R
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.compose.foundation.background
import androidx.compose.material3.RadioButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.raksha.data.AppSettings

private enum class SettingsDialog { Name, Number, Language, About, Privacy }

private val RedTint = Color(0xFFE5383B)
private val GreenTint = Color(0xFF43A047)
private val OrangeTint = Color(0xFFFB8C00)
private val IndigoTint = Color(0xFF7C83FD)
private val BlueTint = Color(0xFF29B6F6)
private val TealTint = Color(0xFF26A69A)

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var dialog by remember { mutableStateOf<SettingsDialog?>(null) }
    val isDark = AppSettings.darkMode ?: isSystemInDarkTheme()
    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "?"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(
            stringResource(R.string.settings_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))

        ProfileCard(name = AppSettings.userName, onClick = { dialog = SettingsDialog.Name })

        SectionLabel(stringResource(R.string.settings_section_emergency))
        SettingsGroup {
            SettingsRow(
                icon = Icons.Filled.Phone, tint = RedTint,
                title = stringResource(R.string.settings_emergency_number_title),
                subtitle = AppSettings.emergencyNumber,
                onClick = { dialog = SettingsDialog.Number }
            ) { Chevron() }
            RowDivider()
            SettingsRow(
                icon = Icons.Filled.LocationOn, tint = GreenTint,
                title = stringResource(R.string.settings_share_gps_title),
                subtitle = stringResource(R.string.settings_share_gps_subtitle),
                onClick = { AppSettings.updateShareGps(!AppSettings.shareGps) }
            ) {
                RakshaSwitch(checked = AppSettings.shareGps, onCheckedChange = { AppSettings.updateShareGps(it) })
            }
            RowDivider()
            SettingsRow(
                icon = Icons.AutoMirrored.Filled.VolumeUp, tint = OrangeTint,
                title = stringResource(R.string.settings_alarm_siren_title),
                subtitle = stringResource(R.string.settings_alarm_siren_subtitle),
                onClick = { AppSettings.updateSirenEnabled(!AppSettings.sirenEnabled) }
            ) {
                RakshaSwitch(checked = AppSettings.sirenEnabled, onCheckedChange = { AppSettings.updateSirenEnabled(it) })
            }
        }

        SectionLabel(stringResource(R.string.settings_section_preferences))
        SettingsGroup {
            SettingsRow(
                icon = Icons.Filled.DarkMode, tint = IndigoTint,
                title = stringResource(R.string.settings_dark_theme_title),
                subtitle = if (isDark) stringResource(R.string.settings_dark_theme_on) else stringResource(R.string.settings_dark_theme_off),
                onClick = { AppSettings.updateDarkMode(!isDark) }
            ) {
                RakshaSwitch(checked = isDark, onCheckedChange = { AppSettings.updateDarkMode(it) })
            }
            RowDivider()
            SettingsRow(
                icon = Icons.Filled.Language, tint = BlueTint,
                title = stringResource(R.string.settings_language_title),
                subtitle = appLanguages.find { it.tag == currentLanguageTag(context) }?.nativeName ?: "English",
                onClick = { dialog = SettingsDialog.Language }
            ) { Chevron() }
        }

        SectionLabel(stringResource(R.string.settings_section_about))
        SettingsGroup {
            SettingsRow(
                icon = Icons.Filled.Shield, tint = RedTint,
                title = stringResource(R.string.settings_about_title),
                subtitle = stringResource(R.string.settings_about_version, version),
                onClick = { dialog = SettingsDialog.About }
            ) { Chevron() }
            RowDivider()
            SettingsRow(
                icon = Icons.Filled.Lock, tint = TealTint,
                title = stringResource(R.string.settings_privacy_title),
                subtitle = stringResource(R.string.settings_privacy_subtitle),
                onClick = { dialog = SettingsDialog.Privacy }
            ) { Chevron() }
        }
        Spacer(Modifier.height(16.dp))
    }

    val close = { dialog = null }
    when (dialog) {
        SettingsDialog.Name -> EditTextDialog(
            title = stringResource(R.string.dialog_profile_title),
            initial = AppSettings.userName,
            keyboardType = KeyboardType.Text,
            onSave = { AppSettings.updateUserName(it) },
            onDismiss = close
        )
        SettingsDialog.Number -> EditTextDialog(
            title = stringResource(R.string.dialog_emergency_number_title),
            initial = AppSettings.emergencyNumber,
            keyboardType = KeyboardType.Phone,
            onSave = { AppSettings.updateEmergencyNumber(it) },
            onDismiss = close
        )
        SettingsDialog.Language -> LanguageDialog(onDismiss = close)
        SettingsDialog.About -> InfoDialog(
            title = stringResource(R.string.dialog_about_title),
            message = stringResource(R.string.dialog_about_message, version),
            onDismiss = close
        )
        SettingsDialog.Privacy -> InfoDialog(
            title = stringResource(R.string.dialog_privacy_title),
            message = stringResource(R.string.dialog_privacy_message),
            onDismiss = close
        )
        null -> Unit
    }
}

@Composable
private fun ProfileCard(name: String, onClick: () -> Unit) {
    SettingsGroup {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = name.ifBlank { stringResource(R.string.settings_profile_empty) },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.settings_profile_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 8.dp, top = 12.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(8.dp))
        trailing()
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun Chevron() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun EditTextDialog(
    title: String,
    initial: String,
    keyboardType: KeyboardType,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(text.trim()); onDismiss() },
                enabled = text.isNotBlank()
            ) { Text(stringResource(R.string.button_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) }
        }
    )
}

@Composable
private fun InfoDialog(title: String, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_ok)) } }
    )
}
private data class AppLanguage(val tag: String, val nativeName: String)

private val appLanguages = listOf(
    AppLanguage("en", "English"),
    AppLanguage("hi", "हिन्दी"),
    AppLanguage("zh", "中文"),
    AppLanguage("ja", "日本語"),
    AppLanguage("de", "Deutsch")
)

private fun currentLanguageTag(context: Context): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val locales = context.getSystemService(LocaleManager::class.java).applicationLocales
        if (!locales.isEmpty) return locales[0].language
    }
    return "en"
}

private fun setAppLanguage(context: Context, tag: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList.forLanguageTags(tag)
    }
}

@Composable
private fun LanguageDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        InfoDialog(
            title = stringResource(R.string.dialog_language_title),
            message = stringResource(R.string.dialog_language_unavailable_message),
            onDismiss = onDismiss
        )
        return
    }
    val current = remember { currentLanguageTag(context) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_language_select_title)) },
        text = {
            Column {
                appLanguages.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                setAppLanguage(context, lang.tag)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = lang.tag == current, onClick = null)
                        Spacer(Modifier.width(12.dp))
                        Text(lang.nativeName, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) } }
    )
}
@Composable
private fun RakshaSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
            uncheckedTrackColor = MaterialTheme.colorScheme.surface
        )
    )
}