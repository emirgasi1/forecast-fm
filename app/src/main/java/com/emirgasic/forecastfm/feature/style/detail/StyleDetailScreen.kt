package com.emirgasic.forecastfm.feature.style.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.emirgasic.forecastfm.core.ui.components.common.DetailRow
import com.emirgasic.forecastfm.core.ui.components.common.LoadingScreen
import com.emirgasic.forecastfm.core.ui.components.common.TagChip
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun StyleDetailScreen(
    navController: NavController,
    outfitId: String?,
    modifier: Modifier = Modifier,
    viewModel: StyleDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val outfit by viewModel.outfit.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val forecastColors = LocalForecastColors.current

    LaunchedEffect(outfitId) {
        outfitId?.let {
            viewModel.loadOutfit(it)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 60.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingScreen()
            }
        } else {
            outfit?.let { item ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                ) {
                    Text(
                        text = "Back",
                        style = MaterialTheme.typography.titleMedium,
                        color = forecastColors.title,
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = forecastColors.shadow.copy(alpha = 0.3f),
                                spotColor = forecastColors.shadow.copy(alpha = 0.45f)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                color = forecastColors.border,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = forecastColors.title,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TagChip(text = item.weatherCondition)
                        TagChip(text = item.season)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Where to Buy",
                        style = MaterialTheme.typography.titleLarge,
                        color = forecastColors.title
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(
                        label = "Store",
                        value = item.storeName ?: "Not available"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow(
                        label = "Address",
                        value = item.storeAddress ?: "Not available"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow(
                        label = "Price",
                        value = item.price ?: "Not available"
                    )

                    if (!item.storePhone.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${item.storePhone}")
                                    }
                                    context.startActivity(intent)
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Phone",
                                style = MaterialTheme.typography.bodyLarge,
                                color = forecastColors.body
                            )
                            Text(
                                text = item.storePhone,
                                style = MaterialTheme.typography.bodyLarge,
                                color = forecastColors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val address = item.storeAddress ?: "Sarajevo"
                            val uri = "geo:0,0?q=${address.replace(" ", "+")}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = forecastColors.primary,
                            contentColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Text(
                            text = "Get Directions to Store",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (!item.productUrl.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.productUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, forecastColors.border)
                        ) {
                            Text(
                                text = "Buy Online",
                                style = MaterialTheme.typography.titleMedium,
                                color = forecastColors.title
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}