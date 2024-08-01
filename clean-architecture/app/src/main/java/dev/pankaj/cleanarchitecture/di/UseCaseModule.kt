package dev.pankaj.cleanarchitecture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pankaj.cleanarchitecture.domain.repository.IAuthRepository
import dev.pankaj.cleanarchitecture.domain.repository.IUserRepository
import dev.pankaj.cleanarchitecture.domain.usecase.AuthUseCase
import dev.pankaj.cleanarchitecture.domain.usecase.UserUseCase
import javax.inject.Singleton

/**
 * Hilt module responsible for providing use case dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)class UseCaseModule {

    /**
     * Provides an instance of UserUseCase.
     *
     * @param userRepository The IUserRepository dependency.
     * @return An instance of UserUseCase.
     */
    @Singleton
    @Provides
    fun provideUserUseCase(userRepository: IUserRepository): UserUseCase {
        return UserUseCase(userRepository)
    }

    /**
     * Provides an instance of AuthUseCase.
     *
     * @param authRepository The IAuthRepository dependency.
     * @return An instance of AuthUseCase.
     */
    @Singleton
    @Provides
    fun provideAuthUseCase(authRepository: IAuthRepository): AuthUseCase {
        return AuthUseCase(authRepository)
    }
}

