package com.example.newsapiclient.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapiclient.data.model.APIResponse
import com.example.newsapiclient.data.util.Resource
import com.example.newsapiclient.domain.usecase.GetNewsHeadlinesUseCase
import com.example.newsapiclient.domain.usecase.GetSavedNewsUseCase
import com.example.newsapiclient.domain.usecase.GetSearchedNewsUseCase
import kotlinx.coroutines.launch
import java.lang.Exception

class NewsViewModel(
    private val app:Application,
    private val getNewsHeadlinesUseCase: GetNewsHeadlinesUseCase,
    private val getSearchedNewsUseCase: GetSearchedNewsUseCase
):AndroidViewModel(app) {
    private var _newsHeadlines:MutableLiveData<Resource<APIResponse>> = MutableLiveData()
    val newsHeadlines:LiveData<Resource<APIResponse>> = _newsHeadlines

    private var _searchHeadlines:MutableLiveData<Resource<APIResponse>> = MutableLiveData()
    val searchHeadlines:LiveData<Resource<APIResponse>> = _searchHeadlines

    fun getNewsHeadlines(country:String,page:Int){
        viewModelScope.launch{
            _newsHeadlines.postValue(Resource.Loading())
            try {
                if(isNetworkAvailable(app)){
                    val apiResult = getNewsHeadlinesUseCase.execute(country,page)
                    _newsHeadlines.postValue(apiResult)
                }
            }catch (e:Exception){
                _newsHeadlines.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun isNetworkAvailable(context: Context?):Boolean{
        if(context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if(capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->{
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->{
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->{
                        return true
                    }
                }
            }
        }
        return false
    }

    fun getSearchedNews(
        country:String,
        searchQuery:String,
        page:Int
    )=viewModelScope.launch {
        try{
            _searchHeadlines.postValue(Resource.Loading())
            if(isNetworkAvailable(app)){
                val response = getSearchedNewsUseCase.execute(country,searchQuery,page)
                _searchHeadlines.postValue(response)
            }else{
                _searchHeadlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (e:Exception){
            _searchHeadlines.postValue(Resource.Error(e.message.toString()))
        }
    }
}