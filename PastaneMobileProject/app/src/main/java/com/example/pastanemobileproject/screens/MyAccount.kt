@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pastanemobileproject.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pastanemobileproject.DBHelper
import com.example.pastanemobileproject.components.BottomNavigationBar

@Composable
fun MyAccountScreen(navController: NavController, kullaniciId: Int) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }

    var ad by remember { mutableStateOf(TextFieldValue("")) }
    var soyad by remember { mutableStateOf(TextFieldValue("")) }
    var telefon by remember { mutableStateOf(TextFieldValue("")) }

    var adresListesi by remember { mutableStateOf(emptyList<Map<String, String>>()) }
    var seciliAdresIndex by remember { mutableStateOf(-1) }

    var ilce by remember { mutableStateOf(TextFieldValue("")) }
    var mahalle by remember { mutableStateOf(TextFieldValue("")) }
    var postaKodu by remember { mutableStateOf(TextFieldValue("")) }
    var adres by remember { mutableStateOf(TextFieldValue("")) }

    val ankaraIlceleri = listOf(
        "Altındağ", "Akyurt", "Ayaş", "Bala", "Beypazarı", "Çamlıdere", "Çankaya",
        "Çubuk", "Elmadağ", "Etimesgut", "Evren", "Gölbaşı", "Güdül", "Haymana",
        "Kahramankazan", "Kalecik", "Keçiören", "Kızılcahamam", "Mamak", "Nallıhan",
        "Pol+atlı", "Pursaklar", "Sincan", "Şereflikoçhisar", "Yenimahalle"
    )
    var ilceExpanded by remember { mutableStateOf(false) }

    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val bilgiler = dbHelper.getKullaniciBilgileri(kullaniciId)
        ad = TextFieldValue(bilgiler["ad"] ?: "")
        soyad = TextFieldValue(bilgiler["soyad"] ?: "")
        telefon = TextFieldValue(bilgiler["telefon"] ?: "")
        adresListesi = dbHelper.getAdresListesi(kullaniciId)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = "kullanici",
                kullaniciId = kullaniciId,
                totalPrice = 0.0
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFFDF6F0), Color(0xFFFFD6C0))))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Hesap Bilgileri", fontSize = 24.sp, color = Color(0xFF6D4C41))

            listOf(
                Triple("Ad", ad, { value: TextFieldValue -> ad = value }),
                Triple("Soyad", soyad, { value: TextFieldValue -> soyad = value }),
                Triple("Telefon", telefon, { value: TextFieldValue -> telefon = value })
            ).forEach { (label, state, onValueChange) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = state,
                        onValueChange = onValueChange,
                        label = { Text(label) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = if (label == "Telefon") KeyboardOptions(keyboardType = KeyboardType.Phone) else KeyboardOptions.Default
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            dbHelper.updateKullaniciBilgileri(kullaniciId, ad.text, soyad.text, telefon.text)
                            Toast.makeText(context, "$label güncellendi", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043))
                    ) {
                        Text("Güncelle", color = Color.White)
                    }
                }
            }

            Divider(thickness = 2.dp, color = Color.Gray)
            Text("Adreslerim", fontSize = 20.sp, color = Color(0xFF6D4C41))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                adresListesi.forEachIndexed { index, _ ->
                    Button(
                        onClick = {
                            seciliAdresIndex = index
                            val secili = adresListesi[index]
                            ilce = TextFieldValue(secili["ilce"] ?: "")
                            mahalle = TextFieldValue(secili["mahalle"] ?: "")
                            postaKodu = TextFieldValue(secili["posta_kodu"] ?: "")
                            adres = TextFieldValue(secili["adres"] ?: "")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043))
                    ) {
                        Text("Adres ${index + 1}", color = Color.White)
                    }
                }
            }

            if (seciliAdresIndex != -1) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ExposedDropdownMenuBox(
                        expanded = ilceExpanded,
                        onExpandedChange = { ilceExpanded = !ilceExpanded }
                    ) {
                        OutlinedTextField(
                            value = ilce,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("İlçe") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ilceExpanded) },
                            modifier = Modifier.menuAnchor().weight(1f),
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = ilceExpanded,
                            onDismissRequest = { ilceExpanded = false }
                        ) {
                            ankaraIlceleri.forEach { secenek ->
                                DropdownMenuItem(
                                    text = { Text(secenek) },
                                    onClick = {
                                        ilce = TextFieldValue(secenek)
                                        ilceExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val id = adresListesi[seciliAdresIndex]["id"]!!.toInt()
                            dbHelper.updateAdresField(id, "ilce", ilce.text)
                            Toast.makeText(context, "İlçe güncellendi", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043))
                    ) {
                        Text("Güncelle", color = Color.White)
                    }
                }

                listOf(
                    listOf("Mahalle", mahalle, { value: TextFieldValue -> mahalle = value }, "mahalle"),
                    listOf("Posta Kodu", postaKodu, { value: TextFieldValue -> postaKodu = value }, "posta_kodu"),
                    listOf("Adres", adres, { value: TextFieldValue -> adres = value }, "adres")
                ).forEach { (label, state, onValueChange, field) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = state as TextFieldValue,
                            onValueChange = onValueChange as (TextFieldValue) -> Unit,
                            label = { Text(label as String) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val id = adresListesi[seciliAdresIndex]["id"]!!.toInt()
                                dbHelper.updateAdresField(id, field as String, state.text)
                                Toast.makeText(context, "$label güncellendi", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043))
                        ) {
                            Text("Güncelle", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🔻 ÇIKIŞ BUTONU
            Button(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Çıkış Yap", color = Color.White)
            }

            // 🔒 Onay Diyaloğu
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Çıkış Yap") },
                    text = { Text("Çıkış yapmak istediğinize emin misiniz?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showLogoutDialog = false
                            navController.navigate("welcome")
                        }) {
                            Text("Evet")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Hayır")
                        }
                    }
                )
            }
        }
    }
}
