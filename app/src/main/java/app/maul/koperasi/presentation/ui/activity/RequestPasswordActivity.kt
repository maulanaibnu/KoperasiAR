package app.maul.koperasi.presentation.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityRequestPasswordBinding
import app.maul.koperasi.presentation.ui.login.LoginActivity
import app.maul.koperasi.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RequestPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestPasswordBinding
    private val viewModel: AuthViewModel by viewModels()
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil email yang dikirim dari ForgotPasswordActivity
        userEmail = intent.getStringExtra("USER_EMAIL")
        if (userEmail == null) {
            Toast.makeText(this, "Terjadi kesalahan, email tidak ditemukan.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupClickListener()
        observeViewModel()
    }

    private fun setupClickListener() {
        binding.btnResetPassword.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()

            if (otp.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "OTP dan password baru tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil fungsi reset password di ViewModel
            userEmail?.let { email ->
                viewModel.resetPassword(email, otp, newPassword)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loading.collectLatest { isLoading ->
                binding.progressBarReset.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.btnResetPassword.isEnabled = !isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { errorMessage ->
                errorMessage?.let {
                    // PERBAIKAN DI SINI
                    Toast.makeText(this@RequestPasswordActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            // Mengamati status sukses reset password
            viewModel.resetPasswordSuccess.collectLatest { successMessage ->
                successMessage?.let {
                    // PERBAIKAN DI SINI
                    Toast.makeText(this@RequestPasswordActivity, it, Toast.LENGTH_LONG).show()

                    // PERBAIKAN DI SINI
                    // Jika sukses, kembali ke halaman Login dan hapus semua activity sebelumnya
                    val intent = Intent(this@RequestPasswordActivity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}