package io.github.brightennnn.mmtokenmonitor.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.brightennnn.mmtokenmonitor.data.repository.ThemeRepository
import androidx.compose.ui.platform.LocalView
import io.github.brightennnn.mmtokenmonitor.ui.components.HapticFeedback
import io.github.brightennnn.mmtokenmonitor.ui.screens.home.HomeScreen
import io.github.brightennnn.mmtokenmonitor.ui.screens.settings.SettingsNavHost
import kotlinx.coroutines.launch

private data class BottomNavDestination(
    val label: String,
    val icon: ImageVector
)

private val bottomNavDestinations = listOf(
    BottomNavDestination(label = "用量", icon = Icons.Default.Home),
    BottomNavDestination(label = "设置", icon = Icons.Default.Settings)
)

private const val ANIM_DURATION = 300

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    themeRepository: ThemeRepository
) {
    val view = LocalView.current
    val pagerState = rememberPagerState(pageCount = { bottomNavDestinations.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                bottomNavDestinations.forEachIndexed { index, dest ->
                    NavigationBarItem(
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            HapticFeedback.perform(view)
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> HomeScreen(themeRepository = themeRepository)
                1 -> SettingsNavHost(themeRepository = themeRepository)
            }
        }
    }
}
