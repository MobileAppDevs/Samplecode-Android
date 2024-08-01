package dev.pankaj.cleanarchitecture.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * Provides utility functions for checking network connectivity.
 */
object NetworkUtils {

    /**
     * Checks if the device hasan active network connection.
     *
     * @param context The Context to access system services.
     * @return True if a network connection is available, false otherwise.
     */
    fun isNetworkAvailable(context: Context?): Boolean {
        context ?: return false // Handle null context
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager ?: return false // Handle null connectivity manager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        // Check for cellular, Wi-Fi, or Ethernet connectivity
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}