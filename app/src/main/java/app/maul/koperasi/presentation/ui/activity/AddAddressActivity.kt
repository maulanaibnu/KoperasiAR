package app.maul.koperasi.presentation.ui.activity


import android.app.Activity
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
import app.maul.koperasi.databinding.ActivityAddAddressBinding
import app.maul.koperasi.databinding.BottomSheetCityBinding
import app.maul.koperasi.model.address.AddressRequest
import app.maul.koperasi.viewmodel.AddressViewModel
import app.maul.koperasi.viewmodel.RajaOngkirViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBinding
    private val viewModel: AddressViewModel by viewModels()

    private var selectedIdDestination: Int = 0

    private val labelOptions = listOf("Rumah", "Toko")

    private val rajaOngkirViewModel by viewModels<RajaOngkirViewModel>()

    private lateinit var cityAdapter: CityAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLabelDropdown()

        binding.imgBack.setOnClickListener { finish() }
        binding.etCity.setOnClickListener { showFullScreenBottomSheet() }


        binding.btnSaveAlamat.setOnClickListener {
            val recipientName = binding.etNamaPenerima.text?.toString()?.trim() ?: ""
            val phoneNumber = binding.etNoTelefon.text?.toString()?.trim() ?: ""
            val label = binding.actvLabelAlamat.text?.toString()?.trim() ?: ""
            val street = binding.etAlamatLengkap.text?.toString()?.trim() ?: ""
            val notes = binding.etNotes.text?.toString()?.trim() ?: ""
            val city = binding.etCity.text?.toString()?.trim() ?: ""
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
                id_destination = selectedIdDestination,
                isDefault = isDefault // Kirimkan nilai isDefault dari SwitchMaterial
            )

            Toast.makeText(this, "Menambahkan alamat baru...", Toast.LENGTH_SHORT).show()
            viewModel.createAddress(request)
        }

        lifecycleScope.launch {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@AddAddressActivity, it, Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                    viewModel.resetSuccess()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(this@AddAddressActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetError()
                }
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
            if(destinationList.isNotEmpty()){
                bottomSheetView.tvDesc.visibility = View.VISIBLE
                bottomSheetView.rvCity.visibility = View.VISIBLE
                cityAdapter.updateData(destinationList)
            }else{
                bottomSheetView.tvDesc.visibility = View.GONE
                bottomSheetView.rvCity.visibility = View.GONE
                Toast.makeText(this, "Data kosong", Toast.LENGTH_SHORT).show()
            }
        }

        rajaOngkirViewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error ?: "Terjadi kesalahan pada RajaOngkir", Toast.LENGTH_SHORT).show()
        }

        cityAdapter = CityAdapter(mutableListOf(), object : CityAdapter.onClickCity {
            override fun onSelectCity(label: String, id: Int) {
                selectedIdDestination = id
                binding.etCity.setText(label)
                dialog.dismiss()
            }
        })

        bottomSheetView.rvCity.apply {
            adapter = cityAdapter
            layoutManager = LinearLayoutManager(this@AddAddressActivity)
        }

        bottomSheetView.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.length >= 3) {
                    rajaOngkirViewModel.searchDestination(query)
                }
            }
        })

        dialog.show()
    }
}
