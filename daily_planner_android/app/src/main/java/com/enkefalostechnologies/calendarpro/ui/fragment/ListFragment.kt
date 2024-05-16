package com.enkefalostechnologies.calendarpro.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.ListGroup
import com.amplifyframework.datastore.generated.model.SubscriptionStatus
import com.amplifyframework.datastore.generated.model.Tasks
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.StorageConstant
import com.enkefalostechnologies.calendarpro.databinding.CreateListDialogBinding
import com.enkefalostechnologies.calendarpro.databinding.FragmentListBinding
import com.enkefalostechnologies.calendarpro.model.User
import com.enkefalostechnologies.calendarpro.ui.HomeActivity
import com.enkefalostechnologies.calendarpro.ui.adapter.ListAdapter
import com.enkefalostechnologies.calendarpro.ui.base.BaseFragment
import com.enkefalostechnologies.calendarpro.ui.viewModel.fragment.HubFragmentViewModel
import com.enkefalostechnologies.calendarpro.ui.viewModel.fragment.ListFragmentViewModel
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil.handleExceptions
import com.enkefalostechnologies.calendarpro.util.AppUtil.loadBannerAd
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.DialogUtil.createNewListDialog
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.ListAdapterListener
import com.enkefalostechnologies.calendarpro.util.NewListCreateListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError

class ListFragment : BaseFragment<FragmentListBinding>(R.layout.fragment_list) {

    val viewModel: ListFragmentViewModel by viewModels { ListFragmentViewModel.Factory }
     var listGroupAdapter: ListAdapter?=null
    override fun setupViews() {
        displayAdsView()
        listGroupAdapter = ListAdapter(requireActivity(), object : ListAdapterListener {
            override fun onItemClicked(listId: String, name: String) {
                (requireActivity() as HomeActivity).openListTasksFragment(listId, name)
            }
        })
    }




    private fun displayAdsView(){
        if (isUserLoggedIn()) {
            if (getSubscriptionStatus()==SubscriptionStatus.ACTIVE) {
                binding.adView.gone()
            } else {
                binding.adView.visible()
                setAds()
            }
        } else {
            binding.adView.visible()
            setAds()

        }
    }
    override fun setupListeners() {
        binding.fab.ibFabAdd.setOnClickListener {
            requireActivity().createNewListDialog(CreateListDialogBinding.inflate(
                LayoutInflater.from(
                    requireActivity()
                )
            ).root,
                object : NewListCreateListener {
                    override fun onCreateClicked(dialog: Dialog, listName: String) {
                            viewModel.createList(getUserEmail(),deviceId(),listName)
                            dialog.dismiss()
                    }
                }).show()

        }

    }

    override fun fetchInitialData() {
        viewModel.fetchList(getUserEmail(),deviceId())
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setupObservers() {
        viewModel.listGroupDataList.observe(this, listGroupListObserver)
        viewModel.onError.observe(this,onErrorObserver)
    }
    val listGroupListObserver=Observer<List<ListGroup>>{ data->
        setListAdapter(data)
        dialog.close()
    }
    val onErrorObserver= Observer<DataStoreException> {
        dialog.close()
    }

    override fun removeObserver() {
        viewModel.listGroupDataList.removeObserver(listGroupListObserver)
        viewModel.onError.removeObserver(onErrorObserver)
    }

    //fun fetchLists() =if(isUserLoggedIn()) amplifyDataModelUtil.getListsByUserId(getUserId()) else amplifyDataModelUtil.getListsByDeviceId(deviceId())
    private fun setListAdapter(list: List<ListGroup>) {
        if (list.isEmpty()) {
            binding.tvNoTaskFound2.visible()
            binding.rvList.gone()
        } else {
            listGroupAdapter?.setItems(list)
            binding.rvList.adapter = listGroupAdapter
            binding.tvNoTaskFound2.gone()
            binding.rvList.visible()
        }
    }

    private fun setAds() {
        binding.adView.loadBannerAd()
        binding.adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.adView.loadBannerAd()
                },2000)
            }

            override fun onAdLoaded() {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.adView.loadBannerAd()
                },2000)
            }

            override fun onAdClosed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.adView.loadBannerAd()
                },2000)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        Log.i("Lifecycle", "ListFragment -> onPause()")
    }

    override fun onResume() {
        Log.i("Lifecycle", "ListFragment -> onResume()")
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        Log.i("Lifecycle", "ListFragment -> onStop()")
    }

    override fun onDestroy() {
        Log.i("Lifecycle", "ListFragment -> onDestroy()")
        super.onDestroy()
    }
}