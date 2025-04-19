package com.cericatto.gravitytilt.ui.home

data class HomeScreenState(
	val loading : Boolean = true,
	val option: OptionPlanet = OptionPlanet.MARS
)

enum class OptionPlanet {
	MARS, EARTH
}
