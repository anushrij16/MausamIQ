package com.aetherx.mausamiq.domain.repository

import com.aetherx.mausamiq.data.local.entity.SavedLocationEntity
import com.aetherx.mausamiq.data.local.entity.UserEntity
import com.aetherx.mausamiq.domain.model.LocationItem
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getActiveUser(): Flow<UserEntity?>
    suspend fun saveUser(user: UserEntity)
    suspend fun clearUser()

    fun getSavedLocations(): Flow<List<SavedLocationEntity>>
    suspend fun addSavedLocation(location: LocationItem)
    suspend fun deleteSavedLocation(location: SavedLocationEntity)
    suspend fun seedDefaultLocationsIfEmpty()
}
