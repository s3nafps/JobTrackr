package ms.dev.jobtrackerpro.domain.repository

import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.domain.model.Communication

interface CommunicationsRepository {
    
    fun getCommunications(applicationId: Long): Flow<List<Communication>>
    
    fun getAllCommunications(): Flow<List<Communication>>
    
    fun getCommunicationById(id: Long): Flow<Communication?>
    
    suspend fun insertCommunication(communication: Communication): Long
    
    suspend fun updateCommunication(communication: Communication)
    
    suspend fun deleteCommunication(id: Long)
    
    suspend fun deleteByApplicationId(applicationId: Long)
    
    suspend fun deleteAll()
}
