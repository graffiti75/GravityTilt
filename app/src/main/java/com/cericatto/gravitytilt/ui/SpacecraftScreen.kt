package com.cericatto.gravitytilt.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.cericatto.gravitytilt.R
import com.cericatto.gravitytilt.ui.home.OptionPlanet
import kotlinx.coroutines.delay
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun SpacecraftScreen(
	option: OptionPlanet,
	modifier: Modifier = Modifier
) {
	val context = LocalContext.current
	val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
	val rotationSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) }

	var pitch by remember { mutableFloatStateOf(0f) }
	var roll by remember { mutableFloatStateOf(0f) }
	var posX by remember { mutableFloatStateOf(0f) }
	var posY by remember { mutableFloatStateOf(0f) }
	var velX by remember { mutableFloatStateOf(0f) }
	var velY by remember { mutableFloatStateOf(0f) }
	var lastTime by remember { mutableLongStateOf(0L) }

	val sensorListener = remember {
		object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent) {
				if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
					val rotationMatrix = FloatArray(9)
					SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
					val orientation = FloatArray(3)
					SensorManager.getOrientation(rotationMatrix, orientation)
					roll = orientation[2]
					pitch = orientation[1]
				}
			}

			override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
		}
	}

	DisposableEffect(Unit) {
		sensorManager.registerListener(
			sensorListener,
			rotationSensor,
			SensorManager.SENSOR_DELAY_GAME
		)
		onDispose {
			sensorManager.unregisterListener(sensorListener)
		}
	}

	LaunchedEffect(option) {
		lastTime = System.nanoTime()
		while (true) {
			delay(16)
			val currentTime = System.nanoTime()
			val deltaTime = (currentTime - lastTime) / 1_000_000_000f
			lastTime = currentTime

			val g = when (option) {
				OptionPlanet.EARTH -> 98.1f
				OptionPlanet.MARS -> 37.2f
			}

			val d = when (option) {
				OptionPlanet.EARTH -> -ln(0.9f)
				OptionPlanet.MARS -> -ln(0.99f)
			}

			val maxSpeed = when (option) {
				OptionPlanet.EARTH -> 500f
				OptionPlanet.MARS -> Float.MAX_VALUE
			}

			val k = 0.1f

			val tiltX = Math.toDegrees(roll.toDouble()).toFloat()
			val tiltY = Math.toDegrees(pitch.toDouble()).toFloat()

			// Acceleration: a(t) = g * k * tilt
			val aX = g * k * tiltX
			val aY = -g * k * tiltY

			// Friction: f(t) = d * v(t)
			val fX = d * velX
			val fY = d * velY

			// Velocity: v(t + delta) = v(t) + (a(t) - f(t)) * delta
			velX += (aX - fX) * deltaTime
			velY += (aY - fY) * deltaTime

			// Apply max speed
			val speed = sqrt(velX * velX + velY * velY)
			if (speed > maxSpeed) {
				val scale = maxSpeed / speed
				velX *= scale
				velY *= scale
			}

			// Update position
			posX += velX * deltaTime
			posY += velY * deltaTime

			println("Planet: $option, g: $g, lambda: $d, velX: $velX, velY: $velY")
		}
	}

	Box(
		modifier = modifier.fillMaxSize()
	) {
		Image(
			painter = painterResource(id = R.drawable.ufo),
			contentDescription = "Alien Spacecraft",
			modifier = Modifier
				.align(Alignment.Center)
				.size(100.dp)
				.offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
		)
	}
}

@Preview(showBackground = true)
@Composable
fun SpacecraftScreenPreview() {
	SpacecraftScreen(
		option = OptionPlanet.EARTH
	)
}