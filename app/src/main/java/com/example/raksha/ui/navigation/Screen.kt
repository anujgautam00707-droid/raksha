package com.example.raksha.ui.navigation

import androidx.annotation.StringRes
import com.example.raksha.R

sealed class Screen(val route: String, @StringRes val labelRes: Int) {
    data object Home : Screen("home", R.string.nav_home)
    data object Contacts : Screen("contacts", R.string.nav_contacts)
    data object Tools : Screen("tools", R.string.nav_tools)
    data object Settings : Screen("settings", R.string.nav_settings)
}

val bottomNavItems = listOf(Screen.Home, Screen.Contacts, Screen.Tools, Screen.Settings)
