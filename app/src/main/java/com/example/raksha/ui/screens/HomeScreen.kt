package com.example.raksha.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raksha.R

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val batteryLevel = rememberBatteryLevel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader()

        TestModeBanner()

        Spacer(modifier = Modifier.height(28.dp))

        SosButton(onClick = {
            Toast.makeText(context, "TEST MODE: SOS not sent", Toast.LENGTH_SHORT).show()
        })

        Spacer(modifier = Modifier.height(36.dp))

        SecondaryActions(
            onCallEmergency = {
                Toast.makeText(context, "Call Emergency – test mode", Toast.LENGTH_SHORT).show()
            },
            onCallTrusted = {
                Toast.makeText(context, "Call Trusted Contact – test mode", Toast.LENGTH_SHORT).show()
            },
            onAlarm = {
                Toast.makeText(context, "Alarm – test mode", Toast.LENGTH_SHORT).show()
            },
            onFakeCall = {
                Toast.makeText(context, "Fake Call – test mode", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(36.dp))

        StatusCardsGrid(batteryLevel = batteryLevel)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AppHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.home_app_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.home_status_line),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TestModeBanner() {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.home_test_mode_banner),
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SosButton(onClick: () -> Unit) {
    // Outer ring (larger, subtle)
    Box(
        modifier = Modifier
            .size(260.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        // Middle ring
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            // Main button
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .shadow(16.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.home_sos_title),
                        color = MaterialTheme.colorScheme.onError,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.home_sos_subtitle),
                        color = MaterialTheme.colorScheme.onError,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SecondaryActions(
    onCallEmergency: () -> Unit,
    onCallTrusted: () -> Unit,
    onAlarm: () -> Unit,
    onFakeCall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionButton(
                label = stringResource(R.string.home_action_call_emergency),
                icon = Icons.Filled.Call,
                isEmphatic = true,
                modifier = Modifier.weight(1f),
                onClick = onCallEmergency
            )
            ActionButton(
                label = stringResource(R.string.home_action_call_trusted),
                icon = Icons.Filled.People,
                isEmphatic = false,
                modifier = Modifier.weight(1f),
                onClick = onCallTrusted
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionButton(
                label = stringResource(R.string.home_action_alarm),
                icon = Icons.Filled.Notifications,
                isEmphatic = false,
                modifier = Modifier.weight(1f),
                onClick = onAlarm
            )
            ActionButton(
                label = stringResource(R.string.home_action_fake_call),
                icon = Icons.Filled.Phone,
                isEmphatic = false,
                modifier = Modifier.weight(1f),
                onClick = onFakeCall
            )
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: ImageVector,
    isEmphatic: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEmphatic) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            contentColor = if (isEmphatic) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = if (isEmphatic) 8.dp else 4.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier
                .size(20.dp)
                .padding(end = 6.dp)
        )
        Text(
            text = label,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun StatusCardsGrid(batteryLevel: Int?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatusCard(
                stringResource(R.string.status_gps_label),
                stringResource(R.string.status_gps_value),
                Icons.Filled.LocationOn,
                Color.Unspecified,
                Modifier.weight(1f)
            )
            StatusCard(
                stringResource(R.string.status_signal_label),
                stringResource(R.string.status_signal_value),
                Icons.Filled.SignalCellularAlt,
                Color.Green,
                Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            val batteryStatus = batteryLevel?.let { stringResource(R.string.status_battery_value, it) } ?: "…"
            val batteryColor = when {
                batteryLevel == null -> Color.Unspecified
                batteryLevel > 50 -> Color.Green
                batteryLevel > 20 -> Color(0xFFFFA500)
                else -> Color.Red
            }
            StatusCard(
                stringResource(R.string.status_battery_label),
                batteryStatus,
                Icons.Filled.BatteryChargingFull,
                batteryColor,
                Modifier.weight(1f)
            )
            StatusCard(
                stringResource(R.string.status_sos_status_label),
                stringResource(R.string.status_sos_status_value),
                Icons.Filled.Emergency,
                Color.Unspecified,
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatusCard(
    label: String,
    value: String,
    icon: ImageVector,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (statusColor != Color.Unspecified) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
