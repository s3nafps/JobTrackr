package ms.dev.jobtrackerpro.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ms.dev.jobtrackerpro.data.local.dao.CommunicationDao
import ms.dev.jobtrackerpro.data.mapper.toCommunicationDomainList
import ms.dev.jobtrackerpro.data.mapper.toDomain
import ms.dev.jobtrackerpro.data.mapper.toEntity
import ms.dev.jobtrackerpro.domain.model.Communication
import ms.dev.jobtrackerpro.domain.repository.CommunicationsRepository
import javax.inject.Inject

class CommunicationsRepositoryImpl @Inject constructor(
    private val communicationDao: CommunicationDao
) : CommunicationsRepository {
    
    override fun getCommunications(applicationId: Long): Flow<List<Communication>> {
        return communicationDao.getCommunications(applicationId).map { it.toCommunicationDomainList() }
    }
    
    override fun getAllCommunications(): Flow<List<Communication>> {
        return communicationDao.getAllCommunications().map { it.toCommunicationDomainList() }
    }
    
    override fun getCommunicationById(id: Long): Flow<Communication?> {
        return communicationDao.getCommunicationById(id).map { it?.toDomain() }
    }
    
    override suspend fun insertCommunication(communication: Communication): Long {
        return communicationDao.insert(communication.toEntity())
    }
    
    override suspend fun updateCommunication(communication: Communication) {
        communicationDao.update(communication.toEntity())
    }
    
    override suspend fun deleteCommunication(id: Long) {
        communicationDao.deleteById(id)
    }
    
    override suspend fun deleteByApplicationId(applicationId: Long) {
        communicationDao.deleteByApplicationId(applicationId)
    }
    
    override suspend fun deleteAll() {
        communicationDao.deleteAll()
    }
}
