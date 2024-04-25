package com.ba.weather.ui.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ba.weather.R
import com.ba.weather.model.City

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navigateToForecast: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val searchState by viewModel.searchQuery.collectAsState()
    val savedCities by viewModel.savedCities.collectAsState()
    val cities by viewModel.searchResult.collectAsState()
    val favouriteCity by viewModel.favouriteCity.collectAsState()
    var expandedCardId by remember { mutableStateOf<Int?>(null) }

    val cardWidth = (LocalConfiguration.current.screenWidthDp.dp - 20.dp)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                OutlinedTextField(
                    value = searchState.query,
                    onValueChange = viewModel::onSearchQueryValueChanged,
                    modifier = Modifier.fillMaxWidth(),
                    isError = searchState.error.isNotEmpty(),
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                    ),
                    label = { Text(text = stringResource(id = R.string.search)) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                viewModel.onSearch()
                                focusManager.clearFocus()
                            },
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.search),
                                contentDescription = stringResource(id = R.string.search),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Search,
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.onSearch()
                            focusManager.clearFocus()
                        },
                    ),
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                )
                if (searchState.error.isNotEmpty()) {
                    Text(
                        text = searchState.error,
                        modifier = Modifier.padding(start = 10.dp),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
        items(items = cities, key = { it.id }) { city ->
            CityCard(
                city = city,
                width = cardWidth,
                isSaved = false,
                isExpanded = city.id == expandedCardId,
                onExpand = {
                    expandedCardId = city.id.takeUnless { it == expandedCardId }
                },
                onSelect = {
                    viewModel.setCityAsFavourite(city)
                    navigateToForecast()
                },
                onSave = { viewModel.saveCity(city) },
            )
        }
        favouriteCity?.let { city ->
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.favourite_city),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f),
                        fontSize = 14.sp,
                    )
                    CityCard(
                        city = city,
                        width = cardWidth,
                        isSaved = true,
                        isExpanded = false,
                    )
                }
            }
        }
        if (savedCities.isNotEmpty()) item {
            Text(
                text = stringResource(id = R.string.saved_cities),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f),
                fontSize = 14.sp,
            )
        }
        items(items = savedCities, key = { it.id }) { city ->
            CityCard(
                city = city,
                width = cardWidth,
                isSaved = true,
                isExpanded = city.id == expandedCardId,
                onExpand = {
                    expandedCardId = city.id.takeUnless { it == expandedCardId }
                },
                onSelect = {
                    viewModel.setCityAsFavourite(city)
                    navigateToForecast()
                },
                onRemove = { viewModel.removeCity(city) },
            )
        }
    }
}

@Composable
private fun CityCard(
    city: City,
    width: Dp,
    isSaved: Boolean,
    isExpanded: Boolean,
    onExpand: (() -> Unit)? = null,
    onSelect: () -> Unit = {},
    onSave: () -> Unit = {},
    onRemove: () -> Unit = {},
) {
    val cardWidth by animateDpAsState(targetValue = if (isExpanded) width * .6f else width)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .width(cardWidth)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                .clickable(enabled = onExpand != null) {
                    onExpand?.invoke()
                }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = city.name,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = city.country,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f),
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 5.dp),
            )
        }
        FilledIconButton(
            onClick = onSelect,
            modifier = Modifier.weight(1f),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Icon(
                imageVector = Icons.Rounded.StarBorder,
                contentDescription = null,
            )
        }
        FilledIconButton(
            onClick = if (isSaved) onRemove else onSave,
            modifier = Modifier.weight(1f),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isSaved) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.tertiaryContainer,
            ),
        ) {
            Icon(
                imageVector = if (isSaved) Icons.Default.Delete else Icons.Default.Add,
                contentDescription = null,
            )
        }
    }
}