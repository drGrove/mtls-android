package com.dannygrove.mtls

import kotlinx.coroutines.flow.Flow

class GetServersUseCase(
  private val repository: MtlsRepository
) {
  operator fun invoke(): Flow<List<Server>> {
    return repository.getServers()
  }
}
