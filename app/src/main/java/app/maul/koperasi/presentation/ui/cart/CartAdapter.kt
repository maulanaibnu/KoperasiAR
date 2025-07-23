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
    private val onItemSelectedChange: (CartItem?, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var selectedItem: CartItem? = null
    private var selectedItemPosition: Int = RecyclerView.NO_POSITION

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
        holder.binding.cbProduct.isChecked = cartItem.id == selectedItem?.id
        holder.binding.cbProduct.setOnCheckedChangeListener { _, isChecked ->
            val previousSelectedItemPosition = selectedItemPosition

            if (isChecked) {
                selectedItem = cartItem
                selectedItemPosition = holder.adapterPosition
                // Batalkan centang pada item sebelumnya
                if (previousSelectedItemPosition != RecyclerView.NO_POSITION && previousSelectedItemPosition != selectedItemPosition) {
                    notifyItemChanged(previousSelectedItemPosition)
                }
            } else {
                if (selectedItem?.id == cartItem.id) {
                    selectedItem = null
                    selectedItemPosition = RecyclerView.NO_POSITION
                }
            }
            // Kirim perubahan ke fragment
            onItemSelectedChange(selectedItem, calculateTotalPrice())
        }
    }

    fun getSelectedItem(): CartItem? {
        return selectedItem
    }

    private fun calculateTotalPrice(): Int {
        return selectedItem?.product?.let { product -> product.price * (selectedItem?.quantity ?: 0) } ?: 0
    }

    fun updateData(newCartItems: List<CartItem>) {
        val currentSelectedItemId = selectedItem?.id

        cartItems = newCartItems

        selectedItem = if (currentSelectedItemId != null) {
            newCartItems.find { it.id == currentSelectedItemId }
        } else {
            null
        }

        selectedItemPosition = if (selectedItem != null) {
            cartItems.indexOf(selectedItem)
        } else {
            RecyclerView.NO_POSITION
        }

        // 5. Beri tahu RecyclerView untuk menggambar ulang dirinya
        notifyDataSetChanged()

        // 6. Kirim total harga yang BARU dan BENAR ke fragment
        onItemSelectedChange(selectedItem, calculateTotalPrice())
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