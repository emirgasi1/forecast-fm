package com.emirgasic.forecastfm.core.ui.components.feed.comment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommentInputField(
    value: String,modifier:Modifier,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
){

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        placeholder = {
            Text("Add a comment...")
        },

        trailingIcon = {

            IconButton(
                onClick = onSendClick
            ){
                Text(
                    text = ">",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

        },

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),

        singleLine = true
    )
}