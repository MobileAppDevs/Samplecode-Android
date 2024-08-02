package com.nisha.mvvmstructure.presentation

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.nisha.mvvmstructure.databinding.ActivityMainBinding
import com.nisha.mvvmstructure.presentation.adapter.MyAdapter
import com.nisha.mvvmstructure.utils.NetworkResult
import com.nisha.mvvmstructure.utils.Utils.progressDialog
import com.nisha.mvvmstructure.data.viewmodels.MainViewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // ViewModel for the MainActivity
    private val viewModel: MainViewModels by viewModels()

    // Binding object for the activity layout
    private lateinit var binding: ActivityMainBinding

    // Adapter for the RecyclerView
    private var adapter: MyAdapter? = null

    // Progress dialog for showing loading state
    private var pDialog: Dialog? = null

    // onCreate function called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the activity layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the progress dialog
        pDialog = progressDialog()

        setAdapter()
        fetchDetails()
        initObserver()
    }

    // Function to set up the adapter for the RecyclerView
    private fun setAdapter() {
        // Initialize the adapter
        adapter = MyAdapter()
        // Set the adapter to the RecyclerView
        binding.rvMovies.adapter = adapter
    }

    // Function to fetch details from the ViewModel
    private fun fetchDetails() {
        // Show the progress dialog
        pDialog?.show()
        // Fetch the movie response from the ViewModel
        viewModel.fetchDetailsResponse()
    }

    // Function to initialize observers for LiveData
    private fun initObserver() {
        // Observe the response LiveData from the ViewModel
        viewModel.response.observe(this) { res ->
            when (res) {
                is NetworkResult.Success -> {
                    // If the response is successful, update the adapter with the data
                    res.data?.let { data ->
                        adapter?.setData(data.results)
                    }
                }
                is NetworkResult.Error -> {
                    // Handle the error case
                }
            }
            // Hide the progress dialog
            pDialog?.hide()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pDialog?.dismiss()
    }
}
