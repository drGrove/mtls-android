package com.dannygrove.mtls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditServerViewModel @Inject constructor(
  private val serverUseCases: ServerUseCases
) : ViewModel() {
  private val _eventFlow = MutableSharedFlow<UiEvent>()
  val eventFlow = _eventFlow.asSharedFlow()

  private val currentServerId: Int? = null

  fun onEvent(event: AddEditServerEvent) {
    when (event) {
      is AddEditServerEvent.SaveServer -> {
        viewModelScope.launch {
          try {
//            serverUseCases.addServer(
//              Server(
//                id = currentServerId,
//                name =
//              )
//            )
          } catch (e: InvalidServerException) {

          }
        }
      }
    }
  }

  sealed class UiEvent {
    object SaveServer : UiEvent()
  }
}
