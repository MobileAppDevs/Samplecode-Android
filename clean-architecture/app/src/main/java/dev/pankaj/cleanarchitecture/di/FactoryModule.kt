package dev.pankaj.cleanarchitecture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pankaj.cleanarchitecture.App
import dev.pankaj.cleanarchitecture.domain.usecase.AuthUseCase
import dev.pankaj.cleanarchitecture.domain.usecase.UserUseCase
import dev.pankaj.cleanarchitecture.presentation.auth.viewmodel.AuthViewModelFactory
import javax.inject.Singleton

/**
 * Hilt module responsible for providing ViewModel factories.
 */
@Module
@InstallIn(SingletonComponent::class)
class FactoryModule {

    /**
     * Provides an instance of AuthViewModelFactory.
     *
     * @param app The application instance.
     * @param userUseCase The UserUseCase dependency.
     * @param authUseCase The AuthUseCase dependency.
     * @return An instance of AuthViewModelFactory.
     */
    @Singleton
    @Provides
    fun provideSignInViewModelFactory(
        app: App,
        userUseCase: UserUseCase,
        authUseCase: AuthUseCase,
    ): AuthViewModelFactory {
        return AuthViewModelFactory(app, userUseCase, authUseCase)
    }
}