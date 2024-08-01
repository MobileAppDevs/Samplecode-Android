package dev.pankaj.cleanarchitecture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pankaj.cleanarchitecture.data.dataSource.auth.AuthDataSource
import dev.pankaj.cleanarchitecture.data.dataSource.auth.IAuthDataSource
import dev.pankaj.cleanarchitecture.data.dataSource.user.IUserLocalDataSource
import dev.pankaj.cleanarchitecture.data.dataSource.user.UserLocalDataSource
import dev.pankaj.cleanarchitecture.data.local.dao.UserDao
import dev.pankaj.cleanarchitecture.data.remote.api.ApiService

/**
 * Hilt module responsible for providing data source dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)object DataSourceModule {

    /**
     * Provides an instance of IUserLocalDataSource.
     *
     * @param userDAO The UserDao dependency, providing access to the user data in the database.
     * @return An instance of IUserLocalDataSource, responsible for managing local user data.
     */@Provides
    fun provideUserLocalDataSource(userDAO: UserDao): IUserLocalDataSource {
        return UserLocalDataSource(userDAO)
    }

    /**
     * Provides an instance of IAuthDataSource.
     *
     * @param apiService The ApiService dependency, used for making network requests related to authentication.
     * @return An instance of IAuthDataSource, responsible for handling authentication data from the network.
     */
    @Provides
    fun provideAuthDataSource(apiService: ApiService): IAuthDataSource {
        return AuthDataSource(apiService)
    }}