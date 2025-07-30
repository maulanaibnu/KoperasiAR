package app.maul.koperasi.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.login.LoginResponse
import app.maul.koperasi.model.login.LoginResult
import app.maul.koperasi.model.register.RegisterRequest
import app.maul.koperasi.model.register.RegisterResponse
import app.maul.koperasi.model.user.ForgotPasswordRequest
import app.maul.koperasi.model.user.ForgotPasswordResponse
import app.maul.koperasi.model.user.ResetPasswordRequest
import app.maul.koperasi.model.user.ResetPasswordResponse
import app.maul.koperasi.model.verify.VerifyResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService) {

    private val doPostRegister: MutableLiveData<RegisterRequest?> = MutableLiveData()
    fun postRegisterObserver(): LiveData<RegisterRequest?> = doPostRegister

    private val doPostLogin: MutableLiveData<LoginResult?> = MutableLiveData()
    fun postLoginObserver(): LiveData<LoginResult?> = doPostLogin

    private val doPostVerify: MutableLiveData<VerifyResponse?> = MutableLiveData()
    fun postVerifyObserver(): LiveData<VerifyResponse?> = doPostVerify

    private val message: MutableLiveData<String> = MutableLiveData()
    fun messageObserver(): LiveData<String> = message

    suspend fun doPostRegister(
        name : String,
        email : String,
        password : String,
    ) {
        try {
            val response = apiService.register(name,email,password)
            if (!response.statusSuccess.isNullOrBlank()) {
                val body = response.user

                doPostRegister.postValue(body)
            } else {
                doPostRegister.postValue(null)
            }
        } catch (t: Throwable) {
            doPostRegister.postValue(null)
        }
    }

    suspend fun doPostLogin(
        name : String,
        email : String,
    ) {
        try {
            val response = apiService.login(name,email)
            if (response.status == 200) {
                val body = response.data

                doPostLogin.postValue(body)
            } else {
                doPostLogin.postValue(null)
            }
        } catch (t: Throwable) {
            doPostLogin.postValue(null)
        }
    }

    suspend fun doPostVerify(
        otp : String,
        email : String,
    ) {
        try {
            val response = apiService.verify(otp, email)
            if (response.status == 200) {
                val body = response
                doPostVerify.postValue(body)
            } else {
                doPostVerify.postValue(null)
            }
        } catch (t: Throwable) {
            doPostVerify.postValue(null)
        }
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): ForgotPasswordResponse {
        return apiService.forgotPassword(request)
    }

    suspend fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse {
        return apiService.resetPassword(request)
    }

}