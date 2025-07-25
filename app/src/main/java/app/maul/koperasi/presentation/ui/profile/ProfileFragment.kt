package app.maul.koperasi.presentation.ui.profile

import android.app.AlertDialog
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

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel : UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        observeViewModel()

        val token = Preferences.getToken(requireContext())
        if (token != null && token.isNotEmpty()) {
            viewModel.getUserProfile(token)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir, silahkan login kembali.", Toast.LENGTH_LONG).show()
        }

        setupClickListener()
    }

    override fun onResume() {
        super.onResume()
        val token = Preferences.getToken(requireContext())
        if (token != null && token.isNotEmpty()) {
            viewModel.getUserProfile(token)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir, silahkan login kembali.", Toast.LENGTH_LONG).show()
        }
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
        binding.linearPassword.setOnClickListener {
            startActivity((Intent(activity, ChangePasswordActivity::class.java)))
        }
        binding.linearLogout.setOnClickListener {
            logout()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userProfile.collect { user ->
                user?.let {
                    updateUI(it)
                    Preferences.setId(requireActivity(), user.id
                    )
                }
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


    private fun logout() {
        // Membuat dan menampilkan AlertDialog
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout") // Judul dialog
            .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?") // Pesan untuk user
            .setPositiveButton("Ya") { _, _ ->
                // Kode ini hanya akan dijalankan jika user menekan tombol "Ya"
                val intent = Intent(activity, LoginActivity::class.java).also {
                    Preferences.clearName(requireActivity())
                    Preferences.clearToken(requireActivity())
                }
                activity?.startActivity(intent)
                activity?.finish()
            }
            .setNegativeButton("Batal", null) // Tombol "Batal" tidak melakukan apa-apa selain menutup dialog
            .show() // Menampilkan dialog ke layar
    }

    // Pastikan untuk membersihkan binding untuk menghindari memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}