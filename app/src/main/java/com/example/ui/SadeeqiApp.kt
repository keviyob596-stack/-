package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audio.SoundManager
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.friends.FriendsScreen
import com.example.ui.screens.friends.PrivateChatScreen
import com.example.ui.screens.games.AutobusGameScreen
import com.example.ui.screens.games.GamesHubScreen
import com.example.ui.screens.games.QuizGameScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.LeaderboardScreen
import com.example.ui.screens.home.NotificationsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.StoreScreen
import com.example.ui.screens.rooms.RoomDetailScreen
import com.example.ui.screens.rooms.RoomsScreen
import com.example.ui.theme.*

sealed class Screen {
    object Home : Screen()
    object Games : Screen()
    object Rooms : Screen()
    object Friends : Screen()
    object Profile : Screen()
    object Store : Screen()
    object Notifications : Screen()
    object Leaderboard : Screen()
    object Admin : Screen()
    data class Autobus(val isOffline: Boolean) : Screen()
    data class Quiz(val gameType: String, val isOffline: Boolean) : Screen()
    data class RoomDetail(val roomId: Long) : Screen()
    data class PrivateChat(val otherUserId: Long) : Screen()
}

@Composable
fun SadeeqiApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    if (currentUser == null) {
        AuthScreen(viewModel = viewModel)
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                // Show bottom bar only on top-level screens
                if (currentScreen is Screen.Home ||
                    currentScreen is Screen.Games ||
                    currentScreen is Screen.Rooms ||
                    currentScreen is Screen.Friends ||
                    currentScreen is Screen.Profile
                ) {
                    SadeeqiBottomNavigation(
                        currentScreen = currentScreen,
                        onSelectScreen = { screen ->
                            SoundManager.playClick()
                            currentScreen = screen
                        }
                    )
                }
            },
            containerColor = SadeeqiBlack
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val screen = currentScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToGames = { currentScreen = Screen.Games },
                            onNavigateToRooms = { currentScreen = Screen.Rooms },
                            onNavigateToFriends = { currentScreen = Screen.Friends },
                            onNavigateToStore = { currentScreen = Screen.Store },
                            onNavigateToNotifications = { currentScreen = Screen.Notifications },
                            onStartAutobus = { currentScreen = Screen.Autobus(isOffline = false) },
                            onStartQuiz = { qType -> currentScreen = Screen.Quiz(qType, isOffline = false) },
                            onEnterRoom = { rId -> currentScreen = Screen.RoomDetail(rId) }
                        )
                    }
                    is Screen.Games -> {
                        GamesHubScreen(
                            viewModel = viewModel,
                            onStartAutobus = { isOff -> currentScreen = Screen.Autobus(isOff) },
                            onStartQuiz = { qType, isOff -> currentScreen = Screen.Quiz(qType, isOff) }
                        )
                    }
                    is Screen.Rooms -> {
                        RoomsScreen(
                            viewModel = viewModel,
                            onEnterRoom = { rId -> currentScreen = Screen.RoomDetail(rId) }
                        )
                    }
                    is Screen.Friends -> {
                        FriendsScreen(
                            viewModel = viewModel,
                            onOpenChat = { fId -> currentScreen = Screen.PrivateChat(fId) }
                        )
                    }
                    is Screen.Profile -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            onOpenStore = { currentScreen = Screen.Store },
                            onOpenAdmin = { currentScreen = Screen.Admin }
                        )
                    }
                    is Screen.Store -> {
                        BackHandler { currentScreen = Screen.Profile }
                        StoreScreen(viewModel = viewModel, onBack = { currentScreen = Screen.Profile })
                    }
                    is Screen.Notifications -> {
                        BackHandler { currentScreen = Screen.Home }
                        NotificationsScreen(viewModel = viewModel, onBack = { currentScreen = Screen.Home })
                    }
                    is Screen.Leaderboard -> {
                        BackHandler { currentScreen = Screen.Home }
                        LeaderboardScreen(viewModel = viewModel, onBack = { currentScreen = Screen.Home })
                    }
                    is Screen.Admin -> {
                        BackHandler { currentScreen = Screen.Profile }
                        AdminScreen(viewModel = viewModel, onBack = { currentScreen = Screen.Profile })
                    }
                    is Screen.Autobus -> {
                        BackHandler { currentScreen = Screen.Games }
                        AutobusGameScreen(isOffline = screen.isOffline, viewModel = viewModel, onBack = { currentScreen = Screen.Games })
                    }
                    is Screen.Quiz -> {
                        BackHandler { currentScreen = Screen.Games }
                        QuizGameScreen(gameType = screen.gameType, isOffline = screen.isOffline, viewModel = viewModel, onBack = { currentScreen = Screen.Games })
                    }
                    is Screen.RoomDetail -> {
                        BackHandler { currentScreen = Screen.Rooms }
                        RoomDetailScreen(roomId = screen.roomId, viewModel = viewModel, onBack = { currentScreen = Screen.Rooms })
                    }
                    is Screen.PrivateChat -> {
                        BackHandler { currentScreen = Screen.Friends }
                        PrivateChatScreen(otherUserId = screen.otherUserId, viewModel = viewModel, onBack = { currentScreen = Screen.Friends })
                    }
                }
            }
        }
    }
}

@Composable
fun SadeeqiBottomNavigation(
    currentScreen: Screen,
    onSelectScreen: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = SadeeqiDarkCard,
        contentColor = SadeeqiWhite,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SadeeqiDarkBorder)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val items = listOf(
            Triple("الرئيسية", Icons.Default.Home, Screen.Home as Screen),
            Triple("الألعاب", Icons.Default.SportsEsports, Screen.Games as Screen),
            Triple("القعدات", Icons.Default.Mic, Screen.Rooms as Screen),
            Triple("الأصدقاء", Icons.Default.People, Screen.Friends as Screen),
            Triple("حسابي", Icons.Default.Person, Screen.Profile as Screen)
        )

        items.forEach { (title, icon, scr) ->
            val isSelected = when (scr) {
                is Screen.Home -> currentScreen is Screen.Home
                is Screen.Games -> currentScreen is Screen.Games
                is Screen.Rooms -> currentScreen is Screen.Rooms
                is Screen.Friends -> currentScreen is Screen.Friends
                is Screen.Profile -> currentScreen is Screen.Profile
                else -> false
            }
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectScreen(scr) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SadeeqiBlack,
                    selectedTextColor = SadeeqiYellow,
                    indicatorColor = SadeeqiYellow,
                    unselectedIconColor = SadeeqiGray,
                    unselectedTextColor = SadeeqiGray
                )
            )
        }
    }
}
