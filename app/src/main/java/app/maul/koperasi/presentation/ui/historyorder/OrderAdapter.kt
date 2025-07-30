// app/maul/koperasi/presentation/ui/historyorder/OrderAdapter.kt

package app.maul.koperasi.presentation.ui.historyorder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.R
import app.maul.koperasi.databinding.OrderListItemBinding
import app.maul.koperasi.model.order.HistoryItem
import com.bumptech.glide.Glide
import org.json.JSONArray
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private var orderList: List<HistoryItem>,
    private val listener: OrderItemListener
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    fun submitList(newList: List<HistoryItem>) {
        orderList = newList
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(val binding: OrderListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = OrderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        val firstDetail = order.orderDetails.firstOrNull()
        val product = firstDetail?.product

        holder.binding.apply {
            historyStatus.text = formatStatus(order.paymentStatus)
            historyDate.text = formatDisplayDate(order.createdAt)
            historyProductName.text = product?.name ?: "Produk tidak tersedia"

            // Memuat gambar produk dari JSON string
            val baseUrl = "https://koperasi.simagang.my.id/"
            val imagePath = product?.images?.let { parseImage(it) }

            if (!imagePath.isNullOrEmpty()) {
                val fullImageUrl = baseUrl + imagePath
                Log.d("ImageDebug_HistoryList", "Mencoba memuat gambar untuk produk '${product?.name}': $fullImageUrl")
                Glide.with(holder.itemView.context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.product)
                    .error(R.drawable.product)
                    .into(historyImgProduct)
            } else {

                Log.w("ImageDebug_HistoryList", "URL gambar KOSONG untuk produk: ${product?.name}")
                historyImgProduct.setImageResource(R.drawable.product)
            }
            totalBelanja.text = "Total Belanja"
            historyTotalPrice.text = formatRupiah(order.totalPrice.toDoubleOrNull() ?: 0.0)
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(order)
        }
    }

    private fun formatStatus(status: String): String {
        return when (status) {
            "pending" -> "Menunggu Pembayaran"
            "success" -> "Pesanan Selesai"
            "cancel" -> "Pesanan Dibatalkan"
            "expired" -> "Pembayaran Kedaluwarsa"
            "failure" -> "Pembayaran Gagal"
            else -> "Status Tidak Diketahui"
        }
    }

    private fun formatDisplayDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale("id", "ID"))
            outputFormat.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    // Fungsi bantu parsing string JSON array: ["img1.png", "img2.png"]
    private fun parseImage(images: String): String? {
        return try {
            val jsonArray = JSONArray(images)
            jsonArray.getString(0) // Ambil gambar pertama
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        fun formatRupiah(amount: Double): String {
            val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
            return "Rp ${formatter.format(amount)}"
        }
    }

    override fun getItemCount(): Int = orderList.size
}


interface OrderItemListener {
    fun onItemClick(order: HistoryItem)
}