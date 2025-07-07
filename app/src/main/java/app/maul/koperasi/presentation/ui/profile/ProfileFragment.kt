package app.maul.koperasi.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.R
import app.maul.koperasi.databinding.FragmentProfileBinding
import app.maul.koperasi.model.user.User
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.activity.AddressActivity
import app.maul.koperasi.presentation.ui.chatbot.ChatbotActivity
import app.maul.koperasi.presentation.ui.login.LoginActivity
import app.maul.koperasi.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel : UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Pindahkan semua logika yang berinteraksi dengan view ke onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Panggil observer SEBELUM meminta data, agar siap menerima data
        observeViewModel()

        val token = Preferences.getToken(requireContext())
        if (token != null && token.isNotEmpty()) {
            viewModel.getUserProfile(token)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir, silahkan login kembali.", Toast.LENGTH_LONG).show()
        }

        setupClickListener()
    }


    private fun setupClickListener(){
        binding.linearPusatBantuan.setOnClickListener {
            startActivity(Intent(activity, ChatbotActivity::class.java))
        }
        binding.linearEditProfile.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }
        binding.linearAddress.setOnClickListener {
            startActivity(Intent(activity, AddressActivity::class.java))
        }
        binding.linearLogout.setOnClickListener {
            logout()
        }
    }

    private fun observeViewModel() {
        // Mengamati data profil pengguna
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userProfile.collect { user ->
                // 'it' adalah objek User. Cek jika tidak null lalu update UI
                user?.let { updateUI(it) }
            }
        }

        // Mengamati status loading untuk menampilkan/menyembunyikan progress bar
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loading.collect { isLoading ->
                // Jika Anda punya ProgressBar, atur visibilitasnya di sini
                // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // Mengamati pesan error
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collect { errorMsg ->
                errorMsg?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateUI(user: User) {
        binding.apply {
            // Ini adalah baris kunci untuk menampilkan nama
            tvName.text = user.name

            // PENTING: Ganti "BASE_IMAGE_URL" dengan URL dasar server gambar Anda
            // Contoh: "http://api.koperasi.com/images/"
//            val imageUrl = "BASE_IMAGE_URL" + user.profile_image
            val imageUrl = user.profile_image

            Glide.with(this@ProfileFragment)
                .load(imageUrl)
                .placeholder(R.drawable.img_1) // Gambar default saat loading
                .error(R.drawable.img_1)      // Gambar default jika error
                .into(profileImage)
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

    // Pastikan untuk membersihkan binding untuk menghindari memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}