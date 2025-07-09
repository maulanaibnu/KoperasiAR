// app/maul/koperasi/presentation/ui/historyorder/OrderAdapter.kt

package app.maul.koperasi.presentation.ui.historyorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.OrderListItemBinding
import app.maul.koperasi.model.order.HistoryItem
import com.bumptech.glide.Glide
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

        holder.binding.apply {
            // Mengatur teks status pesanan berdasarkan paymentStatus
            val statusText = when (order.paymentStatus) {
                "0" -> "Menunggu Pembayaran"
                "1" -> "Pesanan Diproses"
                "2" -> "Pesanan Selesai"
                else -> "Status Tidak Diketahui"
            }
            historyStatus.text = statusText

            // Memformat dan menampilkan tanggal pembuatan pesanan
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.isLenient = false // Pastikan parsing ketat
                val date = inputFormat.parse(order.createdAt)
                val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale("id", "ID")) // Format: 12 Des 2024
                hidtoryDate.text = outputFormat.format(date)
            } catch (e: Exception) {
                // Fallback jika format tanggal tidak sesuai
                hidtoryDate.text = order.createdAt
                e.printStackTrace() // Cetak stack trace untuk debugging
            }

            // Menampilkan nama produk
            historyProductName.text = order.nameProduct

            // Memuat gambar produk menggunakan Glide
            Glide.with(holder.itemView.context)
                .load(order.product.images) // URL gambar dari objek produk
                .placeholder(app.maul.koperasi.R.drawable.product) // Gambar placeholder saat loading
                .error(app.maul.koperasi.R.drawable.product) // Gambar jika terjadi error
                .into(historyImgProduct)

            // Menampilkan total belanja dan total harga
            totalBelanja.text = "Total Belanja" // Label
            historyTotalPrice.text = "Rp ${order.totalPrice}" // Nilai total harga
        }

        // Mengatur listener klik untuk setiap item dalam RecyclerView
        holder.itemView.setOnClickListener {
            listener.onItemClick(order) // Memanggil callback onItemClick pada listener
        }
    }

    override fun getItemCount(): Int = orderList.size
}

// Interface untuk mendefinisikan callback saat item riwayat pesanan diklik
interface OrderItemListener {
    fun onItemClick(order: HistoryItem)
}