package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.EaseInQuart
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import com.example.R
import com.example.data.QuizCategory
import com.example.data.QuizRepository
import com.example.data.Question
import com.example.ui.theme.*

private sealed interface ScreenState {
    object Home : ScreenState
    data class Quiz(val category: QuizCategory) : ScreenState
    data class Results(val category: QuizCategory) : ScreenState
    object Error : ScreenState
}

@Composable
fun QuizApp(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val selectedId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val isCompleted by viewModel.quizCompleted.collectAsStateWithLifecycle()
    val highScores by viewModel.highScores.collectAsStateWithLifecycle()
    val unlockedBadges by viewModel.unlockedBadges.collectAsStateWithLifecycle()
    val activeCategory = viewModel.activeCategory

    val screenState = remember(selectedId, isCompleted, activeCategory) {
        when {
            selectedId == null -> ScreenState.Home
            activeCategory == null -> ScreenState.Error
            isCompleted -> ScreenState.Results(activeCategory)
            else -> ScreenState.Quiz(activeCategory)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VelvetBlackBackground)
    ) {
        AnimatedContent(
            targetState = screenState,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(400, easing = EaseOutQuart)
                ) + scaleIn(
                    initialScale = 0.96f,
                    animationSpec = tween(400, easing = EaseOutQuart)
                ) + slideInVertically(
                    initialOffsetY = { (it * 0.05f).toInt() },
                    animationSpec = tween(400, easing = EaseOutQuart)
                ) togetherWith fadeOut(
                    animationSpec = tween(300, easing = EaseInQuart)
                ) + scaleOut(
                    targetScale = 0.96f,
                    animationSpec = tween(300, easing = EaseInQuart)
                ) + slideOutVertically(
                    targetOffsetY = { (it * 0.05f).toInt() },
                    animationSpec = tween(300, easing = EaseInQuart)
                )
            },
            label = "ScreenTransition"
        ) { state ->
            when (state) {
                is ScreenState.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        highScores = highScores,
                        unlockedBadges = unlockedBadges
                    )
                }
                is ScreenState.Quiz -> {
                    QuizContentScreen(
                        viewModel = viewModel,
                        category = state.category
                    )
                }
                is ScreenState.Results -> {
                    ResultsScreen(
                        viewModel = viewModel,
                        category = state.category,
                        highScores = highScores,
                        unlockedBadges = unlockedBadges
                    )
                }
                is ScreenState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error loading quiz.", color = GoldPrimary)
                    }
                }
            }
        }

        val resumePromptCategoryId by viewModel.resumePromptCategoryId.collectAsStateWithLifecycle()
        resumePromptCategoryId?.let { catId ->
            val cat = com.example.data.QuizRepository.getCategoryById(catId)
            if (cat != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissResumePrompt() },
                    containerColor = CharcoalSurface,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.border(BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)), RoundedCornerShape(24.dp)),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Resume Quest",
                            tint = GoldPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "RESUME UNFINISHED QUEST?",
                            color = PureWhite,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Text(
                            text = "You have an unfinished attempt for '${cat.title}'. Would you like to resume from where you left off or start a fresh quest?",
                            color = OffWhite,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.startFreshCategory(catId) }
                        ) {
                            Text(
                                text = "START FRESH",
                                color = MutedText,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.resumeSavedCategory(catId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = VelvetBlackBackground
                            ),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text(
                                text = "RESUME QUEST",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: QuizViewModel,
    highScores: List<com.example.data.QuizScoreEntity>,
    unlockedBadges: List<com.example.data.UnlockedBadgeEntity>
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Quizzes, 1 = Celebration, 2 = Quotes
    var selectedEra by remember { mutableStateOf("early") } // "early", "eighties", "legacy"
    var selectedCelebrationItem by remember { mutableStateOf<CelebrationItem?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CharcoalSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(0.dp))
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Quizzes",
                            tint = if (activeTab == 0) GoldPrimary else MutedText
                        )
                    },
                    label = {
                        Text(
                            text = "Quizzes",
                            color = if (activeTab == 0) GoldPrimary else MutedText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GoldPrimary.copy(alpha = 0.15f))
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Celebration",
                            tint = if (activeTab == 1) GoldPrimary else MutedText
                        )
                    },
                    label = {
                        Text(
                            text = "Celebration",
                            color = if (activeTab == 1) GoldPrimary else MutedText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GoldPrimary.copy(alpha = 0.15f))
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Quotes",
                            tint = if (activeTab == 2) GoldPrimary else MutedText
                        )
                    },
                    label = {
                        Text(
                            text = "Quotes",
                            color = if (activeTab == 2) GoldPrimary else MutedText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = GoldPrimary.copy(alpha = 0.15f))
                )
            }
        },
        containerColor = VelvetBlackBackground,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> {
                    // TAB 0: QUIZ CENTER
                    QuizCenterScreen(
                        viewModel = viewModel,
                        highScores = highScores,
                        unlockedBadges = unlockedBadges,
                        selectedEra = selectedEra,
                        onEraSelected = { selectedEra = it }
                    )
                }
                1 -> {
                    // TAB 1: LIFE CELEBRATION
                    CelebrationScreen(
                        onItemClick = { selectedCelebrationItem = it }
                    )
                }
                2 -> {
                    // TAB 2: INSPIRING QUOTES
                    QuotesScreen()
                }
            }

            // Life Celebration Curators Detail Popup Dialog
            selectedCelebrationItem?.let { item ->
                AlertDialog(
                    onDismissRequest = { selectedCelebrationItem = null },
                    containerColor = CharcoalSurface,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.border(BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)), RoundedCornerShape(24.dp)),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star Spec",
                            tint = GoldPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.title,
                                color = PureWhite,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = item.subtitle,
                                color = GoldSecondary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(top = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    text = {
                        Text(
                            text = item.details,
                            color = OffWhite,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { selectedCelebrationItem = null },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = VelvetBlackBackground
                            ),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text(
                                text = "CLOSE RECORD STORY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun QuizCenterScreen(
    viewModel: QuizViewModel,
    highScores: List<com.example.data.QuizScoreEntity>,
    unlockedBadges: List<com.example.data.UnlockedBadgeEntity>,
    selectedEra: String,
    onEraSelected: (String) -> Unit
) {
    val filteredQuizzes = remember(selectedEra) {
        QuizRepository.categories.filter { it.id.startsWith(selectedEra) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Grand visual header with Banner image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_mj_banner),
                    contentDescription = "Michael Jackson Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Black overlay gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, VelvetBlackBackground),
                                startY = 80f
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(
                            text = "KING OF POP",
                            modifier = Modifier
                                .testTag("app_subtitle")
                                .padding(bottom = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldTertiary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 4.sp
                        )
                        ShinyGoldSignatureLogo(
                            scale = 0.9f,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "ULTIMATE QUEST",
                            modifier = Modifier.testTag("app_title"),
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldPrimary,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 5.sp
                        )
                    }
                }
            }
        }

        // Stats Card & Unlocked Badges Cabinet
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalSurface)
                    .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Premium Stats",
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACHIEVEMENT VAULT",
                            style = MaterialTheme.typography.titleLarge,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (highScores.isNotEmpty() || unlockedBadges.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.resetAllData() },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("reset_data_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset stats",
                                tint = MutedText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val totalQuests = QuizRepository.categories.sumOf { it.questions.size }
                    val completedQuestsCount = highScores.sumOf { it.bestScore }
                    val quizzesCompleted = highScores.filter { it.completedCount > 0 }.size

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$quizzesCompleted/72",
                            style = MaterialTheme.typography.headlineMedium,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Quizzes Done",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText
                        )
                    }
                    Box(
                        modifier = Modifier
                            .height(35.dp)
                            .width(1.dp)
                            .background(CharcoalBorder)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$completedQuestsCount/$totalQuests",
                            style = MaterialTheme.typography.headlineMedium,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Correct Answers",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText
                        )
                    }
                    Box(
                        modifier = Modifier
                            .height(35.dp)
                            .width(1.dp)
                            .background(CharcoalBorder)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val percentage = if (totalQuests > 0) {
                            (completedQuestsCount * 100) / totalQuests
                        } else 0
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Accuracy",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "MEMORABILIA TROPHY CASE",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Render dynamic badges: Fedora, Vinyl, Glove based on era completion status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val isEarlyUnlocked = unlockedBadges.any { it.badgeId.startsWith("badge_early_") }
                    val isEightiesUnlocked = unlockedBadges.any { it.badgeId.startsWith("badge_eighties_") }
                    val isLegacyUnlocked = unlockedBadges.any { it.badgeId.startsWith("badge_legacy_") }

                    class BadgeCardData(
                        val badgeId: String,
                        val label: String,
                        val drawableId: Int,
                        val isUnlocked: Boolean
                    )

                    val badgeList = listOf(
                        BadgeCardData("badge_early", "Early Era Medal", R.drawable.img_mj_banner, isEarlyUnlocked),
                        BadgeCardData("badge_eighties", "Gold 80s Badge", R.drawable.img_app_icon_fg, isEightiesUnlocked),
                        BadgeCardData("badge_legacy", "Legacy Crest", R.drawable.img_mj_vinyl, isLegacyUnlocked)
                    )

                    badgeList.forEach { badge ->
                        val badgeId = badge.badgeId
                        val label = badge.label
                        val drawableId = badge.drawableId
                        val isUnlocked = badge.isUnlocked
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(if (isUnlocked) GoldSecondary.copy(alpha = 0.2f) else Color(0x11FFFFFF))
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (isUnlocked) GoldPrimary else Color(0x22FFFFFF)
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = drawableId),
                                    contentDescription = label,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .alpha(if (isUnlocked) 1.0f else 0.4f),
                                    contentScale = ContentScale.Crop
                                )
                                if (!isUnlocked) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0x77000000)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked",
                                            tint = MutedText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUnlocked) GoldPrimary else MutedText,
                                fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                              )
                        }
                    }
                }
            }
        }

        // Section tab choices: 'Early Era', '80s Pop Icon', 'Legacy'
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "SELECT AN ICONIC ERA",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // The 3 major pills selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val eras = listOf(
                        Triple("early", "Early Era", "1960s - '70s"),
                        Triple("eighties", "80s Pop Icon", "Peak Records"),
                        Triple("legacy", "Legacy Crest", "1990s - Late")
                    )

                    eras.forEach { (prefix, label, dates) ->
                        val selected = selectedEra == prefix
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) GoldPrimary else CharcoalSurface)
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (selected) GoldPrimary else CharcoalBorder
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onEraSelected(prefix) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) VelvetBlackBackground else PureWhite,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = dates,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 8.sp,
                                color = if (selected) VelvetBlackBackground.copy(alpha = 0.7f) else MutedText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Subtopic items title helper
        item {
            Text(
                text = "CHOOSE A TARGET TOPIC QUEST (${filteredQuizzes.size} AVAILABLE)",
                style = MaterialTheme.typography.labelLarge,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 4.dp)
            )
        }

        // Filtered selectable list (24 quizzes)
        items(filteredQuizzes) { category ->
            val scoreRecord = highScores.find { it.categoryId == category.id }
            val completed = scoreRecord != null && scoreRecord.completedCount > 0
            val bestScore = scoreRecord?.bestScore ?: 0

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalSurface)
                    .border(
                        BorderStroke(
                            1.dp,
                            if (completed) GoldPrimary.copy(alpha = 0.5f) else CharcoalBorder
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { viewModel.selectCategory(category.id) }
                    .testTag("category_card_${category.id}")
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LEVEL: ${category.level}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (category.level == "Expert") GoldPrimary else GoldSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                            if (completed) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(
                                            GoldPrimary.copy(alpha = 0.15f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, pyPercent = 2)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completed icon",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PASSED",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Image(
                        painter = painterResource(id = category.imageResId),
                        contentDescription = category.title,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Quests icon",
                            tint = GoldPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${category.questions.size} Quests",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldPrimary,
                            fontSize = 10.sp
                        )

                        if (completed) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Best Score icon",
                                tint = GoldPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Best: $bestScore/${category.questions.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.selectCategory(category.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (completed) CharcoalSurface else GoldPrimary,
                            contentColor = if (completed) GoldPrimary else VelvetBlackBackground
                        ),
                        border = if (completed) BorderStroke(1.dp, GoldPrimary) else null,
                        shape = RoundedCornerShape(50.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("start_quiz_button_${category.id}")
                    ) {
                        Text(
                            text = if (completed) "RETRY QUEST" else "ENTER QUEST",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

data class CelebrationItem(
    val title: String,
    val subtitle: String,
    val value: String,
    val details: String
)

@Composable
fun CelebrationScreen(
    onItemClick: (CelebrationItem) -> Unit
) {
    val awardsList = listOf(
        CelebrationItem(
            "Guinness World Records Match",
            "Highest Level Entertainer Achievement",
            "Most Successful Entertainer of All Time",
            "Inducted directly into the Guinness Book of World Records as the single most successful entertainer in global human history. Outstanding milestones include setting the world record for the best-selling studio album of all time ('Thriller', calculated with over 100 million copies distributed globally) and the historical 8 Grammys single-night sweep."
        ),
        CelebrationItem(
            "13 Grammy Awards + Legend",
            "Unprecedented Album Master sweeps",
            "Best Single Night record sweep",
            "Michael Jackson shattered global music industry records on February 28, 1984, by winning a historic eight Grammys in a single evening, highlighting his absolute visual and digital sonic dominance. In addition, in 1993, he was internationally recognized with the Grammy Legend Award, presented directly by his loving sister Janet."
        ),
        CelebrationItem(
            "26 American Music Awards",
            "Artist of the Century Honoree",
            "All-Time AMA Trophy Sovereign",
            "With an incredible lifetime total of 26 AMA awards, Michael Jackson holds more trophies than any other solo performer in history. In 2002, he was officially crowned with the AMA 'Artist of the Century' award, a testament to his three-decade-long reign over global music styles."
        ),
        CelebrationItem(
            "Rock & Roll Hall of Fame",
            "Historical Dual Induction",
            "Two-Time Hall of Fame Legend",
            "Michael Jackson achieved the rare distinction of being inducted into the Rock and Roll Hall of Fame twice. First in 1997 as the pre-eminent lead vocalist, key writer, and frontman of the Jackson 5, and second in 2001 as a solo pioneer who forever redefined the pop format."
        )
    )

    val albumsList = listOf(
        CelebrationItem(
            "Thriller (1982)",
            "World Record Holder",
            "100 Million+ Global Sales",
            "Produced alongside Quincy Jones, 'Thriller' is the ultimate musical artifact in human history. Blending classic disco rhythms, hard arena rock beats ('Beat It'), smooth R&B melodies ('Billie Jean'), and lush analog synthesizers ('Human Nature'), it spent 37 non-consecutive weeks at #1 on the Billboard 200, setting a solo landmark."
        ),
        CelebrationItem(
            "Bad (1987)",
            "Hot 100 Historic Single Sweep",
            "5 Consecutive Billboard #1 Hits",
            "Michael Jackson became the first artist in chart history to yield five consecutive number-one single hits from a single album ('I Just Can't Stop Loving You', 'Bad', 'The Way You Make Me Feel', 'Man in the Mirror', and 'Dirty Diana'). The record bad tour went on to gross over $125 million across 16 months."
        ),
        CelebrationItem(
            "Dangerous (1991)",
            "The New Jack Swing Revolution",
            "Teddy Riley Partnership",
            "A majestic album capturing the transition of the '90s. Co-produced with urban rhythm wizard Teddy Riley, Michael incorporated industrial metal beats, dense synthesizers, quick-fire rap bridges, and gorgeous, soaring choral spirituals ('Heal the World', 'Will You Be There'). Features Mark Ryden's legendary custom catalog artwork."
        ),
        CelebrationItem(
            "Off the Wall (1979)",
            "Adult Solo Landmark",
            "Breakthrough Disco & Funk",
            "The album that officially declared Michael Jackson's adult maturity. Blending disco percussion, jazzy brass chords, and deep, sincere soul, Michael became the first solo recording artist to secure four US top 10 hit tracks from a single venture, establishing his legendary partnership with Quincy Jones."
        )
    )

    val memorabiliaList = listOf(
        CelebrationItem(
            "Swarovski Crystal Glove",
            "Performance Mastery Element",
            "Hand-Stitched Swarovski Glove",
            "Worn first during the legendary Motown 25 performance in 1983, Michael Jackson's single white glove was custom-stitched with hundreds of Swarovski rhinestones. Designed so that the high-power spotlights would bounce off his fingers during the Moonwalk, drawing the audience's focus to his kinetic physical expressions."
        ),
        CelebrationItem(
            "Anti-Gravity Lean Shoes",
            "U.S. Patent Number 5,255,452",
            "Peg-Lock Stage Footwear",
            "An ingenious piece of performance hardware co-patented by Michael Jackson. The boots feature custom triangular-cut heel slots that slide over steel pegs rising on cue from the stage floor. Once locked, Michael and his dancers could tilt forward at an extreme, gravity-defying 45-degree angle."
        ),
        CelebrationItem(
            "Custom Borsalino Fedoras",
            "The Dance Finale Tradition",
            "Black Silk-Lined Silk Hat",
            "Michael Jackson's black fedora became synonymous with 'Billie Jean'. Handcrafted by Italian luxury milliners Borsalino (and Worth & Worth), each hat was custom-stamped with Michael's name in gold foil on the inside band. Michael traditionally completed the dance by throwing his fedora into the ecstatic crowd."
        ),
        CelebrationItem(
            "The Royal Military Jackets",
            "Royal Sequin Dress Coats",
            "Gold Epaulet Parade Wear",
            "Designed by Michael's styling team, these jackets drew deep inspiration from high-society British royal officers. Featuring heavy gold sashes, metallic epaulets, custom crystal armbands (worn to symbolize global suffering and childhood hope), and intricate brocade embroidery, they cemented his King of Pop persona."
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "MUSEUM & CELEBRATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldTertiary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                ShinyGoldSignatureLogo(scale = 0.85f, modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "HONORING THE LIFE, MOVES & SECRETS OF THE KING OF POP",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 10.sp,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
            }
        }

        // 1. AWARDS MUSEUM HEAD
        item {
            Text(
                text = "✪ WORLD-RECORD AWARDS",
                style = MaterialTheme.typography.labelLarge,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(awardsList) { item ->
            CelebrationCard(item = item, onClick = { onItemClick(item) })
        }

        // 2. ALBUMS VAULT HEAD
        item {
            Text(
                text = "✪ PLATINUM ALBUM VAULT",
                style = MaterialTheme.typography.labelLarge,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(albumsList) { item ->
            CelebrationCard(item = item, onClick = { onItemClick(item) })
        }

        // 3. MEMORABILIA HEAD
        item {
            Text(
                text = "✪ EXTRAORDINARY MEMORABILIA (STUFF HE OWNED)",
                style = MaterialTheme.typography.labelLarge,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(memorabiliaList) { item ->
            CelebrationCard(item = item, onClick = { onItemClick(item) })
        }
    }
}

@Composable
fun CelebrationCard(
    item: CelebrationItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CharcoalSurface)
            .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.subtitle.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoldPrimary,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary.copy(alpha = 0.1f))
                    .border(BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Read story",
                    tint = GoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

data class QuoteItem(
    val quote: String,
    val context: String
)

@Composable
fun QuotesScreen() {
    val quotesList = remember {
        listOf(
            QuoteItem(
                "In a world filled with hate, we must still dare to hope. In a world filled with anger, we must still dare to comfort. In a world filled with despair, we must still dare to dream. And in a world filled with distrust, we must still dare to believe.",
                "From Michael's landmark Oxford Union speech on global child love."
            ),
            QuoteItem(
                "Lies run sprints, but the truth runs marathons. The truth will always win this race in the end.",
                "Reflecting on his resilience and his relationship with media gossip."
            ),
            QuoteItem(
                "To give someone a piece of your heart is worth far more than all the wealth and material treasures in the universe.",
                "On charity, humanitarian foundations, and spiritual kindness."
            ),
            QuoteItem(
                "If you enter this world knowing you are loved and you leave this world knowing the same, then everything that happens in between can be dealt with.",
                "The core philosophy directing his life, music, and Neverland Valley Ranch."
            ),
            QuoteItem(
                "Consciousness expresses itself through creation. This world we live in is the dance of the creator. Dancers come and go in the twinkling of an eye, but the dance lives on.",
                "From Michael's reflective poetry book, 'Dancing the Dream'."
            ),
            QuoteItem(
                "Laughter is such a medicine. It’s like a bath for the soul. It wipes away all the dirt of prejudice and anger, and lets you feel like a child again.",
                "About the pure joy and innocence of children."
            ),
            QuoteItem(
                "The greatest education in the world is watching the masters at work. You study the greats and you become greater.",
                "Regarding his absolute perfectionism and respect for pioneers like Fred Astaire."
            ),
            QuoteItem(
                "I'm just like anyone. I cut and I bleed and I embarrass easily. If you write negative things simply because they sell, you forget we are human souls.",
                "Regarding the high burdens of extreme global visual scrutiny."
            ),
            QuoteItem(
                "Let us dream of tomorrow where we can truly love from the soul, and know love as the ultimate truth at the heart of all creation.",
                "An inspiring call for global humanitarian solidarity."
            ),
            QuoteItem(
                "People ask me how I make music. I tell them I just step into it. It’s like stepping into a river and joining the flow. Every song is a gift of the divine.",
                "On his natural songwriting process and global musical currents."
            )
        )
    }

    var selectedQuoteIndex by remember { mutableStateOf(0) }
    val featuredQuote = quotesList[selectedQuoteIndex]

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "WORDS OF MAJESTY",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldTertiary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                ShinyGoldSignatureLogo(scale = 0.85f, modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "INSPIRING QUOTES DIRECTLY FROM THE KING OF POP",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 10.sp,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
            }
        }

        // 1. FEATURED DAILY REVELATION CARD WITH GOLD GRADIENT
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF231E12), Color(0xFF13110E))
                        )
                    )
                    .border(BorderStroke(1.5.dp, GoldPrimary), RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Spiritual Heart",
                    tint = GoldPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "“${featuredQuote.quote}”",
                    style = TextStyle(
                        fontStyle = FontStyle.Italic,
                        fontSize = 17.sp,
                        lineHeight = 26.sp,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "— MICHAEL JACKSON",
                    style = MaterialTheme.typography.labelSmall,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = featuredQuote.context,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = MutedText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        selectedQuoteIndex = (selectedQuoteIndex + 1) % quotesList.size
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = VelvetBlackBackground
                    ),
                    shape = RoundedCornerShape(50.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "REVEAL NEXT WORDS OF MAJESTY 🌟",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2. COMPLETE LIBRARY TEXT HEAD
        item {
            Text(
                text = "✪ ALL WORDS OF MAJESTY",
                style = MaterialTheme.typography.labelLarge,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Render all other quotes list
        items(quotesList) { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CharcoalSurface)
                    .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Quote Heart",
                        tint = GoldSecondary,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "“${item.quote}”",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OffWhite,
                            lineHeight = 20.sp,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.context,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = GoldSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizContentScreen(
    viewModel: QuizViewModel,
    category: QuizCategory
) {
    val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val selectedOption by viewModel.selectedOptionIndex.collectAsStateWithLifecycle()
    val checked by viewModel.answerChecked.collectAsStateWithLifecycle()
    val points by viewModel.points.collectAsStateWithLifecycle()
    val correctCount by viewModel.correctAnswersCount.collectAsStateWithLifecycle()
    val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()

    val currentQuestion = category.questions[currentIndex]
    val totalQuestions = category.questions.size
    val progress = (currentIndex + 1).toFloat() / totalQuestions.toFloat()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .background(VelvetBlackBackground)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Header navigation mimicking styled Design elements
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CharcoalSurface)
                                .border(BorderStroke(1.dp, CharcoalBorder), CircleShape)
                                .clickable { viewModel.pauseQuiz() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back",
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                            Text(
                                text = "QUIZ LEVEL: ${category.level.uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldPrimary.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = PureWhite,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Pause Button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CharcoalSurface)
                                    .border(BorderStroke(1.dp, CharcoalBorder), CircleShape)
                                    .clickable { viewModel.pauseQuiz() }
                                    .testTag("pause_quiz_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(13.dp)
                                            .background(GoldPrimary)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(13.dp)
                                            .background(GoldPrimary)
                                    )
                                }
                            }

                            // Current Score container
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$correctCount",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = VelvetBlackBackground,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                Spacer(modifier = Modifier.height(16.dp))

                // Golden progress bar & stats
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(CharcoalSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(GoldSecondary, GoldPrimary)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUEST ${String.format("%02d", currentIndex + 1)}/${String.format("%02d", totalQuestions)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MutedText,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "$points POINTS",
                        style = MaterialTheme.typography.labelLarge,
                        color = GoldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        bottomBar = {
            // Elegant modern bottom bar presenting choices confirmation
            Row(
                modifier = Modifier
                    .background(CharcoalSurface)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mimicking '+3' profile stacking in design mock
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF222222))
                            .border(BorderStroke(1.dp, VelvetBlackBackground), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MJ", fontSize = 10.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width((-6).dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary)
                            .border(BorderStroke(1.dp, VelvetBlackBackground), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("K", fontSize = 10.sp, color = VelvetBlackBackground, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width((-6).dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF444444))
                            .border(BorderStroke(1.dp, VelvetBlackBackground), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+3", fontSize = 10.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }

                // SUBMIT / NEXT button
                val enabled = selectedOption != null
                Button(
                    onClick = {
                        if (checked) {
                            viewModel.nextQuestion()
                        } else {
                            viewModel.checkAnswer()
                        }
                    },
                    enabled = enabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (checked) GoldPrimary else PureWhite,
                        contentColor = VelvetBlackBackground,
                        disabledContainerColor = Color(0x33FFFFFF),
                        disabledContentColor = Color(0x55FFFFFF)
                    ),
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    modifier = Modifier
                        .testTag(if (checked) "next_question_button" else "submit_answer_button")
                        .shadow(
                            elevation = if (enabled) 12.dp else 0.dp,
                            shape = RoundedCornerShape(100.dp),
                            clip = false
                        )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (checked) "NEXT QUESTION" else "SUBMIT ANSWER",
                            style = MaterialTheme.typography.labelLarge,
                            color = VelvetBlackBackground,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Bolt Action",
                            tint = VelvetBlackBackground,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        containerColor = VelvetBlackBackground,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Elegant rounded container containing a thematic placeholder/image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(CharcoalSurface)
                        .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(24.dp))
                ) {
                    Image(
                        painter = painterResource(id = category.imageResId),
                        contentDescription = "Question illustration",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Golden overlay transparent gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xAA050505)),
                                    startY = 50f
                                )
                            )
                    )
                    // Subtitle badge mimicking design's "VICTORY TOUR" info badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xCC000000), RoundedCornerShape(50.dp))
                                .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(50.dp))
                                .padding(horizontal = 12.dp, pyPercent = 4)
                        ) {
                            Text(
                                text = "QUEST MASTER • CHRONICLE",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            )
                        }
                    }
                    // Glowing stars
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star Decoration",
                            tint = GoldPrimary,
                            modifier = Modifier
                                .size(24.dp)
                                .shadow(8.dp, CircleShape)
                        )
                    }
                }
            }

            // The highly detailed Question text
            item {
                Spacer(modifier = Modifier.height(6.dp))
                val annotatedQuestion = buildAnnotatedString {
                    val text = currentQuestion.text
                    // We can highlight specific keywords (Year, Name, Award, Album) in Gold to match styled mockup
                    val words = text.split(" ")
                    words.forEachIndexed { idx, word ->
                        val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "")
                        val highlight = cleanWord == "Grammy" || cleanWord == "Awards" ||
                                cleanWord == "Thriller" || cleanWord == "Michael" ||
                                cleanWord == "beat" || cleanWord == "world" ||
                                cleanWord == "record" || cleanWord == "1984" ||
                                cleanWord == "1983" || cleanWord == "1972" ||
                                cleanWord == "1993" || cleanWord == "1987" ||
                                cleanWord == "Billie" || cleanWord == "Jean" ||
                                cleanWord == "Smooth" || cleanWord == "Criminal"

                        if (highlight) {
                            withStyle(style = SpanStyle(color = GoldPrimary, fontWeight = FontWeight.Bold)) {
                                append(word)
                            }
                        } else {
                            withStyle(style = SpanStyle(color = PureWhite)) {
                                append(word)
                            }
                        }
                        if (idx < words.size - 1) append(" ")
                    }
                }

                Text(
                    text = annotatedQuestion,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 19.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Options List
            itemsIndexed(currentQuestion.options) { optionIdx, option ->
                val isSelected = selectedOption == optionIdx
                val isCorrect = optionIdx == currentQuestion.correctOptionIndex

                val optionBg = when {
                    checked && isCorrect -> CorrectGreen
                    checked && isSelected && !isCorrect -> IncorrectRed
                    isSelected -> GoldPrimary
                    else -> CharcoalSurface
                }

                val optionTextColor = when {
                    checked && isCorrect -> PureWhite
                    checked && isSelected && !isCorrect -> PureWhite
                    isSelected -> VelvetBlackBackground
                    else -> PureWhite
                }

                val optionBorder = when {
                    checked && isCorrect -> BorderStroke(1.dp, CorrectGreen)
                    checked && isSelected && !isCorrect -> BorderStroke(1.dp, IncorrectRed)
                    isSelected -> BorderStroke(1.dp, GoldPrimary)
                    else -> BorderStroke(1.dp, CharcoalBorder)
                }

                val prefix = when (optionIdx) {
                    0 -> "A"
                    1 -> "B"
                    2 -> "C"
                    else -> "D"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(optionBg)
                        .border(optionBorder, RoundedCornerShape(16.dp))
                        .clickable(!checked) { viewModel.selectOption(optionIdx) }
                        .testTag("option_button_$optionIdx")
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$prefix. $option",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected || (checked && isCorrect)) FontWeight.Bold else FontWeight.Medium,
                        color = optionTextColor,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    if (checked) {
                        if (isCorrect) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Correct",
                                tint = if (isSelected) PureWhite else GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        } else if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Incorrect",
                                tint = PureWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .border(BorderStroke(1.dp, MutedText), CircleShape)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (isSelected) VelvetBlackBackground else CharcoalBorder
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(VelvetBlackBackground)
                                )
                            }
                        }
                    }
                }
            }

            // Explanation Section visible only after Checking state
            item {
                AnimatedVisibility(
                    visible = checked,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 }),
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CharcoalSurface)
                            .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "RECORD KNOWLEDGE REVEAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQuestion.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OffWhite,
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // PAUSE OVERLAY HERE
    if (isPaused) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VelvetBlackBackground.copy(alpha = 0.95f))
                .clickable(enabled = true, onClick = {}) // Block clicks on elements underneath
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CharcoalSurface)
                    .border(BorderStroke(1.5.dp, GoldPrimary), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(32.dp)
                                .background(GoldPrimary, RoundedCornerShape(2.dp))
                        )
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(32.dp)
                                .background(GoldPrimary, RoundedCornerShape(2.dp))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "QUEST SUSPENDED",
                    color = PureWhite,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = category.title,
                    color = GoldSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Current state snapshot
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VelvetBlackBackground, RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CURRENT RECOLLECTION REPORT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "QUEST",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                            Text(
                                text = "${currentIndex + 1}/${totalQuestions}",
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "POINTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                            Text(
                                text = "$points",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Button(
                    onClick = { viewModel.resumeQuiz() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = VelvetBlackBackground
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("resume_quiz_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Resume Icon",
                            tint = VelvetBlackBackground,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "RESUME QUEST",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.saveAndExitQuiz() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CharcoalSurface,
                        contentColor = PureWhite
                    ),
                    border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_and_exit_quiz_button")
                ) {
                    Text(
                        text = "SAVE PROGRESS & EXIT",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { viewModel.discardAndExitQuiz() },
                    modifier = Modifier.testTag("abandon_quiz_button")
                ) {
                    Text(
                        text = "ABANDON QUEST (DISCARD)",
                        color = Color(0xFFEF5350), // Red
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
}

@Composable
fun ResultsScreen(
    viewModel: QuizViewModel,
    category: QuizCategory,
    highScores: List<com.example.data.QuizScoreEntity>,
    unlockedBadges: List<com.example.data.UnlockedBadgeEntity>
) {
    val correctCount by viewModel.correctAnswersCount.collectAsStateWithLifecycle()
    val points by viewModel.points.collectAsStateWithLifecycle()
    val totalQuestions = category.questions.size

    val badgeUnlocked = (correctCount.toFloat() / totalQuestions.toFloat()) >= 0.8f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "QUEST COMPLETED",
                style = MaterialTheme.typography.labelLarge,
                color = MutedText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            ShinyGoldSignatureLogo(
                scale = 0.85f,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Massive centered Moonwalk achievement graphic
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(CharcoalSurface)
                    .border(BorderStroke(2.dp, GoldPrimary), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_mj_moonwalk),
                    contentDescription = "Michael Jackson Performance",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gold foil circle overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xAA050505)),
                                startY = 100f
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = "FINAL SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$correctCount / $totalQuestions",
                style = MaterialTheme.typography.displayLarge,
                color = GoldPrimary,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "$points TOTAL XP POINTS",
                style = MaterialTheme.typography.titleLarge,
                color = PureWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic ranking card evaluated based on score
            val (rankTitle, rankDesc) = when {
                correctCount >= 8 -> Pair("The True Moonwalker 🌟", "Preeminent expertise! Michael would be absolutely proud of your elite precision.")
                correctCount >= 5 -> Pair("Thriller Super Fan! 🎉", "Fantastic memory! You know the King of Pop's legacy, with only minor gaps.")
                else -> Pair("MJ Casual Fan 🎵", "A good start, but there's so much rich history left to master. Spin 'Off the Wall' and try again!")
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalSurface)
                    .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = rankTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rankDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OffWhite,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Badge unlock reveal if threshold was reached
            if (badgeUnlocked) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GoldPrimary.copy(alpha = 0.08f))
                        .border(BorderStroke(1.dp, GoldPrimary), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Badge Unlocked",
                        tint = GoldPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NEW MEMORABILIA UNLOCKED!",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = category.rewardBadgeTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = category.rewardBadgeDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x11FFFFFF))
                        .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Badge locked",
                        tint = MutedText,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "BADGE REQUIRES 80% OR HIGHER",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = category.rewardBadgeTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = MutedText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Correctly answer at least 8 questions on this category to secure this reward in your gallery.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.selectCategory(category.id) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CharcoalSurface,
                    contentColor = GoldPrimary
                ),
                border = BorderStroke(1.dp, CharcoalBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("retry_quiz_button"),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "RETRY",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { viewModel.exitQuiz() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = VelvetBlackBackground
                ),
                modifier = Modifier
                    .weight(1.5f)
                    .height(50.dp)
                    .testTag("return_home_button"),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "RETURN TO VAULT",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Inline helper extension for vertical cell padding percentages in custom layouts
private fun Modifier.padding(horizontal: androidx.compose.ui.unit.Dp, pyPercent: Int) =
    this.padding(horizontal = horizontal, vertical = (pyPercent * 2).dp)

@Composable
fun ShinyGoldSignatureLogo(
    modifier: Modifier = Modifier,
    scale: Float = 1.0f
) {
    val goldBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFFBD6), // Shining highlight gold
            Color(0xFFE5C158), // Medium sparkling gold
            Color(0xFFD4AF37), // Core beautiful gold
            Color(0xFF8E6E27), // Deep golden shadow
            Color(0xFFFFFBD6)  // Highlight gold wrap
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(800f * scale, 800f * scale)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "Michael",
            style = TextStyle(
                fontFamily = FontFamily.Cursive,
                fontStyle = FontStyle.Italic,
                fontSize = (56 * scale).sp,
                fontWeight = FontWeight.Bold,
                brush = goldBrush,
                shadow = Shadow(
                    color = Color(0xBB000000),
                    offset = androidx.compose.ui.geometry.Offset(3f, 5f),
                    blurRadius = 8f
                ),
                letterSpacing = (0.5 * scale).sp
            ),
            modifier = Modifier.testTag("signature_text_logo")
        )
        
        Spacer(modifier = Modifier.height((2 * scale).dp))
        
        Canvas(
            modifier = Modifier
                .width((140 * scale).dp)
                .height((10 * scale).dp)
        ) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(2f, height * 0.1f)
                // A beautiful sweep stroke running left to right under the name
                quadraticTo(
                    width * 0.5f, height * 1.5f,
                    width - 4f, height * 0.4f
                )
            }
            drawPath(
                path = path,
                brush = goldBrush,
                style = Stroke(
                    width = 4.dp.toPx() * scale,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}
