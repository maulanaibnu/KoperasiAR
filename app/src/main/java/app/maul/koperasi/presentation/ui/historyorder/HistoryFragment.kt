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
import app.maul.koperasi.R
import app.maul.koperasi.databinding.FragmentHistoryBinding
import app.maul.koperasi.model.order.HistoryItem
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.order.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment(), OrderItemListener { // Pastikan Anda mengimplementasikan OrderItemListener

    private val orderViewModel by viewModels<OrderViewModel>()
    private var _binding: FragmentHistoryBinding? = null
    // Menggunakan !! karena binding akan dijamin tidak null setelah onCreateView
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

        // Setup RecyclerView dengan adapter
        setupRecyclerView()

        // Ambil ID pengguna dari Preferences
        val userId = Preferences.getId(requireContext())
        if (userId != -1) {
            // Panggil API untuk mendapatkan semua pesanan pengguna
            orderViewModel.getAllOrders(userId)
        } else {
            // Tampilkan pesan jika ID pengguna tidak valid
            Toast.makeText(requireContext(), "ID Pengguna tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }

        // Amati LiveData dari ViewModel untuk memperbarui UI
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddress.layoutManager = layoutManager
        // Inisialisasi adapter dengan list kosong dan listener fragment ini
        orderAdapter = OrderAdapter(emptyList(), this)
        binding.rvAddress.adapter = orderAdapter
    }

    private fun observeViewModel() {
        // Mengamati daftar pesanan dari ViewModel
        orderViewModel.orders.observe(viewLifecycleOwner) { orders ->
            if (orders.isNotEmpty()) {
                // Perbarui data di adapter menggunakan metode submitList
                orderAdapter.submitList(orders)
            } else {
                // Jika tidak ada transaksi, tampilkan pesan dan kosongkan daftar
                Toast.makeText(requireContext(), "Tidak ada transaksi ditemukan.", Toast.LENGTH_SHORT).show()
                orderAdapter.submitList(emptyList())
            }
        }

        // Mengamati status loading dari ViewModel (opsional, untuk ProgressBar)
        orderViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Contoh: binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Mengamati pesan error dari ViewModel
        orderViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Implementasi metode onItemClick dari OrderItemListener
    override fun onItemClick(order: HistoryItem) {
        // Buat Intent untuk membuka HistoryDetailActivity
        val intent = Intent(requireContext(), HistoryDetailActivity::class.java).apply {
            // Kirim objek HistoryItem yang diklik sebagai Parcelable
            putExtra(HistoryDetailActivity.EXTRA_ORDER_HISTORY, order)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Mengosongkan binding untuk mencegah memory leaks
        _binding = null
    }
}