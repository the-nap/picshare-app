package com.picshare.app.ui.events

interface AppEvent {

  data class PostDeleted(val postId: String): AppEvent
  data class Message(val message: String): AppEvent
}