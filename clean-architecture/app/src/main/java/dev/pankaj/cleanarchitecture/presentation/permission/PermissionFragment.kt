package dev.pankaj.cleanarchitecture.presentation.permission

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.pankaj.cleanarchitecture.R
import dev.pankaj.cleanarchitecture.databinding.FragmentPermissionBinding
import dev.pankaj.cleanarchitecture.extensions.checkPermissionIsAllowed
import dev.pankaj.cleanarchitecture.extensions.disable
import dev.pankaj.cleanarchitecture.extensions.enable
import dev.pankaj.cleanarchitecture.extensions.permissionStorage

/**
 * Fragment for handling runtime permissions.
 */
@AndroidEntryPoint
class PermissionFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Initializes views and sets up listeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    /**
     * Checks permissions when the fragment resumes.
     */
    override fun onResume() {
        super.onResume()
        checkPermissionIfAlreadyAllowed()
    }/**
     * Cleans up the binding reference when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Initializes views in the fragment.
     */
    private fun initViews() {
        // Initialize views here if needed
    }

    /**
     * Sets up click listeners for interactive elements.
     */
    private fun initListeners() {
        binding.camera.setOnClickListener(this)
        binding.storage.setOnClickListener(this)
        binding.microphone.setOnClickListener(this)
        binding.notification.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
    }

    /**
     * Handles click events on various views.
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            binding.camera.id -> requestPermission(arrayOf(Manifest.permission.CAMERA))
            binding.storage.id -> requestPermission(permissionStorage())
            binding.microphone.id -> requestPermission(arrayOf(Manifest.permission.RECORD_AUDIO))
            binding.notification.id -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermission(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                }
            }
            binding.btnNext.id -> {
                if (v.context.checkPermissionIsAllowed()) {
                    v.disable()
                    v.findNavController()
                        .navigate(R.id.action_permissionFragment_to_loginFragment)
                } else {
                    Log.e("PermissionFragment", "Permissions not granted")
                }
            }
        }
    }

    /**
     * Initiates the permission request process.
     */
    private fun requestPermission(permissions: Array<String>) {
        permissionRequest.launch(permissions)
    }

    /**
     * Handles the result of the permission request.
     */
    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle camera permission
            val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
            if (cameraPermissionGranted) {
                binding.camera.disable()
            }

            // Handle storage permission
            var storagePermissionGranted = false
            permissionStorage().map {
                storagePermissionGranted = permissions[it] == true
            }
            if (storagePermissionGranted) {
                binding.storage.disable()
            }

            // Handle microphone permission
            val microphonePermissionGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
            if (microphonePermissionGranted) {
                binding.microphone.disable()
            }

            // Handle notification permission
            val notificationPermissionGranted = permissions[if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else {
                true
            }] ?: false
            if (notificationPermissionGranted) {
                binding.notification.disable()
            }
        }

    /**
     * Checks if permissions are already granted and updates UI accordingly.
     */
    private fun checkPermissionIfAlreadyAllowed(){
        val context = binding.root.context
        if (context.checkPermissionIsAllowed(Manifest.permission.CAMERA)) {
            binding.camera.disable()}else{
            binding.camera.enable()
        }

        // Handle storage permission
        if (context.checkPermissionIsAllowed(permissionStorage())) {
            binding.storage.disable()
        }else{
            binding.storage.enable()
        }

        // Handle microphone permission
        if (context.checkPermissionIsAllowed(Manifest.permission.RECORD_AUDIO)) {
            binding.microphone.disable()
        }else{
            binding.microphone.enable()
        }

        // Handle notification permission
        val notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            context.checkPermissionIsAllowed(Manifest.permission.POST_NOTIFICATIONS)
        }else{
            true
        }
        if (notificationPermissionGranted) {
            binding.notification.disable()
        }else{
            binding.notification.enable()
        }
    }
}
