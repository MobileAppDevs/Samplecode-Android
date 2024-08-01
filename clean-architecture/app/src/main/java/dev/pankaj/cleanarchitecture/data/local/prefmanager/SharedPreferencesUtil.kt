package dev.pankaj.cleanarchitecture.data.local.prefmanager

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * A utility class for interacting with SharedPreferences, providing methods for saving,
 * retrieving, and managing data.
 */
class SharedPreferencesUtil @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) {

    /**
     * Saves a string value associated with the given key.
     *
     * @param key The key to identify the stored value.* @param value The string value to be saved.
     */
    fun saveString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    /**
     * Retrieves a string value associated with the given key.
     *
     * @param key The key to identify the stored value.
     * @param defaultValue The value to return if the key is not found.
     * @return The stored string value or the default value if not found.
     */
    fun getString(key: String, defaultValue: String?= null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    /**
     * Saves an integer value associated with the given key.
     *
     * @param key The key to identify the stored value.
     * @param value The integer value to be saved.
     */
    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    /**
     * Retrieves an integer value associated with the given key.
     *
     * @param key The key to identify the stored value.
     * @param defaultValue The value to return if the key is not found.
     * @return The stored integer value or the default value if not found.
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    /**
     * Saves a boolean value associated with the given key.
     *
     * @param key The key to identify the stored value.
     * @param value The boolean value to be saved.
     */
    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    /**
     * Retrieves a boolean value associated with the given key.
     *
     * @param key The key to identify the stored value.
     * @param defaultValue The value to return if the key is not found.
     * @return The stored boolean value or the default value if not found.
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * Removes the value associated with the given key.
     *
     * @param key The key to identify the value to be removed.
     */
    fun remove(key: String) {
        editor.remove(key).apply()
    }

    /**
     * Clears all data from SharedPreferences.
     */
    fun clear() {
        editor.clear().apply()
    }

    /**
     * Checks if SharedPreferences contains a value associated with the given key.
     *
     * @param key The key to check.
     * @return True if the key exists, false otherwise.
     */
    fun containKey(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}