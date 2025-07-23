package app.maul.koperasi.presentation.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.databinding.FragmentHomeBinding
import app.maul.koperasi.model.product.Product
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.activity.DetailProductActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val homeViewModel by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    // [FIX 1] Inisialisasi adapter di sini, awalnya dengan list kosong
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupAdapter() // Panggil fungsi setup adapter
        showProductRV() // Panggil fungsi untuk observasi

        homeViewModel.getAllProducts()
        setName()
        return binding.root
    }

    // [FIX 2] Buat fungsi terpisah untuk setup adapter sekali saja
    private fun setupAdapter() {
        productAdapter = ProductAdapter(emptyList(), object : ProductItemListener {
            override fun onItemClick(product: Product) {
                startActivity(Intent(activity, DetailProductActivity::class.java).apply {
                    putExtra("product_id", product.id)
                })
            }
        })

        val mlayoutManager = GridLayoutManager(activity, 2)
        binding.rvProduct.layoutManager = mlayoutManager
        binding.rvProduct.adapter = productAdapter
    }

    private fun setName() {
        val name = Preferences.getName(requireActivity())
        binding.tvNameUser.text = name
    }

    private fun showProductRV() {
        // [FIX 3] Di dalam observer, HANYA panggil fungsi updateData
        homeViewModel.products.observe(viewLifecycleOwner, Observer { product ->
            println("DATA DITERIMA: $product")
            productAdapter.updateData(product)
        })
    }
}