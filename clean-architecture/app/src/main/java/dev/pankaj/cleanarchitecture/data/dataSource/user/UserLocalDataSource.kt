package dev.pankaj.cleanarchitecture.data.dataSource.user

import dev.pankaj.cleanarchitecture.data.local.entity.UserEntity
import dev.pankaj.cleanarchitecture.data.local.dao.UserDao
import javax.inject.Inject
class UserLocalDataSource @Inject constructor(private val userDAO: UserDao) : IUserLocalDataSource {

    /**
     * Saves a userentity to the local database.
     *
     * @param user The UserEntity object to be saved.
     */
    override suspend fun saveUser(user: UserEntity) {
        userDAO.insert(user)
    }
}