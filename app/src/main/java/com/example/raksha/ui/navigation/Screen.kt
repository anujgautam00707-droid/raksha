package com.example.raksha.ui.navigation

sealed class Screen(val route: String, val label: String, val icon: String) {
    data object Home : Screen("home", "Home", "🏠")
    data object Contacts : Screen("contacts", "Contacts", "👥")
    data object Tools : Screen("tools", "Tools", "🧰")
    data object Settings : Screen("settings", "Settings", "⚙")
}

val bottomNavItems = listOf(Screen.Home, Screen.Contacts, Screen.Tools, Screen.Settings)
