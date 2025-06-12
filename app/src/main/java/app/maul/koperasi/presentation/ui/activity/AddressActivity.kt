package app.maul.koperasi.presentation.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.R
import app.maul.koperasi.adapter.AddressAdapter
import app.maul.koperasi.databinding.ActivityAddressBinding
import app.maul.koperasi.presentation.ui.register.RegisterActivity
import app.maul.koperasi.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddressBinding
    private lateinit var addressAdapter: AddressAdapter
    private val viewModel: AddressViewModel by viewModels()

    // 1. ActivityResultLauncher untuk AddAddressActivity
    private val addAddressLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getAddresses() // refresh data setelah alamat baru ditambah
        }
    }
    private val editAddressLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getAddresses() // Refresh list setelah edit berhasil
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        addAddress()

        viewModel.getAddresses()
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(
            onSelect = { address ->
                // Aksi ketika alamat dipilih, misal simpan ke ViewModel/Preference
                viewModel.setSelectedId(address.id)
                addressAdapter.setSelectedId(address.id)
                Toast.makeText(this, "Alamat ${address.label} dipilih!", Toast.LENGTH_SHORT).show()

            },
            onChange = { address ->
                val intent = Intent(this, EditAddressActivity::class.java)
                intent.putExtra("ADDRESS_ID", address.id) // Kirim id alamat ke EditAddressActivity
                editAddressLauncher.launch(intent)
            }
        )
        binding.rvAddress.adapter = addressAdapter
    }

    private fun setupObservers() {
        // Observe daftar alamat
        lifecycleScope.launchWhenStarted {
            viewModel.addresses.collect { list ->
                addressAdapter.submitList(list)
                binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        // Observe loading
        lifecycleScope.launchWhenStarted {
            viewModel.loading.collect { isLoading ->
                if (isLoading) {
                    binding.shimmerContainer.visibility = View.VISIBLE
                    binding.shimmerContainer.startShimmer()
                    binding.rvAddress.visibility = View.GONE
                } else {
                    binding.shimmerContainer.visibility = View.GONE
                    binding.shimmerContainer.stopShimmer()
                    binding.rvAddress.visibility = View.VISIBLE
                }
            }
        }

        // Observe error
        lifecycleScope.launchWhenStarted {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(this@AddressActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetError()
                }
            }
        }

        // Observe sukses
        lifecycleScope.launchWhenStarted {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@AddressActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetSuccess()
                }
            }
        }
    }



    private fun addAddress(){
        binding.btnAddAlamat.setOnClickListener(){
            val intent = Intent(this, AddAddressActivity::class.java)
            addAddressLauncher.launch(intent)        }
    }
}