package app.maul.koperasi.presentation.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.R
import app.maul.koperasi.adapter.CityAdapter
import app.maul.koperasi.databinding.ActivityEditAddressBinding
import app.maul.koperasi.databinding.BottomSheetCityBinding
import app.maul.koperasi.model.address.AddressData
import app.maul.koperasi.model.address.AddressRequest
import app.maul.koperasi.viewmodel.AddressViewModel
import app.maul.koperasi.viewmodel.RajaOngkirViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditAddressBinding
    private val viewModel: AddressViewModel by viewModels()

    private val rajaOngkirViewModel: RajaOngkirViewModel by viewModels()
    private lateinit var cityAdapter: CityAdapter

    private var currentAddressId: Int = 0
    private val labelOptions = listOf("Rumah", "Toko")

    private var currentAddressData: AddressData? = null

    private var selectedIdDestination: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLabelDropdown()


        binding.imgBack.setOnClickListener { finish() }

        binding.etCity.setOnClickListener { showFullScreenBottomSheet() }

        currentAddressId = intent.getIntExtra("ADDRESS_ID", 0)
        if (currentAddressId != 0) {
            viewModel.getAddressById(currentAddressId)
        } else {
            Toast.makeText(this, "ID Alamat tidak ditemukan untuk diedit.", Toast.LENGTH_SHORT).show()
            finish() // Langsung tutup jika ID tidak valid
        }

        lifecycleScope.launchWhenStarted {
            viewModel.address.collect { data ->
                if (data != null) {
                    currentAddressData = data
                    setData(data)
                }
            }
        }

        binding.btnSaveEdtAlamat.setOnClickListener {
            val recipientName = binding.etNamaPenerima.text.toString().trim()
            val phoneNumber = binding.etNoTelefon.text.toString().trim()
            val label = binding.actvLabelAlamat.text.toString().trim()
            val street = binding.etAlamatLengkap.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()
            val city = binding.etCity.text.toString().trim()
            val isDefault = binding.switchIsDefault.isChecked

            if (recipientName.isBlank() || phoneNumber.isBlank() || label.isBlank() || street.isBlank() || notes.isBlank() || city.isBlank()) {
                Toast.makeText(this, "Isi semua data terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = AddressRequest(
                recipient_name = recipientName,
                phone_number = phoneNumber,
                label = label,
                street = street,
                notes = notes,
                city = city,
                id_destination = selectedIdDestination, // Gunakan selectedIdDestination
                isDefault = isDefault // Kirimkan nilai isDefault dari SwitchMaterial
            )

            viewModel.updateAddress(currentAddressId, request)
        }

        binding.btnDelAlamat.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus Alamat")
                .setMessage("Apakah Anda yakin ingin menghapus alamat ini? Aksi ini tidak dapat dibatalkan.")
                .setPositiveButton("Ya, Hapus") { _, _ ->
                    viewModel.deleteAddress(currentAddressId)
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        lifecycleScope.launch { // Menggunakan lifecycleScope.launch untuk collect Flow
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@EditAddressActivity, it, Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                    viewModel.resetSuccess() // Reset success message
                }
            }
        }
        lifecycleScope.launch { // Menggunakan lifecycleScope.launch untuk collect Flow
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(this@EditAddressActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetError() // Reset error message
                }
            }
        }
    }

    private fun setData(addressData: AddressData) {
        // Simpan id destinasi awal dari data yang diterima
        selectedIdDestination = addressData.id_destination

        binding.apply {
            etNamaPenerima.setText(addressData.recipient_name)
            etNoTelefon.setText(addressData.phone_number)
            actvLabelAlamat.setText(addressData.label ?: labelOptions.first(), false)
            etAlamatLengkap.setText(addressData.street)
            etNotes.setText(addressData.notes)
            etCity.setText(addressData.city)
            // Set status SwitchMaterial berdasarkan data isDefault
            switchIsDefault.isChecked = addressData.isDefault

            if (addressData.isDefault) {
                switchIsDefault.isEnabled = false
                switchIsDefault.alpha = 0.5f
            } else {
                switchIsDefault.isEnabled = true
                switchIsDefault.alpha = 1.0f
            }
        }
    }

    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()

    private fun setupLabelDropdown() {
        val paddingStart = 16.dpToPx()
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

    private fun showFullScreenBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.FullScreenBottomSheetDialogTheme)
        val bottomSheetView = BottomSheetCityBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetView.root)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.requestLayout()
            }
        }

        rajaOngkirViewModel.destinationResult.observe(this) { destinationList ->
            if (destinationList.isNotEmpty()) {
                bottomSheetView.tvDesc.visibility = View.VISIBLE
                bottomSheetView.rvCity.visibility = View.VISIBLE
                cityAdapter.updateData(destinationList)
            } else {
                bottomSheetView.tvDesc.visibility = View.GONE
                bottomSheetView.rvCity.visibility = View.GONE
            }
        }

        rajaOngkirViewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
        }

        cityAdapter = CityAdapter(mutableListOf(), object : CityAdapter.onClickCity {
            override fun onSelectCity(label: String, id: Int) {
                selectedIdDestination = id // Pastikan menggunakan selectedIdDestination
                binding.etCity.setText(label)
                dialog.dismiss()
            }
        })

        bottomSheetView.rvCity.apply {
            adapter = cityAdapter
            layoutManager = LinearLayoutManager(this@EditAddressActivity)
        }

        bottomSheetView.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.length >= 3) {
                    rajaOngkirViewModel.searchDestination(query)
                } else {
                    cityAdapter.updateData(emptyList())
                }
            }
        })
        dialog.show()
    }
}