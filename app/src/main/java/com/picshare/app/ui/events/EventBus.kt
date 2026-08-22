package com.picshare.app.ui.events

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventBus @Inject constructor() {

  private val _events = Channel<String>()
  val events = _events.receiveAsFlow()

  suspend fun send(toShow: String) = _events.send(toShow)
}