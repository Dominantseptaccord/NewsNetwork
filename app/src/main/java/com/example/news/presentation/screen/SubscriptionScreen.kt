@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.news.presentation.screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.SelectableChipColors
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.news.domain.model.Article
import dagger.hilt.android.qualifiers.ApplicationContext


@Composable
fun Subscriptions(
    modifier: Modifier = Modifier,
    viewModel: SubscriptionViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit
){
    val state by viewModel.state.collectAsState()
    val articles = state.articles
    Scaffold(
        modifier = modifier,
        topBar = {
            SubscriptionTopBar(
                onRefreshClick = {
                    viewModel.processCommand(SubscriptionCommand.UpdateArticles)
                },
                onClearClick = {
                    viewModel.processCommand(SubscriptionCommand.ClearArticles)
                },
                onSettingsClick = {
                    onNavigateToSettings()
                }
            )
        }
    ){innerPadding ->
        LazyColumn(

            contentPadding = innerPadding
        ) {
            item {
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
                    value = state.topic,
                    onValueChange = {
                        viewModel.processCommand(SubscriptionCommand.InputTopic(it))
                    },
                    colors = TextFieldDefaults.textFieldColors(),
                    placeholder = {
                        Icons.Default.Add
                    }
                )
            }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    onClick = {
                        viewModel.processCommand(SubscriptionCommand.AddSubscription(state.topic))
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Subscription"
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            text = "Add Subscription"
                        )
                    },
                    shape = CircleShape,
                )
            }
            item {
                BulletChipSubscription(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    chips = state.subscriptions,
                    text = "Subscription",
                    onClick = {
                        viewModel.processCommand(SubscriptionCommand.ToggleSubscription(it))
                    },
                    onDeleted = {
                        viewModel.processCommand(SubscriptionCommand.DeleteSubscription(it))
                    }
                )
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Article (${articles.size})",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            items(items = articles){ article ->
                ArticleCard(
                    modifier = Modifier,
                    article = article
                )
            }
        }
    }


}
@Composable
fun SubscriptionTopBar(
    modifier: Modifier = Modifier,
    onRefreshClick: () -> Unit,
    onClearClick: () -> Unit,
    onSettingsClick: () -> Unit
){
    TopAppBar(
        title = { Text("My News") },
        actions = {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.clip(CircleShape).clickable {
                    onRefreshClick()
                }.padding(8.dp),
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh"
            )
            Icon(
                modifier = Modifier.clip(CircleShape).clickable {
                    onClearClick()

                }.padding(8.dp),
                imageVector = Icons.Default.Close,
                contentDescription = "Close"
            )
            Icon(
                modifier = Modifier.clip(CircleShape).clickable {
                    onSettingsClick()
                    Log.d("Blya", "lol")
                }.padding(8.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings"
            )
        }
    )
}
@Composable
fun BulletChipSubscription(
    modifier: Modifier = Modifier,
    chips: Map<String, Boolean>,
    text: String,
    onClick: (String) -> Unit,
    onDeleted: (String) -> Unit
){
    val topics = chips.keys.toList()
    Column(
        modifier = modifier
    ) {
        Text(
            text = "$text (${chips.size})",
            fontWeight = FontWeight.Bold,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items(
                items = topics
            ){topic ->
                val isSelected = chips[topic] ?: false
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onClick(topic)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedLabelColor = Color.White,
                        selectedContainerColor = Color.DarkGray,
                        disabledContainerColor = Color.White,
                        disabledLabelColor = Color.Black
                    ),
                    label = {
                        Text(
                            text = topic
                        )
                    },
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.clickable {
                                onDeleted(topic)
                            },
                            imageVector = Icons.Default.Delete,
                            contentDescription = "$text chip delete.",
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ArticleCard(
    modifier: Modifier = Modifier,
    article: Article
) {
    val currentContext = LocalContext.current

    Column(
        modifier = modifier.padding(16.dp).clip(
            shape = RoundedCornerShape(16.dp)
        ).background(Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            AsyncImage(
                modifier = Modifier.fillMaxWidth().clip(
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                ),
                model = article.imageUrl,
                contentDescription = "${article.title} image",
                contentScale = ContentScale.FillWidth
            )
        }
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = article.title,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = article.description
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = article.sourceName
                )
                Text(
                    text = article.sourceName
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, article.url.toUri())
                        currentContext.startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "${article.title} read",
                        tint = Color.White
                    )
                    Text(
                        text = "Read"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT,"${article.title}\n${article.url}")
                        }
                        currentContext.startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "${article.title} share",
                        tint = Color.White
                    )
                    Text(
                        text = "Share"
                    )
                }
            }
        }
    }
}

