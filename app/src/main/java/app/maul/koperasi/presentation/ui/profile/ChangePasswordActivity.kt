package app.maul.koperasi.presentation.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityChangePasswordBinding
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil token dari SharedPreferences
        val token = Preferences.getToken(this)

        binding.btnSavePassword.setOnClickListener {
            val oldPass = binding.etOldPassword.text.toString().trim()
            val newPass = binding.etNewPassword.text.toString().trim()
            val confirmPass = binding.etConfirmPassword.text.toString().trim()

            Log.d("ChangePassDebug", "1. Password dari EditText: [$oldPass]")

            if (token.isNullOrEmpty()) {
                Toast.makeText(this, "Sesi tidak valid.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil fungsi di ViewModel
            viewModel.changePassword(token, oldPass, newPass, confirmPass)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // Observer untuk menampilkan pesan sukses
            viewModel.changePasswordSuccess.collect { successMessage ->
                successMessage?.let {
                    Toast.makeText(this@ChangePasswordActivity, it, Toast.LENGTH_LONG).show()
                    finish() // Tutup activity jika berhasil
                }
            }
        }

        lifecycleScope.launch {
            // Observer untuk menampilkan pesan error
            viewModel.error.collect { error ->
                error?.let { Toast.makeText(this@ChangePasswordActivity, it, Toast.LENGTH_LONG).show() }
            }
        }

        lifecycleScope.launch {
            // Observer untuk loading
            viewModel.loading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.btnSavePassword.isEnabled = !isLoading
            }
        }
    }
}