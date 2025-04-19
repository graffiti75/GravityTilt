package com.cericatto.gravitytilt.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun TiltSensorScreen(
	modifier: Modifier = Modifier
) {
	val context = LocalContext.current
	// State to hold sensor values.
	var rotationValues by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }

	// Initialize SensorManager.
	val sensorManager = remember {
		context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
	}

	// Get Rotation Vector Sensor.
	val rotationSensor = remember {
		sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
	}

	// Create SensorEventListener.
	val sensorListener = remember {
		object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent) {
				if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
					rotationValues = event.values.clone()
				}
			}

			override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
				// Handle accuracy changes if needed
			}
		}
	}

	// Register/unregister sensor listener with Compose lifecycle.
	DisposableEffect(Unit) {
		sensorManager.registerListener(
			sensorListener,
			rotationSensor,
			SensorManager.SENSOR_DELAY_NORMAL
		)

		onDispose {
			sensorManager.unregisterListener(sensorListener)
		}
	}

	// Convert rotation vector to Euler angles (in degrees).
	val rotationMatrix = FloatArray(9)
	val orientationAngles = FloatArray(3)
	SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationValues)
	SensorManager.getOrientation(rotationMatrix, orientationAngles)

	// Extract tilt angles (pitch for X-axis, roll for Y-axis)
	val xTilt = (orientationAngles[1] * 180 / Math.PI).roundToInt() // Pitch
	val yTilt = (orientationAngles[2] * 180 / Math.PI).roundToInt() // Roll

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier
			.fillMaxSize()
			.background(Color.Blue.copy(alpha = 0.5f))
			.padding(16.dp)
	) {
		Text(
			text = "Device Tilt",
			fontSize = 40.sp,
			modifier = Modifier.padding(bottom = 16.dp)
		)
		Text(
			text = "X-Axis Tilt (Pitch): $xTilt°",
			fontSize = 18.sp,
			modifier = Modifier.padding(4.dp)
		)
		Text(
			text = "Y-Axis Tilt (Roll): $yTilt°",
			fontSize = 18.sp,
			modifier = Modifier.padding(4.dp)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun TiltSensorScreen() {
	TiltSensorScreen()
}