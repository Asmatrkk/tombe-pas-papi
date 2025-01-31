@file:OptIn(ExperimentalMaterial3Api::class)

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
    var showAlertDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") } // État pour la barre de recherche

    // Instance MQTT pour la réception d'alertes
    val mqttHelper = remember {
        MqttHelper(
            brokerUrl = "tcp://test.mosquitto.org:1883",
            topic = "zigbee/alertes"
        ) { message ->
            alertMessage = message
            showAlertDialog = true
        }
    }

    // Lancement de la connexion MQTT une seule fois
    LaunchedEffect(Unit) {
        mqttHelper.connect()
    }

    val backgroundColor = Color(0xFFF3F4F6) // Fond gris clair
    val primaryColor = Color(0xFFCE1DCC) // Couleur du SplashScreen
    val alertColor = Color(0xFFD8BFD8)
    val buttonColor = Color(0xFFCE1DCC)

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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                painter = painterResource(id = R.drawable.p4), // Remplace avec une vraie image
                contentDescription = "Profil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 📌 Barre de recherche
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Rechercher une alerte...") },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White, // Utilisation correcte du containerColor
                focusedIndicatorColor = primaryColor,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 📌 Carte Alerte CLIQUABLE
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = alertColor),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showAlertDialog = true }
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

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = alertMessage,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 📌 Liste des Alertes
        Column(modifier = Modifier.fillMaxWidth()) {
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
                Button(
                    onClick = { /* Logique d'appel 118 */ showAlertDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("📞 Appeler 118", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAlertDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Annuler", color = Color.White)
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
