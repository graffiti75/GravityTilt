package com.cericatto.gravitytilt.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
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
import kotlin.math.sin
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

	val g = when (option) {
		OptionPlanet.EARTH -> 98.1f
		OptionPlanet.MARS -> 37.2f
	}

	val lambda = when (option) {
		OptionPlanet.EARTH -> -ln(0.9f)
		OptionPlanet.MARS -> -ln(0.99f)
	}

	val maxSpeed = when (option) {
		OptionPlanet.EARTH -> 500f
		OptionPlanet.MARS -> Float.MAX_VALUE
	}

	println("option: $option")
	println("g: $g")

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

	LaunchedEffect(Unit) {
		lastTime = System.nanoTime()
		while (true) {
			delay(16)
			val currentTime = System.nanoTime()
			val deltaTime = (currentTime - lastTime) / 1_000_000_000f
			lastTime = currentTime

			val k = 0.1f

			val rollDegrees = Math.toDegrees(roll.toDouble()).toFloat()
			val pitchDegrees = Math.toDegrees(pitch.toDouble()).toFloat()
			val effectiveRoll = k * rollDegrees
			val effectivePitch = k * pitchDegrees

			val aX = g * sin(Math.toRadians(effectiveRoll.toDouble())).toFloat()
			val aY = -g * sin(Math.toRadians(effectivePitch.toDouble())).toFloat()

			velX = velX * (1 - lambda * deltaTime) + aX * deltaTime
			velY = velY * (1 - lambda * deltaTime) + aY * deltaTime

			val speed = sqrt(velX * velX + velY * velY)
			if (speed > maxSpeed) {
				val scale = maxSpeed / speed
//				velX *= if (option == OptionPlanet.MARS) scale / 10f else scale
//				velY *= if (option == OptionPlanet.MARS) scale / 10f else scale
				velX *= scale
				velY *= scale
			}

			println("maxSpeed: $maxSpeed")
			println("lambda: $lambda")
//			println("velX: $velX, velY: $velY")
			posX += velX * deltaTime
			posY += velY * deltaTime
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