package app.maul.koperasi.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.maul.koperasi.databinding.FragmentProfileBinding
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.activity.AddressActivity
import app.maul.koperasi.presentation.ui.chatbot.ChatbotActivity
import app.maul.koperasi.presentation.ui.login.LoginActivity

class ProfileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        doLogout()
        goToChatbot()
        setAdress()
        editProfile()
        return binding.root
    }

    private fun goToChatbot(){
        binding.linearPusatBantuan.setOnClickListener {
            startActivity(Intent(activity, ChatbotActivity::class.java))
        }
    }

    private fun editProfile(){
        binding.linearEditProfile.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }
    }

    private fun setAdress(){
        binding.linearAddress.setOnClickListener {
            startActivity(Intent(activity, AddressActivity::class.java))
        }
    }

    private fun doLogout(){
        binding.linearLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout(){
        val intent = Intent(activity, LoginActivity::class.java).also {
            Preferences.clearName(requireActivity())
            Preferences.clearToken(requireActivity())
        }
        activity?.startActivity(intent)
        activity?.finish()
    }



}