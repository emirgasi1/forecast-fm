package com.emirgasic.forecastfm.core.ui.components.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveOptionsBottomSheet(
    onDismiss: () -> Unit,
    onSavePost: () -> Unit,
    onSavePlaylist: () -> Unit,
    onSaveStyle: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val forecastColors = LocalForecastColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Save to...",
                style = MaterialTheme.typography.titleLarge,
                color = forecastColors.title,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSavePost()
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.bookmark),
                    contentDescription = "Save Post",
                    modifier = Modifier.size(24.dp),
                    tint = forecastColors.body
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Save Post",
                        style = MaterialTheme.typography.titleMedium,
                        color = forecastColors.title
                    )
                    Text(
                        text = "Save this post to your collection",
                        style = MaterialTheme.typography.bodySmall,
                        color = forecastColors.muted
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSavePlaylist()
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.music),
                    contentDescription = "Save Playlist",
                    modifier = Modifier.size(24.dp),
                    tint = forecastColors.body
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Save Playlist",
                        style = MaterialTheme.typography.titleMedium,
                        color = forecastColors.title
                    )
                    Text(
                        text = "Save the playlist mentioned in this post",
                        style = MaterialTheme.typography.bodySmall,
                        color = forecastColors.muted
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSaveStyle()
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.clothes),
                    contentDescription = "Save Style",
                    modifier = Modifier.size(24.dp),
                    tint = forecastColors.body
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Save Style",
                        style = MaterialTheme.typography.titleMedium,
                        color = forecastColors.title
                    )
                    Text(
                        text = "Save the outfit/style from this post",
                        style = MaterialTheme.typography.bodySmall,
                        color = forecastColors.muted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}