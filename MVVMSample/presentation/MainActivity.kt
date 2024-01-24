package com.ongraph.mvvmcode.presentation

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.ongraph.mvvmcode.R
import com.ongraph.mvvmcode.databinding.ActivityMainBinding
import com.ongraph.mvvmcode.presentation.adapter.MovieAdapter
import com.ongraph.mvvmcode.utils.NetworkResult
import com.ongraph.mvvmcode.utils.Utils.progressDialog
import com.ongraph.mvvmcode.viewmodels.MainViewModels
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModels by viewModels()

    private lateinit var binding: ActivityMainBinding
    private var adapter: MovieAdapter?=null
    private var pDialog: Dialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pDialog = progressDialog()
        setAdapter()
        fetchMovies()
        initObserver()
    }


    private fun setAdapter(){
        adapter = MovieAdapter()
        binding.rvMovies.layoutManager = GridLayoutManager(this, 3)
        binding.rvMovies.adapter = adapter

    }

    private fun fetchMovies(){
        pDialog?.show()
        viewModel.fetchMovieResponse()
    }


    private fun initObserver(){
        viewModel.response.observe(this){ res->
            when (res) {
                is NetworkResult.Success -> {
                    res.data?.let { data->
                        adapter?.setData(data)
                    }
                }
                is NetworkResult.Error -> {

                }
            }
            pDialog?.hide()
        }
    }
}