package com.example.appchute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appchute.mqtt.MqttHelper
import com.example.appchute.screens.AlertListScreen
import com.example.appchute.ui.theme.AppchuteTheme
import com.example.chute.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppchuteTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash_screen") {
                    composable("splash_screen") { SplashScreen(navController) }
                    composable("alert_screen") { MqttAlertScreen() }
                
                }
            }
        }
    }
}

@Composable
fun MqttAlertScreen() {
    var alertMessage by remember { mutableStateOf("En attente d'alertes...") }
    var showAlertDialog by remember { mutableStateOf(false) } // Variable pour afficher le pop-up

    // Instance MQTT pour la réception d'alertes
    val mqttHelper = remember {
        MqttHelper(
            brokerUrl = "tcp://test.mosquitto.org:1883",
            topic = "zigbee/alertes"
        ) { message ->
            alertMessage = message
            showAlertDialog = true // Ouvrir le pop-up quand une alerte arrive
        }
    }

    // Lancement de la connexion MQTT une seule fois
    LaunchedEffect(Unit) {
        mqttHelper.connect()
    }

    val backgroundColor = Color(0xFFF3F4F6) // Fond gris clair
    val primaryColor = Color(0xFFCE1DCC) // Couleur du SplashScreen
    val alertColor = Color(0xFFFFE082) // Jaune pour la carte d'alerte
    val buttonColor = Color(0xFFCE1DCC) // Couleur du bouton

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 📌 En-tête avec "Bienvenue Papi"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Action Menu */ }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.Black)
            }
            Column {
                Text(
                    text = "Bienvenue Papi 👋",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Aujourd'hui, 31 Janvier",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
            Image(
                painter = painterResource(id = R.drawable.p3), // Remplace avec une vraie image
                contentDescription = "Profil",
                modifier = Modifier
                    .size(190.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 📌 Section Calendrier
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryColor, RoundedCornerShape(16.dp)) // 📌 Fond de la couleur du SplashScreen
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("31", "01", "02", "03", "04", "05").forEach { day ->
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (day == "30") Color.Black else Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (day == "30") Color.White else Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 📌 Carte Alerte CLIQUABLE
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = alertColor),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showAlertDialog = true } // 📌 Rend la carte cliquable
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "⚠️ Alerte détectée",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = alertMessage,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 📌 Liste des Alertes
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Alerte Chute - 14h30", "Alerte Mouvement - 12h45").forEach { alert ->
                AlertCard(alert)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 📌 Bouton stylisé "Tout va bien"
        Button(
            onClick = {
                mqttHelper.publishMessage("zigbee/alertes", "Tout va bien")
            },
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text(
                "✅ Confirmer 'Tout va bien'",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }

    // 📌 POP-UP LORSQU'ON CLIQUE SUR L'ALERTE
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text("Alerte Urgente !") },
            text = { Text("Voulez-vous appeler le numéro d'urgence 118 ?") },
            confirmButton = {
                Button(onClick = { /* Logique d'appel 118 */ showAlertDialog = false }) {
                    Text("📞 Appeler 118")
                }
            },
            dismissButton = {
                Button(onClick = { showAlertDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

// 📌 Composable pour les alertes sous forme de carte
@Composable
fun AlertCard(alertText: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = alertText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.ArrowForward,
                contentDescription = "Voir plus",
                tint = Color.Black
            )
        }
    }
}
