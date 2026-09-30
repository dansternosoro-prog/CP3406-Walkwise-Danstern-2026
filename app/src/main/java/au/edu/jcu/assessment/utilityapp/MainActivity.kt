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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme

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
    val walkWiseViewModel: WalkWiseViewModel = viewModel()
    var selectedTab by remember { mutableStateOf("WalkWise") }

    // Settings shared between both screens.
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "WalkWise"
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
                            contentDescription = "Settings"
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
                    allowLightRain = walkWiseViewModel.allowLightRain
                )

                "Settings" -> SettingsScreen(
                    maximumTemperature = walkWiseViewModel.maximumTemperature,
                    onMaximumTemperatureChange = walkWiseViewModel::updateMaximumTemperature,
                    maximumUvIndex = walkWiseViewModel.maximumUvIndex,
                    onMaximumUvChange = walkWiseViewModel::updateMaximumUvIndex,
                    allowLightRain = walkWiseViewModel.allowLightRain,
                    onAllowLightRainChange = walkWiseViewModel::updateAllowLightRain
                )
            }
        }
    }
}

@Composable
fun UtilityScreen(
    maximumTemperature: Float,
    maximumUvIndex: Float,
    allowLightRain: Boolean
) {
    // Temporary values used while we build the interface.
    // These will be replaced with live API data.
    val currentTemperature = 26.0
    val currentUvIndex = 4.2
    val rainChance = 20
    val windSpeed = 12

    val maximumRainChance = if (allowLightRain) 40 else 10
    val rainIsAcceptable = rainChance <= maximumRainChance

    val conditionsAreSuitable =
        currentTemperature <= maximumTemperature &&
                currentUvIndex <= maximumUvIndex &&
                rainIsAcceptable

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "WalkWise",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Brisbane • Sample conditions",
            style = MaterialTheme.typography.bodyMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (conditionsAreSuitable) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (conditionsAreSuitable) {
                        "Good time for a walk"
                    } else {
                        "Consider walking later"
                    },
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = if (conditionsAreSuitable) {
                        "The current conditions match your preferences."
                    } else {
                        "One or more conditions are outside your preferences."
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConditionCard(
                label = "Temperature",
                value = "$currentTemperature°C",
                modifier = Modifier.weight(1f)
            )

            ConditionCard(
                label = "UV index",
                value = currentUvIndex.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConditionCard(
                label = "Rain chance",
                value = "$rainChance%",
                modifier = Modifier.weight(1f)
            )

            ConditionCard(
                label = "Wind",
                value = "$windSpeed km/h",
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                // Live refresh will be connected to the API later.
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh conditions")
        }

        Text(
            text = "Live weather connection will be added next.",
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
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Walk preferences",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Maximum temperature: ${maximumTemperature.toInt()}°C"
        )

        Slider(
            value = maximumTemperature,
            onValueChange = onMaximumTemperatureChange,
            valueRange = 20f..40f,
            steps = 19
        )

        Text(
            text = "Maximum UV index: ${maximumUvIndex.toInt()}"
        )

        Slider(
            value = maximumUvIndex,
            onValueChange = onMaximumUvChange,
            valueRange = 1f..12f,
            steps = 10
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Allow light rain",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Accept a rain chance of up to 40%",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked = allowLightRain,
                onCheckedChange = onAllowLightRainChange
            )
        }

        Text(
            text = "These preferences immediately change the recommendation on the WalkWise screen.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    MobileTech_UtilityAppTheme {
        UtilityApp()
    }
}