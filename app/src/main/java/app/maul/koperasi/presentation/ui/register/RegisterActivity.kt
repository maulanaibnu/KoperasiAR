package app.maul.koperasi.presentation.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityRegisterBinding
import app.maul.koperasi.presentation.ui.activity.VerifyActivity
import app.maul.koperasi.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel>()

    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnRegis.setOnClickListener {
            binding.apply {
                val name = binding.etName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                if(name.isNotBlank() && email.isNotBlank() && password.isNotBlank()){
                    doRegister(name,email, password)
                }else{
                    Toast.makeText(this@RegisterActivity, "Input tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            finish()
        }


    }

    private fun doRegister(name : String,email : String,password : String){
        authViewModel.doPostRegister(name, email, password)
        authViewModel.doPostRegisterObserver().observe(this){ x ->
            if(x != null){
                startActivity(Intent(this, VerifyActivity::class.java).also{
                    it.putExtra("email",binding.etEmail.text.toString().trim())
                    it.putExtra("otp",x.otp)
                })
                Toast.makeText(this, "${x.otp}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}