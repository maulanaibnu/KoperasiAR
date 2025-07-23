package app.maul.koperasi.presentation.ui.cart

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ProductListCartBinding
import app.maul.koperasi.model.cart.CartItem
import app.maul.koperasi.utils.Constant
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    // List sekarang private untuk enkapsulasi yang lebih baik
    private var cartItems: List<CartItem>,
    private val onDeleteClick: (CartItem) -> Unit,
    private val onAddClick: (CartItem) -> Unit,
    private val onMinClick: (CartItem) -> Unit,
    private val onItemSelectedChange: (List<CartItem>, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val selectedItems = mutableSetOf<CartItem>()

    inner class CartViewHolder(val binding: ProductListCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ProductListCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        // [FIX UTAMA] Gunakan 'let' untuk null safety. Kode hanya berjalan jika product tidak null.
        cartItem.product?.let { product ->
            // Jika produk ada, bind data produk
            bindProductData(holder, cartItem, product)
        } ?: run {
            // Jika produk null, bind tampilan untuk produk yang hilang
            bindNullProductData(holder, cartItem)
        }

        // Logika untuk checkbox, berada di luar karena bergantung pada 'cartItem', bukan 'product'
        setupCheckbox(holder, cartItem)
    }

    private fun bindProductData(holder: CartViewHolder, cartItem: CartItem, product: app.maul.koperasi.model.cart.ProductItem) {
        holder.binding.apply {
            // Memuat gambar
            if (product.images.isNotEmpty()) {
                Glide.with(holder.itemView)
                    .load(Constant.BASE_URL + product.images[0])
                    .placeholder(R.drawable.product)
                    .error(R.drawable.product)
                    .into(imgProduct)
            } else {
                imgProduct.setImageResource(R.drawable.product)
            }

            // Mengisi UI
            tvProductName.text = product.name
            tvProductPrice.text = formatRupiah(product.price)
            tvQuantity.text = cartItem.quantity.toString()

            // Mengaktifkan kembali tombol jika sebelumnya dinonaktifkan
            btnPlus.isEnabled = true
            btnMinus.isEnabled = true

            // Setup listener
            setupListeners(holder, cartItem)
        }
    }

    fun getSelectedItems(): List<CartItem> {
        return selectedItems.toList()
    }

    private fun bindNullProductData(holder: CartViewHolder, cartItem: CartItem) {
        holder.binding.apply {
            imgProduct.setImageResource(R.drawable.baseline_error_outline_24) // Gambar untuk item rusak
            tvProductName.text = "Produk tidak tersedia"
            tvProductPrice.text = ""
            tvQuantity.text = "0"

            // Nonaktifkan tombol yang tidak relevan
            btnPlus.isEnabled = false
            btnMinus.isEnabled = false

            // Hanya fungsikan tombol hapus
            imgDelete.setOnClickListener { onDeleteClick(cartItem) }
        }
    }

    private fun setupListeners(holder: CartViewHolder, cartItem: CartItem) {
        holder.binding.imgDelete.setOnClickListener { onDeleteClick(cartItem) }
        holder.binding.btnPlus.setOnClickListener { onAddClick(cartItem) }
        holder.binding.btnMinus.setOnClickListener {
            if (cartItem.quantity > 1) {
                onMinClick(cartItem)
            } else {
                onDeleteClick(cartItem)
            }
        }
    }

    private fun setupCheckbox(holder: CartViewHolder, cartItem: CartItem) {
        holder.binding.cbProduct.setOnCheckedChangeListener(null)
        holder.binding.cbProduct.isChecked = selectedItems.contains(cartItem)
        holder.binding.cbProduct.setOnCheckedChangeListener { _, isChecked ->
            Log.d("TESTED","$cartItem")
            if (isChecked) {
                selectedItems.add(cartItem)
            } else {
                selectedItems.remove(cartItem)
            }
            // Kirim semua item yang dipilih + total harga ke fragment
            onItemSelectedChange(selectedItems.toList(), calculateTotalPrice())
        }
    }


    private fun calculateTotalPrice(): Int {
        return selectedItems.sumOf { it.product?.price?.times(it.quantity) ?: 0 }
    }

    fun updateData(newCartItems: List<CartItem>) {
        cartItems = newCartItems

        val selectedIds = selectedItems.map { it.id }
        selectedItems.clear()
        selectedItems.addAll(newCartItems.filter { selectedIds.contains(it.id) })

        notifyDataSetChanged()
        onItemSelectedChange(selectedItems.toList(), calculateTotalPrice())
    }


    override fun getItemCount(): Int {
        Log.d("CartAdapter", "getItemCount mengembalikan: ${cartItems.size}")
        return cartItems.size
    }

    private fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 0
        var formattedPriceString = formatter.format(amount)
        if (formattedPriceString.startsWith("Rp") && !formattedPriceString.startsWith("Rp ")) {
            return "Rp. " + formattedPriceString.substring(2)
        }
        return formattedPriceString
    }
}