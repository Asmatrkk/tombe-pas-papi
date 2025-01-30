package com.example.appchute.mqtt

import android.util.Log
import org.eclipse.paho.client.mqttv3.*

class MqttHelper(
    private val brokerUrl: String = "tcp://test.mosquitto.org:1883", // URL du broker MQTT
    private val topic: String = "zigbee/alertes",
    private val onMessageReceived: (String) -> Unit
) {
    private var mqttClient: MqttClient? = null

    fun connect() {
        if (mqttClient != null && mqttClient!!.isConnected) {
            Log.d("MQTT", "⚠️ Déjà connecté à MQTT")
            return
        }

        try {
            Log.d("MQTT", "🔌 Tentative de connexion à $brokerUrl...")

            mqttClient = MqttClient(brokerUrl, MqttClient.generateClientId(), null)
            val options = MqttConnectOptions().apply {
                isCleanSession = true
            }

            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.e("MQTT", "❌ Connexion perdue : ${cause?.message}")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = message.toString()
                    Log.d("MQTT", "📩 Message reçu sur [$topic]: $msg")
                    onMessageReceived(msg) // Envoie le message à l'UI
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("MQTT", "✅ Message publié avec succès")
                }
            })

            mqttClient?.connect(options)
            Log.d("MQTT", "✅ Connecté à $brokerUrl")

            // S'abonner au topic pour recevoir les alertes
            mqttClient?.subscribe(topic) { _, message ->
                val msg = message.toString()
                Log.d("MQTT", "📩 Alerte reçue : $msg")
                onMessageReceived(msg) // Envoie le message à l'UI
            }

            Log.d("MQTT", "📡 Abonné au topic : $topic")

        } catch (e: MqttException) {
            Log.e("MQTT", "❌ Erreur de connexion : ${e.message}")
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            Log.d("MQTT", "🔌 Tentative de déconnexion...")
            mqttClient?.disconnect()
            mqttClient = null // Réinitialiser
            Log.d("MQTT", "✅ Déconnecté de MQTT")
        } catch (e: MqttException) {
            Log.e("MQTT", "❌ Erreur de déconnexion : ${e.message}")
        }
    }
}
