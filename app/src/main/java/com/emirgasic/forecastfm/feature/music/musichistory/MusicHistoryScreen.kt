package com.emirgasic.forecastfm.feature.music.musichistory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.music.musichistory.HistorySection
import com.emirgasic.forecastfm.core.ui.components.music.musichistory.MusicHistoryEntryCard

@Composable
fun MusicHistoryScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MusicHistoryViewModel = viewModel()
) {

    val history by viewModel.history.collectAsState()
    val groupedHistory = history.groupBy { it.section }
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    )
    {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {


            groupedHistory.forEach { (section, entries) ->

                item {
                    SectionTitle(
                        title = section
                    )
                }


                items(entries) { entry ->

                    MusicHistoryEntryCard(
                        playlist = entry.title,
                        weatherIcon = painterResource(entry.weatherIcon),
                        weather = entry.weather,
                        temperature = entry.temperature,
                        location = entry.location,
                        time = entry.time
                    )

                }
            }
        }
    }}