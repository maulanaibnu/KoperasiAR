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
import java.text.NumberFormat
import java.util.ArrayList
import java.util.Locale

@AndroidEntryPoint
class CartFragment : Fragment() {
    private var _binding : FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel by viewModels<CartViewModel>()

    private lateinit var cartAdapter: CartAdapter
    private var currentSelectedCartItem: CartItem? = null
    private var currentTotalPrice: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        getUserCart()
        observeDeleteCart()
        observeUpdateCart()
        setupRecyclerView(emptyList())

        binding.btnBuy.setOnClickListener {
            val userId = Preferences.getId(requireActivity())
            val selectedItems = cartAdapter.getSelectedItems()
            val selectedItemForCheckout = currentSelectedCartItem

            if (selectedItemForCheckout == null) {
                Toast.makeText(requireContext(), "Pilih produk terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calculate total price and prepare OrderDetail list
            val total = selectedItemForCheckout.product.price * selectedItemForCheckout.quantity
            val orderDetails = ArrayList<OrderDetail>().apply {
                add(
                    OrderDetail(
                        id = 0, // Assuming ID will be generated server-side
                        id_order = 0, // Placeholder, will be set after order creation
                        id_product = selectedItemForCheckout.product_id,
                        name_product = selectedItemForCheckout.product.name,
                        image_url = selectedItemForCheckout.product.images,
                        price = selectedItemForCheckout.product.price,
                        qty = selectedItemForCheckout.quantity,
                        createdAt = "", // Placeholder
                        updatedAt = "" // Placeholder
                    )
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
                updateTotalPriceDisplay(0) // Set total harga ke 0 jika keranjang kosong
            } else {
                binding.emptycart.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE
                cartAdapter.updateCart(cartItems) // Perbarui data adapter
            }
        })
    }

    private fun observeDeleteCart() {
        cartViewModel.deleteCartResponse.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            getUserCart()
        })
    }

    private fun observeUpdateCart() {
        cartViewModel.updateCartResponse.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            getUserCart()
        })
    }


    private fun setupRecyclerView(cartItems: List<CartItem>) {
        cartAdapter = CartAdapter(
            cartItems,
            onDeleteClick = { cartItem -> cartViewModel.deleteCartItem(cartItem.id) },
            onAddClick = { cartItem -> cartViewModel.addCartItem(cartItem.id, cartItem.quantity + 1) },
            onMinClick = { cartItem -> cartViewModel.minCartItem(cartItem.id, cartItem.quantity - 1) },
            onItemSelectedChange = { selectedItem, totalPrice ->
                currentSelectedCartItem = selectedItem
                currentTotalPrice = totalPrice
                updateTotalPriceDisplay(totalPrice)
            }
        )
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    private fun updateTotalPriceDisplay(totalPrice: Int) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 0
        var formattedTotalPriceString = formatter.format(totalPrice)

        if (formattedTotalPriceString.startsWith("Rp") && !formattedTotalPriceString.startsWith("Rp ")) {

            formattedTotalPriceString = "Rp. " + formattedTotalPriceString.substring(2)
        }

        binding.tvTotalPrice.text = formattedTotalPriceString
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10 && RESULT_OK == resultCode){
            getUserCart()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}