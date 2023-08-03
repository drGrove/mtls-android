package com.dannygrove.mtls

import kotlin.jvm.Throws

class AddServerUseCase(
  private val repository: MtlsRepository
) {
  @Throws(InvalidServerException::class)
  suspend operator fun invoke(server: Server) {
    if (server.name.isBlank()) {
      throw InvalidServerException("Name of the server cannot be blank")
    }
    if (server.url.isBlank()) {
      throw InvalidServerException("The url to the server cannot be blank")
    }
    repository.addServer(server)
  }
}
