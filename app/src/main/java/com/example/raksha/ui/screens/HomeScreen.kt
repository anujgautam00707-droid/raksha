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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TestModeBanner()

        Spacer(modifier = Modifier.height(24.dp))

        SosButton(onClick = {
            Toast.makeText(context, "TEST MODE: SOS not sent", Toast.LENGTH_SHORT).show()
        })

        Spacer(modifier = Modifier.height(32.dp))

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

        Spacer(modifier = Modifier.height(32.dp))

        StatusCardsGrid()

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TestModeBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "⚠ TEST MODE — no real alerts are sent",
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SosButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(220.dp)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.error)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SOS",
                color = MaterialTheme.colorScheme.onError,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap for help",
                color = MaterialTheme.colorScheme.onError,
                fontSize = 14.sp
            )
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
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionButton("Call Emergency", Modifier.weight(1f), onCallEmergency)
            ActionButton("Call Trusted", Modifier.weight(1f), onCallTrusted)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionButton("Alarm", Modifier.weight(1f), onAlarm)
            ActionButton("Fake Call", Modifier.weight(1f), onFakeCall)
        }
    }
}

@Composable
private fun ActionButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text = text, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun StatusCardsGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatusCard("GPS", "Locating…", Modifier.weight(1f))
            StatusCard("Signal", "Strong", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatusCard("Battery", "82%", Modifier.weight(1f))
            StatusCard("SOS Status", "Inactive", Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatusCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
