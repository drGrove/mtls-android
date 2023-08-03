package com.dannygrove.mtls

import kotlinx.coroutines.flow.Flow

interface MtlsRepository {
  fun getServers(): Flow<List<Server>>

  suspend fun getServerById(id: Int): Server?

  suspend fun addServer(server: Server)

  suspend fun deleteServer(server: Server)

  suspend fun updateSever(server: Server)
}
