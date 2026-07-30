package com.emirgasic.forecastfm.core.ui.components.style.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp


@Composable
fun PostActionButtons(
    onPostClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
){

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){

        Button(
            onClick = onPostClick
        ){
            Text("Post")
        }


        Spacer(
            modifier = Modifier.width(10.dp)
        )


        Button(
            onClick = onDeleteClick
        ){
            Text("Delete")
        }
    }
}