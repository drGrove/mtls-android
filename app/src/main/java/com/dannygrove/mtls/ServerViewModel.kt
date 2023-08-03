package com.dannygrove.mtls

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
  private val serverUseCases: ServerUseCases,
) : ViewModel() {

  private val _state = mutableStateOf(ServersState())
  val state: State<ServersState> = _state

  private var recentlyDeletedServer: Server? = null
  private var getServersJob: Job? = null

  init {
    getServers()
  }

  fun onEvent(event: ServersEvent) {
    when (event) {
      is ServersEvent.DeleteServer -> {
        viewModelScope.launch {
          serverUseCases.deleteServer(event.server)
          recentlyDeletedServer = event.server
        }
      }

      is ServersEvent.RestoreServer -> {
        viewModelScope.launch {
          serverUseCases.addServer(recentlyDeletedServer ?: return@launch)
          recentlyDeletedServer = null
        }
      }
    }
  }

  private fun getServers() {
    getServersJob?.cancel()
    getServersJob = serverUseCases.getServer()
      .onEach { servers ->
        _state.value = state.value.copy(
          servers = servers,
        )
      }
      .launchIn(viewModelScope)
  }
}
