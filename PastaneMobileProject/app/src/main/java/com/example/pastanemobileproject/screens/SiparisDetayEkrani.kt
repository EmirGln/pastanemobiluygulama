package com.example.pastanemobileproject.screens

import androidx.compose.foundation.background
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

@Composable
fun SiparisDetayEkrani(navController: NavController, siparislerId: Int) {
    val context = LocalContext.current
    val dbHelper = DBHelper(context)

    var detayList by remember { mutableStateOf(emptyList<Map<String, Any>>()) }
    var toplamFiyat by remember { mutableStateOf(0.0) }
    var siparisDurumu by remember { mutableStateOf("") }
    var siparisZamani by remember { mutableStateOf("") }
    var adres by remember { mutableStateOf("") }
    var odemeSekli by remember { mutableStateOf("") }

    LaunchedEffect(siparislerId) {
        val (detaylar, toplam) = dbHelper.getSiparisDetaylari(siparislerId)
        detayList = detaylar
        toplamFiyat = toplam

        // Sipariş bilgilerini çek (tarih, durum)
        val siparis = dbHelper.getSiparisInfo(siparislerId)
        siparisDurumu = siparis["durum"] ?: "-"
        siparisZamani = siparis["zaman"] ?: "-"
        adres = siparis["adres"] ?: "-"
        odemeSekli = siparis["odeme"] ?: "-"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFDF6F0), Color(0xFFFFD6C0))))
            .padding(16.dp)
    ) {
        Text("Sipariş Detayları", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6D4C41))
        Spacer(modifier = Modifier.height(12.dp))

        // Ek Bilgiler
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sipariş Tarihi: $siparisZamani", fontSize = 14.sp)
                Text("Sipariş Durumu: $siparisDurumu", fontSize = 14.sp)
                Text("Adres: $adres", fontSize = 14.sp)
                Text("Ödeme Şekli: $odemeSekli", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ürünler
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(detayList) { detay ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ürün: ${detay["urunAdi"]}", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF6D4C41))
                        Text("Açıklama: ${detay["aciklama"]}", fontSize = 14.sp, color = Color.Gray)
                        Text("Fiyat: ₺${detay["fiyat"]}", fontSize = 14.sp)
                        Text("Adet: ${detay["adet"]}", fontSize = 14.sp)
                        Text("Toplam: ₺${detay["toplam"]}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF7043))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Genel Toplam
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Genel Toplam:", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                Text("₺${"%.2f".format(toplamFiyat)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF7043))
            }
        }
    }
}

