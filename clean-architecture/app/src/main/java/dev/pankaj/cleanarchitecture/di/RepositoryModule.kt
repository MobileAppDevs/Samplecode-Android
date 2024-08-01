package dev.pankaj.cleanarchitecture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pankaj.cleanarchitecture.data.dataSource.auth.IAuthDataSource
import dev.pankaj.cleanarchitecture.data.dataSource.user.IUserLocalDataSource
import dev.pankaj.cleanarchitecture.data.local.prefmanager.SharedPreferencesUtil
import dev.pankaj.cleanarchitecture.data.repository.auth.AuthRepository
import dev.pankaj.cleanarchitecture.data.repository.user.UserRepository
import dev.pankaj.cleanarchitecture.domain.repository.IAuthRepository
import dev.pankaj.cleanarchitecture.domain.repository.IUserRepository

/**
 * Hilt module responsible for providing repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    /**
     * Provides an instance of IUserRepository.
     *
     * @param userLocalDataSource The IUserLocalDataSource dependency.
     * @return An instance of IUserRepository.
     */
    @Provides
    fun provideUserRepository(userLocalDataSource: IUserLocalDataSource): IUserRepository {
        return UserRepository(userLocalDataSource)
    }

    /**
     * Provides an instance of IAuthRepository.
     *
     * @param authDataSource The IAuthDataSource dependency.
     * @param sharedPreferencesUtil The SharedPreferencesUtil dependency.
     * @return An instance of IAuthRepository.
     */
    @Provides
    fun provideAuthRepository(
        authDataSource: IAuthDataSource,
        sharedPreferencesUtil: SharedPreferencesUtil
    ): IAuthRepository {
        return AuthRepository(authDataSource, sharedPreferencesUtil)
    }}