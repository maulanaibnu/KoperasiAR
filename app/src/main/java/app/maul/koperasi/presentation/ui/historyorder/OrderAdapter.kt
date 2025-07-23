// app/maul/koperasi/presentation/ui/historyorder/OrderAdapter.kt

package app.maul.koperasi.presentation.ui.historyorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.R
import app.maul.koperasi.databinding.OrderListItemBinding
import app.maul.koperasi.model.order.HistoryItem
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    // orderList sekarang private, yang merupakan praktik baik untuk enkapsulasi data
    private var orderList: List<HistoryItem>,
    private val listener: OrderItemListener
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // Metode publik untuk memperbarui data adapter
    fun submitList(newList: List<HistoryItem>) {
        orderList = newList
        // Memberi tahu RecyclerView bahwa set data telah berubah
        // Ini akan memicu RecyclerView untuk menggambar ulang item-itemnya
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(val binding: OrderListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            OrderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        // [FIX UTAMA] Lakukan pengecekan null pada objek 'product'
        order.product?.let { product ->
            // --- BLOK INI HANYA BERJALAN JIKA PRODUK TIDAK NULL ---
            holder.binding.apply {
                // Mengatur status
                historyStatus.text = formatStatus(order.paymentStatus)

                // Mengatur tanggal
                historyDate.text = formatDisplayDate(order.createdAt)

                // Mengatur nama produk
                historyProductName.text = product.name // Ambil dari objek produk

                // Memuat gambar produk
                val baseUrl = "https://koperasi.simagang.my.id/"
                if (product.images.isNotEmpty()) {
                    Glide.with(holder.itemView.context)
                        .load(baseUrl + product.images) // sudah String!
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product)
                        .into(historyImgProduct)
                } else {
                    historyImgProduct.setImageResource(R.drawable.product)
                }

                // Menampilkan total harga
                totalBelanja.text = "Total Belanja"
                historyTotalPrice.text = formatRupiah(order.totalPrice.toDoubleOrNull() ?: 0.0)
            }
        } ?: run {
//            // --- BLOK INI HANYA BERJALAN JIKA PRODUK ADALAH NULL ---
//            holder.binding.apply {
//                historyStatus.text = formatStatus(order.paymentStatus)
//                historyDate.text = formatDisplayDate(order.createdAt)
//                historyProductName.text = "Produk telah dihapus"
//                historyImgProduct.setImageResource(R.drawable.) // Gambar placeholder untuk item rusak
//                totalBelanja.text = "Total Belanja"
//                historyTotalPrice.text = formatRupiah(order.totalPrice.toDoubleOrNull() ?: 0.0)
//            }
        }


        holder.itemView.setOnClickListener {
            listener.onItemClick(order)
        }
    }

    // Tambahkan helper function ini di dalam OrderAdapter untuk merapikan kode
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
    companion object {
        fun formatRupiah(amount: Double): String {
            val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
            return "Rp ${formatter.format(amount)}" // Menggunakan "Rp " tanpa titik
        }
    }
    override fun getItemCount(): Int = orderList.size
}

interface OrderItemListener {
    fun onItemClick(order: HistoryItem)
}