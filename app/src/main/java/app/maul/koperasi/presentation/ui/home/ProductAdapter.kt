package app.maul.koperasi.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.ProductListItemBinding
import app.maul.koperasi.model.product.Product
import app.maul.koperasi.utils.Constant
import com.bumptech.glide.Glide
import java.text.DecimalFormat

class ProductAdapter(private var listProduct: List<Product>, private val listener: ProductItemListener) :
    RecyclerView.Adapter<ProductAdapter.ProductsViewholder>() {

    inner class ProductsViewholder(val binding: ProductListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewholder {
        return ProductsViewholder(
            ProductListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductsViewholder, position: Int) {
        val product = listProduct[position]
        holder.binding.apply {
            // [FIX 1] Ambil gambar pertama dari list
            if (product.images.isNotEmpty()) {
                Glide.with(holder.itemView)
                    .load(Constant.BASE_URL + product.images[0])
                    .into(imageProduct)
            }

            textTittle.text = product.name
            stoctProduct.text = product.quantity.toString()

            val formatter = DecimalFormat("#,###")
            val formattedPrice = formatter.format(product.price).replace(',', '.')
            textPrice.text = "Rp. $formattedPrice"
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(product)
        }
    }

    override fun getItemCount(): Int {
        return listProduct.size
    }

    // [FIX 2] Tambahkan fungsi untuk update data
    fun updateData(newProducts: List<Product>) {
        this.listProduct = newProducts
        notifyDataSetChanged() // Memberi tahu adapter bahwa data telah berubah
    }
}

interface ProductItemListener {
    fun onItemClick(product: Product)
}