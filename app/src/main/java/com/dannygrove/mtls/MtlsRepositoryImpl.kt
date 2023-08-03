package com.dannygrove.mtls

import kotlinx.coroutines.flow.Flow

class MtlsRepositoryImpl(
  private val dao: ServerDao
) : MtlsRepository {
  override fun getServers(): Flow<List<Server>> {
    return dao.getServers()
  }

  override suspend fun getServerById(id: Int): Server? {
    return dao.getServerById(id)
  }

  override suspend fun addServer(server: Server) {
    dao.addServer(server)
  }

  override suspend fun deleteServer(server: Server) {
    dao.deleteServer(server)
  }

  override suspend fun updateSever(server: Server) {
    dao.updateServer(server)
  }
}
