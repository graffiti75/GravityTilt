package com.cericatto.gravitytilt.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(): ViewModel() {

	private val _state = MutableStateFlow(HomeScreenState())
	val state: StateFlow<HomeScreenState> = _state.asStateFlow()

	fun onAction(action: HomeScreenAction) {
		when (action) {
			is HomeScreenAction.OnToggleButton -> onToggleButton()
		}
	}

	init {
		 _state.update { state ->
			 state.copy(
				 loading = false
			 )
		 }
	}

	private fun onToggleButton() {
		_state.update { state ->
			state.copy(
				option = if (_state.value.option == OptionPlanet.EARTH) {
					OptionPlanet.MARS
				} else {
					OptionPlanet.EARTH
				}
			)
		}
	}
}