package app.maul.koperasi.preference

import android.content.Context
import android.content.SharedPreferences

class LoginPreferences(val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "login_preferences"
        private const val KEY_TOKEN = "token"
    }

    var token: String?
    get() = sharedPreferences.getString(KEY_TOKEN, "")
    set(value) {
        sharedPreferences.edit().putString(KEY_TOKEN, value).apply()
    }
}