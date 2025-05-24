package com.example.pastanemobileproject.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(navController: NavController, kullaniciId: Int) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }

    var toplamFiyat by remember { mutableStateOf(0.0) }
    var kullaniciAdi by remember { mutableStateOf("") }
    var urunler by remember { mutableStateOf(emptyList<Triple<String, String, Double>>()) }
    var selectedCategory by remember { mutableStateOf("Simit ve Poğaçalar") }

    val kategoriList = listOf("Simit ve Poğaçalar", "Tatlılar", "Kurabiyeler", "İçecekler")

    LaunchedEffect(Unit) {
        val db = dbHelper.readableDatabase
        toplamFiyat = dbHelper.getSepetToplamFiyat(db, kullaniciId)
        kullaniciAdi = dbHelper.getKullaniciAdi(kullaniciId)
        urunler = dbHelper.getProductsByCategoryWithPrice(selectedCategory)
        db.close()
    }

    LaunchedEffect(selectedCategory) {
        val db = dbHelper.readableDatabase
        urunler = dbHelper.getProductsByCategoryWithPrice(selectedCategory)
        db.close()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = "home",
                kullaniciId = kullaniciId,
                totalPrice = toplamFiyat
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
        ) {
            Text(
                text = "Hoşgeldiniz, $kullaniciAdi 👋",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D4C41),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            Text(
                text = "Kategoriler",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6D4C41),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyRow(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(kategoriList) { kategori ->
                    val isSelected = selectedCategory == kategori
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = if (isSelected) 8.dp else 2.dp,
                        color = if (isSelected) Color(0xFFFF7043) else Color.White,
                        modifier = Modifier
                            .clickable { selectedCategory = kategori }
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = kategori,
                                color = if (isSelected) Color.White else Color(0xFFFF7043),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(urunler) { urun ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val encodedUrunAdi = URLEncoder.encode(urun.first, StandardCharsets.UTF_8.toString())
                                val encodedAciklama = URLEncoder.encode(urun.second, StandardCharsets.UTF_8.toString())
                                val fiyat = urun.third.toString()
                                navController.navigate("details/$encodedUrunAdi/$encodedAciklama/$fiyat/$kullaniciId")
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(urun.first, fontWeight = FontWeight.Bold)
                                Text(urun.second, color = Color.Gray, fontSize = 14.sp)
                                Text("₺${urun.third}", color = Color(0xFFFF7043), fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = {
                                val db = dbHelper.writableDatabase
                                dbHelper.sepeteEkle(db, urun.first, kullaniciId)
                                toplamFiyat = dbHelper.getSepetToplamFiyat(db, kullaniciId)
                                db.close()
                                Toast.makeText(context, "${urun.first} sepete eklendi!", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Sepete ekle",
                                    tint = Color(0xFFFF7043)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
