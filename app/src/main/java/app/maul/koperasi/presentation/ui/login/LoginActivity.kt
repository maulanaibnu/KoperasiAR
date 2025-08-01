package app.maul.koperasi.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.MainActivity
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityLoginBinding
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.activity.ForgotPasswordActivity
import app.maul.koperasi.presentation.ui.register.RegisterActivity
import app.maul.koperasi.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel>()

    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        binding.btnLogin.setOnClickListener {
            binding.apply {
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                if(email.isNotBlank() && password.isNotBlank()){
                    doLogin(email, password)
                }else{
                    Toast.makeText(this@LoginActivity, "Input tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvForgetPassword.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            authViewModel.error.collect { errorMessage ->
                if (!errorMessage.isNullOrEmpty()) {
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            authViewModel.loading.collect { isLoading ->
            }
        }

        lifecycleScope.launch {
            authViewModel.loginResult.collect { result ->
                if (result != null) {
                }
            }
        }
    }

    private fun doLogin(email : String,password : String){
        authViewModel.doPostLogin(email, password)
        authViewModel.doPostLoginObserver().observe(this){
            if(it != null){
                Preferences.setToken(this@LoginActivity, it.token)
                Preferences.setName(this@LoginActivity, it.name)
                Preferences.setId(this@LoginActivity, it.id)
                val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }
    }
}