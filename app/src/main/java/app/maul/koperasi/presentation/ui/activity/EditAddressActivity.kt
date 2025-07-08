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

@AndroidEntryPoint
class EditAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditAddressBinding
    private val viewModel: AddressViewModel by viewModels()

    // BARU: Inisialisasi ViewModel dan Adapter untuk RajaOngkir
    private val rajaOngkirViewModel: RajaOngkirViewModel by viewModels()
    private lateinit var cityAdapter: CityAdapter

    private var id = 0
    private val labelOptions = listOf("Rumah", "Toko")
    private var isSetAsDefault = false
    private var currentAddressData: AddressData? = null

    // BARU: Variabel untuk menyimpan ID kota yang dipilih
    private var addressId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLabelDropdown()
        setupDefaultCheckbox()

        binding.imgBack.setOnClickListener { finish() }

        // BARU: Tambahkan click listener untuk membuka bottom sheet kota
        binding.etCity.setOnClickListener { showFullScreenBottomSheet() }

        id = intent.getIntExtra("ADDRESS_ID", 0)
        if (id != 0) {
            viewModel.getAddressById(id)
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
            val city = binding.etCity.text.toString().trim() // Ambil nama kota dari EditText

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
                id_destination = addressId, // Gunakan ID kota yang baru/lama
                is_default = isSetAsDefault
            )

            if (id != 0) {
                viewModel.updateAddress(id, request)
            }
        }

        binding.btnDelAlamat.setOnClickListener {
            // Tampilkan dialog konfirmasi sebelum menghapus
            AlertDialog.Builder(this)
                .setTitle("Hapus Alamat")
                .setMessage("Apakah Anda yakin ingin menghapus alamat ini? Aksi ini tidak dapat dibatalkan.")
                .setPositiveButton("Ya, Hapus") { _, _ ->
                    // Jika pengguna menekan "Ya", panggil fungsi delete di ViewModel
                    viewModel.deleteAddress(id)
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Observer success & error
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

    private fun setData(addressData: AddressData) {
        // Simpan id destinasi awal
        addressId = addressData.id_destination

        binding.apply {
            etNamaPenerima.setText(addressData.recipient_name)
            etNoTelefon.setText(addressData.phone_number)
            actvLabelAlamat.setText(addressData.label ?: labelOptions.first(), false)
            etAlamatLengkap.setText(addressData.street)
            etNotes.setText(addressData.notes)
            etCity.setText(addressData.city) // MODIFIKASI: Tambahkan baris ini
        }

        isSetAsDefault = addressData.isDefault
        updateCheckboxVisual()

        if (addressData.isDefault) {
            binding.containerSetDefault.isClickable = false
            binding.containerSetDefault.isFocusable = false
        } else {
            binding.containerSetDefault.isClickable = true
            binding.containerSetDefault.isFocusable = true
        }
    }

    private fun setupDefaultCheckbox() {
        binding.containerSetDefault.setOnClickListener {
            isSetAsDefault = !isSetAsDefault
            updateCheckboxVisual()
        }
    }

    private fun updateCheckboxVisual() {
        if (isSetAsDefault) {
            binding.ivSetDefault.setImageResource(R.drawable.check_box_done)
        } else {
            binding.ivSetDefault.setImageResource(R.drawable.check_box_blank)
        }
    }

    // --- BARU: SALIN SEMUA FUNGSI DI BAWAH INI DARI ADD_ADDRESS_ACTIVITY ---

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
                addressId = id
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
                    cityAdapter.updateData(emptyList()) // Kosongkan list jika query pendek
                }
            }
        })
        dialog.show()
    }
}