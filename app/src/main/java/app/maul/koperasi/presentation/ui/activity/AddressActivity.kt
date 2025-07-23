package app.maul.koperasi.presentation.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private val viewModel: AddressViewModel by viewModels()

    private val addAddressLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("AddressActivity", "addAddressLauncher callback dipicu. Result Code: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("AddressActivity", "Result OK dari Add/Edit Address. Memanggil viewModel.getAddresses().")
            viewModel.getAddresses()
        }else{
            Log.d("AddressActivity", "Result not OK dari Add/Edit Address.")
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
        lifecycleScope.launch {
            viewModel.addresses.collect { addresses ->
                val list = addresses ?: emptyList()
                addressAdapter.submitList(list)

                binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }

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

        lifecycleScope.launch {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(this@AddressActivity, it, Toast.LENGTH_LONG).show()
                    viewModel.resetError()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@AddressActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetSuccess()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btnAddAlamat.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            addAddressLauncher.launch(intent)
        }
    }
}
