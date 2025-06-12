package app.maul.koperasi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository : AuthRepository
) : ViewModel() {

    fun doPostRegister(name : String,email : String,password : String){
        viewModelScope.launch {
            authRepository.doPostRegister(name,email, password)
        }
    }

    fun doPostRegisterObserver() = authRepository.postRegisterObserver()

    fun doPostLogin(email : String,password : String){
        viewModelScope.launch {
            authRepository.doPostLogin(email,password)
        }
    }

    fun doPostLoginObserver() = authRepository.postLoginObserver()

    fun doPostVerify(otp : String, email:String){
        viewModelScope.launch {
            authRepository.doPostVerify(otp, email)
        }
    }

    fun doPostVerifyObserver() = authRepository.postVerifyObserver()

}