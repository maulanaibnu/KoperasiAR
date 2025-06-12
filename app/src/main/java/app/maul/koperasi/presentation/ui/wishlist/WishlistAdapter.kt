package app.maul.koperasi.presentation.ui.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.WishlistListItemBinding
import app.maul.koperasi.model.wishlist.Wishlist
import app.maul.koperasi.utils.Constant
import com.bumptech.glide.Glide

class WishlistAdapter (private var listWishlist: List<Wishlist>, private val listener: WishlistItemListener): RecyclerView.Adapter<WishlistAdapter.WishlistsViewholder>(){
    inner class WishlistsViewholder( val binding: WishlistListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistsViewholder {
        return WishlistsViewholder(WishlistListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: WishlistsViewholder, position: Int) {
        holder.binding.apply {
            Glide.with(holder.itemView)
                .load(Constant.BASE_URL + listWishlist[position].product.images)
                .into(imageProduct)

            textTittle.text = listWishlist[position].product.name
            val prefix = "Rp. "
            textPrice.text = "$prefix${listWishlist[position].product.price}"
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(listWishlist[position])
        }
    }

    override fun getItemCount(): Int {
        return listWishlist.size
    }
}
interface WishlistItemListener {
    fun onItemClick(wishlist: Wishlist)
}