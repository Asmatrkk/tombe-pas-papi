package com.example.appchute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appchute.mqtt.MqttHelper
import com.example.appchute.ui.theme.AppchuteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppchuteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MqttAlertScreen()
                }
            }
        }
    }
}

@Composable
fun MqttAlertScreen() {
    var alertMessage by remember { mutableStateOf("En attente d'alertes...") }

    // Crée une seule instance de MQTT Helper
    val mqttHelper = remember {
        MqttHelper(
            brokerUrl = "tcp://test.mosquitto.org:1883",
            topic = "zigbee/alertes"
        ) { message ->
            alertMessage = message // Met à jour l'affichage avec l'alerte reçue
        }
    }

    // Lancement de la connexion MQTT une seule fois
    LaunchedEffect(Unit) {
        mqttHelper.connect()
    }

    // Interface utilisateur pour afficher les alertes
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Alerte MQTT reçue :", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))
        Text(alertMessage, style = MaterialTheme.typography.bodyLarge)
    }
}
