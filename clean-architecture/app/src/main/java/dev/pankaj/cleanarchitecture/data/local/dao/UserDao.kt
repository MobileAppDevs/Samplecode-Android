package dev.pankaj.cleanarchitecture.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.pankaj.cleanarchitecture.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    /**
     * Inserts a user entity into the database. If a user with the same primary key alreadyexists,
     * it will be replaced with the new data.
     *
     * @param user The UserEntity object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    /*** Retrieves all users from the database as a Flow.
     *
     * @return A Flow emitting a list of all UserEntity objects in the database.
     */
    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<UserEntity>>
}