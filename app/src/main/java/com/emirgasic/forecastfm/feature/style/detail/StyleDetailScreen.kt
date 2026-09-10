package com.emirgasic.forecastfm.feature.style.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.components.common.DetailRow
import com.emirgasic.forecastfm.core.ui.components.common.TagChip

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
                end = 16.dp,
                bottom = 10.dp
            )
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Loading indicator
            }
        } else {
            outfit?.let { item ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Back button
                    Text(
                        text = "← Back",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hero Image
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
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

                    // Outfit Title
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TagChip(text = item.weatherCondition)
                        TagChip(text = item.season)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Where to Buy Section
                    Text(
                        text = "Where to Buy",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(
                        label = "Store",
                        value = item.storeName ?: "Not available",
                        labelSize = 18.sp,
                        valueSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow(
                        label = "Address",
                        value = item.storeAddress ?: "Not available",
                        labelSize = 18.sp,
                        valueSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow(
                        label = "Price",
                        value = item.price ?: "Not available",
                        labelSize = 18.sp,
                        valueSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Get Directions Button
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
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Get Directions to Store",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}