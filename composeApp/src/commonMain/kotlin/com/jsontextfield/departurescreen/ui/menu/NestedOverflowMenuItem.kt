// You can create a new file, e.g., NestedOverflowMenuItem.kt

package com.jsontextfield.departurescreen.ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * An item for an overflow menu that, when clicked, opens a nested sub-menu.
 *
 * @param icon The leading icon for the menu item.
 * @param tooltip The text to display for the menu item.
 * @param onDismissRequest A lambda to call when the parent overflow menu should be dismissed.
 * @param menuContent The composable content of the nested menu to show.
 */
@Composable
fun NestedOverflowMenuItem(
    icon: ImageVector,
    tooltip: String,
    onDismissRequest: () -> Unit,
    menuContent: @Composable (onDismiss: () -> Unit) -> Unit
) {
    // State to control the visibility of the nested menu
    var isSubMenuExpanded by remember { mutableStateOf(false) }

    // Use a Box to properly anchor the nested menu to its parent item
    Box {
        // This is the item the user sees and clicks in the main overflow menu
        DropdownMenuItem(
            text = { Text(text = tooltip) },
            leadingIcon = { Icon(icon, null) },
            onClick = { isSubMenuExpanded = true } // Click to open the nested menu
            // We don't need a trailing icon here, but one could be added (e.g., a right arrow)
        )

        // This is the nested menu that appears when the item is clicked
        DropdownMenu(
            expanded = isSubMenuExpanded,
            onDismissRequest = onDismissRequest  // Click outside to close sub-menu
        ) {
            // The content of the menu (e.g., RadioMenuItems for sorting)
            // It receives a lambda to dismiss EVERYTHING when an item is selected.
            menuContent(onDismissRequest)
        }
    }
}
