package com.example.courseworkmobile


sealed class Screen(val route: String) {
    data object News : Screen("news")
    data object User : Screen("user")
    data object Notifications : Screen("notifications")
    data object Auth : Screen("auth")
    data object Admin : Screen("admin")
    data object CreateNews : Screen("create_news")
    data object EditNews : Screen("edit_news/{id}")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Screen) return false
        return route == other.route
    }

    override fun hashCode(): Int {
        return route.hashCode()
    }
}