package io.github.brightennnn.mmtokenmonitor.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import io.github.brightennnn.mmtokenmonitor.data.repository.ThemeRepository
import io.github.brightennnn.mmtokenmonitor.ui.components.BottomNavItem
import io.github.brightennnn.mmtokenmonitor.ui.components.FloatingBottomNav
import io.github.brightennnn.mmtokenmonitor.ui.components.HapticFeedback
import io.github.brightennnn.mmtokenmonitor.ui.screens.home.HomeScreen
import io.github.brightennnn.mmtokenmonitor.ui.screens.settings.SettingsNavHost
import kotlinx.coroutines.launch

private val bottomNavDestinations = listOf(
    BottomNavItem(label = "用量", icon = Icons.Default.Home),
    BottomNavItem(label = "设置", icon = Icons.Default.Settings)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    themeRepository: ThemeRepository
) {
    val view = LocalView.current
    val pagerState = rememberPagerState(pageCount = { bottomNavDestinations.size })
    val coroutineScope = rememberCoroutineScope()

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()

    Box(modifier = Modifier.fillMaxSize()) {
        // 内容层：完全铺满，但顶部避开状态栏，底部保留导航栏
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusBarPadding.calculateTopPadding())
        ) { page ->
            when (page) {
                0 -> HomeScreen(themeRepository = themeRepository)
                1 -> SettingsNavHost(themeRepository = themeRepository)
            }
        }

        // 悬浮底栏：浮在内容上方，贴着底部
        FloatingBottomNav(
            selectedIndex = pagerState.currentPage,
            items = bottomNavDestinations,
            onItemClick = { index ->
                HapticFeedback.perform(view)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
