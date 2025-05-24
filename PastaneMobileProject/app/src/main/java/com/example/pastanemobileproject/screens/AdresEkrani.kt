@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pastanemobileproject.screens

import android.widget.Toast
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pastanemobileproject.DBHelper

@Composable
fun AdresEkrani(navController: NavController, kullaniciId: Int) {
    val context = LocalContext.current
    val dbHelper = DBHelper(context)

    var selectedIlce by remember { mutableStateOf("") }
    var mahalle by remember { mutableStateOf("") }
    var postaKodu by remember { mutableStateOf("") }
    var adres by remember { mutableStateOf("") }

    var mahalleHata by remember { mutableStateOf("") }
    var postaKoduHata by remember { mutableStateOf("") }
    var adresHata by remember { mutableStateOf("") }

    var secilenAdresZatenKayitli by remember { mutableStateOf(false) }

    val ankaraIlceleri = listOf(
        "Altındağ", "Akyurt", "Ayaş", "Bala", "Beypazarı", "Çamlıdere", "Çankaya",
        "Çubuk", "Elmadağ", "Etimesgut", "Evren", "Gölbaşı", "Güdül", "Haymana",
        "Kahramankazan", "Kalecik", "Keçiören", "Kızılcahamam", "Mamak", "Nallıhan",
        "Polatlı", "Pursaklar", "Sincan", "Şereflikoçhisar", "Yenimahalle"
    )
    var expanded by remember { mutableStateOf(false) }

    val mevcutAdresler = remember { mutableStateListOf<Map<String, String>>() }

    // 📥 Veritabanından adresleri yükle
    LaunchedEffect(Unit) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT ilce, mahalle, posta_kodu, adres FROM adresler WHERE kullanici_id = ? ORDER BY id DESC",
            arrayOf(kullaniciId.toString())
        )
        while (cursor.moveToNext()) {
            mevcutAdresler.add(
                mapOf(
                    "ilce" to cursor.getString(0),
                    "mahalle" to cursor.getString(1),
                    "posta_kodu" to cursor.getString(2),
                    "adres" to cursor.getString(3)
                )
            )
        }
        cursor.close()
        db.close()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFDF6F0), Color(0xFFFFD6C0))))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Adres Bilgilerini Gir",
                fontSize = 22.sp,
                color = Color(0xFF6D4C41)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedIlce,
                    onValueChange = {},
                    label = { Text("İlçe Seçin") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ankaraIlceleri.forEach { ilce ->
                        DropdownMenuItem(
                            text = { Text(ilce) },
                            onClick = {
                                selectedIlce = ilce
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = mahalle,
                onValueChange = {
                    mahalle = it
                    mahalleHata = if (mahalle.matches(Regex("^[a-zA-ZçğıöşüÇĞİÖŞÜ ]+$"))) "" else "Geçersiz mahalle adı"
                },
                label = { Text("Mahalle") },
                modifier = Modifier.fillMaxWidth(),
                isError = mahalleHata.isNotEmpty()
            )
            if (mahalleHata.isNotEmpty()) {
                Text(mahalleHata, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = postaKodu,
                onValueChange = {
                    if (it.matches(Regex("^[0-9]{0,5}$"))) {
                        postaKodu = it
                        postaKoduHata = if (postaKodu.length == 5) "" else "5 haneli olmalı"
                    }
                },
                label = { Text("Posta Kodu") },
                modifier = Modifier.fillMaxWidth(),
                isError = postaKoduHata.isNotEmpty(),
                visualTransformation = VisualTransformation.None
            )
            if (postaKoduHata.isNotEmpty()) {
                Text(postaKoduHata, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = adres,
                onValueChange = {
                    adres = it
                    adresHata = if (adres.length > 10) "" else "Adres en az 10 karakter olmalı"
                },
                label = { Text("Adres") },
                modifier = Modifier.fillMaxWidth(),
                isError = adresHata.isNotEmpty()
            )
            if (adresHata.isNotEmpty()) {
                Text(adresHata, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (selectedIlce.isNotBlank() && mahalleHata.isEmpty() && postaKoduHata.isEmpty() && adresHata.isEmpty()
                        && mahalle.isNotBlank() && postaKodu.isNotBlank() && adres.isNotBlank()
                    ) {
                        if (!secilenAdresZatenKayitli) {
                            val success = dbHelper.addAddress(kullaniciId, selectedIlce, mahalle, postaKodu, adres)
                            if (!success) {
                                Toast.makeText(context, "Adres eklenemedi", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                        }
                        navController.navigate("odeme/$kullaniciId")
                    } else {
                        Toast.makeText(context, "Tüm alanları doldurun", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Alışverişi Tamamla", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (mevcutAdresler.isNotEmpty()) {
                Text("📦 Kayıtlı Adresler", fontSize = 18.sp, color = Color(0xFF6D4C41))
            }
        }

        items(mevcutAdresler) { adr ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedIlce = adr["ilce"] ?: ""
                        mahalle = adr["mahalle"] ?: ""
                        postaKodu = adr["posta_kodu"] ?: ""
                        adres = adr["adres"] ?: ""
                        secilenAdresZatenKayitli = true
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("İlçe: ${adr["ilce"]}")
                    Text("Mahalle: ${adr["mahalle"]}")
                    Text("Posta Kodu: ${adr["posta_kodu"]}")
                    Text("Adres: ${adr["adres"]}")
                }
            }
        }
    }
}
