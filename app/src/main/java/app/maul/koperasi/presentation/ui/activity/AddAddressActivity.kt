package app.maul.koperasi.presentation.ui.activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityAddAddressBinding
import app.maul.koperasi.model.address.AddressRequest
import app.maul.koperasi.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddressActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddAddressBinding
    private val viewModel: AddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imgBack.setOnClickListener { finish() }

        binding.btnSaveAlamat.setOnClickListener {
            val recipientName = binding.etNamaPenerima.text?.toString()?.trim() ?: ""
            val phoneNumber = binding.etNoTelefon.text?.toString()?.trim() ?: ""
            val label = binding.etLabelAlamat.text?.toString()?.trim() ?: ""
            val street = binding.etAlamatLengkap.text?.toString()?.trim() ?: ""
            val notes = binding.etKodePos.text?.toString()?.trim() ?: "" // Kode pos diisi ke notes (atau ganti field jika backend mendukung postalCode)

            // Validasi sederhana (opsional)
            if (recipientName.isBlank() || phoneNumber.isBlank() || label.isBlank() || street.isBlank() || notes.isBlank()) {
                Toast.makeText(this, "Isi semua data terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = AddressRequest(
                recipient_name = recipientName,
                phone_number = phoneNumber,
                label = label,
                street = street,
                notes = notes
            )
            viewModel.createAddress(request)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@AddAddressActivity, it, Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK) // Kirim status OK ke AddressActivity
                    finish() // Kembali ke AddressActivity
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(this@AddAddressActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetError()
                }
            }
        }
    }
}