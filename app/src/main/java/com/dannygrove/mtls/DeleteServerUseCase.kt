package com.dannygrove.mtls

class DeleteServerUseCase(
  private val repository: MtlsRepository
) {
  suspend operator fun invoke(server: Server) {
    repository.deleteServer(server)
  }
}
