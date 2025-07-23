package app.maul.koperasi.presentation.ui.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.R
import app.maul.koperasi.databinding.WishlistListItemBinding
import app.maul.koperasi.model.wishlist.Wishlist
import app.maul.koperasi.utils.Constant
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class WishlistAdapter(
    private var listWishlist: List<Wishlist>,
    private val listener: WishlistItemListener
) : RecyclerView.Adapter<WishlistAdapter.WishlistsViewholder>() {

    inner class WishlistsViewholder(val binding: WishlistListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistsViewholder {
        return WishlistsViewholder(
            WishlistListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WishlistsViewholder, position: Int) {
        val currentWishlist = listWishlist[position]

        currentWishlist.product?.let { product ->
            // Jika produk ADA, tampilkan detailnya
            holder.binding.apply {
                val productImages = product.images
                if (productImages.isNotEmpty()) {
                    Glide.with(holder.itemView)
                        .load(Constant.BASE_URL + productImages[0])
                        .into(imageProduct)
                } else {
                    // Tampilkan gambar placeholder jika produk tidak punya gambar
                    imageProduct.setImageResource(R.drawable.baseline_error_outline_24) // Ganti dengan drawable Anda
                }

                textTittle.text = product.name

                val priceAsNumber = product.price.toString().toDoubleOrNull() ?: 0.0
                textPrice.text = formatRupiah(priceAsNumber)
            }

            holder.itemView.setOnClickListener {
                listener.onItemClick(currentWishlist)
            }

        } ?: run {
            // [FIX] Jika produk NULL, tampilkan pesan bahwa produk tidak tersedia
            // Kode ini akan berjalan jika currentWishlist.product adalah null
            holder.binding.apply {
                imageProduct.setImageResource(R.drawable.baseline_error_outline_24) // Ganti dengan drawable Anda
                textTittle.text = "Produk tidak tersedia"
                textPrice.text = ""
            }
            // Matikan klik pada item ini
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        return listWishlist.size
    }
    fun updateData(newWishlist: List<Wishlist>) {
        this.listWishlist = newWishlist
        notifyDataSetChanged()
    }
    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        // Hapus ,00 di akhir jika tidak ada desimal
        numberFormat.maximumFractionDigits = 0
        // Mengubah format default "Rp10.000" menjadi "Rp. 10.000"
        return numberFormat.format(number).replace("Rp", "Rp. ")
    }
}

interface WishlistItemListener {
    fun onItemClick(wishlist: Wishlist)
}