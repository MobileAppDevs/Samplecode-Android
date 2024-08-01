package dev.pankaj.cleanarchitecture.presentation.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.pankaj.cleanarchitecture.R
import dev.pankaj.cleanarchitecture.databinding.FragmentStartBinding
import dev.pankaj.cleanarchitecture.extensions.checkPermissionIsAllowed


/**
 * Fragment displayed when the app starts.
 */
@AndroidEntryPoint
class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.openLogin.setOnClickListener {
            it?.findNavController()?.let {controller->
                if (it.context.checkPermissionIsAllowed()){
                    controller.navigate(R.id.action_startFragment_to_loginFragment)
                }else {
                    controller.navigate(R.id.action_startFragment_to_permissionFragment)
                }
            }
        }
        return root
    }

    /*** Cleans up resources.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}