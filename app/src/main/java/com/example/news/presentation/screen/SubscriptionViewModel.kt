package com.example.news.presentation.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.data.mapper.queryToString
import com.example.news.domain.model.Article
import com.example.news.domain.usecase.newsUseCase.AddSubscriptionUseCase
import com.example.news.domain.usecase.newsUseCase.ClearAllArticlesUseCase
import com.example.news.domain.usecase.newsUseCase.DeleteSubscriptionUseCase
import com.example.news.domain.usecase.newsUseCase.GetAllSubscriptionUseCase
import com.example.news.domain.usecase.newsUseCase.GetArticleByTopicUseCase
import com.example.news.domain.usecase.newsUseCase.UpdateArticlesForTopicUseCase
import com.example.news.domain.usecase.newsUseCase.UpdateArticlesFromSubscription
import com.example.news.domain.usecase.settingsUseCase.RefreshSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    val addSubscriptionUseCase: AddSubscriptionUseCase,
    val clearAllArticlesUseCase: ClearAllArticlesUseCase,
    val deleteSubscriptionUseCase: DeleteSubscriptionUseCase,
    val getAllSubscriptionUseCase: GetAllSubscriptionUseCase,
    val updateArticlesForTopicUseCase: UpdateArticlesForTopicUseCase,
    val updateArticlesFromSubscription: UpdateArticlesFromSubscription,
    val getArticleByTopicUseCase: GetArticleByTopicUseCase,
    val refreshSettingsUseCase: RefreshSettingsUseCase
) : ViewModel() {
    val _state = MutableStateFlow(Subscription())
    val state = _state.asStateFlow()

    init {
        observeAllSubscription()
        observeAllArticles()
    }
    fun processCommand(command: SubscriptionCommand){
        when(command){

            is SubscriptionCommand.AddSubscription -> {
                viewModelScope.launch {
                    _state.update {previousState ->
                        addSubscriptionUseCase(command.topic)
                        val updated = previousState.subscriptions.toMutableMap().apply {
                            put(command.topic,true)
                        }
                        Log.d("AddSubArticles","${previousState.articles}")
                        previousState.copy(subscriptions = updated, articles = previousState.articles)
                    }
                }
            }
            SubscriptionCommand.ClearArticles -> {
                val topics = state.value.selectedTopics
                viewModelScope.launch {
                    clearAllArticlesUseCase(topics)
                }
            }
            is SubscriptionCommand.DeleteSubscription -> {
                viewModelScope.launch {
                    try {
                        _state.update { previousState ->
                            deleteSubscriptionUseCase(command.topic)
                            Log.d("Exg","before delete")
                            val currentSubscription = previousState.subscriptions - command.topic
                            Log.d("Deleted","$currentSubscription")
                            previousState.copy(subscriptions = currentSubscription)
                        }
                    } catch (e: Exception) {
                        Log.e("DeleteSub", "Error deleting subscription", e)
                    }
                }
            }
            is SubscriptionCommand.InputTopic -> {
                _state.update { previousState ->
                    previousState.copy(topic = command.topic)
                }
            }
            is SubscriptionCommand.ToggleSubscription -> {
                _state.update {previousState ->
                    val lst = previousState.subscriptions.toMutableMap()
                    val selected = lst[command.topic] ?: false
                    lst[command.topic] = !selected
                    previousState.copy(subscriptions = lst)
                }
            }
            SubscriptionCommand.UpdateArticles -> {
                viewModelScope.launch {
                    updateArticlesFromSubscription()
                }
            }
        }
    }
    fun observeAllSubscription(){
        getAllSubscriptionUseCase().onEach { subscriptions ->
            _state.update { previousState ->
                val updatedSubs = subscriptions.associateWith {topic ->
                    previousState.subscriptions[topic] ?: true
                }
                previousState.copy(subscriptions = updatedSubs)
            }
        }.launchIn(viewModelScope)
    }

    fun observeAllArticles(){
        Log.d("Observer","I am started...")
        state.map { it.selectedTopics }.distinctUntilChanged().onEach { Log.d("ObserverTopics", "selectedTopics = $it") }.flatMapLatest {
            getArticleByTopicUseCase(it)
        }
            .onEach {
                Log.d("Observer","IAMCHANGED")
                _state.update { previousState ->
                    previousState.copy(articles = it)
                }
            }
            .launchIn(viewModelScope)
    }
}
sealed interface SubscriptionCommand{
    data object UpdateArticles : SubscriptionCommand
    data class InputTopic(val topic: String) : SubscriptionCommand
    data class AddSubscription(val topic: String) : SubscriptionCommand
    data class DeleteSubscription(val topic: String) : SubscriptionCommand
    data class ToggleSubscription(val topic: String) : SubscriptionCommand
    data object ClearArticles : SubscriptionCommand
}
data class Subscription(
    val topic: String = "",
    val subscriptions: Map<String,Boolean> = mapOf(),
    val articles: List<Article> = listOf()
){
    val selectedTopics
        get() = subscriptions.filter { it.value }.map { it.key }
}