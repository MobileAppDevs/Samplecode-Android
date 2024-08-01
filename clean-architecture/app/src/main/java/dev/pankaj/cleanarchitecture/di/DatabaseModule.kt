package dev.pankaj.cleanarchitecture.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pankaj.cleanarchitecture.data.local.database.AppDatabase
import dev.pankaj.cleanarchitecture.data.local.dao.UserDao
import javax.inject.Singleton

/**
 * Hilt module responsible for providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)object DatabaseModule {

    /**
     * Provides an instance of AppDatabase.
     *
     * @param appContext The application context.
     * @return The AppDatabase instance.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context):AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    /**
     * Provides an instance of UserDao.
     *
     * @param database The AppDatabase instance.
     * @return The UserDao instance.
     */
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}
