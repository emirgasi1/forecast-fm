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
import androidx.lifecycle.viewmodel.compose.viewModel
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
    postId: String,
    modifier: Modifier = Modifier,
    viewModel: CommentsViewModel = viewModel()
) {

    val comments by viewModel.comments.collectAsState()

    LaunchedEffect(postId) {
        viewModel.loadComments(postId)
    }

    var commentText by remember {
        mutableStateOf("")
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),

        bottomBar = {

            CommentInputField(
                value = commentText,

                onValueChange = {
                    commentText = it
                },

                onSendClick = {

                    viewModel.addComment(
                        postId = postId,
                        text = commentText
                    )

                    commentText = ""
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

            items(comments) { comment ->

                CommentCard(
                    profileImage = painterResource(comment.user.profileImage),
                    username = comment.user.username,
                    time = comment.time,
                    comment = comment.text,
                    likes = comment.likes.toString()
                )

            }

        }

    }

}