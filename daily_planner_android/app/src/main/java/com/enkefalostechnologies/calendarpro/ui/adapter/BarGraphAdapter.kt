package com.enkefalostechnologies.calendarpro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.ItembargraphBinding


class BarGraphAdapter() : RecyclerView.Adapter<BarGraphAdapter.ListViewHolder>() {

     var list:List<BarData> = listOf()
    val max=12
    var valueOnePer=0f
    var valueOneGrid=0f
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItembargraphBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }
   fun setItems(newList:List<BarData>){
       this.list=newList
//       notifyDataSetChanged()
   }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {


        with(holder){
            with(list[position]) {
                if(position==0){
                    binding.llView.setBackgroundResource(R.drawable.bordered_bg_left)
                }
                valueOnePer=(max.toFloat()/100f)
                val barData: BarData = list[position]
                val lowerPart=barData.value/valueOnePer
                val upperPart=100-lowerPart


                val excellentParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,9.toFloat()/valueOnePer)
                val transparentExcellentParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,100-(9.toFloat()/valueOnePer))
                binding.excellentView.layoutParams=excellentParam
                binding.transparentExcellentView.layoutParams=transparentExcellentParam


                val goodParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,5.toFloat()/valueOnePer)
                val transparentGoodParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,100-(5.toFloat()/valueOnePer))
                binding.goodView.layoutParams=goodParam
                binding.transparentGoodView.layoutParams=transparentGoodParam

                val poorParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,0.0009.toFloat()/valueOnePer)
                val transparentPoorParam=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,100-(0.0009.toFloat()/valueOnePer))
                binding.poorView.layoutParams=poorParam
                binding.transparentPoorView.layoutParams=transparentPoorParam

                val transparentParam= LinearLayout.LayoutParams(10,0,upperPart)
                val coloredParam= LinearLayout.LayoutParams(10,0,lowerPart)
                binding.transparentView.layoutParams=transparentParam
                binding.coloredView.layoutParams=coloredParam



                when(barData.value){
                     0.toFloat(),1.toFloat(),2.toFloat(),3.toFloat(),4.toFloat()->binding.coloredView.setBackgroundColor(binding.coloredView.context.getColor(R.color.color_FF8A04))
                     5.toFloat(),6.toFloat(),7.toFloat(),8.toFloat()->binding.coloredView.setBackgroundColor(binding.coloredView.context.getColor(R.color.color_BFE61A))
                     9.toFloat(),10.toFloat(),11.toFloat(),12.toFloat()->binding.coloredView.setBackgroundColor(binding.coloredView.context.getColor(R.color.color_65FF00))
                }
                val slideUp: Animation =
                    AnimationUtils.loadAnimation(holder.itemView.context, R.anim.bottom_to_top_slide)
                binding.coloredView.startAnimation(slideUp)
                slideUp.start()
                binding.labelTextView.text = barData.label
            }
        }
    }

    inner class ListViewHolder(val binding: ItembargraphBinding)
        :RecyclerView.ViewHolder(binding.root)

}
