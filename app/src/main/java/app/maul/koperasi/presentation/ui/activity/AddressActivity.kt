package app.maul.koperasi.presentation.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.adapter.AddressAdapter
import app.maul.koperasi.databinding.ActivityAddressBinding
import app.maul.koperasi.model.address.AddressData
import app.maul.koperasi.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private lateinit var addressAdapter: AddressAdapter

    // INI INJECT VIEWMODEL DARI HILT!
    private val viewModel: AddressViewModel by viewModels()

    private val addAddressLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getAddresses()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.getAddresses()

    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(
            onSetDefault = { address ->
                viewModel.setDefaultAddress(address.id)

            },
            onChange = { address ->
                val intent = Intent(this, EditAddressActivity::class.java)
                intent.putExtra("ADDRESS_ID", address.id)
                addAddressLauncher.launch(intent)
            }
        )

        binding.rvAddress.apply {
            adapter = addressAdapter
            layoutManager = LinearLayoutManager(this@AddressActivity)
        }

    }

    private fun setupObservers() {
        // Mengamati perubahan pada daftar alamat
        lifecycleScope.launch {
            viewModel.addresses.collect { addresses ->
                val list = addresses ?: emptyList()
                addressAdapter.submitList(list)

                // Menampilkan atau menyembunyikan tampilan "empty state"
                binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE


            }
        }

        // Mengamati status loading
        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                binding.shimmerContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.rvAddress.visibility = if (isLoading) View.GONE else View.VISIBLE
                if (isLoading) {
                    binding.shimmerContainer.startShimmer()
                } else {
                    binding.shimmerContainer.stopShimmer()
                }
            }
        }

        // Mengamati pesan error
        lifecycleScope.launch {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(this@AddressActivity, it, Toast.LENGTH_LONG).show()
                    viewModel.resetError() // Reset agar toast tidak muncul lagi
                }
            }
        }

        // Mengamati pesan sukses
        lifecycleScope.launch {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@AddressActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetSuccess() // Reset agar toast tidak muncul lagi
                }
            }
        }
    }



    private fun setupClickListeners() {
        // Tombol kembali
        binding.imgBack.setOnClickListener {
            finish()
        }

        // Tombol tambah alamat baru
        binding.btnAddAlamat.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            addAddressLauncher.launch(intent)
        }
    }


}
