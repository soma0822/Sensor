package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sensor.ui.theme.SensorTheme
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button



object  SensorType {
    var m = mutableMapOf(
        "accelerometer" to Sensor.TYPE_ACCELEROMETER,
        "gyroscope" to Sensor.TYPE_GYROSCOPE,
        "magnetic_field" to Sensor.TYPE_MAGNETIC_FIELD,
        "light" to Sensor.TYPE_LIGHT,
        "ambient_temperature" to Sensor.TYPE_AMBIENT_TEMPERATURE
    )
}

object ValueState {
    var mutableMap = mutableMapOf<Int, MutableState<Float>>()

    init {
        for (sensorType in SensorType.m) {
            mutableMap[sensorType.value] = mutableStateOf(0f)
        }
    }

    fun setValue(sensorType: Int, value: Float) {
        mutableMap[sensorType]?.value = value
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            SensorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting("Android")
                        Spacer(modifier = Modifier.height(16.dp))
                        TemperatureDisplay()
                        Row {
                            Button(onClick = {
                                // 温度を測定する処理
                                measureTemperature(sensorManager)
                            }) {
                                Text(text = "温度を測定")
                            }
                        }
                    }
                }
            }
        }
    }
}


class SensorListener(private val type: Int) : SensorEventListener {
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // センサーの精度が変更されたときの処理
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == type) {
            val value = event.values[0]
            ValueState.setValue(type, value)
        }
    }
}

fun measureTemperature(sensorManager: SensorManager) {
    // センサーを取得
    for(sensorType in SensorType.m) {
        val temperatureSensor = sensorManager.getDefaultSensor(sensorType.value)
        // センサーが存在するかを確認
        if (temperatureSensor != null) {
            // センサーリスナーを作成
            val sensorListener = SensorListener(sensorType.value)
            // リスナーを登録
            sensorManager.registerListener(
                sensorListener,
                temperatureSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } else {
            // センサーが見つからない場合はエラーをログに記録
            println("Ambient temperature sensor not found")
        }
    }
}

@Composable
fun TemperatureDisplay() {
    for (sensorType in SensorType.m) {
        val value = ValueState.mutableMap[sensorType.value]?.value
        Text(text = "${sensorType.key}: $value", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SensorTheme {
        Greeting("Android")
    }
}