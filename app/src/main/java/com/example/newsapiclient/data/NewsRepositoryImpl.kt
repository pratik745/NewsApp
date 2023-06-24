package com.example.newsapiclient.data

import com.example.newsapiclient.data.model.APIResponse
import com.example.newsapiclient.data.model.Article
import com.example.newsapiclient.data.repository.datasource.NewsRemoteDataSource
import com.example.newsapiclient.data.util.Resource
import com.example.newsapiclient.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class NewsRepositoryImpl(
    private val dataSource: NewsRemoteDataSource
):NewsRepository {
    override suspend fun getNewsHeadlines(country:String,page:Int): Resource<APIResponse> {
        return responseToResource(dataSource.getTopHeadlines(country,page))
    }

    override suspend fun getSearchedNews(country:String,searchQuery:String,page:Int): Resource<APIResponse> {
       return responseToResource(dataSource.getSearchedNews(country,searchQuery,page))
    }

    override suspend fun saveNews(article: Article) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNews(article: Article) {
        TODO("Not yet implemented")
    }

    override fun getSavedNews(): Flow<List<Article>> {
        TODO("Not yet implemented")
    }

    private fun responseToResource(apiResponse: Response<APIResponse>):Resource<APIResponse>{
        if(apiResponse.isSuccessful){
            apiResponse.body()?.let { result->
                return Resource.Success(result)
            }
        }
        return Resource.Error(apiResponse.message())
    }
}