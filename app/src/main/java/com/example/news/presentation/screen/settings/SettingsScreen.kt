@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.news.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.news.data.mapper.intervalToListString
import com.example.news.data.mapper.languageToListString
import com.example.news.data.mapper.queryToString
import com.example.news.domain.model.Interval
import com.example.news.domain.model.Language
import com.example.news.domain.model.Settings


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar()
        },
    ){innerPadding ->
        when(state){
            SettingsState.InitialSettings -> {
                Column(
                    modifier = modifier.padding(innerPadding)
                ) {
                    SettingDropDownMenuLanguage(
                        modifier = Modifier,
                        title = "Search Language",
                        subtitle = "Select Language",
                        lst = Language.entries.toList().languageToListString()
                    )
                    SettingDropDownMenuLanguage(
                        modifier = Modifier,
                        title = "Select Interval",
                        subtitle = "Select Interval",
                        lst = Interval.entries.toList().intervalToListString()
                    )
                }
            }
            is SettingsState.Setting -> {
                Column(
                    modifier = modifier.padding(innerPadding)
                ) {
                    SettingDropDownMenuLanguage(
                        modifier = Modifier,
                        title = "Search Language",
                        subtitle = "Select Language",
                        lst = Language.entries.toList().languageToListString()
                    )
                    SettingDropDownMenuLanguage(
                        modifier = Modifier,
                        title = "Select Interval",
                        subtitle = "Select Interval",
                        lst = Interval.entries.toList().intervalToListString()
                    )
                }
            }
        }
    }

}


@Composable
fun SettingsTopBar(
    modifier: Modifier = Modifier,

){
    TopAppBar(
        title = {
            Text(
                text = "Settings"
            )
        }
    )
}

@Composable
fun SettingDropDownMenuLanguage(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    lst: List<String>
) {

    var expanded by remember { mutableStateOf(false) }
    val options = lst
    var selectedOptions by remember { mutableStateOf("Choose") }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.background(Color.Red),
        ) {
            Text(
                textAlign = TextAlign.Start,
                text = title
            )
            Text(
                text = subtitle
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    value = selectedOptions,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    options.forEach { language ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = language
                                )
                            },
                            onClick = {
                                selectedOptions = language
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
