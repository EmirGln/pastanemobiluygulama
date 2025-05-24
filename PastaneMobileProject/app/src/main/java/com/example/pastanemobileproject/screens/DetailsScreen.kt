package com.example.pastanemobileproject.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pastanemobileproject.DBHelper

@Composable
fun DetailsScreen(
    urunAdi: String,
    aciklama: String,
    fiyat: Double,
    kullaniciId: Int
) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    var miktar by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF6F0))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = urunAdi, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = aciklama, fontSize = 18.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "₺$fiyat", fontSize = 24.sp, color = Color(0xFFFF7043), fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                if (miktar > 1) miktar--
            }) {
                Icon(Icons.Default.Remove, contentDescription = "Azalt")
            }

            Text(text = "$miktar", fontSize = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))

            IconButton(onClick = {
                miktar++
            }) {
                Icon(Icons.Default.Add, contentDescription = "Artır")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val db = dbHelper.writableDatabase
                repeat(miktar) {
                    dbHelper.sepeteEkle(db, urunAdi, kullaniciId)
                }
                db.close()
                Toast.makeText(context, "$miktar adet sepete eklendi", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sepete Ekle", color = Color.White, fontSize = 18.sp)
        }
    }
}
