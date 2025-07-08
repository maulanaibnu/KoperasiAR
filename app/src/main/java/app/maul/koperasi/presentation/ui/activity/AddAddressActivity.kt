package app.maul.koperasi.presentation.ui.activity


import android.app.Activity
import android.content.res.Resources
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
import app.maul.koperasi.model.address.AddressData
import app.maul.koperasi.model.address.AddressRequest
import app.maul.koperasi.viewmodel.AddressViewModel
import app.maul.koperasi.viewmodel.RajaOngkirViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBinding
    // INI INJECT VIEWMODEL DARI HILT!
    private val viewModel: AddressViewModel by viewModels()

    private var  id = 0

    private var addressId = 0

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


        binding.btnSaveAlamat.setOnClickListener {
            val recipientName = binding.etNamaPenerima.text?.toString()?.trim() ?: ""
            val phoneNumber = binding.etNoTelefon.text?.toString()?.trim() ?: ""
            val label = binding.actvLabelAlamat.text?.toString()?.trim() ?: ""
            val street = binding.etAlamatLengkap.text?.toString()?.trim() ?: ""
            val notes = binding.etNotes.text?.toString()?.trim() ?: ""
            val city = binding.etCity.text?.toString()?.trim() ?: ""

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
                id_destination = addressId
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
            Toast.makeText(this, error ?: "Error apa kek", Toast.LENGTH_SHORT).show()
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
