package dev.pankaj.cleanarchitecture.data.repository.user

import dev.pankaj.cleanarchitecture.data.dataSource.user.IUserLocalDataSource
import dev.pankaj.cleanarchitecture.data.local.entity.UserEntity
import dev.pankaj.cleanarchitecture.domain.repository.IUserRepository

/**
 * Repository responsible for managing user data, including saving user information.
 */
class UserRepository(private val userLocalDataSource: IUserLocalDataSource) : IUserRepository {

    /**
     * Saves user information to the local data source.
     *
     * @param user The UserEntity object to be saved.
     */
    override suspend fun saveUser(user: UserEntity) {
        userLocalDataSource.saveUser(user)
    }
}