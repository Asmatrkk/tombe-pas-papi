package com.example.chute

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appchute.R
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SplashScreen(navController: NavController) {
    // Déclenche la navigation après 3 secondes
    LaunchedEffect(Unit) {
        delay(1000)
        navController.navigate("alert_screen") {
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    // Dégradé de fond
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFCE1DCC), Color(0xFFFF8425))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.p), 
                contentDescription = "Logo",
                modifier = Modifier.size(420.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chargement....",
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}
