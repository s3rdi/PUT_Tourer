package com.example.tourer.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tourer.R
import com.example.tourer.model.Route
import com.example.tourer.viewmodel.RouteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RouteListScreen(viewModel: RouteViewModel, routes: List<Route>, isDarkTheme: Boolean, onThemeToggle: () -> Unit, onRouteClick: (String) -> Unit) {
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val config = LocalConfiguration.current
    val isTablet = config.screenWidthDp >= 600 && config.screenHeightDp >= 480
    val isExpanded = config.screenWidthDp >= 840 && config.screenHeightDp >= 480
    val isLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE

    val time by viewModel.timeInSeconds.collectAsState()
    val timeString by viewModel.formattedTime.collectAsState()

    val bgColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF2F5FA)
    val surfaceColor = if (isDarkTheme) Color(0xFF242424) else Color(0xFFFFFFFF)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val accentColor = if (isDarkTheme) Color.Green else Color(0xFF1EBA12)

    var isSearchExpanded by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BackHandler(enabled = isSearchExpanded) {
        isSearchExpanded = false
        viewModel.updateSearchQuery("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchExpanded) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            placeholder = { Text("Szukaj trasy...", color = textColor.copy(alpha = 0.5f)) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                cursorColor = accentColor
                            )
                        )
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    } else {
                        Text("Tourer", color = textColor, fontSize = if (isTablet) 28.sp else if (isLandscape) 16.sp else 20.sp)
                    }
                },
                actions = {
                    if (isSearchExpanded) {
                        IconButton(onClick = {
                            if (searchQuery.isNotEmpty()) {
                                viewModel.updateSearchQuery("")
                            } else {
                                isSearchExpanded = false
                            }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Zamknij", tint = textColor)
                        }
                    } else {
                        if (isLandscape) {
                            val buttonStyle = ButtonDefaults.buttonColors(
                                containerColor = surfaceColor,
                                contentColor = textColor
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(end = 8.dp)) {
                                Button(
                                    onClick = { viewModel.setFilter("wszystkie") },
                                    colors = buttonStyle,
                                    border = if (selectedFilter == "wszystkie") BorderStroke(2.dp, accentColor) else null,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "wszystkie", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Wszystkie", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { viewModel.setFilter("biegowa") },
                                    colors = buttonStyle,
                                    border = if (selectedFilter == "biegowa") BorderStroke(2.dp, accentColor) else null,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.DirectionsRun, contentDescription = "biegowe", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Biegowe", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { viewModel.setFilter("rowerowa") },
                                    colors = buttonStyle,
                                    border = if (selectedFilter == "rowerowa") BorderStroke(2.dp, accentColor) else null,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.DirectionsBike, contentDescription = "rowerowe", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rowerowe", fontSize = 12.sp)
                                }
                            }
                        }

                        IconButton(onClick = { isSearchExpanded = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Szukaj", tint = textColor)
                        }

                        IconButton(onClick = onThemeToggle) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Zmień motyw",
                                tint = textColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor

    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    if (searchQuery.isEmpty()) {
                        isSearchExpanded = false
                    }
                })
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            6.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        val buttonStyle = ButtonDefaults.buttonColors(
                            containerColor = surfaceColor,
                            contentColor = textColor
                        )

                        Button(
                            onClick = { viewModel.setFilter("wszystkie") },
                            colors = buttonStyle,
                            border = if (selectedFilter == "wszystkie") BorderStroke(
                                2.dp,
                                accentColor
                            ) else null,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "wszystkie trasy"
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Wszystkie", fontSize = if (isTablet) 20.sp else 14.sp)
                        }

                        Button(
                            onClick = { viewModel.setFilter("biegowa") },
                            colors = buttonStyle,
                            border = if (selectedFilter == "biegowa") BorderStroke(
                                2.dp,
                                accentColor
                            ) else null,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = "biegowe trasy"
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Biegowe", fontSize = if (isTablet) 20.sp else 14.sp)
                        }

                        Button(
                            onClick = { viewModel.setFilter("rowerowa") },
                            colors = buttonStyle,
                            border = if (selectedFilter == "rowerowa") BorderStroke(
                                2.dp,
                                accentColor
                            ) else null,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBike,
                                contentDescription = "rowerowe trasy"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rowerowe", fontSize = if (isTablet) 20.sp else 14.sp)
                        }
                    }
                }

                val filteredRoutes = routes.filter { route ->
                    val matchesType = selectedFilter == "wszystkie" || route.type == selectedFilter
                    val matchesSearch = route.name.contains(searchQuery, ignoreCase = true)
                    matchesType && matchesSearch
                }

                Text(
                    text = "${filteredRoutes.size} tras",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = if (isTablet) 20.sp else 14.sp
                    ),
                    color = textColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                LazyVerticalGrid(
                    columns = if (isLandscape && !isTablet) GridCells.Fixed(4) else GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(filteredRoutes) { route ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = surfaceColor,
                                contentColor = textColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRouteClick(route.id.toString()) }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                RouteImageStack(
                                    image1 = route.image1,
                                    image2 = route.image2,
                                    image3 = route.image3,
                                    cardColor = surfaceColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = route.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = if (isTablet) 20.sp else 14.sp
                                    ),
                                    minLines = 2,
                                    maxLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                val typeIcon = if (route.type.lowercase() == "biegowa") {
                                    Icons.Default.DirectionsRun
                                } else {
                                    Icons.Default.DirectionsBike
                                }

                                Icon(
                                    imageVector = typeIcon,
                                    contentDescription = "Typ: ${route.type}",
                                    tint = accentColor,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            val activeRouteName by viewModel.activeTimerRouteName.collectAsState()
            val activeRouteId by viewModel.activeTimerRouteId.collectAsState()
            val isTimerRunning by viewModel.isTimerRunning.collectAsState()
            val showStopConfirm by viewModel.showStopConfirm.collectAsState()

            if (!isExpanded && (time > 0 || isTimerRunning)) {
                ActiveTimerCard(
                    viewModel = viewModel,
                    timeString = timeString,
                    activeRouteName = activeRouteName,
                    activeRouteId = activeRouteId,
                    isTimerRunning = isTimerRunning,
                    showStopConfirm = showStopConfirm,
                    time = time,
                    surfaceColor = surfaceColor,
                    textColor = textColor,
                    bgColor = bgColor,
                    accentColor = accentColor,
                    isTablet = isTablet
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(viewModel: RouteViewModel, route: Route?, isDarkTheme: Boolean, onThemeToggle: () -> Unit, onBackClick: () -> Unit) {
    val config = LocalConfiguration.current
    val isTablet = config.screenWidthDp >= 600 && config.screenHeightDp >= 480
    val isExpanded = config.screenWidthDp >= 840 && config.screenHeightDp >= 480
    val isLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE

    val time by viewModel.timeInSeconds.collectAsState()
    val timeString by viewModel.formattedTime.collectAsState()

    val bgColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF2F5FA)
    val surfaceColor = if (isDarkTheme) Color(0xFF242424) else Color(0xFFFFFFFF)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val accentColor = if (isDarkTheme) Color.Green else Color(0xFF1EBA12)

    Scaffold (
        topBar = { TopAppBar(
            title = { Text(route?.name?: "brak danych", color = textColor, fontSize = if (isTablet) 28.sp else if (isLandscape) 16.sp else 20.sp)},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = bgColor
            ),
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "powrót do listy",
                        tint = textColor
                    )
                }
            },
            actions = {
                if (!isExpanded) {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Zmień motyw",
                            tint = textColor
                        )
                    }
                }
            }
        )},
        containerColor = bgColor
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (route != null) {
                    Text(
                        text = "Typ: ${route.type}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = if (isTablet) 20.sp else 14.sp
                        ),
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Opis: ${route.description}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = if (isTablet) 20.sp else 14.sp
                        ),
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    RouteImage(imageName = route.image1)
                    RouteImage(imageName = route.image2)
                    RouteImage(imageName = route.image3)

                    val logs by remember(route.id) {
                        viewModel.getLogsForRoute(route.id)
                    }.collectAsState(initial = emptyList())

                    if (logs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Tabela wyników",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = if (isTablet) 24.sp else 20.sp
                            ),
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        logs.forEachIndexed { index, log ->
                            val logMinutes = log.timeInSeconds / 60
                            val logSeconds = log.timeInSeconds % 60
                            val logTimeString = String.format(Locale.getDefault(), "%02d:%02d", logMinutes, logSeconds)

                            val dateString = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                                Date(log.dateInMillis)
                            )

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = surfaceColor,
                                    contentColor = textColor
                                ),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${index + 1}.",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))

                                        Text(
                                            text = dateString,
                                            fontSize = 16.sp
                                        )
                                    }

                                    Text(
                                        text = logTimeString,
                                        color = accentColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(text = "Nie znaleziono trasy!", fontSize = if (isTablet) 20.sp else 14.sp)
                }
                Spacer(modifier = Modifier.height(120.dp))
            }

            val activeRouteName by viewModel.activeTimerRouteName.collectAsState()
            val activeRouteId by viewModel.activeTimerRouteId.collectAsState()
            val isTimerRunning by viewModel.isTimerRunning.collectAsState()
            val showStopConfirm by viewModel.showStopConfirm.collectAsState()

            ActiveTimerCard(
                viewModel = viewModel,
                timeString = timeString,
                activeRouteName = activeRouteName ?: route?.name,
                activeRouteId = activeRouteId ?: route?.id?.toString(),
                isTimerRunning = isTimerRunning,
                showStopConfirm = showStopConfirm,
                time = time,
                surfaceColor = surfaceColor,
                textColor = textColor,
                bgColor = bgColor,
                accentColor = accentColor,
                isTablet = isTablet
            )
        }
    }
}

