package dev.pankaj.cleanarchitecture.domain.repository

import dev.pankaj.cleanarchitecture.data.local.entity.UserEntity

/**
 * Defines the contract for a repository responsible for managing user data.
 */
interface IUserRepository {

    /**
     * Savesuser information.
     *
     * @param user The UserEntity object to be saved.
     */
    suspend fun saveUser(user: UserEntity)
}