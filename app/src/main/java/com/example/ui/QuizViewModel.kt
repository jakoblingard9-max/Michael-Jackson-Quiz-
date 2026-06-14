package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.QuizCategory
import com.example.data.QuizDatabase
import com.example.data.QuizRepository
import com.example.data.QuizScoreEntity
import com.example.data.UnlockedBadgeEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        QuizDatabase::class.java,
        "mj_quiz_database"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.quizDao()

    // Exposed Flows from Room persistence
    val highScores: StateFlow<List<QuizScoreEntity>> = dao.getAllScoresFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val unlockedBadges: StateFlow<List<UnlockedBadgeEntity>> = dao.getAllBadgesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Quiz UI state
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _answerChecked = MutableStateFlow(false)
    val answerChecked: StateFlow<Boolean> = _answerChecked.asStateFlow()

    private val _correctAnswersCount = MutableStateFlow(0)
    val correctAnswersCount: StateFlow<Int> = _correctAnswersCount.asStateFlow()

    private val _points = MutableStateFlow(0)
    val points: StateFlow<Int> = _points.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    // Saved in-memory sessions for resume functionality
    data class SavedQuizSession(
        val categoryId: String,
        val currentQuestionIndex: Int,
        val selectedOptionIndex: Int?,
        val answerChecked: Boolean,
        val correctAnswersCount: Int,
        val points: Int
    )

    private val _savedSessions = mutableMapOf<String, SavedQuizSession>()

    // Current category ID that has active prompt for resuming
    private val _resumePromptCategoryId = MutableStateFlow<String?>(null)
    val resumePromptCategoryId: StateFlow<String?> = _resumePromptCategoryId.asStateFlow()

    // Active pause state
    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    // Quick access helper for currently active quiz category
    val activeCategory: QuizCategory?
        get() = _selectedCategoryId.value?.let { QuizRepository.getCategoryById(it) }

    fun selectCategory(categoryId: String) {
        val hasSaved = _savedSessions.containsKey(categoryId)
        if (hasSaved) {
            _resumePromptCategoryId.value = categoryId
        } else {
            startFreshCategory(categoryId)
        }
    }

    fun startFreshCategory(categoryId: String) {
        _resumePromptCategoryId.value = null
        _selectedCategoryId.value = categoryId
        _currentQuestionIndex.value = 0
        _selectedOptionIndex.value = null
        _answerChecked.value = false
        _correctAnswersCount.value = 0
        _points.value = 0
        _quizCompleted.value = false
        _isPaused.value = false
    }

    fun resumeSavedCategory(categoryId: String) {
        _resumePromptCategoryId.value = null
        val session = _savedSessions[categoryId]
        if (session != null) {
            _selectedCategoryId.value = session.categoryId
            _currentQuestionIndex.value = session.currentQuestionIndex
            _selectedOptionIndex.value = session.selectedOptionIndex
            _answerChecked.value = session.answerChecked
            _correctAnswersCount.value = session.correctAnswersCount
            _points.value = session.points
            _quizCompleted.value = false
            _isPaused.value = false
        } else {
            startFreshCategory(categoryId)
        }
    }

    fun dismissResumePrompt() {
        _resumePromptCategoryId.value = null
    }

    fun pauseQuiz() {
        if (_selectedCategoryId.value != null && !_quizCompleted.value) {
            _isPaused.value = true
        }
    }

    fun resumeQuiz() {
        _isPaused.value = false
    }

    fun saveAndExitQuiz() {
        val categoryId = _selectedCategoryId.value ?: return
        _savedSessions[categoryId] = SavedQuizSession(
            categoryId = categoryId,
            currentQuestionIndex = _currentQuestionIndex.value,
            selectedOptionIndex = _selectedOptionIndex.value,
            answerChecked = _answerChecked.value,
            correctAnswersCount = _correctAnswersCount.value,
            points = _points.value
        )
        _selectedCategoryId.value = null
        _quizCompleted.value = false
        _isPaused.value = false
    }

    fun discardAndExitQuiz() {
        val categoryId = _selectedCategoryId.value ?: return
        _savedSessions.remove(categoryId)
        _selectedCategoryId.value = null
        _quizCompleted.value = false
        _isPaused.value = false
    }

    fun selectOption(optionIndex: Int) {
        if (!_answerChecked.value) {
            _selectedOptionIndex.value = optionIndex
        }
    }

    fun checkAnswer() {
        val category = activeCategory ?: return
        val currentQuestion = category.questions.getOrNull(_currentQuestionIndex.value) ?: return
        val selected = _selectedOptionIndex.value ?: return

        if (!_answerChecked.value) {
            _answerChecked.value = true
            if (selected == currentQuestion.correctOptionIndex) {
                _correctAnswersCount.value += 1
                // Add points: base points of 100 per correct answer
                _points.value += 100
            }
        }
    }

    fun nextQuestion() {
        val category = activeCategory ?: return
        val nextIndex = _currentQuestionIndex.value + 1

        if (nextIndex < category.questions.size) {
            _currentQuestionIndex.value = nextIndex
            _selectedOptionIndex.value = null
            _answerChecked.value = false
        } else {
            // End of quiz - Save to database
            _quizCompleted.value = true
            saveQuizResult()
        }
    }

    private fun saveQuizResult() {
        val category = activeCategory ?: return
        val score = _correctAnswersCount.value
        val maxScore = category.questions.size

        _savedSessions.remove(category.id)

        viewModelScope.launch {
            // Check existing record
            val existing = dao.getScoreForCategory(category.id)
            val newBest = if (existing != null) {
                maxOf(existing.bestScore, score)
            } else {
                score
            }
            val completedCount = (existing?.completedCount ?: 0) + 1

            dao.saveScore(
                QuizScoreEntity(
                    categoryId = category.id,
                    bestScore = newBest,
                    maxScore = maxScore,
                    completedCount = completedCount
                )
            )

            // Unlock badge if score is at least 80% (8/10 correct answers)
            val successRate = (score.toFloat() / maxScore.toFloat()) >= 0.8f
            if (successRate) {
                dao.unlockBadge(
                    UnlockedBadgeEntity(
                        badgeId = category.rewardBadgeId,
                        title = category.rewardBadgeTitle,
                        description = category.rewardBadgeDesc,
                        category = category.title
                    )
                )
            }
        }
    }

    fun exitQuiz() {
        _selectedCategoryId.value = null
        _quizCompleted.value = false
        _selectedOptionIndex.value = null
        _answerChecked.value = false
    }

    fun resetAllData() {
        viewModelScope.launch {
            dao.clearAllScores()
            dao.clearAllBadges()
        }
        _savedSessions.clear()
        exitQuiz()
    }
}
