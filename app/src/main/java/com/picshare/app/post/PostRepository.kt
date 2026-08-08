package com.picshare.app.post

class PostRepository {

  private val dataSource: NetworkDataSource;

  suspend fun getPost(id: String){}

  suspend fun getPosts(toSearch: String){}

  suspend fun upload(){}

  suspend fun delete(id: String){}

  suspend fun like(id: String){}

  suspend fun likes(id: String){}

}