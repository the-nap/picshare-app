package com.picshare.app.ui.feed

import androidx.lifecycle.ViewModel
import com.picshare.app.post.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
  private val repository: PostRepository
): ViewModel(){
}