package com.cericatto.gravitytilt.ui.home

data class HomeScreenState(
	val loading : Boolean = true,
	val option: OptionPlanet = OptionPlanet.EARTH
)

enum class OptionPlanet {
	MARS, EARTH
}
