package com.dream.friend.di

import android.content.Context
import com.dream.friend.common.PreferenceHandler
import com.dream.friend.data.network.ApiHelper
import com.dream.friend.data.repository.dataSource.UserLoginDataSource
import com.dream.friend.data.repository.dataSourceImpl.UserLoginDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideLocalRepository(
        @ApplicationContext
        context: Context
    ) = PreferenceHandler(context)

    @Provides
    @Singleton
    fun provideApiHelperWithToken(
        @ApplicationContext
        context: Context
    ) =
        ApiHelper(context).createAppService()

    @Provides
    @Singleton
    fun provideApiHelperWithoutToken(
        @ApplicationContext
        context: Context
    ) =
        ApiHelper(context).createService()

    @Provides
    fun provideUserLoginDataSource(
        @ApplicationContext
        context: Context
    ): UserLoginDataSource = UserLoginDataSourceImpl(context)

}