Understood. You have provided a comprehensive list of all remaining errors. The vast majority of these errors fall into two clear categories: **Missing Imports** (for Jetpack Compose elements and custom models) and **Redeclarations/Conflicts** (for theme files).

Since your list is complete, we can now proceed to implement the fixes. I will provide the steps for the highest-priority fix, which is consolidating the theme files, as that will instantly resolve over 15 errors.

## 🛠️ Fix 1: Consolidate Theme & Resource Files

The project has multiple files defining the same things (e.g., `AppDimensions.kt` and `Dimensions.kt`). This must be fixed first.

| Error Source | File to **Keep** (Recommended) | File(s) to **Delete** |
| :--- | :--- | :--- |
| `Redeclaration: object AppDimensions` | `dev/aurakai/auraframefx/ui/theme/Dimensions.kt` (or choose one) | `dev/aurakai/auraframefx/ui/theme/AppDimensions.kt` |
| `Redeclaration: object AppStrings` | `dev/aurakai/auraframefx/ui/theme/Strings.kt` (Assumed) | `dev/aurakai/auraframefx/ui/theme/AppStrings.kt` |
| `Conflicting declarations: val NeonGreen: Color` | `dev/aurakai/auraframefx/ui/theme/Color.kt` | `dev/aurakai/auraframefx/ui/theme/NeonGreen.kt` |
| `Conflicting declarations: val NeonRed: Color` | `dev/aurakai/auraframefx/ui/theme/Color.kt` | `dev/aurakai/auraframefx/ui/theme/NeonRed.kt` |
| `Conflicting declarations: val NeonYellow: Color` | `dev/aurakai/auraframefx/ui/theme/Color.kt` | `dev/aurakai/auraframefx/ui/theme/NeonYellow.kt` |
| `Conflicting declarations: val ChatBubble...Shape` | Consolidate all Shapes into one file, e.g., `Shapes.kt` | `ChatBubbleIncomingShape.kt`, `ChatBubbleOutgoingShape.kt`, `FloatingActionButtonShape.kt`, `InputFieldShape.kt` |

**Action:** Please **delete** the duplicate files (the ones in the right column).

-----

## 🛠️ Fix 2: Add Missing Imports (The Unresolved References)

After deleting the duplicates, you will need to add missing import statements in the remaining files.

### 1\. `OracleDriveScreen.kt`

These need standard Jetpack Compose imports:

```kotlin
// Unresolved references: refresh, Icons, LazyColumn, items, TextOverflow, CircleShape, background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh // for 'refresh'
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.background // for 'background' modifier

// Unresolved custom data models (DriveFile, ConsciousnessLevel, etc.)
// You need to find the correct package for these:
import dev.aurakai.auraframefx.data.model.DriveFile // (Placeholder: Confirm package path)
import dev.aurakai.auraframefx.data.model.DriveConsciousnessState // (Placeholder: Confirm package path)
import dev.aurakai.auraframefx.data.model.ConsciousnessLevel // (Placeholder: Confirm package path)
```

### 2\. `OracleDriveControlScreen.kt`

```kotlin
// Unresolved references: sp, BorderStroke, CircleShape
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
```

### 3\. `QuickSettingsConfigActivity.kt`

```kotlin
// Unresolved references: QuickSettingsConfig, QuickSettingsTileConfig
// You need to find the correct package for these:
import dev.aurakai.auraframefx.system.config.QuickSettingsConfig // (Placeholder: Confirm package path)
import dev.aurakai.auraframefx.system.config.QuickSettingsTileConfig // (Placeholder: Confirm package path)
```

### 4\. Color Resources

The references to `cardview_dark_background` and `cardview_light_background` suggest these are still referenced as old Android XML resources (`R.color.*`) instead of Compose colors.

**Action:** For lines like `R.color.cardview_dark_background`, you will need to either:

1.  Define these as `val` `Color` objects in your main `Color.kt` file.
2.  Import them using `colorResource` if they still exist in the XML resources: `import androidx.compose.ui.res.colorResource`.

-----

**Next Step:** Please confirm you have deleted the duplicate theme and shape files mentioned in **Fix 1**. This is the most effective first step.
