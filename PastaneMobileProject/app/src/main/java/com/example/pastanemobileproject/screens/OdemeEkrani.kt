@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pastanemobileproject.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pastanemobileproject.DBHelper
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Composable
fun OdemeEkrani(navController: NavController, kullaniciId: Int) {
    val context = LocalContext.current
    val dbHelper = DBHelper(context)

    var kartAdi by remember { mutableStateOf("") }
    var kartNumarasi by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var skt by remember { mutableStateOf("") }
    var kapidaOdeme by remember { mutableStateOf(false) }
    var toplamFiyat by remember { mutableStateOf(0.0) }
    var sepetFiyati by remember { mutableStateOf(0.0) }

    var kartNumarasiHata by remember { mutableStateOf("") }
    var cvvHata by remember { mutableStateOf("") }
    var sktHata by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        sepetFiyati = dbHelper.getSepetToplamFiyat(dbHelper.readableDatabase, kullaniciId)
        toplamFiyat = sepetFiyati
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFFDF6F0), Color(0xFFFFD6C0))))
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ödeme Bilgilerini Gir", fontSize = 22.sp, color = Color(0xFF6D4C41))

        Spacer(modifier = Modifier.height(12.dp))

        Text("Sepet Tutarı: ₺${"%.2f".format(sepetFiyati)}", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = kapidaOdeme,
                onCheckedChange = {
                    kapidaOdeme = it
                    toplamFiyat = if (kapidaOdeme) sepetFiyati + 10 else sepetFiyati
                },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF7043))
            )
            Text("Daha Öncelikli Sipariş (+₺10)", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Toplam Tutar: ₺${"%.2f".format(toplamFiyat)}", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = kartAdi,
            onValueChange = { kartAdi = it },
            label = { Text("Kart Adı (Ad Soyad)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = kartNumarasi,
            onValueChange = {
                if (it.matches(Regex("^[0-9]{0,16}$"))) {
                    kartNumarasi = it
                    kartNumarasiHata = if (kartNumarasi.length == 16) "" else "Kart numarası 16 haneli olmalı"
                }
            },
            label = { Text("Kart Numarası") },
            isError = kartNumarasiHata.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (kartNumarasiHata.isNotEmpty()) {
            Text(kartNumarasiHata, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = cvv,
            onValueChange = {
                if (it.matches(Regex("^[0-9]{0,3}$"))) {
                    cvv = it
                    cvvHata = if (cvv.length == 3) "" else "CVV 3 haneli olmalı"
                }
            },
            label = { Text("CVV") },
            isError = cvvHata.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (cvvHata.isNotEmpty()) {
            Text(cvvHata, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = skt,
            onValueChange = {
                if (it.matches(Regex("^[0-9]{0,2}/?[0-9]{0,2}$"))) {
                    if (it.length == 2 && !it.contains("/")) {
                        skt = "$it/"
                    } else {
                        skt = it
                    }

                    if (skt.length == 5) {
                        try {
                            val currentYear = LocalDate.now().year % 100
                            val currentMonth = LocalDate.now().monthValue
                            val parts = skt.split("/")
                            val month = parts[0].toInt()
                            val year = parts[1].toInt()

                            sktHata = if (month in 1..12 && (year > currentYear || (year == currentYear && month >= currentMonth))) {
                                ""
                            } else {
                                "Son kullanma tarihi geçmiş"
                            }
                        } catch (e: DateTimeParseException) {
                            sktHata = "Geçersiz tarih formatı"
                        }
                    } else {
                        sktHata = "Tarih MM/YY formatında olmalı"
                    }
                }
            },
            label = { Text("Son Kullanma Tarihi (MM/YY)") },
            isError = sktHata.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = VisualTransformation.None
        )
        if (sktHata.isNotEmpty()) {
            Text(sktHata, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (kartAdi.isEmpty() || kartNumarasi.isEmpty() || cvv.isEmpty() || skt.isEmpty()) {
                    Toast.makeText(context, "Lütfen tüm ödeme bilgilerini giriniz.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                try {
                    val odemeBasarili = dbHelper.addPayment(kullaniciId, kartAdi, kartNumarasi, cvv, skt)
                    if (odemeBasarili) {
                        Toast.makeText(context, "Sipariş başarıyla alındı.", Toast.LENGTH_LONG).show()
                        navController.navigate("home/$kullaniciId")
                    } else {
                        Toast.makeText(context, "Ödeme alınamadı.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Ödeme Yap", color = Color.White, fontSize = 16.sp)
        }
    }
}
