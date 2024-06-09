package com.example.c001apk.compose.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.c001apk.compose.ui.component.BackButton
import com.example.c001apk.compose.util.decode
import com.example.c001apk.compose.util.noRippleClickable
import kotlinx.coroutines.launch

/**
 * Created by bggRGjQaUbCoE on 2024/6/9
 */

enum class SearchType {
    FEED, APP, GAME, PRODUCT, USER, TOPIC
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    onBackClick: () -> Unit,
    keyword: String,
    title: String?,
    pageType: String?,
    pageParam: String?,
    onViewUser: (String) -> Unit,
    onViewFeed: (String, String?) -> Unit,
    onOpenLink: (String) -> Unit,
    onCopyText: (String?) -> Unit,
) {

    val layoutDirection = LocalLayoutDirection.current
    val tabList = SearchType.entries
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        initialPage = 0, // TODO
        pageCount = { tabList.size }
    )
    var refreshState by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton { onBackClick() }
                },
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { onBackClick() },
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = keyword,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                            maxLines = 1,
                        )
                        if (!title.isNullOrEmpty()) {
                            Text(
                                text = "$pageType: $title",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            )
                        }
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { dropdownMenuExpanded = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            expanded = dropdownMenuExpanded,
                            onDismissRequest = { dropdownMenuExpanded = false }
                        ) {
                            listOf("Type", "Order")
                                .forEachIndexed { index, menu ->
                                    DropdownMenuItem(
                                        text = { Text(menu) },
                                        onClick = {
                                            dropdownMenuExpanded = false
                                            // TODO:
                                        }
                                    )
                                }

                        }
                    }
                },
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateLeftPadding(layoutDirection),
                    end = paddingValues.calculateRightPadding(layoutDirection),
                    top = paddingValues.calculateTopPadding()
                )
        ) {

            SecondaryScrollableTabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier
                            .tabIndicatorOffset(
                                pagerState.currentPage,
                                matchContentSize = true
                            )
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    )
                },
            ) {
                tabList.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            if (pagerState.currentPage == index) {
                                refreshState = true
                            }
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(text = tab.name) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState
            ) { index ->
                SearchContentScreen(
                    paddingValues = paddingValues,
                    searchType = SearchType.valueOf(SearchType.entries[index].name),
                    keyword = keyword.decode,
                    pageType = pageType,
                    pageParam = pageParam,
                    refreshState = refreshState,
                    resetRefreshState = {
                        refreshState = false
                    },
                    onViewUser = onViewUser,
                    onViewFeed = onViewFeed,
                    onOpenLink = onOpenLink,
                    onCopyText = onCopyText,
                )
            }
        }

    }

}