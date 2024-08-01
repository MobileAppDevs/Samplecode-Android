package dev.pankaj.cleanarchitecture.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pankaj.cleanarchitecture.data.local.prefmanager.SharedPreferencesUtil
import javax.inject.Singleton

/**
 * Hilt module responsible for providing SharedPreferences related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)object SharedPreferencesModule {

    /**
     * Provides an instance of SharedPreferences.
     *
     * @param context The application context.
     * @return The SharedPreferences instance.
     */
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context:Context): SharedPreferences {
        return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    /**
     * Provides an instance of SharedPreferences.Editor.
     *
     * @param sharedPreferences The SharedPreferences instance.
     * @return The SharedPreferences.Editor instance.
     */
    @Provides
    @Singleton
    fun provideSharedPreferencesEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    /**
     * Provides an instance of SharedPreferencesUtil.
     ** @param sharedPreferences The SharedPreferences instance.
     * @param editor The SharedPreferences.Editor instance.
     * @return The SharedPreferencesUtil instance.
     */
    @Provides
    @Singleton
    fun provideSharedPreferencesUtil(
        sharedPreferences: SharedPreferences,
        editor: SharedPreferences.Editor
    ): SharedPreferencesUtil {
        return SharedPreferencesUtil(sharedPreferences, editor)
    }
}

