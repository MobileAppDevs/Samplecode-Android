package dev.pankaj.cleanarchitecture.domain.usecase

import dev.pankaj.cleanarchitecture.data.local.entity.UserEntity
import dev.pankaj.cleanarchitecture.domain.repository.IUserRepository

/**
 * Use case responsible for handling user-related operations, such as saving user information.
 */
class UserUseCase(private val iUserRepository: IUserRepository) {

    /**
     * Executes the save user use case, delegating the saving operation to the user repository.
     *
     * @param user The UserEntity object to be saved.
     */
    suspend fun saveUser(user: UserEntity) {iUserRepository.saveUser(user)
    }
}
