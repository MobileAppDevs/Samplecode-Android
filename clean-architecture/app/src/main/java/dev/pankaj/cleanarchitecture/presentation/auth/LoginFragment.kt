package dev.pankaj.cleanarchitecture.presentation.auth

import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.pankaj.cleanarchitecture.R
import dev.pankaj.cleanarchitecture.data.remote.model.LoginRequest
import dev.pankaj.cleanarchitecture.data.remote.model.LoginResponse
import dev.pankaj.cleanarchitecture.databinding.FragmentLoginBinding
import dev.pankaj.cleanarchitecture.extensions.hide
import dev.pankaj.cleanarchitecture.extensions.show
import dev.pankaj.cleanarchitecture.presentation.auth.viewmodel.AuthViewModel
import dev.pankaj.cleanarchitecture.presentation.auth.viewmodel.AuthViewModelFactory
import javax.inject.Inject
import dev.pankaj.cleanarchitecture.utils.*

/**
 * Fragment responsible for handling user login.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: AuthViewModelFactory
    private lateinit var viewModel: AuthViewModel

    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Initializes the ViewModel, sets up click listeners, and observes LiveData.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        addObserver()
        binding.login.setOnClickListener { validateAndLogin() }
    }

    /**
     * Observes the loginResponse LiveData and handles different Result states.
     */
    private fun addObserver() {
        viewModel.loginResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> setLoadingIndicator(result.isLoading)
                is Result.Success -> handleLoginSuccess(result.data)
                is Result.Error -> handleLoginError(result.exception.message)
                is Result.Message -> showMessage(result.msg)
            }
        }
    }

    /**
     * Validates user input and initiates the login process if valid.
     */
    private fun validateAndLogin() {
        val username = binding.username.editText?.text.toString().trim()
        val password = binding.password.editText?.text.toString().trim()

        var isValid = true

        if (username.isEmpty()) {
            binding.username.error = getString(R.string.error_username_required)
            isValid = false
        } else {
            binding.username.error = null
        }
        if (password.isEmpty()) {
            binding.password.error = getString(R.string.error_password_required)
            isValid = false
        } else {
            binding.password.error = null
        }

        if (isValid) {
            val loginRequest = LoginRequest(username, password)
            viewModel.login(loginRequest)
        }
    }

    /**
     * Shows or hides the loading indicator based on the isLoading state.
     */
    private fun setLoadingIndicator(isLoading: Boolean) {
        if (isLoading) binding.loading.show()
        else binding.loading.hide()
    }

    /**
     * Handles successful login and navigates to the home screen.
     */
    private fun handleLoginSuccess(data: LoginResponse) {
        findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
    }

    /**
     * Handles login errors and displays an error message.
     */
    private fun handleLoginError(message: String?) {
        Toast.makeText(requireContext(), message ?: "Login failed", Toast.LENGTH_SHORT).show()
    }

    /**
     * Displays a generic message to the user.
     */
    private fun showMessage(message: String?) {
        Toast.makeText(requireContext(), message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    /**
     * Cleans up the binding reference when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}