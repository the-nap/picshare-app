package com.picshare.app.ui.events

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventBus @Inject constructor() {

  private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 1)
  val events = _events.asSharedFlow()

  suspend fun send(event: AppEvent) = _events.emit(event)
}