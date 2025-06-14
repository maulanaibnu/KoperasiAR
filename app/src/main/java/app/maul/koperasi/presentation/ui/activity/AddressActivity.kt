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
        addAddress()

        binding.imgBack.setOnClickListener { finish() }

        viewModel.getAddresses()
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(
            onSelect = { address ->
                viewModel.setSelectedId(address.id)
                addressAdapter.setSelectedId(address.id)
                Toast.makeText(this, "Alamat ${address.label} dipilih!", Toast.LENGTH_SHORT).show()
            },
            onChange = { address ->
                val intent = Intent(this, AddAddressActivity::class.java)
                intent.putExtra("ADDRESS_ID", address.id)
                addAddressLauncher.launch(intent)
            }
        )

        binding.rvAddress.adapter = addressAdapter
        binding.rvAddress.layoutManager = LinearLayoutManager(this)

    }

    private fun setupObservers() {


        // Observe daftar alamat
        lifecycleScope.launchWhenStarted {

            viewModel.addresses.collect { list ->
                Toast.makeText(this@AddressActivity, "$list", Toast.LENGTH_SHORT).show()
                binding.rvAddress.visibility = View.VISIBLE
                addressAdapter.submitList(list)
                binding.emptyState.visibility = if (list?.isEmpty() == true) View.VISIBLE else View.GONE
            }
        }

        // Observe loading
        lifecycleScope.launchWhenStarted {
            viewModel.loading.collect { isLoading ->
                binding.shimmerContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
                if (isLoading) {
                    binding.shimmerContainer.startShimmer()
                    binding.rvAddress.visibility = View.GONE
                } else {
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

        // Observe success
        lifecycleScope.launchWhenStarted {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@AddressActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetSuccess()
                }
            }
        }
    }

    private fun addAddress() {
        binding.btnAddAlamat.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            addAddressLauncher.launch(intent)
        }
    }
}
