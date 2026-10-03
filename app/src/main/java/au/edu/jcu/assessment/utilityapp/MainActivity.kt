package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MobileTech_UtilityAppTheme {
                UtilityApp()
            }
        }
    }
}

@Composable
fun UtilityApp() {
    val context = androidx.compose.ui.platform.LocalContext.current

    val walkWiseViewModel: WalkWiseViewModel = viewModel(
        factory = WalkWiseViewModelFactory(
            AppContainer.weatherRepository,
            context
        )
    )

    var selectedTab by remember { mutableStateOf("WalkWise") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null
                        )
                    },
                    label = { Text("WalkWise") },
                    selected = selectedTab == "WalkWise",
                    onClick = { selectedTab = "WalkWise" }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                "WalkWise" -> UtilityScreen(
                    maximumTemperature = walkWiseViewModel.maximumTemperature,
                    maximumUvIndex = walkWiseViewModel.maximumUvIndex,
                    allowLightRain = walkWiseViewModel.allowLightRain,
                    currentTemperature = walkWiseViewModel.currentTemperature,
                    currentUvIndex = walkWiseViewModel.currentUvIndex,
                    rainChance = walkWiseViewModel.rainChance,
                    windSpeed = walkWiseViewModel.windSpeed,
                    isLoading = walkWiseViewModel.isLoading,
                    errorMessage = walkWiseViewModel.errorMessage,
                    onRefresh = walkWiseViewModel::refreshWeather
                )

                "Settings" -> SettingsScreen(
                    maximumTemperature = walkWiseViewModel.maximumTemperature,
                    onMaximumTemperatureChange =
                        walkWiseViewModel::updateMaximumTemperature,
                    maximumUvIndex = walkWiseViewModel.maximumUvIndex,
                    onMaximumUvChange =
                        walkWiseViewModel::updateMaximumUvIndex,
                    allowLightRain = walkWiseViewModel.allowLightRain,
                    onAllowLightRainChange =
                        walkWiseViewModel::updateAllowLightRain
                )
            }
        }
    }
}

@Composable
fun UtilityScreen(
    maximumTemperature: Float,
    maximumUvIndex: Float,
    allowLightRain: Boolean,
    currentTemperature: Double,
    currentUvIndex: Double,
    rainChance: Int,
    windSpeed: Double,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit
) {
    val maximumRainChance = if (allowLightRain) 40 else 10

    val reasons = buildList {
        if (currentTemperature > maximumTemperature) {
            add("Temperature exceeds your ${maximumTemperature.toInt()}°C limit.")
        }
        if (currentUvIndex > maximumUvIndex) {
            add("Today's maximum UV exceeds your ${maximumUvIndex.toInt()} limit.")
        }
        if (rainChance > maximumRainChance) {
            add("Rain chance exceeds your $maximumRainChance% limit.")
        }
    }

    val conditionsAreSuitable = reasons.isEmpty()

    val recommendationTitle = when {
        isLoading -> "Checking conditions"
        errorMessage != null -> "Unable to update weather"
        conditionsAreSuitable -> "Good time for a walk"
        else -> "Conditions exceed your limits"
    }

    val recommendationDetail = when {
        isLoading -> "Fetching the latest weather for Brisbane."
        errorMessage != null ->
            "Try refreshing again. Displayed values may be out of date."
        conditionsAreSuitable ->
            "Temperature, UV and rain chance are within your preferences."
        else -> reasons.joinToString("\n")
    }

    val recommendationColor = when {
        isLoading || errorMessage != null ->
            MaterialTheme.colorScheme.surface
        conditionsAreSuitable ->
            MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "WalkWise",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Brisbane · Your walking outlook",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = recommendationColor
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "WALKING OUTLOOK",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = recommendationTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = recommendationDetail,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = "Weather overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ConditionCard(
                label = "Temperature",
                value = if (isLoading) "—" else
                    String.format(Locale.UK, "%.1f°C", currentTemperature),
                modifier = Modifier.weight(1f)
            )

            ConditionCard(
                label = "Today's max UV",
                value = if (isLoading) "—" else
                    String.format(Locale.UK, "%.1f", currentUvIndex),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ConditionCard(
                label = "Rain chance",
                value = if (isLoading) "—" else "$rainChance%",
                modifier = Modifier.weight(1f)
            )

            ConditionCard(
                label = "Wind",
                value = if (isLoading) "—" else
                    String.format(Locale.UK, "%.1f km/h", windSpeed),
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = onRefresh,
            enabled = !isLoading,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp)
        ) {
            Text(
                text = if (isLoading) "Updating weather…" else "Refresh weather",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = "Weather source: Open-Meteo. The outlook compares " +
                    "temperature, UV and rain chance with your preferences. " +
                    "Wind is shown for information.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ConditionCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.heightIn(min = 116.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SettingsScreen(
    maximumTemperature: Float,
    onMaximumTemperatureChange: (Float) -> Unit,
    maximumUvIndex: Float,
    onMaximumUvChange: (Float) -> Unit,
    allowLightRain: Boolean,
    onAllowLightRainChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Make your walking outlook personal.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        PreferenceCard {
            Text(
                text = "Temperature limit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "${maximumTemperature.toInt()}°C",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Your preferred maximum temperature for a walk.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Slider(
                value = maximumTemperature,
                onValueChange = onMaximumTemperatureChange,
                valueRange = 20f..40f,
                steps = 19
            )

            RangeLabels(start = "20°C", end = "40°C")
        }

        PreferenceCard {
            Text(
                text = "UV limit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = maximumUvIndex.toInt().toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Your preferred maximum UV index.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Slider(
                value = maximumUvIndex,
                onValueChange = onMaximumUvChange,
                valueRange = 1f..12f,
                steps = 10
            )

            RangeLabels(start = "1", end = "12")
        }

        PreferenceCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Rain tolerance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = if (allowLightRain) {
                            "Accept a rain chance up to 40%."
                        } else {
                            "Accept a rain chance up to 10%."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = allowLightRain,
                    onCheckedChange = onAllowLightRainChange
                )
            }
        }

        Text(
            text = "Changes apply immediately to your WalkWise outlook.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PreferenceCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun RangeLabels(
    start: String,
    end: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = start,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = end,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    MobileTech_UtilityAppTheme {
        UtilityScreen(
            maximumTemperature = 30f,
            maximumUvIndex = 8f,
            allowLightRain = true,
            currentTemperature = 24.6,
            currentUvIndex = 4.2,
            rainChance = 20,
            windSpeed = 9.0,
            isLoading = false,
            errorMessage = null,
            onRefresh = {}
        )
    }
}
