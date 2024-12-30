package com.example.project3

object UserHolder {
    private var user: User? = null

    fun setUser(user: User?) {
        this.user = user
    }

    fun getUser(): User? {
        return user
    }

    fun clearUser() {
        user = null
    }
}
