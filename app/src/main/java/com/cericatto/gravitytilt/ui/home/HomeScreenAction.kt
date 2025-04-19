package com.cericatto.gravitytilt.ui.home

sealed interface HomeScreenAction {
	data object OnToggleButton : HomeScreenAction
}