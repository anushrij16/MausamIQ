package com.aetherx.mausamiq.data.repository

import com.aetherx.mausamiq.data.local.dao.SavedLocationDao
import com.aetherx.mausamiq.data.local.dao.UserDao
import com.aetherx.mausamiq.data.local.entity.SavedLocationEntity
import com.aetherx.mausamiq.data.local.entity.UserEntity
import com.aetherx.mausamiq.domain.model.LocationItem
import com.aetherx.mausamiq.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val locationDao: SavedLocationDao
) : IUserRepository {

    override fun getActiveUser(): Flow<UserEntity?> = userDao.getActiveUser()

    override suspend fun saveUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun clearUser() {
        userDao.clearUsers()
        locationDao.clearLocations()
    }

    override fun getSavedLocations(): Flow<List<SavedLocationEntity>> = locationDao.getAllLocations()

    override suspend fun addSavedLocation(location: LocationItem) {
        locationDao.insertLocation(
            SavedLocationEntity(
                name = location.name,
                type = location.type.name,
                latitude = location.latitude,
                longitude = location.longitude,
                isPrimary = location.isPrimary
            )
        )
    }

    override suspend fun deleteSavedLocation(location: SavedLocationEntity) {
        locationDao.deleteLocation(location)
    }

    override suspend fun seedDefaultLocationsIfEmpty() {
        val existing = locationDao.getAllLocations().firstOrNull()
        if (existing.isNullOrEmpty()) {
            locationDao.insertLocations(
                listOf(
                    SavedLocationEntity(name = "Home Residence", type = "HOME", latitude = 28.6139, longitude = 77.2090, isPrimary = true),
                    SavedLocationEntity(name = "Engineering Campus", type = "COLLEGE", latitude = 28.5450, longitude = 77.1926, isPrimary = false),
                    SavedLocationEntity(name = "Innovation Tech Park", type = "WORK", latitude = 28.4595, longitude = 77.0266, isPrimary = false)
                )
            )
        }
    }
}
