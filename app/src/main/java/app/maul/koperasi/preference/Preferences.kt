package app.maul.koperasi.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE

class Preferences {
    companion object {
        fun getToken(context: Context):String{
            val pref = context.getSharedPreferences("TOKEN", MODE_PRIVATE)
            val token = pref?.getString("TOKEN", "UNDEFINED")
            return token!!
        }

        fun setToken(context: Context, token: String) {
            val pref = context.getSharedPreferences("TOKEN", MODE_PRIVATE)
            pref.edit().apply {
                putString("TOKEN", token)
                apply()
            }
        }

        fun clearToken(context: Context) {
            val pref = context.getSharedPreferences("TOKEN", MODE_PRIVATE)
            pref.edit().clear().apply()
        }

        fun getName(context: Context): String {
            val pref = context.getSharedPreferences("NAME", MODE_PRIVATE)
            val token = pref?.getString("NAME", "JhonDoe")
            return token!!
        }

        fun setName(context: Context, name: String) {
            val pref = context.getSharedPreferences("NAME", MODE_PRIVATE)
            pref.edit().apply {
                putString("NAME", name)
                apply()
            }
        }

        fun clearName(context: Context) {
            val pref = context.getSharedPreferences("NAME", MODE_PRIVATE)
            pref.edit().clear().apply()
        }

        fun getId(context: Context): Int {
            val pref = context.getSharedPreferences("ID", MODE_PRIVATE)
            val id = pref?.getInt("ID", 0)
            return id!!
        }

        fun setId(context: Context, id: Int) {
            val pref = context.getSharedPreferences("ID", MODE_PRIVATE)
            pref.edit().apply {
                putInt("ID", id)
                apply()
            }
        }

        fun clearId(context: Context) {
            val pref = context.getSharedPreferences("ID", MODE_PRIVATE)
            pref.edit().clear().apply()
        }

    }
}