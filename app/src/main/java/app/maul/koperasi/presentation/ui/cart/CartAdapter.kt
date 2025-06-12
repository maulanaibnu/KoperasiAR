package app.maul.koperasi.presentation.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.ProductListCartBinding
import app.maul.koperasi.model.cart.CartItem
import app.maul.koperasi.utils.Constant
import com.bumptech.glide.Glide

class CartAdapter(
    var cartItems: List<CartItem>,
    private val onDeleteClick: (CartItem) -> Unit,
    private val onAddClick: (CartItem) -> Unit,
    private val onMinClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val selectedItems = ArrayList<CartItem>()

    inner class CartViewHolder(val binding: ProductListCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ProductListCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val product = cartItem.product

        holder.binding.apply {
            Glide.with(holder.itemView)
                .load(
                    Constant.BASE_URL + product.images)
                .into(imgProduct)
            tvProductName.text = product.name
            tvProductPrice.text = "Rp. ${product.price}"
            tvQuantity.text = cartItem.quantity.toString()

            // Atur checkbox sesuai status item dalam set
            cbProduct.isChecked = selectedItems.contains(cartItem)

            // Event checkbox
            cbProduct.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(cartItem)
                } else {
                    selectedItems.remove(cartItem)
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
    fun getSelectedItems(): List<CartItem> {
        return selectedItems.toList()
    }

    fun updateCart(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}