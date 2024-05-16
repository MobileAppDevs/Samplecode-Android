package com.enkefalostechnologies.calendarpro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.ListGroup
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.ItemCountryBinding
import com.enkefalostechnologies.calendarpro.databinding.ItemListBinding
import com.enkefalostechnologies.calendarpro.model.CountryModel
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.CountryAdapterListener
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.setImageFromUrl
import com.enkefalostechnologies.calendarpro.util.Extension.setSvgImageUrl
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.ListAdapterListener

class CountryAdapter(var listener: CountryAdapterListener) :
    RecyclerView.Adapter<CountryAdapter.ListViewHolder>() {

    var list: List<CountryModel> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemCountryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    fun setItems(newList: List<CountryModel>) {
        this.list = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.tvName.text = name
                binding.tvFlag.text = flag
                if (isChecked == true) {
                    binding.parentView.background = AppCompatResources.getDrawable(
                        binding.root.context,
                        R.drawable.task_bg_country_selected
                    )
                    binding.ivCheck.visible()
                } else {
                    binding.parentView.background = AppCompatResources.getDrawable(
                        binding.root.context,
                        R.drawable.task_bg
                    )
                    binding.ivCheck.gone()
                }

                binding.parentView.setOnClickListener {
                    if (list.filter { it.isChecked == true }.size<4) {
                            listener.onItemClicked(this)
                    }else if(isChecked==true){
                        listener.onItemClicked(this)
                    }else{
                        binding.parentView.context.showToast("Only 4 countries are allowed.")
                    }
                }

            }
        }
    }

    inner class ListViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root)

}
