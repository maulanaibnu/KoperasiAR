package app.maul.koperasi.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.databinding.ActivityForgotPasswordBinding
import app.maul.koperasi.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListener()
        observeViewModel()
    }

    private fun setupClickListener() {
        binding.btnVerify.setOnClickListener {
            val email = binding.etEmailForget.text.toString().trim()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Masukkan alamat email yang valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Panggil fungsi di ViewModel
            viewModel.requestForgotPassword(email)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // Mengamati status loading
            viewModel.loading.collectLatest { isLoading ->
                binding.progressBarSigUp.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.btnVerify.isEnabled = !isLoading
            }
        }

        lifecycleScope.launch {
            // Mengamati pesan error
            viewModel.error.collectLatest { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(this@ForgotPasswordActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            // Mengamati status sukses pengiriman email
            viewModel.forgotPasswordSuccess.collectLatest { successMessage ->
                successMessage?.let {
                    Toast.makeText(this@ForgotPasswordActivity, it, Toast.LENGTH_LONG).show()

                    // Jika email berhasil dikirim, pindah ke ResetPasswordActivity
                    val intent = Intent(this@ForgotPasswordActivity, RequestPasswordActivity::class.java).apply {
                        // Kirim email ke activity berikutnya
                        putExtra("USER_EMAIL", binding.etEmailForget.text.toString().trim())
                    }
                    startActivity(intent)
                }
            }
        }
    }
}