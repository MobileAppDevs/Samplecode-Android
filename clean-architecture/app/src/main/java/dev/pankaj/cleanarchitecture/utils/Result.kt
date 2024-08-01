package dev.pankaj.cleanarchitecture.utils

/**
 * A sealed class representing the result of an operation, which can be either Success, Error, Message, or Loading.
 */sealed class Result<out T : Any> {

    /**
     * Represents a successful operation with a result of type [T].
     *
     * @param data The successful result data.
     */
    data class Success<out T : Any>(val data: T) : Result<T>()

    /**
     * Represents a failed operation with an exception.
     *
     * @param exception The exception that occurred during the operation.
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Represents a message to be displayed to the user.
     *
     * @param msg The message string.
     */
    data class Message(val msg: String) : Result<Nothing>()

    /**
     * Represents a loading state.
     *
     * @param isLoading True if the operation is currently loading,false otherwise.
     */
    data class Loading(val isLoading: Boolean) : Result<Nothing>()

    /**
     * Provides a string representation of the Result object.
     */
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Loading -> "Loading[isLoading=$isLoading]" // Corrected toString for Loading
            is Message -> "Message[msg=$msg]" // Corrected toString for Message
        }
    }
}