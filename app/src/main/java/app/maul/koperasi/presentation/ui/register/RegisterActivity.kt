package app.maul.koperasi.presentation.ui.register

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.databinding.ActivityRegisterBinding
import app.maul.koperasi.presentation.ui.activity.VerifyActivity
import app.maul.koperasi.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.mail.*
import javax.mail.internet.*
import java.util.Properties

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        binding.tvLogin.setOnClickListener {
            finish()
        }
        setupObservers()
    }
    private fun setupObservers() {
        lifecycleScope.launch {
            authViewModel.error.collect { errorMessage ->
                if (!errorMessage.isNullOrEmpty()) {
                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            authViewModel.loading.collect { isLoading ->
                if (isLoading) {

                    showLoadingDialog()
                } else {
                    hideLoadingDialog()
                }
            }
        }
    }

    private fun doRegister(name : String, email : String, password : String){
        authViewModel.doPostRegister(name, email, password)
        authViewModel.doPostRegisterObserver().observe(this){ x ->
            if(x != null){
                val otp = x.otp
                lifecycleScope.launch(Dispatchers.IO) {
                    val senderEmail = "maulanaibnuf@gmail.com"
                    val senderPassword = "uebfifhfeidqgote"
                    val subject = "Kode Verifikasi Koperasi KPRI"
                    val recipientEmail = email

                    val htmlMessage = """
                        <html>
                        <head>
                            <style>
                                body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); overflow: hidden; }
                                .header { background-color: #4CAF50; color: #ffffff; padding: 20px; text-align: center; }
                                .content { padding: 30px; text-align: center; }
                                .otp-code { font-size: 36px; font-weight: bold; color: #333333; margin: 20px 0; padding: 10px 20px; background-color: #f0f0f0; border-radius: 5px; display: inline-block; }
                                .footer { background-color: #f4f4f4; color: #888888; text-align: center; padding: 20px; font-size: 12px; }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">
                                    <h1>Verifikasi Akun Anda</h1>
                                </div>
                                <div class="content">
                                    <p>Halo,</p>
                                    <p>Terima kasih telah mendaftar di Koperasi KPRI Tegal. Untuk memverifikasi alamat email Anda, silakan gunakan kode verifikasi di bawah ini:</p>
                                    <div class="otp-code">$otp</div>
                                    <p>Kode ini berlaku untuk waktu terbatas. Jangan bagikan kode ini kepada siapa pun.</p>
                                    <p>Terima kasih,<br>Tim Koperasi tegal</p>
                                </div>
                                <div class="footer">
                                    <p>&copy; ${Calendar.getInstance().get(Calendar.YEAR)} Deshop. Semua hak dilindungi.</p>
                                </div>
                            </div>
                        </body>
                        </html>
                    """.trimIndent()
                    val props = Properties().apply {
                        put("mail.smtp.auth", "true")
                        put("mail.smtp.starttls.enable", "true")
                        put("mail.smtp.host", "smtp.gmail.com")
                        put("mail.smtp.port", "587")
                    }
                    try {
                        val session = Session.getInstance(props, object : Authenticator() {
                            override fun getPasswordAuthentication(): PasswordAuthentication {
                                return PasswordAuthentication(senderEmail, senderPassword)
                            }
                        })

                        val message = MimeMessage(session).apply {
                            setFrom(InternetAddress(senderEmail))
                            addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                            setSubject(subject)
                            setContent(htmlMessage, "text/html; charset=utf-8")
                        }
                        Transport.send(message)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "Kode verifikasi telah dikirim ke $recipientEmail", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@RegisterActivity, VerifyActivity::class.java).also{
                                it.putExtra("email", binding.etEmail.text.toString().trim())
                                it.putExtra("otp", otp)
                            })
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterActivity", "Gagal mengirim email: ${e.message}", e)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "Gagal mengirim kode verifikasi. Coba lagi.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@RegisterActivity, "Pendaftaran gagal. Respon OTP null.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var loadingDialog: Dialog? = null
    private fun showLoadingDialog() {

        loadingDialog?.dismiss()

        val dialog = Dialog(this)
        dialog.setContentView(ProgressBar(this))
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        loadingDialog = dialog
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }
}