package com.dream.friend.util

import android.content.Context
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

object Extensions {

   fun View.visible(){this.visibility=View.VISIBLE}
   fun View.gone(){this.visibility=View.GONE}
   fun View.invisible(){this.visibility=View.INVISIBLE}

   fun View.setBackground( context: Context, @DrawableRes drawable:Int){ this.background=ContextCompat.getDrawable(context, drawable) }

}