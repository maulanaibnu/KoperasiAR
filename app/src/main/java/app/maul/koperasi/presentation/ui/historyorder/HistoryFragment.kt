package app.maul.koperasi.presentation.ui.historyorder

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
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.order.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private val orderViewModel by viewModels<OrderViewModel>()
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

//        setupRecyclerView()

        orderViewModel.getAllOrders(Preferences.getId(requireContext()))

        return binding.root
    }

//    private fun setupRecyclerView() {
//        val layoutManager = LinearLayoutManager(requireContext())
//        binding.rvAddress.layoutManager = layoutManager
//        orderViewModel.orders.observe(viewLifecycleOwner) { orders ->
//            println(orders)
//            orderAdapter = OrderAdapter(orders)
//            binding.rvAddress.adapter = orderAdapter
//        }
//    }
}