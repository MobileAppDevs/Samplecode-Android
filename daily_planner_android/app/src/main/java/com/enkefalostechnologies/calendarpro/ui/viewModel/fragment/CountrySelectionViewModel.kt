package com.enkefalostechnologies.calendarpro.ui.viewModel.fragment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.enkefalostechnologies.calendarpro.api.model.ErrorResponse
import com.enkefalostechnologies.calendarpro.api.model.SuccessResponse
import com.enkefalostechnologies.calendarpro.api.network.Resource
import com.enkefalostechnologies.calendarpro.database.AmplifyAuth
import com.enkefalostechnologies.calendarpro.database.DatabaseHandler
import com.enkefalostechnologies.calendarpro.model.TaskModel2
import com.enkefalostechnologies.calendarpro.ui.HomeActivity
import com.enkefalostechnologies.calendarpro.ui.repository.Repository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CountrySelectionViewModel(
    private val repository: Repository
):ViewModel() {
    var apiResponse= MutableLiveData<Resource<SuccessResponse>>()
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CountrySelectionViewModel(repository = Repository(AmplifyAuth(), DatabaseHandler()))
            }
        }
    }
    
    
    fun getHolidays(context: Context,tag:String)=viewModelScope.launch(Dispatchers.IO) {
        try {
            apiResponse.postValue(Resource.Loading())
                repository.getHolidays(context,tag).enqueue(object :
                    Callback<SuccessResponse> {
                    override fun onResponse(
                        call: Call<SuccessResponse>,
                        response: Response<SuccessResponse>
                    ) {
                        response.body()?.let { apiResponse.postValue(Resource.Success(it)) }
                        try {
                            response.errorBody()?.let {
                                    apiResponse.postValue(Resource.Error(
                                        Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java).error.message
                                    ))
                            }
                        } catch (e: Exception) {
                            apiResponse.postValue(Resource.Error(e.message.toString()))
                        }
                    }
                    override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                        apiResponse.postValue(Resource.Error(t.message.toString()))
                    }
                })
        } catch (e: Exception) {
            apiResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun createNormalTask(
        taskModel: TaskModel2
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createTask(taskModel)
        }
    }

    fun deleteHolidayEvents(
        email:String,deviceId:String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteHolidayEvents(email,deviceId)
        }
    }
    

}