package app.maul.koperasi.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.ProductListItemBinding
import app.maul.koperasi.model.product.Product
import app.maul.koperasi.utils.Constant
import com.bumptech.glide.Glide

class ProductAdapter (private var listProduct: List<Product>, private val listener: ProductItemListener): RecyclerView.Adapter<ProductAdapter.ProductsViewholder>(){
    inner class ProductsViewholder( val binding: ProductListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewholder {
        return ProductsViewholder(ProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ProductsViewholder, position: Int) {
        holder.binding.apply {
            Glide.with(holder.itemView)
                .load(Constant.BASE_URL + listProduct[position].images)
                .into(imageProduct)

            textTittle.text = listProduct[position].name
            val prefix = "Rp. "
            textPrice.text = "$prefix${listProduct[position].price}"
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(listProduct[position])
        }
    }

    override fun getItemCount(): Int {
        return listProduct.size
    }
}
interface ProductItemListener {
    fun onItemClick(product: Product)
}