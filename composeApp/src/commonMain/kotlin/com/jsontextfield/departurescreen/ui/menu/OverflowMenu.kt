import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import com.jsontextfield.departurescreen.ui.menu.Action
import com.jsontextfield.departurescreen.ui.menu.NestedOverflowMenuItem
import com.jsontextfield.departurescreen.ui.menu.OverflowMenuItem

@Composable
fun OverflowMenu(
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    actions: List<Action>,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest
    ) {
        actions.forEach { action ->
            // DECIDE WHICH MENU ITEM TO USE
            if (action.menuContent == null) {
                // This is a simple action with an onClick
                OverflowMenuItem(
                    icon = action.icon,
                    tooltip = action.tooltip,
                    onClick = {
                        action.onClick?.invoke()
                        onDismissRequest() // Close the overflow menu
                    }
                )
            } else {
                // This is a complex action that has a nested menu
                NestedOverflowMenuItem(
                    icon = action.icon,
                    tooltip = action.tooltip,
                    // Pass the main dismiss function to the nested menu
                    onDismissRequest = onDismissRequest,
                    menuContent = action.menuContent
                )
            }
        }
    }
}
