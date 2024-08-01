package dev.pankaj.cleanarchitecture.presentation.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.pankaj.cleanarchitecture.databinding.FragmentCartBinding

/**
 * Fragment representing the user's shopping cart.
 */
@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    /**
     * Inflates the layout for this fragment and sets up the ViewModel and data observation.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        // Observe the ViewModel's text LiveData and update the TextView accordingly
        cartViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root}

    /**
     * Cleans up the binding reference when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}