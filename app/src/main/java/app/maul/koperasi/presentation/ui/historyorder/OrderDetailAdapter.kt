package app.maul.koperasi.presentation.ui.historyorder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.R
import app.maul.koperasi.databinding.DetailHistoryItemBinding
import app.maul.koperasi.model.order.OrderDetail
import com.bumptech.glide.Glide
import org.json.JSONArray
import java.text.NumberFormat
import java.util.Locale

class OrderDetailAdapter(private val items: List<OrderDetail>) :
    RecyclerView.Adapter<OrderDetailAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(val binding: DetailHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = DetailHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = items[position]
        val product = item.product

        holder.binding.apply {
            historyProductName.text = item.name_product
            totalQuantityOrder.text = item.qty.toString()
            priceProductHistory.text = formatRupiah(item.price.toDouble())

            val imagePath = product?.images?.let { parseImage(it) }
            val baseUrl = "https://koperasi.simagang.my.id/"

            if (!imagePath.isNullOrEmpty()) {
                val fullImageUrl = baseUrl + imagePath

                Log.d("ImageDebug_Detail", "Mencoba memuat gambar untuk produk '${item.name_product}': $fullImageUrl")

                Glide.with(holder.itemView.context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.product)
                    .error(R.drawable.product)
                    .into(historyImgProduct)
            } else {
                Log.w("ImageDebug_Detail", "URL gambar KOSONG untuk produk: ${item.name_product}")
                historyImgProduct.setImageResource(R.drawable.product)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp ${formatter.format(amount)}"
    }

    private fun parseImage(images: String): String? {
        if (images.isBlank() || images == "[]") return null
        return try {
            val jsonArray = JSONArray(images)
            jsonArray.getString(0)
        } catch (e: Exception) {
            null
        }
    }
}