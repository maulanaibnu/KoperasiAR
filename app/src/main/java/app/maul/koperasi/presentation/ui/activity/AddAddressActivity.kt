package app.maul.koperasi.presentation.ui.activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.databinding.ActivityAddAddressBinding
import app.maul.koperasi.model.address.AddressData
import app.maul.koperasi.model.address.AddressRequest
import app.maul.koperasi.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBinding
    // INI INJECT VIEWMODEL DARI HILT!
    private val viewModel: AddressViewModel by viewModels()

    private var  id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener { finish() }

        id = intent.getIntExtra("ADDRESS_ID", 0)
        if(id != 0){
            viewModel.getAddressById(id)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.address.collect { data ->
                if(data != null){
                    setData(data)
                }
            }
        }


        binding.btnSaveAlamat.setOnClickListener {
            val recipientName = binding.etNamaPenerima.text?.toString()?.trim() ?: ""
            val phoneNumber = binding.etNoTelefon.text?.toString()?.trim() ?: ""
            val label = binding.etLabelAlamat.text?.toString()?.trim() ?: ""
            val street = binding.etAlamatLengkap.text?.toString()?.trim() ?: ""
            val notes = binding.etKodePos.text?.toString()?.trim() ?: ""

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
            if(id != 0){
                viewModel.updateAddress(id,request)
            }else{
                viewModel.createAddress(request)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@AddAddressActivity, it, Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
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

    private fun setData(addressData: AddressData){
        binding.apply {
            etNamaPenerima.setText(addressData.recipient_name)
            etNoTelefon.setText(addressData.phone_number)
            etLabelAlamat.setText(addressData.label)
            etAlamatLengkap.setText(addressData.street)
        }
    }
}
