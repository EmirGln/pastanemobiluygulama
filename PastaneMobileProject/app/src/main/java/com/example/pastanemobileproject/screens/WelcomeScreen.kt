@file:OptIn(ExperimentalAnimationApi::class)

package com.example.pastanemobileproject.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.pastanemobileproject.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val sloganlar = listOf(
        "Taptaze kurabiyeler seni bekliyor!",
        "Kahveni simitle tamamla ☕🥐",
        "Günün tatlısı: Mozaik Pasta 🍫",
        "Pastaneye gitme, pastane sana gelsin!",
        "Fırından yeni çıktı! 🔥"
    )
    var index by remember { mutableStateOf(0) }

    // 2.5 saniyede bir slogan değiştir
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            index = (index + 1) % sloganlar.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFA726),
                        Color(0xFFFF7043)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            // Üst kısım - Logo ve Karşılama
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.icon_pastane),
                    contentDescription = "Pastane Icon",
                    modifier = Modifier.size(140.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PastanApp’e Hoş Geldiniz!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ✅ Animasyonlu slogan alanı
                AnimatedContent(
                    targetState = sloganlar[index],
                    transitionSpec = { fadeIn() with fadeOut() }
                ) { slogan ->
                    Text(
                        text = slogan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth()
                    )
                }
            }

            // Giriş / Kayıt butonları
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onLoginClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFFF7043)
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(130.dp)
                ) {
                    Text(text = "Giriş Yap", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onRegisterClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFFF7043)
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(130.dp)
                ) {
                    Text(text = "Kayıt Ol", fontWeight = FontWeight.SemiBold)
                }
            }

            // Alt bilgilendirme alanı
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Güvenli ve hızlı sipariş deneyimi",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🥐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "En taze ürünler kapınıza gelsin!",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