@Composable
fun RouteImage(imageName: String) {
    if (imageName.isNotBlank()) {
        val context = LocalContext.current

        val imageId = remember(imageName) {
            context.resources.getIdentifier(imageName, "drawable", context.packageName)
        }

        if (imageId != 0) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "zdjęcie trasy",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(vertical = 8.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun RouteImageStack(image1: String, image2: String, image3: String, cardColor: Color) {
    val context = LocalContext.current

    val id1 = remember(image1) {
        context.resources.getIdentifier(image1, "drawable", context.packageName)
    }

    val id2 = remember(image1) {
        context.resources.getIdentifier(image2, "drawable", context.packageName)
    }

    val id3 = remember(image1) {
        context.resources.getIdentifier(image3, "drawable", context.packageName)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (id3 != 0) {
            Image(
                painter = painterResource(id = id3),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .offset(x = 16.dp, y = 16.dp)
                    .border(3.dp, cardColor, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
        if (id2 != 0) {
            Image(
                painter = painterResource(id = id2),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .offset(x = 8.dp, y = 8.dp)
                    .border(3.dp, cardColor, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
        if (id1 != 0) {
            Image(
                painter = painterResource(id = id1),
                contentDescription = "Okładka trasy",
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .border(3.dp, cardColor, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onStartClick: () -> Unit
) {
    val bgColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF2F5FA)
    val surfaceColor = if (isDarkTheme) Color(0xFF242424) else Color(0xFFFFFFFF)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val accentColor = if (isDarkTheme) Color.Green else Color(0xFF1EBA12)

    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }

    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(30f) }

    val buttonAlpha = remember { Animatable(0f) }
    val buttonOffset = remember { Animatable(40f) }

    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
        }
        delay(300)

        launch {
            textAlpha.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
        }
        launch {
            textOffset.animateTo(0f, animationSpec = tween(800, easing = FastOutSlowInEasing))
        }
        delay(400)

        launch {
            buttonAlpha.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        }
        launch {
            buttonOffset.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", color = textColor) },
                actions = {
                    IconButton(
                        onClick = onThemeToggle,
                        modifier = Modifier.alpha(textAlpha.value)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Zmień motyw",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = BiasAlignment(horizontalBias = 0f, verticalBias = -0.6f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Tourer",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ),
                    color = accentColor,
                    modifier = Modifier
                        .alpha(textAlpha.value)
                        .offset(y = textOffset.value.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                val logoResId = if (isDarkTheme) R.drawable.logo_dark else R.drawable.logo_light
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "Logo Tourer",
                    modifier = Modifier
                        .size(160.dp)
                        .scale(logoScale.value)
                        .alpha(logoAlpha.value)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Twoja następna przygoda czeka!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 20.sp
                    ),
                    color = textColor,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .alpha(textAlpha.value)
                        .offset(y = textOffset.value.dp)
                )
            }

            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
                    .fillMaxWidth(0.7f)
                    .height(56.dp)
                    .alpha(buttonAlpha.value)
                    .offset(y = buttonOffset.value.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Zaczynajmy!", fontSize = 20.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun BoxScope.ActiveTimerCard(
    viewModel: RouteViewModel,
    timeString: String,
    activeRouteName: String?,
    activeRouteId: String?,
    isTimerRunning: Boolean,
    showStopConfirm: Boolean,
    time: Int,
    surfaceColor: Color,
    textColor: Color,
    bgColor: Color,
    accentColor: Color,
    isTablet: Boolean
) {
    val isStopEnabled = time > 0 && activeRouteId != null
    Card(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 32.dp),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor,
            contentColor = textColor
        ),
        elevation = CardDefaults.cardElevation(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (activeRouteName != null && (isTimerRunning || time > 0)) {
                Text(
                    text = activeRouteName,
                    style = if (isTablet) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium,
                    color = accentColor,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            if (showStopConfirm) {
                Text(
                    text = "Kliknij by anulować, przytrzymaj by zapisać",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = timeString, fontSize = 32.sp)
                Spacer(modifier = Modifier.width(24.dp))

                val buttonStyle = ButtonDefaults.buttonColors(
                    containerColor = bgColor,
                    contentColor = textColor
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (isTimerRunning)
                                viewModel.pauseTimer()
                            else {
                                viewModel.startTimer(activeRouteId ?: "", activeRouteName ?: "")
                            }
                        },
                        colors = buttonStyle,
                        border = if (isTimerRunning) BorderStroke(
                            2.dp,
                            accentColor
                        ) else null,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isTimerRunning) "Pauza" else "Start"
                        )
                    }
                    val scope = rememberCoroutineScope()
                    val holdProgress = remember { Animatable(0f) }
                    var isSaved by remember { mutableStateOf(false) }

                    Surface(
                        shape = RoundedCornerShape(40.dp),
                        color = if (isSaved) accentColor else bgColor,
                        contentColor = textColor,
                        modifier = Modifier
                            .height(40.dp)
                            .alpha(if (isStopEnabled) 1f else 0.6f)
                            .clip(RoundedCornerShape(40.dp))
                            .pointerInput(isStopEnabled) {
                                if (!isStopEnabled) return@pointerInput

                                detectTapGestures(
                                    onPress = {
                                        if (isSaved) return@detectTapGestures

                                        val job = scope.launch {
                                            holdProgress.animateTo(
                                                targetValue = 1f,
                                                animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
                                            )

                                            isSaved = true
                                            delay(1000)
                                            viewModel.longStop(activeRouteId)

                                            isSaved = false
                                            holdProgress.snapTo(0f)
                                        }
                                        tryAwaitRelease()

                                        if (!isSaved) {
                                            job.cancel()
                                            scope.launch {
                                                holdProgress.animateTo(0f, tween(300))
                                            }
                                        }
                                    },
                                    onTap = {
                                        if (!isSaved) viewModel.clickStop()
                                    }
                                )
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 26.dp)
                                .width(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Stop,
                                contentDescription = "Zatrzymaj i zapisz"
                            )

                            if (!isSaved && holdProgress.value > 0f) {
                                CircularProgressIndicator(
                                    progress = { holdProgress.value },
                                    modifier = Modifier.requiredSize(34.dp),
                                    color = accentColor,
                                    trackColor = Color.Transparent,
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}