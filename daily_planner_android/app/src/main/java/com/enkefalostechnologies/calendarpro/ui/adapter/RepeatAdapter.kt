package com.enkefalostechnologies.calendarpro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.ListGroup
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.ItemListBinding
import com.enkefalostechnologies.calendarpro.databinding.ItemRepeatBinding
import com.enkefalostechnologies.calendarpro.util.ListAdapterListener
import com.enkefalostechnologies.calendarpro.util.RepeatAdapterListener

class RepeatAdapter(var listener:RepeatAdapterListener)
    : RecyclerView.Adapter<RepeatAdapter.ListViewHolder>() {

    var list:MutableList<RepeatDays> = mutableListOf()
    var sList:MutableList<RepeatDays> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRepeatBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }
   fun setItems(newList:MutableList<RepeatDays>, selectedList:MutableList<RepeatDays>){
       this.list=newList
       this.sList=selectedList
       notifyDataSetChanged()
   }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(holder){
            with(list[position]) {
                if(sList.contains(list[position])){
                    binding.tvTitle.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.color_D3F26A)
                }else{
                    binding.tvTitle.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.white)
                }
                binding.tvTitle.setText(list[position].name)
                holder.itemView.setOnClickListener {
                       listener.onItemClicked(this)
                }
            }
        }
    }

    inner class ListViewHolder(val binding: ItemRepeatBinding)
        :RecyclerView.ViewHolder(binding.root)

}
