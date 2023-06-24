package com.example.newsapiclient.presentation.di

import com.example.newsapiclient.data.api.NewsApiService
import com.example.newsapiclient.data.repository.datasource.NewsRemoteDataSource
import com.example.newsapiclient.data.repository.datasourceImpl.NewsRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteDataSourceModule {

    @Provides
    @Singleton
    fun provideNewsHeadlinesDataSource(newsApiService: NewsApiService):NewsRemoteDataSource{
        return NewsRemoteDataSourceImpl(newsApiService)
    }
}