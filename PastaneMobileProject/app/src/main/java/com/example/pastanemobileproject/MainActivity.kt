// MainActivity.kt
package com.example.pastanemobileproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.pastanemobileproject.screens.*
import com.example.pastanemobileproject.ui.theme.PastaneMobileProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PastaneMobileProjectTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("welcome") {
            WelcomeScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { kullaniciId ->
                    navController.navigate("home/$kullaniciId") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = { navController.navigate("login") }
            )
        }

        composable("home/{kullaniciId}", arguments = listOf(navArgument("kullaniciId") { type = NavType.IntType })) {
            val kullaniciId = it.arguments?.getInt("kullaniciId") ?: -1
            HomeScreen(navController, kullaniciId)
        }

        composable("sepet/{kullaniciId}", arguments = listOf(navArgument("kullaniciId") { type = NavType.IntType })) {
            val id = it.arguments?.getInt("kullaniciId") ?: -1
            SepetScreen(navController, id)
        }

        composable("adres/{kullaniciId}", arguments = listOf(navArgument("kullaniciId") { type = NavType.IntType })) {
            val id = it.arguments?.getInt("kullaniciId") ?: -1
            AdresEkrani(navController, id)
        }

        composable("odeme/{kullaniciId}", arguments = listOf(navArgument("kullaniciId") { type = NavType.IntType })) {
            val id = it.arguments?.getInt("kullaniciId") ?: -1
            OdemeEkrani(navController, id)
        }

        composable("siparisler/{kullaniciId}", arguments = listOf(navArgument("kullaniciId") { type = NavType.IntType })) {
            val id = it.arguments?.getInt("kullaniciId") ?: -1
            SiparislerEkrani(navController, id)
        }

        composable("siparisDetay/{siparislerId}", arguments = listOf(navArgument("siparislerId") { type = NavType.IntType })) {
            val id = it.arguments?.getInt("siparislerId") ?: -1
            SiparisDetayEkrani(navController, id)
        }

        // ✅ Hesabım Sayfası (Yeni route)
        composable("kullanici/{kullaniciId}", arguments = listOf(navArgument("kullaniciId") { type = NavType.IntType })) {
            val id = it.arguments?.getInt("kullaniciId") ?: -1
            MyAccountScreen(navController, id)
        }

        composable(
            route = "details/{urunAdi}/{aciklama}/{fiyat}/{kullaniciId}",
            arguments = listOf(
                navArgument("urunAdi") { type = NavType.StringType },
                navArgument("aciklama") { type = NavType.StringType },
                navArgument("fiyat") { type = NavType.StringType },
                navArgument("kullaniciId") { type = NavType.IntType }
            )
        ) {
            val urunAdi = java.net.URLDecoder.decode(it.arguments?.getString("urunAdi") ?: "", "UTF-8")
            val aciklama = java.net.URLDecoder.decode(it.arguments?.getString("aciklama") ?: "", "UTF-8")
            val fiyat = it.arguments?.getString("fiyat")?.toDoubleOrNull() ?: 0.0
            val id = it.arguments?.getInt("kullaniciId") ?: -1

            DetailsScreen(
                urunAdi = urunAdi,
                aciklama = aciklama,
                fiyat = fiyat,
                kullaniciId = id
            )
        }
    }
}
