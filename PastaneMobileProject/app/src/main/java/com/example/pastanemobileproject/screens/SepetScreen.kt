package com.example.pastanemobileproject.screens

import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pastanemobileproject.DBHelper
import com.example.pastanemobileproject.components.BottomNavigationBar

data class SepetUrun(
    val sepetId: Int,
    val urunAdi: String,
    val adet: Int,
    val fiyat: Double
)

@Composable
fun SepetScreen(navController: NavController, kullaniciId: Int) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val db = dbHelper.readableDatabase

    var sepetUrunleri by remember { mutableStateOf(emptyList<SepetUrun>()) }

    LaunchedEffect(kullaniciId) {
        sepetUrunleri = getSepetUrunleri(db, kullaniciId)
    }

    val toplamFiyat = sepetUrunleri.sumOf { it.adet * it.fiyat }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = "sepet",
                kullaniciId = kullaniciId,
                totalPrice = toplamFiyat
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFDF6F0))
                .padding(16.dp)
        ) {
            Text("Sepetim", fontSize = 26.sp, color = Color(0xFF6D4C41))

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(sepetUrunleri) { urun ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = urun.urunAdi, fontSize = 18.sp)
                                Text(text = "₺${urun.fiyat} x ${urun.adet}", color = Color.Gray)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    updateAdet(db, urun.sepetId, urun.adet - 1)
                                    sepetUrunleri = getSepetUrunleri(db, kullaniciId)
                                }) {
                                    Icon(Icons.Filled.RemoveCircle, contentDescription = "Azalt")
                                }

                                Text(text = "${urun.adet}", fontSize = 16.sp)

                                IconButton(onClick = {
                                    updateAdet(db, urun.sepetId, urun.adet + 1)
                                    sepetUrunleri = getSepetUrunleri(db, kullaniciId)
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = "Artır")
                                }

                                IconButton(onClick = {
                                    deleteFromSepet(db, urun.sepetId)
                                    sepetUrunleri = getSepetUrunleri(db, kullaniciId)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Toplam: ₺$toplamFiyat", fontSize = 20.sp, color = Color(0xFFFF7043))

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (sepetUrunleri.isNotEmpty()) {
                        navController.navigate("adres/$kullaniciId")
                    } else {
                        Toast.makeText(context, "Sepetiniz boş. Lütfen ürün ekleyin.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = sepetUrunleri.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043))
            ) {
                Text("Alışverişi Tamamla", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

// 🔽 Veritabanı fonksiyonları
fun getSepetUrunleri(db: SQLiteDatabase, kullaniciId: Int): List<SepetUrun> {
    val list = mutableListOf<SepetUrun>()
    val cursor = db.rawQuery(
        """
        SELECT sepet.id, urunler.urun_adi, sepet.adet, urunler.fiyat
        FROM sepet
        INNER JOIN urunler ON sepet.urun_id = urunler.id
        WHERE sepet.kullanici_id = ?
        """.trimIndent(), arrayOf(kullaniciId.toString())
    )
    while (cursor.moveToNext()) {
        val id = cursor.getInt(0)
        val ad = cursor.getString(1)
        val adet = cursor.getInt(2)
        val fiyat = cursor.getDouble(3)
        list.add(SepetUrun(id, ad, adet, fiyat))
    }
    cursor.close()
    return list
}

fun updateAdet(db: SQLiteDatabase, sepetId: Int, yeniAdet: Int) {
    if (yeniAdet <= 0) {
        deleteFromSepet(db, sepetId)
        return
    }
    db.execSQL("UPDATE sepet SET adet = ? WHERE id = ?", arrayOf(yeniAdet, sepetId))
}

fun deleteFromSepet(db: SQLiteDatabase, sepetId: Int) {
    db.execSQL("DELETE FROM sepet WHERE id = ?", arrayOf(sepetId))
}
