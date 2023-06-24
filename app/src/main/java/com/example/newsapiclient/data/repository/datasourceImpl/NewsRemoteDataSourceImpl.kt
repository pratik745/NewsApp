package com.example.newsapiclient.data.repository.datasourceImpl

import com.example.newsapiclient.data.api.NewsApiService
import com.example.newsapiclient.data.model.APIResponse
import com.example.newsapiclient.data.repository.datasource.NewsRemoteDataSource
import retrofit2.Response

class NewsRemoteDataSourceImpl(
    private val newsApiService: NewsApiService,
):NewsRemoteDataSource {
    override suspend fun getTopHeadlines(country:String,page:Int): Response<APIResponse> {
        return newsApiService.getTopHeadlines(country = country, page = page)
    }

    override suspend fun getSearchedNews(country:String,searchQuery:String,page:Int): Response<APIResponse> {
        return newsApiService.getSearchedTopHeadlines(country = country,searchQuery=searchQuery, page = page)
    }
}