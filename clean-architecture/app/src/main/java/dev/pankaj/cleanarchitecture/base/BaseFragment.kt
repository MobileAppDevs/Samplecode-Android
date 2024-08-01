package dev.pankaj.cleanarchitecture.base

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {

    fun showSnackBar(view: View, message: String) =
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}
