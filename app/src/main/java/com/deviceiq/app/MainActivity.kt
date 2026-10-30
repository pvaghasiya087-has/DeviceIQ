package com.deviceiq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deviceiq.app.ui.theme.DeviceIQTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.deviceiq.app.data.device.DeviceInfo
import com.deviceiq.app.data.device.DeviceInfoProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import com.deviceiq.app.data.battery.BatteryInfoProvider
import com.deviceiq.app.data.battery.BatteryRepository
import com.deviceiq.app.data.battery.local.DeviceIQDatabase
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DeviceIQTheme {
                DeviceIQApp()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceIQApp() {

    val context = LocalContext.current

    val deviceInfoProvider = remember {
        DeviceInfoProvider(context)
    }

    val batteryInfoProvider = remember {
        BatteryInfoProvider(context)
    }

    val database = remember {
        DeviceIQDatabase.getInstance(context)
    }

    val batteryRepository = remember {
        BatteryRepository(
            batteryInfoProvider = batteryInfoProvider,
            batterySnapshotDao = database.batterySnapshotDao()
        )
    }

    LaunchedEffect(Unit) {
        while (true) {

            batteryRepository.collectAndSaveSnapshot()

            delay(30 * 1000L)
        }
    }

    var deviceInfo by remember {
        mutableStateOf(deviceInfoProvider.getDeviceInfo())
    }

    LaunchedEffect(Unit) {
        while (true) {
            deviceInfo = deviceInfoProvider.getDeviceInfo()
            delay(5_000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DeviceIQ",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Device Intelligence",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { innerPadding ->

        DeviceIQDashboard(
            modifier = Modifier.padding(innerPadding),
            deviceInfo = deviceInfo
        )
    }
}

@Composable
fun DeviceIQDashboard(
    modifier: Modifier = Modifier,
    deviceInfo: DeviceInfo
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Device Overview",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        DeviceHealthCard(deviceInfo)
        Text(
            text = "Current Status",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        StatusGrid(deviceInfo)
        Text(
            text = "Things to Check",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        IssueCard(
            title = "Battery drain",
            description = "Battery analytics will appear here.",
            icon = Icons.Default.BatteryFull
        )

        IssueCard(
            title = "Storage",
            description = "Storage analysis will appear here.",
            icon = Icons.Default.Storage
        )

        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        QuickActions()

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun DeviceHealthCard(
    deviceInfo: DeviceInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "—",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = "Device Health",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = deviceInfo.deviceModel,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Android ${deviceInfo.androidVersion}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
@Composable
fun StatusGrid(
    deviceInfo: DeviceInfo
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                modifier = Modifier.weight(1f),
                title = "Battery",
                value = deviceInfo.batteryPercent?.let { "$it%" } ?: "—",
                icon = Icons.Default.BatteryFull
            )

            StatusCard(
                modifier = Modifier.weight(1f),
                title = "Storage",
                value = deviceInfo.storageUsedPercent?.let { "$it%" } ?: "—",
                icon = Icons.Default.Storage
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                modifier = Modifier.weight(1f),
                title = "Memory",
                value = deviceInfo.ramUsedPercent?.let { "$it%" } ?: "—",
                icon = Icons.Default.Memory
            )

            StatusCard(
                modifier = Modifier.weight(1f),
                title = "Temperature",
                value = deviceInfo.batteryTemperature?.let { "$it°C" } ?: "—",
                icon = Icons.Default.DeviceThermostat
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                modifier = Modifier.weight(1f),
                title = "Data",
                value = "—",
                icon = Icons.Default.DataUsage
            )

            StatusCard(
                modifier = Modifier.weight(1f),
                title = "Performance",
                value = "—",
                icon = Icons.Default.Bolt
            )
        }
    }
}

@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun IssueCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun QuickActions() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickAction(
            modifier = Modifier.weight(1f),
            title = "Torch",
            icon = Icons.Default.LightMode
        )

        QuickAction(
            modifier = Modifier.weight(1f),
            title = "Volume",
            icon = Icons.Default.VolumeUp
        )

        QuickAction(
            modifier = Modifier.weight(1f),
            title = "Lock",
            icon = Icons.Default.Lock
        )

        QuickAction(
            modifier = Modifier.weight(1f),
            title = "Test",
            icon = Icons.Default.BugReport
        )
    }
}

@Composable
fun QuickAction(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}