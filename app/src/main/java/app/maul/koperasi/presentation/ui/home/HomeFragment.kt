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
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel.getAllProducts()
        setName()
        showProductRV()
        return binding.root
    }

    private fun setName() {
        val name = Preferences.getName(requireActivity())
        println("name>>>>${name}")
        binding.tvNameUser.text = name
    }

    private fun showProductRV() {
        val mlayoutManager = GridLayoutManager(activity, 2)
        mlayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvProduct.layoutManager = mlayoutManager
        homeViewModel.products.observe(viewLifecycleOwner, Observer { product ->
            println(product)
            productAdapter = ProductAdapter(product, object : ProductItemListener {
                override fun onItemClick(product: Product) {
                    startActivity(Intent(activity, DetailProductActivity::class.java).apply {
                        putExtra("product_id", product.id)
                    })
                }
            })
            binding.rvProduct.adapter = productAdapter
        })
    }


}