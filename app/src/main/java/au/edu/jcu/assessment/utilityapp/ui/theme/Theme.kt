package au.edu.jcu.assessment.utilityapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WalkWiseColorScheme = darkColorScheme(
    primary = WalkLime,
    onPrimary = WalkOnLime,
    primaryContainer = WalkGreenContainer,
    onPrimaryContainer = WalkText,

    secondary = WalkLime,
    onSecondary = WalkOnLime,
    secondaryContainer = WalkGreenContainer,
    onSecondaryContainer = WalkLime,

    tertiary = WalkWarningText,
    onTertiary = WalkBackground,
    tertiaryContainer = WalkWarningContainer,
    onTertiaryContainer = WalkWarningText,

    background = WalkBackground,
    onBackground = WalkText,

    surface = WalkSurface,
    onSurface = WalkText,
    surfaceVariant = WalkSurfaceVariant,
    onSurfaceVariant = WalkMutedText,

    outline = WalkOutline,

    error = WalkError,
    onError = WalkBackground,
    errorContainer = WalkErrorContainer,
    onErrorContainer = WalkError
)

@Composable
fun MobileTech_UtilityAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WalkWiseColorScheme,
        typography = Typography,
        content = content
    )
}