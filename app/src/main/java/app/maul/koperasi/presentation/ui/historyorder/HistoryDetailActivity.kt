package app.maul.koperasi.presentation.ui.historyorder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityHistoryDetailBinding
import app.maul.koperasi.model.order.HistoryItem
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding
    private var transactionId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("HistoryDetailActivity", "onCreate dipanggil.")
        Log.d("HistoryDetailActivity", "Mencoba mengambil data dari Intent.")

        // Cetak semua key yang ada di dalam intent extras
        Log.d("HistoryDetailActivity", "Intent Extras Keys: ${intent.extras?.keySet()}")

        val orderHistory: HistoryItem? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_ORDER_HISTORY, HistoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_ORDER_HISTORY)
        }
        Log.d("HistoryDetailActivity", "Hasil getParcelableExtra: $orderHistory")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imgBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvinvoice.setOnClickListener {
            if (orderHistory != null) {
                val intent = Intent(this, InvoiceDetailActivity::class.java).apply {
                    // Kirim SELURUH OBJEK HistoryItem
                    putExtra(InvoiceDetailActivity.EXTRA_INVOICE_DATA, orderHistory)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Data transaksi tidak lengkap", Toast.LENGTH_SHORT).show()
            }
        }


        orderHistory?.let { historyItem -> // 'historyItem' sekarang adalah objek HistoryItem yang benar

            // --- Mengisi data dari Order (HistoryItem) ---
            val statusText = when (historyItem.paymentStatus) {
                "pending" -> "Menunggu Pembayaran"
                "success" -> "Selesai"
                "cancel" -> "Dibatalkan"
                "expired" -> "Kedaluwarsa"
                "failure" -> "Gagal"
                else -> "Status Tidak Diketahui"
            }
            binding.statushistoryOrder.text = statusText
            binding.orderCodeo.text = historyItem.code
            binding.historyDateOrder.text = formatOrderDate(historyItem.createdAt)
            binding.totalQuantityOrder.text = historyItem.quantity.toString()
            binding.shippingCourirName.text = historyItem.shippingMethod
            binding.nameReceiptOrder.text = historyItem.customerName
            binding.phoneReceiptOrder.text = historyItem.phoneNumber
            binding.adressReceiptOrder.text = historyItem.address
            binding.totalShippingCostHistory.text = formatRupiah(historyItem.shippingCost.toDoubleOrNull() ?: 0.0)
            binding.totalPaymentHistory.text = formatRupiah(historyItem.totalPrice.toDoubleOrNull() ?: 0.0)

            // --- Mengisi data dari Produk yang ada di dalam Order ---
            // Gunakan let lagi untuk keamanan jika produk bisa null
            historyItem.product?.let { product ->
                binding.historyProductName.text = product.name // Ini benar, dari objek product
                binding.priceProductHistory.text = formatRupiah(product.price.toDouble()) // Ambil harga dari detail produk

                // Logika untuk menampilkan gambar produk
                val baseUrl = "https://koperasi.simagang.my.id/"
                if (product.images.isNotEmpty()) {
                    val fullImageUrl = baseUrl + product.images // Sudah string langsung
                    Glide.with(this)
                        .load(fullImageUrl)
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product)
                        .into(binding.historyImgProduct)
                } else {
                    binding.historyImgProduct.setImageResource(R.drawable.product)
                }

                // Kalkulasi total harga produk
                val totalProductCostCalculated = product.price.toDouble() * historyItem.quantity
                binding.totalProductCostHistory.text = formatRupiah(totalProductCostCalculated)
            }

        } ?: run {
            // Ini akan berjalan jika orderHistory adalah null
            Toast.makeText(this, "Detail transaksi tidak dapat dimuat.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun formatOrderDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        return try {

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)


            val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            e.printStackTrace()
            dateString
        }
    }


    private fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp ${formatter.format(amount)}"
    }

    companion object {
        const val EXTRA_ORDER_HISTORY = "extra_order_history"
    }
}