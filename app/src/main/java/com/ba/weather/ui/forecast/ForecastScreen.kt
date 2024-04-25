package com.ba.weather.ui.forecast

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotListedLocation
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ba.weather.R
import com.ba.weather.model.Forecast
import java.time.format.DateTimeFormatter

private val dateTimeFormatter by lazy {
    DateTimeFormatter.ofPattern("EEE, h:mm a")
}
private val timeFormatter by lazy {
    DateTimeFormatter.ofPattern("h:mm a")
}

@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel = hiltViewModel(),
    navigateToSearch: () -> Unit,
) {
    val isLocationAvailable by viewModel.isLocationAvailable.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val forecast by viewModel.forecast.collectAsState()
    val currentHour by viewModel.currentHour.collectAsState()
    val useCurrentLocation by viewModel.useCurrentLocation.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(id = R.string.use_current_location))
            Switch(
                checked = useCurrentLocation,
                onCheckedChange = viewModel::onUseCurrentLocationChanged,
            )
        }
        forecast?.let {
            ForecastInfoScreen(
                forecast = it,
                currentHour = currentHour,
                useCurrentLocation = useCurrentLocation,
                onRefresh = viewModel::refreshForecast,
            )
        }
        if (!isLocationAvailable) {
            NoLocationScreen(navigateToSearch)
        }
        AnimatedVisibility(
            visible = isLoading,
            modifier = Modifier.padding(top = 40.dp),
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
        ) {
            Surface(
                shape = CircleShape,
                tonalElevation = 7.dp,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(10.dp),
                )
            }
        }
    }
}

@Composable
private fun ForecastInfoScreen(
    forecast: Forecast,
    currentHour: Int,
    useCurrentLocation: Boolean,
    onRefresh: () -> Unit,
) {
    val lazyState = rememberLazyListState()
    LaunchedEffect(currentHour) {
        lazyState.scrollToItem(index = currentHour)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dateTimeFormatter.format(forecast.dateTime),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = MaterialTheme.shapes.medium,
                    )
                    .clickable { onRefresh() }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (useCurrentLocation) Icons.Default.LocationOn
                    else Icons.Rounded.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = forecast.locationName,
                    fontSize = 12.sp,
                )
            }
        }
        Spacer(modifier = Modifier.weight(2f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.today),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            TextButton(onClick = { }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(id = R.string.days_7),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = forecast.weather.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                )
                Text(
                    text = stringResource(id = R.string.temp, forecast.weather.temperature),
                    modifier = Modifier.weight(1f),
                    fontSize = 94.sp,
                    fontWeight = FontWeight.Thin,
                    textAlign = TextAlign.Center,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InfoItem(
                    icon = R.drawable.wind,
                    label = R.string.wind,
                    value = stringResource(id = R.string.speed, forecast.weather.windSpeed),
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                )
                InfoItem(
                    icon = R.drawable.droplet,
                    label = R.string.humidity,
                    value = stringResource(id = R.string.percent, forecast.weather.humidity),
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                )
                InfoItem(
                    icon = R.drawable.cloud_rain,
                    label = R.string.chance_of_rain,
                    value = stringResource(id = R.string.percent, forecast.weather.chanceOfRain),
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = lazyState,
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            items(items = forecast.hourly, key = { it.time }) {
                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(
                                if (it.time.hour == currentHour) 1.dp else 0.dp,
                            )
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = timeFormatter.format(it.time),
                        color = MaterialTheme.colorScheme.secondary.copy(
                            alpha = if (it.time.hour == currentHour) 1f else .6f,
                        ),
                        fontSize = 14.sp,
                    )
                    AsyncImage(
                        model = it.icon,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.temp, it.temperature),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(2f))
    }
}

@Composable
private fun InfoItem(
    @DrawableRes icon: Int,
    @StringRes label: Int,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .5f))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = stringResource(id = label),
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .6f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Light,
        )
        Text(
            text = value,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun NoLocationScreen(
    navigateToSearch: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.NotListedLocation,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Text(
            text = stringResource(id = R.string.no_location_found),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = navigateToSearch,
        ) {
            Text(text = stringResource(id = R.string.add_location))
        }
    }
}