package app.maul.koperasi.presentation.ui.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityEditAddressBinding
import app.maul.koperasi.model.address.AddressData
import app.maul.koperasi.model.address.AddressRequest
import app.maul.koperasi.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditAddressBinding

    private val viewModel: AddressViewModel by viewModels()

    private var  id = 0

    private val labelOptions = listOf("Rumah", "Toko")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAddressBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupLabelDropdown()

        binding.imgBack.setOnClickListener { finish() }

        id = intent.getIntExtra("ADDRESS_ID", 0)
        Toast.makeText(this, "$id", Toast.LENGTH_SHORT).show()
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


        binding.btnSaveEdtAlamat.setOnClickListener {
            val recipientName = binding.etNamaPenerima.text?.toString()?.trim() ?: ""
            val phoneNumber = binding.etNoTelefon.text?.toString()?.trim() ?: ""
            val label = binding.actvLabelAlamat.text?.toString()?.trim() ?: ""
            val street = binding.etAlamatLengkap.text?.toString()?.trim() ?: ""
            val notes = binding.etNotes.text?.toString()?.trim() ?: ""

            if (recipientName.isBlank() || phoneNumber.isBlank() || label.isBlank() || street.isBlank() || notes.isBlank()) {
                Toast.makeText(this, "Isi semua data terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = AddressRequest(
                recipient_name = recipientName,
                phone_number = phoneNumber,
                label = label,
                street = street,
                notes = notes,
                city = "",
                id_destination = 0
            )
            if(id != 0){
                Toast.makeText(this, "Update alamat dengan id: $id", Toast.LENGTH_SHORT).show()
                viewModel.updateAddress(id,request)
            }else{
                Toast.makeText(this, "Tambah alamat baru", Toast.LENGTH_SHORT).show()
                viewModel.createAddress(request)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@EditAddressActivity, it, Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(this@EditAddressActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetError()
                }
            }
        }

    }

    //set up dropdown label alamat
    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()

    private fun setupLabelDropdown() {
        val paddingStart = 16.dpToPx() // 16dp padding kiri

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            labelOptions
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                if (view is TextView) {
                    view.setPadding(paddingStart, 0, 0, 0)
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                if (view is TextView) {
                    view.setPadding(paddingStart, 24, 0, 24)
                }
                return view
            }
        }
        binding.actvLabelAlamat.setAdapter(adapter)
    }

    private fun setData(addressData: AddressData){
        binding.apply {
            etNamaPenerima.setText(addressData.recipient_name)
            etNoTelefon.setText(addressData.phone_number)
            actvLabelAlamat.setText(addressData.label ?: labelOptions.first(), false)
            etAlamatLengkap.setText(addressData.street)
            etNotes.setText(addressData.notes)
        }
    }
}