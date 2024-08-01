package dev.pankaj.cleanarchitecture.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Represents a user entity in the local database.
 */
@Entity(
    tableName = "User"
)
data class UserEntity(
    /**
     * The unique identifier for the user.
     */
    @PrimaryKey
    val id: String,

    /**
     * The name of the user.
     */
    val name: String
)