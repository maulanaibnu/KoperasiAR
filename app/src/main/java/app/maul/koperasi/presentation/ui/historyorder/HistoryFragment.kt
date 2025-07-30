package app.maul.koperasi.presentation.ui.historyorder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.databinding.FragmentHistoryBinding
import app.maul.koperasi.model.order.HistoryItem
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.order.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment(), OrderItemListener {

    private val orderViewModel by viewModels<OrderViewModel>()
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRecyclerView()

        val userId = Preferences.getId(requireContext())
        if (userId != -1) {
            orderViewModel.getAllOrders()
        } else {
            Toast.makeText(requireContext(), "ID Pengguna tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }

        observeViewModel()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddress.layoutManager = layoutManager
        orderAdapter = OrderAdapter(emptyList(), this)
        binding.rvAddress.adapter = orderAdapter
    }

    private fun observeViewModel() {
        orderViewModel.orders.observe(viewLifecycleOwner) { orders ->
            if (orders.isNotEmpty()) {
                orderAdapter.submitList(orders)
            } else {
                Toast.makeText(requireContext(), "Tidak ada transaksi ditemukan.", Toast.LENGTH_SHORT).show()
                orderAdapter.submitList(emptyList())
            }
        }

        orderViewModel.isLoading.observe(viewLifecycleOwner) { _ ->
        }

        orderViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onItemClick(order: HistoryItem) {
        val intent = Intent(requireContext(), HistoryDetailActivity::class.java).apply {
            // Hanya kirim ID transaksi
            putExtra(HistoryDetailActivity.EXTRA_ORDER_ID, order.id)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}