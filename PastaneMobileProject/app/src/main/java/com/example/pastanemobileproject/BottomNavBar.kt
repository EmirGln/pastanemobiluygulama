// BottomNavBar.kt
package com.example.pastanemobileproject.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String,
    kullaniciId: Int,
    totalPrice: Double
) {
    val items = listOf(
        BottomNavItem("Anasayfa", Icons.Default.Home, "home/$kullaniciId"),
        BottomNavItem("Sepet", Icons.Default.ShoppingCart, "sepet/$kullaniciId"),
        BottomNavItem("Siparişler", Icons.Default.List, "siparisler/$kullaniciId"),
        BottomNavItem("Hesabım", Icons.Default.Person, "kullanici/$kullaniciId")
    )

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentRoute.startsWith(item.route.split("/")[0]) // sadece "home" gibi

            NavigationBarItem(
                icon = {
                    if (item.title == "Sepet") {
                        BadgedBox(
                            badge = {
                                if (totalPrice > 0.0) {
                                    Badge {
                                        Text("₺%.0f".format(totalPrice))
                                    }
                                }
                            }
                        ) {
                            Icon(item.icon, contentDescription = item.title)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.title)
                    }
                },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(0)
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
