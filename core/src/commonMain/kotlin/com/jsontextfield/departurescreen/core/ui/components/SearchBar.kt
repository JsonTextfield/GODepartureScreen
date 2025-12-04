package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.clear
import departure_screen.core.generated.resources.round_clear_24
import departure_screen.core.generated.resources.round_search_24
import departure_screen.core.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchBar(textFieldState: TextFieldState) {
    BasicTextField(
        textFieldState,
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = Modifier
            .widthIn(max = 300.dp)
            .padding(vertical = 12.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(24.dp)
            ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            showKeyboardOnFocus = false
        ),
        decorator = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .heightIn(min = 56.dp)
            ) {
                Icon(painterResource(Res.drawable.round_search_24), contentDescription = null)
                Box(modifier = Modifier.weight(1f)) {
                    if (textFieldState.text.isEmpty()) {
                        Text(
                            stringResource(Res.string.search),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = .8f
                                )
                            )
                        )
                    }
                    innerTextField()
                }
                if (textFieldState.text.isNotEmpty()) {
                    IconButton(onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd("")
                    }) {
                        Icon(
                            painterResource(Res.drawable.round_clear_24),
                            contentDescription = stringResource(Res.string.clear),
                        )
                    }
                }
            }
        },
    )
}