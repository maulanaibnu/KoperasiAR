package app.maul.koperasi.presentation.ui.cart

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
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
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.checkout.CheckoutActivity
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
        setupRecyclerView(emptyList())
        observeCart()
        observeDeleteCart()
        observeUpdateCart()

        binding.btnBuy.setOnClickListener {
            val userId = Preferences.getId(requireActivity())

            // [FIX] Hapus baris duplikat di sini. Cukup gunakan variabel dari fragment.
            val selectedItemForCheckout = currentSelectedCartItem

            if (selectedItemForCheckout == null) {
                Toast.makeText(requireContext(), "Pilih produk terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pastikan product tidak null sebelum mengakses propertinya
            selectedItemForCheckout.product?.let { product ->
                val total = product.price * selectedItemForCheckout.quantity
                val orderDetails = ArrayList<OrderDetail>().apply {
                    add(
                        OrderDetail(
                            id = 0,
                            id_order = 0,
                            id_product = selectedItemForCheckout.product_id,
                            name_product = product.name,
                            image_url = product.images.firstOrNull() ?: "",
                            price = product.price,
                            qty = selectedItemForCheckout.quantity,
                            createdAt = "",
                            updatedAt = ""
                        )
                    )
                }

                val intent = Intent(requireContext(), CheckoutActivity::class.java)
                intent.putExtra("total", total.toDouble())
                intent.putParcelableArrayListExtra("orderDetails", orderDetails as ArrayList<out Parcelable>)
                startActivityForResult(intent, 10)
            } ?: run {
                // Tampilkan pesan error jika karena suatu hal produknya null
                Toast.makeText(requireContext(), "Data produk pada item ini tidak lengkap.", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // [FIX] Panggil fungsi untuk mengambil data di sini.
        // Ini akan berjalan setiap kali Anda kembali ke halaman keranjang.
        getUserCart()
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
            Log.d("CartFragment", "Observer terpanggil, jumlah item: ${cartItems.size}")
            if (cartItems.isNullOrEmpty()) {
                binding.emptycart.visibility = View.VISIBLE
                binding.rvCart.visibility = View.GONE
                updateTotalPriceDisplay(0) // Set total harga ke 0 jika keranjang kosong
            } else {
                binding.emptycart.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE
                cartAdapter.updateData(cartItems) // Perbarui data adapter
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