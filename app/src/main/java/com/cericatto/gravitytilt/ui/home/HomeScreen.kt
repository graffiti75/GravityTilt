package com.cericatto.gravitytilt.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cericatto.gravitytilt.R
import com.cericatto.gravitytilt.ui.SpacecraftScreen
import com.cericatto.gravitytilt.ui.theme.backgroundFirst
import com.cericatto.gravitytilt.ui.theme.backgroundLast

@Composable
fun HomeScreenRoot(
	modifier: Modifier = Modifier,
	viewModel: HomeScreenViewModel = hiltViewModel()
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	HomeScreen(
		modifier = modifier,
		onAction = viewModel::onAction,
		state = state
	)
}

@Composable
private fun HomeScreen(
	modifier: Modifier = Modifier,
	onAction: (HomeScreenAction) -> Unit,
	state: HomeScreenState
) {
	if (state.loading) {
		Box(
			modifier = Modifier
				.padding(vertical = 20.dp)
				.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			CircularProgressIndicator(
				color = MaterialTheme.colorScheme.primary,
				strokeWidth = 4.dp,
				modifier = Modifier.size(64.dp)
			)
		}
	} else {
		GravityTilt(
			modifier = modifier,
			onAction = onAction,
			state = state
		)
	}
}

@Composable
fun GravityTilt(
	onAction: (HomeScreenAction) -> Unit,
	state: HomeScreenState,
	modifier: Modifier = Modifier
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(
				brush = Brush.verticalGradient(
					colors = listOf(
						backgroundFirst,
						backgroundLast
					)
				)
			)
	) {
		GravityToggle(
			onAction = onAction,
			option = state.option,
			modifier = Modifier
				.padding(top = 100.dp)
				.width(130.dp)
				.align(Alignment.TopCenter)
		)
		Image(
			painter = painterResource(R.drawable.surface),
			contentDescription = "Mars",
			contentScale = ContentScale.Crop,
			modifier = Modifier
				.align(Alignment.BottomStart)
		)
		/*
		Image(
			painter = painterResource(R.drawable.ufo),
			contentDescription = "UFO",
			contentScale = ContentScale.Crop,
			modifier = Modifier
				.align(Alignment.Center)
		)
		 */
		SpacecraftScreen(
			option = state.option,
		)
	}
}

@Composable
fun GravityToggle(
	onAction: (HomeScreenAction) -> Unit,
	option: OptionPlanet,
	modifier: Modifier = Modifier
) {
	val duration = 1000
	val earthAlpha by animateFloatAsState(
		targetValue = if (option == OptionPlanet.EARTH) 1f else 0f,
		animationSpec = tween(durationMillis = duration)
	)
	val marsAlpha by animateFloatAsState(
		targetValue = if (option == OptionPlanet.MARS) 1f else 0f,
		animationSpec = tween(durationMillis = duration)
	)
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceEvenly,
		modifier = modifier
			.clickable {
				onAction(HomeScreenAction.OnToggleButton)
			}
			.background(
				color = Color.White,
				shape = RoundedCornerShape(30.dp)
			)
			.padding(5.dp)
	) {
		Image(
			painter = painterResource(R.drawable.earth),
			contentDescription = "Earth",
			modifier = Modifier
				.weight(1f)
//				.alpha(if (option == OptionPlanet.EARTH) 0f else 1f)
				.alpha(earthAlpha)
		)
		Image(
			painter = painterResource(R.drawable.mars),
			contentDescription = "Mars",
			modifier = Modifier
				.weight(1f)
//				.alpha(if (option == OptionPlanet.MARS) 0f else 1f)
				.alpha(marsAlpha)
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun GravityTiltPreview() {
	GravityTilt(
		onAction = {},
		state = HomeScreenState()
	)
}

@Preview(showBackground = true)
@Composable
private fun GravityTogglePreview() {
	Box(
		contentAlignment = Alignment.Center,
		modifier = Modifier
			.background(Color.Black.copy(alpha = 0.55f))
	) {
		GravityToggle(
			onAction = {},
			option = OptionPlanet.MARS,
			modifier = Modifier
				.padding(top = 60.dp)
				.width(130.dp)
				.align(Alignment.TopCenter)
		)
	}
}