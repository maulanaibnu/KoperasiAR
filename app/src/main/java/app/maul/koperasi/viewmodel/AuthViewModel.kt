package app.maul.koperasi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.AuthRepository
import app.maul.koperasi.model.user.ForgotPasswordRequest
import app.maul.koperasi.model.user.ResetPasswordRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import app.maul.koperasi.model.login.LoginResult
import app.maul.koperasi.model.register.ApiErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.mail.*
import javax.mail.internet.*
import java.util.Properties

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository : AuthRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _loginResult = MutableStateFlow<LoginResult?>(null)
    val loginResult: StateFlow<LoginResult?> get() = _loginResult

    private val _forgotPasswordSuccess = MutableStateFlow<String?>(null)
    val forgotPasswordSuccess: StateFlow<String?> get() = _forgotPasswordSuccess

    private val _resetPasswordSuccess = MutableStateFlow<String?>(null)
    val resetPasswordSuccess: StateFlow<String?> get() = _resetPasswordSuccess

    fun doPostRegister(name : String,email : String,password : String){
        if (name.isBlank()) {
            _error.value = "Nama tidak boleh kosong."
            return
        }
        if (!isEmailValid(email)) {
            return
        }
        if (!isPasswordValid(password)) {
            return
        }
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {

                authRepository.doPostRegister(name, email, password)


            } catch (e: HttpException) {
                // --- INI BAGIAN YANG DIPERBARUI ---
                var errorMessage = "Terjadi kesalahan pada server."
                try {
                    val errorJson = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorJson, ApiErrorResponse::class.java)

                    if (!errorResponse.message.isNullOrEmpty()) {
                        errorMessage = errorResponse.message
                    }

                } catch (jsonError: Exception) {
                    Log.e("AuthViewModel", "Gagal parsing JSON error: $jsonError")
                }
                _error.value = errorMessage

            } catch (e: Exception) {
                _error.value = "Koneksi bermasalah. Periksa internet Anda."
            } finally {
                _loading.value = false
            }
        }
    }

    fun doPostRegisterObserver() = authRepository.postRegisterObserver()

    fun doPostLogin(email : String,password : String){
        if (!isEmailValid(email)) {
            return
        }
        if (password.isBlank()) {
            _error.value = "Password tidak boleh kosong."
            return
        }
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                authRepository.doPostLogin(email, password)

            } catch (e: HttpException) {
                var errorMessage = "Terjadi kesalahan pada server."

                if (e.code() == 404) {
                    try {
                        val errorJson = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorJson, ApiErrorResponse::class.java)

                        errorMessage = errorResponse.message ?: "Email tidak terdaftar."

                    } catch (jsonError: Exception) {

                        errorMessage = "Email atau password salah."
                    }
                }
                _error.value = errorMessage

            } catch (e: Exception) {
                _error.value = "Koneksi bermasalah. Periksa internet Anda."
            } finally {
                _loading.value = false
            }
        }
    }

    fun doPostLoginObserver() = authRepository.postLoginObserver()

    fun doPostVerify(otp : String, email:String){
        if (otp.isBlank() || otp.length < 4) {
            _error.value = "Kode OTP tidak valid."
            return
        }
        viewModelScope.launch {
            authRepository.doPostVerify(otp, email)
        }
    }

    fun doPostVerifyObserver() = authRepository.postVerifyObserver()

    fun requestForgotPassword(email: String) {
        if (!isEmailValid(email)) {
            return
        }
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _forgotPasswordSuccess.value = null

            try {
                val request = ForgotPasswordRequest(email)
                // Panggil API yang akan mengembalikan OTP
                val response = authRepository.forgotPassword(request)

                // Jika response mengandung OTP, panggil fungsi untuk mengirim email
                if (!response.otp.isNullOrEmpty()) {
                    sendResetPasswordEmail(response.otp, email)
                } else {
                    _error.value = response.message ?: "Gagal mendapatkan OTP dari server."
                    _loading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Gagal memproses permintaan: ${e.message}"
                _loading.value = false
            }
        }
    }


    private fun sendResetPasswordEmail(otp: String, recipientEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val senderEmail = "maulanaibnuf@gmail.com"
            val senderPassword = "uebfifhfeidqgote" // Gunakan App Password

            // --- TEMPLATE HTML BARU YANG LEBIH MENARIK ---
            val htmlMessage = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; margin: 0; padding: 0; background-color: #f8f9fa; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); overflow: hidden; border: 1px solid #e9ecef; }
                    .header { background-color: #007bff; /* Warna biru yang modern */ color: #ffffff; padding: 24px; text-align: center; }
                    .header h1 { margin: 0; font-size: 24px; }
                    .content { padding: 32px; text-align: center; color: #495057; line-height: 1.6; }
                    .content p { margin: 0 0 16px; }
                    .otp-code { font-size: 42px; font-weight: 700; color: #212529; margin: 24px 0; padding: 12px 24px; background-color: #e9ecef; border-radius: 8px; display: inline-block; letter-spacing: 4px; }
                    .warning { font-size: 14px; color: #6c757d; }
                    .footer { background-color: #f8f9fa; color: #6c757d; text-align: center; padding: 20px; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Reset Password Akun Anda</h1>
                    </div>
                    <div class="content">
                        <p>Kami menerima permintaan untuk mereset password akun Anda.</p>
                        <p>Gunakan kode di bawah ini untuk melanjutkan proses:</p>
                        <div class="otp-code">$otp</div>
                        <p class="warning">Kode ini hanya berlaku selama 10 menit. Mohon untuk tidak membagikan kode ini kepada siapa pun demi keamanan akun Anda.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; ${Calendar.getInstance().get(Calendar.YEAR)} Koperasi KPRI Tegal. Semua hak dilindungi.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
            // --- AKHIR TEMPLATE HTML BARU ---

            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
            }

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication() = PasswordAuthentication(senderEmail, senderPassword)
            })

            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail))
                    addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                    setSubject("Kode Reset Password untuk Akun Anda")
                    setContent(htmlMessage, "text/html; charset=utf-8")
                }
                Transport.send(message)

                withContext(Dispatchers.Main) {
                    _forgotPasswordSuccess.value = "Email reset password telah dikirim."
                    _loading.value = false
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Gagal mengirim email", e)
                withContext(Dispatchers.Main) {
                    _error.value = "Gagal mengirim email."
                    _loading.value = false
                }
            }
        }
    }

    fun resetPassword(email: String, otp: String, newPassword: String) {
        if (otp.isBlank()) {
            _error.value = "Kode OTP tidak boleh kosong."
            return
        }
        if (isPasswordValid(newPassword)){
            return
        }
        viewModelScope.launch {
            _loading.value = true
            try {
                val request = ResetPasswordRequest(email, otp, newPassword)
                val response = authRepository.resetPassword(request)
                _resetPasswordSuccess.value = response.message
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Gagal mereset password"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _error.value = "Format email tidak valid."
            return false
        }
        return true
    }

    private fun isPasswordValid(password: String): Boolean {
        if (password.length < 8) {
            _error.value = "Password minimal harus 8 karakter."
            return false
        }
        if (!password.matches("^[A-Z].*".toRegex())) {
            _error.value = "Password harus diawali dengan huruf kapital."
            return false
        }
        if (!password.matches(".*[!@#\$%^&*()].*".toRegex())) {
            _error.value = "Password harus mengandung karakter spesial (contoh: !@#\$%)."
            return false
        }
        return true
    }

}