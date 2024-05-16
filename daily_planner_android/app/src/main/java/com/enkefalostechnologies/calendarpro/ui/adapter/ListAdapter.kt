package com.enkefalostechnologies.calendarpro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.ListGroup
import com.amplifyframework.datastore.generated.model.Tasks
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.ItemListBinding
import com.enkefalostechnologies.calendarpro.util.ListAdapterListener

class ListAdapter(var context: FragmentActivity, var listener:ListAdapterListener)
    : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

     var list:List<ListGroup> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }
   fun setItems(newList:List<ListGroup>){
       this.list=newList
       notifyDataSetChanged()
   }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(holder){
            with(list[position]) {
                binding.listName.text = name
                Amplify.DataStore.query(Tasks::class.java,
                    Where.matches(Tasks.LIST_GROUP_ID.eq(id)),{data->
                        context.runOnUiThread {
                            val list= mutableListOf<Tasks>()
                            data.forEach {list.add(it) }
                            binding.tvTaskCount.text =
                                "${list.filter { it.isCompleted == true }.size}/${list.size}"
                        }
                    },{
                        context.runOnUiThread {
                            binding.tvTaskCount.text =
                                "0/0"
                        }

                    })
//                val slideUp: Animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fall_down)
//                holder.itemView.startAnimation(slideUp)
                holder.itemView.setOnClickListener {
                       listener.onItemClicked(id,name)
                }
            }
        }
    }

    inner class ListViewHolder(val binding: ItemListBinding)
        :RecyclerView.ViewHolder(binding.root)

}
