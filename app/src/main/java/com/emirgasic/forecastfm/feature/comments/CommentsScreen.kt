package com.emirgasic.forecastfm.feature.comments
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.BottomBar
import com.emirgasic.forecastfm.core.ui.components.auth.AuthButton
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.feed.comment.CommentCard
import com.emirgasic.forecastfm.core.ui.components.feed.comment.CommentInputField

@Composable
fun CommentsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
){

    var comment by remember {
        mutableStateOf("")
    }


    Scaffold(

        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),

        bottomBar = {

            CommentInputField(
                value = comment,
                onValueChange = {
                    comment = it
                },
                onSendClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            )

        }

    ){ paddingValues ->

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){

            item {
                SectionTitle(
                    title = "Comments"
                )
            }

            item {

                CommentCard(
                    profileImage = painterResource(R.drawable.outfit2),
                    username = "Emir",
                    time = "5 min ago",
                    comment = "Nice outfit!",
                    likes = "12"
                )

            }

            item {

                CommentCard(
                    profileImage = painterResource(R.drawable.outfit1),
                    username = "Sara",
                    time = "10 min ago",
                    comment = "This playlist fits perfectly 🔥",
                    likes = "8"
                )

            }

        }

    }

}