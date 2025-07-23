package app.maul.koperasi.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityVerifyBinding
import app.maul.koperasi.presentation.ui.login.LoginActivity
import app.maul.koperasi.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class VerifyActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVerifyBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val email = intent.getStringExtra("email")
        val otp = intent.getStringExtra("otp")
        binding.etOTP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 4) {
                    if(binding.etOTP.text.toString() == otp){
                        authViewModel.doPostVerify(otp, email.toString())
                        authViewModel.doPostVerifyObserver().observe(this@VerifyActivity){
                            if(it != null){
                                val intent = Intent(this@VerifyActivity, LoginActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                Toast.makeText(this@VerifyActivity, "OTP Verified", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(this@VerifyActivity, "OTP SALAH", Toast.LENGTH_SHORT).show()
                    }


                }
            }
        })
    }
}