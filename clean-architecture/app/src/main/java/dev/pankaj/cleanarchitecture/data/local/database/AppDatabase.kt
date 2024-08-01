package dev.pankaj.cleanarchitecture.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.pankaj.cleanarchitecture.data.local.dao.UserDao
import dev.pankaj.cleanarchitecture.data.local.entity.UserEntity


/**
 * The Room database for the application. Defines the entities to be included in the database
 * and provides access to the corresponding DAOs.
 */
@Database(
    entities = [UserEntity::class],
    version = 1,
    /*
     * Uncomment the below block to enable automatic migrations.
     * autoMigrations = [
     *    AutoMigration(from = 1, to = 2)* ]
     */
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to the UserDao for interacting with user data in the database.
     *
     * @return An instance of the UserDao.
     */
    abstract fun userDao(): UserDao
}