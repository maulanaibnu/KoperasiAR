package app.maul.koperasi.presentation.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.ProductListCartBinding
import app.maul.koperasi.model.cart.CartItem
import app.maul.koperasi.utils.Constant
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    var cartItems: List<CartItem>,
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
        val product = cartItem.product

        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 0
        var formattedPriceString = formatter.format(product.price)

        if (formattedPriceString.startsWith("Rp") && !formattedPriceString.startsWith("Rp ")) {

            formattedPriceString = "Rp. " + formattedPriceString.substring(2)
        }
        holder.binding.apply {
            Glide.with(holder.itemView)
                .load(
                    Constant.BASE_URL + product.images)
                .into(imgProduct)
            tvProductName.text = product.name

            tvProductPrice.text = formattedPriceString
            tvQuantity.text = cartItem.quantity.toString()


            cbProduct.isChecked = (cartItem == selectedItem)

            cbProduct.setOnCheckedChangeListener(null)
            cbProduct.isChecked = (cartItem == selectedItem)


            cbProduct.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (selectedItem != cartItem) {

                        val previousSelectedItemPosition = selectedItemPosition
                        selectedItem = cartItem
                        selectedItemPosition = holder.adapterPosition
                        if (previousSelectedItemPosition != RecyclerView.NO_POSITION) {
                            notifyItemChanged(previousSelectedItemPosition)
                        }

                        onItemSelectedChange(selectedItem, calculateTotalPrice())
                    }
                } else {
                    if (selectedItem == cartItem) {
                        selectedItem = null
                        selectedItemPosition = RecyclerView.NO_POSITION
                        onItemSelectedChange(null, 0)
                    }
                }
            }
        }

        holder.binding.imgDelete.setOnClickListener {
            onDeleteClick(cartItem)
        }

        holder.binding.btnPlus.setOnClickListener {
            onAddClick(cartItem)
        }
        holder.binding.btnMinus.setOnClickListener {
            if(cartItem.quantity > 1){
                onMinClick(cartItem)
            }else {
                onDeleteClick(cartItem)
            }
        }
    }

    // Mengambil item yang dipilih
    fun getSelectedItems(): CartItem? {
        return selectedItem
    }

    private fun calculateTotalPrice(): Int {
        return selectedItem?.let { item -> item.product.price * item.quantity } ?: 0
    }

    fun updateCart(newCartItems: List<CartItem>) {
        val currentSelectedItem = selectedItem
        selectedItem = null
        selectedItemPosition = RecyclerView.NO_POSITION

        if (currentSelectedItem != null) {
            val newIndex = newCartItems.indexOfFirst { it.id == currentSelectedItem.id }
            if (newIndex != -1) {
                selectedItem = newCartItems[newIndex]
                selectedItemPosition = newIndex
            }
        }

        cartItems = newCartItems
        notifyDataSetChanged()
        onItemSelectedChange(selectedItem, calculateTotalPrice())
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}