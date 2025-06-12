package app.maul.koperasi.presentation.ui.cart

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.databinding.FragmentCartBinding
import app.maul.koperasi.model.cart.CartItem
import app.maul.koperasi.model.order.OrderDetail
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.checkout.CheckoutActivity
import app.maul.koperasi.presentation.ui.order.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class CartFragment : Fragment() {
    private var _binding : FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel by viewModels<CartViewModel>()

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        getUserCart()
        observeDeleteCart()
        observeUpdateCart()

        binding.btnBuy.setOnClickListener {
            val userId = Preferences.getId(requireActivity())
            val selectedItems = cartAdapter.getSelectedItems()

            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "Pilih produk terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calculate total price and prepare OrderDetail list
            val total = selectedItems.sumOf { it.product.price * it.quantity }
            val orderDetails = selectedItems.map { cartItem ->
                OrderDetail(
                    id = 0, // Assuming ID will be generated server-side
                    id_order = 0, // Placeholder, will be set after order creation
                    id_product = cartItem.product_id,
                    name_product = cartItem.product.name,
                    price = cartItem.product.price,
                    qty = cartItem.quantity,
                    createdAt = "", // Placeholder
                    updatedAt = "" // Placeholder
                )
            }

//            // Create OrderRequest object
//            val orderRequest = OrderRequest(
//                id_user = userId,
//                total = total.toDouble(),
//                payment_type = "bank_transfer",
//                bank_transfer = "bri",
//                shipping_method = "pending",
//                orderDetails = orderDetails,
//                customer = "",
//                phone_number = "",
//                address = ""
//            )

            val intent = Intent(requireContext(), CheckoutActivity::class.java)
            intent.putExtra("total", total.toDouble())
            intent.putParcelableArrayListExtra("orderDetails", orderDetails as ArrayList<out Parcelable>)
            startActivityForResult(intent, 10)

            // Call createOrder with the constructed OrderRequest
//            orderViewModel.createOrder(orderRequest)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCart()
    }

    private fun getUserCart() {
        val userId = Preferences.getId(requireActivity())
        cartViewModel.getUserCart(userId)
    }

    private fun observeCart() {
        cartViewModel.userCart.observe(viewLifecycleOwner, Observer { cartItems ->
            if (cartItems.isNullOrEmpty()) {
                binding.emptycart.visibility = View.VISIBLE
                binding.rvCart.visibility = View.GONE
            } else {
                binding.emptycart.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE

                if (::cartAdapter.isInitialized) {
                    cartAdapter.updateCart(cartItems) // Perbarui data
                } else {
                    setupRecyclerView(cartItems)
                }
            }
        })
    }

    private fun observeDeleteCart() {
        cartViewModel.deleteCartResponse.observe(viewLifecycleOwner, Observer { message ->
//            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            getUserCart()
        })
    }

    private fun observeUpdateCart() {
        cartViewModel.updateCartResponse.observe(viewLifecycleOwner, Observer { message ->
//            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            getUserCart()
        })
    }


    private fun setupRecyclerView(cartItems: List<CartItem>) {
        cartAdapter = CartAdapter(
            cartItems,
            onDeleteClick = { cartItem -> cartViewModel.deleteCartItem(cartItem.id) },
            onAddClick = { cartItem -> cartViewModel.addCartItem(cartItem.id, cartItem.quantity + 1) },
            onMinClick = { cartItem -> cartViewModel.minCartItem(cartItem.id, cartItem.quantity - 1) }
        )
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10 && RESULT_OK == resultCode){
            getUserCart()
        }
    }
}