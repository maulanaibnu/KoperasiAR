package app.maul.koperasi.presentation.ui.detailProduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.maul.koperasi.R
import app.maul.koperasi.databinding.FragmentDetailProductBinding
import app.maul.koperasi.preference.Preferences
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailProductFragment : Fragment() {
    private val detailProductViewModel by viewModels<DetailProductViewModel>()
    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        backToHome()
        getDetailProduct()
        attachDetailProduct()
        addToCart()
        observeCartResponse()
        return binding.root
    }

    private fun getDetailProduct() {
        val productId = arguments?.getInt("product_id") ?: 0
        println("productId: ${productId}")
        detailProductViewModel.fetchProductDetail(productId, Preferences.getId(requireActivity()))
    }

    private fun backToHome() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailProductFragment_to_homeFragment)
        }
    }

    private fun attachDetailProduct() {
        detailProductViewModel.productDetail.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                val product = it.data
                println(product)
                val prefix = "Rp. "
                Glide.with(this).load(product.images).into(binding.IvProduct)
                binding.ProductPrice.text = "$prefix${product.price}"
                binding.ProductName.text = product.name
                binding.ProductSold.text = " ${product.soldCount} Terjual"
                binding.ProductDesc.text = product.description
            }
        })
    }

    private fun addToCart() {
        binding.btnAddCart.setOnClickListener {
            val userId = Preferences.getId(requireActivity())
            val quantity = 1
            detailProductViewModel.productDetail.value?.data?.let { product ->
                detailProductViewModel.addCart(product.id, userId, quantity)
            }
        }
    }

    private fun observeCartResponse() {
        detailProductViewModel.cartResponse.observe(viewLifecycleOwner) { response ->
            if (response.status == 201) {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}