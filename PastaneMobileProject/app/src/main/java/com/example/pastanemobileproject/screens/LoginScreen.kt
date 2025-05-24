package com.example.pastanemobileproject.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pastanemobileproject.DBHelper
import com.example.pastanemobileproject.R
import androidx.navigation.NavController

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (Int) -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }

    var email by remember { mutableStateOf("") }
    var sifre by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFFFFA726), Color(0xFFFF7043)))
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔙 Geri Butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("welcome") }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🧁 Logo ve Başlık
            Image(
                painter = painterResource(id = R.drawable.icon_pastane),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "PastaneApp'e Giriş",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Taze ürünlerle dolu bir güne başla!",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ✏️ Giriş Formu
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = customTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = sifre,
                onValueChange = { sifre = it },
                label = { Text("Şifre", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = customTextFieldColors()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val db = dbHelper.readableDatabase
                    val cursor = db.rawQuery(
                        "SELECT id FROM kullanicilar WHERE email = ? AND sifre = ?",
                        arrayOf(email, sifre)
                    )

                    if (cursor.moveToFirst()) {
                        val kullaniciId = cursor.getInt(0)
                        Toast.makeText(context, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(kullaniciId)
                    } else {
                        Toast.makeText(context, "Hatalı email veya şifre.", Toast.LENGTH_SHORT).show()
                    }

                    cursor.close()
                    db.close()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFFF7043)
                )
            ) {
                Text("Giriş Yap", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(32.dp)) // sadece 1 satırlık yer bırak

            // 📌 Alt bilgi
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🧁 Her sabah fırından taze çıkmış ürünler!",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "🔒 Bilgileriniz güvende!",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color.White,
    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.White,
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White,
    focusedLeadingIconColor = Color.White,
    unfocusedLeadingIconColor = Color.White
)
