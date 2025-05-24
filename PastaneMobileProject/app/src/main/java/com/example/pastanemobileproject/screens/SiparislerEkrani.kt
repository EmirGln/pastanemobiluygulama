package com.example.pastanemobileproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pastanemobileproject.DBHelper
import com.example.pastanemobileproject.components.BottomNavigationBar
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SiparislerEkrani(navController: NavController, kullaniciId: Int) {
    val context = LocalContext.current
    val dbHelper = DBHelper(context)
    var siparisList by remember { mutableStateOf(emptyList<Map<String, Any>>()) }
    var progressMap by remember { mutableStateOf(mutableMapOf<Int, Float>()) }

    LaunchedEffect(true) {
        while (true) {
            val guncelListe = dbHelper.getSiparisler(kullaniciId)
            siparisList = guncelListe

            for (siparis in guncelListe) {
                val siparisId = siparis["id"] as Int
                val durum = siparis["durumu"] as String

                if (durum == "Hazırlanıyor") {
                    progressMap[siparisId] = (progressMap[siparisId] ?: 0f) + 0.2f
                    if ((progressMap[siparisId] ?: 0f) >= 1f) {
                        dbHelper.updateSiparisDurumu(siparisId, "Yolda")
                    }
                }

                if (durum == "Yolda") {
                    progressMap[siparisId] = (progressMap[siparisId] ?: 0f) + 0.33f
                    if ((progressMap[siparisId] ?: 0f) >= 1f) {
                        dbHelper.updateSiparisDurumu(siparisId, "Teslim Edildi")
                    }
                }
            }

            delay(1000) // Her 1 saniyede bir kontrol et
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = "siparisler",
                kullaniciId = kullaniciId,
                totalPrice = 0.0
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFDF6F0), Color(0xFFFFD6C0))
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Siparişlerim",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6D4C41)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(siparisList) { siparis ->
                    val siparisZamani = siparis["zamani"] as String
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(Date(siparisZamani.toLong()))

                    val durum = siparis["durumu"] as String
                    val siparisId = siparis["id"] as Int
                    val durumRenk = when (durum) {
                        "Hazırlanıyor" -> Color(0xFFFF7043)
                        "Yolda" -> Color(0xFF42A5F5)
                        "Teslim Edildi" -> Color(0xFF66BB6A)
                        else -> Color.Gray
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("siparisDetay/$siparisId")
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Sipariş ID: $siparisId",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6D4C41)
                            )
                            Text(
                                text = "Sipariş Zamanı: $formattedDate",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Durum: $durum",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = durumRenk
                            )
                            if (durum != "Teslim Edildi") {
                                LinearProgressIndicator(
                                    progress = progressMap[siparisId] ?: 0f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    color = durumRenk
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
