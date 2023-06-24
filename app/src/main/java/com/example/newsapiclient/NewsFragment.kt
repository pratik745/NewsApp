package com.example.newsapiclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapiclient.data.util.Resource
import com.example.newsapiclient.databinding.FragmentNewsBinding
import com.example.newsapiclient.presentation.adapter.NewsAdapter
import com.example.newsapiclient.presentation.viewmodels.NewsViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private lateinit var binding : FragmentNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private var country = "us"
    private var page = 1
    private var isScrolling = false
    private var isLoading = false
    private var isLastPage = false
    private var pages = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentNewsBinding.bind(view)
        viewModel = (activity as MainActivity).newsViewModel
        newsAdapter = (activity as MainActivity).newsAdapter
        newsAdapter.setOnItemClickListener{
            val bundle = Bundle().apply {
                putSerializable("selected_article",it)
            }
            findNavController().navigate(
                R.id.action_newsFragment_to_infoFragment,
                bundle
            )
        }
        initRecyclerView()
        getNews()
        setSearchView()
    }

    private fun initRecyclerView(){
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@NewsFragment.onScrollListener)
        }
    }

    private fun getNews(){
        viewModel.getNewsHeadlines(country,page)
        viewModel.newsHeadlines.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success ->{
                    progressBar(false)
                    response.data?.articles?.let {
                        newsAdapter.differ.submitList(it.toList())
                        if(response.data.totalResults!!% 20 == 0){
                            pages = response.data.totalResults/ 20
                        }
                        else{
                            pages = response.data.totalResults/ 20+1
                        }
                        isLastPage = page == pages
                    }
                }
                is Resource.Error ->{
                    progressBar(false)
                    response.message?.let {
                        Toast.makeText(context,"An error Occurred",Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading ->{
                    progressBar(true)
                }
            }
        }
    }

    private fun progressBar(status:Boolean){
        if(status){
            isLoading = true
            binding.progressBar.visibility = View.VISIBLE
        }else{
            isLoading = false
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private var onScrollListener = object : RecyclerView.OnScrollListener(){

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = binding.rvNews.layoutManager as LinearLayoutManager
            val currentListSize = layoutManager.itemCount
            val visibleItem = layoutManager.childCount
            val topVisibleItem = layoutManager.findFirstVisibleItemPosition()

            val hasReachedToEnd = (topVisibleItem + visibleItem) >= currentListSize
            val shouldPaginate = !isLastPage && isScrolling && hasReachedToEnd && !isLoading
            if(shouldPaginate){
                page++
                viewModel.getNewsHeadlines(country,page)
                isScrolling = false
            }

        }

    }

    private fun setSearchView(){
        binding.svNews.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.getSearchedNews("us",p0.toString(),page)
                viewSearchedNews()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
               MainScope().launch {
                   delay(2000)
                   viewModel.getSearchedNews("us",p0.toString(),page)
                   viewSearchedNews()
               }
                return false
            }

        })

        binding.svNews.setOnCloseListener(object :SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                initRecyclerView()
                getNews()
                return false
            }
        })
    }

    fun viewSearchedNews(){
        viewModel.searchHeadlines.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success ->{
                    progressBar(false)
                    response.data?.articles?.let {
                        newsAdapter.differ.submitList(it.toList())
                        if(response.data.totalResults!!% 20 == 0){
                            pages = response.data.totalResults/ 20
                        }
                        else{
                            pages = response.data.totalResults/ 20+1
                        }
                        isLastPage = page == pages
                    }
                }
                is Resource.Error ->{
                    progressBar(false)
                    response.message?.let {
                        Toast.makeText(context,"An error Occurred",Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading ->{
                    progressBar(true)
                }
            }
        }
    }
}