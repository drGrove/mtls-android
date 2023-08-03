package com.dannygrove.mtls

class GetServerUseCase(
  private val repository: MtlsRepository
) {
  suspend operator fun invoke(id: Int): Server? {
    return repository.getServerById(id)
  }
}
