package com.dream.friend.ui.bottomsheet.plan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.dream.friend.R
import com.dream.friend.data.model.PlanFeature
import com.dream.friend.data.model.PlanSchedule


class PlanPagerAdapter(var context: Context, var list: ArrayList<PlanFeature>):PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //val sliderLayout: View = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).inflate(R.layout.item_plan_slider, null)
      val view = LayoutInflater
            .from(context)
            .inflate(
                com.dream.friend.R.layout.item_plan_slider,
                container,
                false
            )
        list[position].image.let {
            Glide.with(context).load(it).into(view.findViewById<ImageView>(R.id.ivIc))
        }

       // view.findViewById<ImageView>(R.id.ivIc).setImageDrawable(context.getDrawable(.imageDrawable))
        view.findViewById<TextView>(com.dream.friend.R.id.tvTitle).text=list[position].perks
        view.findViewById<TextView>(com.dream.friend.R.id.tvDescription).text=list[position].description
        container.addView(view);
        return view;
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return  list.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }
}