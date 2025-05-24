package com.example.pastanemobileproject.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pastanemobileproject.DBHelper
import com.example.pastanemobileproject.R

@Composable
fun RegisterScreen(
    navController: NavController,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }

    var ad by remember { mutableStateOf("") }
    var soyad by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefon by remember { mutableStateOf("") }
    var sifre by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFA726), Color(0xFFFF7043))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔙 Geri Butonu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { navController.navigate("welcome") }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.icon_pastane),
                contentDescription = "Pastane Icon",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "PastaneApp'e Hoş Geldiniz!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Lütfen bilgilerinizi doldurun ve kaydınızı oluşturun.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            RegisterTextField("Ad", ad, { ad = it }, Icons.Default.Person)
            RegisterTextField("Soyad", soyad, { soyad = it }, Icons.Default.Person)
            RegisterTextField("Email", email, { email = it }, Icons.Default.Email)
            RegisterPhoneField("Telefon", telefon, { telefon = it }, Icons.Default.Phone)
            RegisterTextField("Şifre", sifre, { sifre = it }, Icons.Default.Lock, isPassword = true)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")

                    if (!emailRegex.matches(email)) {
                        Toast.makeText(context, "Geçerli bir e-posta giriniz (örn: ornek@mail.com)", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (telefon.length != 11) {
                        Toast.makeText(context, "Telefon numarası tam 11 haneli olmalıdır!", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    val success = dbHelper.addUser(ad, soyad, email, telefon, sifre)
                    if (success) {
                        Toast.makeText(context, "Kayıt başarılı! Giriş yapabilirsiniz.", Toast.LENGTH_LONG).show()
                        onRegisterSuccess()
                    } else {
                        Toast.makeText(context, "Kayıt başarısız! Lütfen tekrar deneyin.", Toast.LENGTH_LONG).show()
                    }
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
                Text("Kayıt Ol", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "📦 Üyelikle sipariş takibi yapabilirsiniz.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RegisterTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = Color.White)
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = OutlinedTextFieldDefaults.colors(
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
    )
}

@Composable
fun RegisterPhoneField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= 11 && it.all { c -> c.isDigit() }) {
                onValueChange(it)
            }
        },
        label = { Text(label, color = Color.White) },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = Color.White)
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = OutlinedTextFieldDefaults.colors(
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
    )
}
