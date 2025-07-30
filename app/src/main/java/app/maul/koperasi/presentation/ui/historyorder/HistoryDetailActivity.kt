package app.maul.koperasi.presentation.ui.historyorder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityHistoryDetailBinding
import app.maul.koperasi.model.order.HistoryItem
import app.maul.koperasi.model.order.OrderDetail
import app.maul.koperasi.presentation.ui.order.OrderViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding
    // 2. Panggil ViewModel yang sudah Anda buat
    private val viewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Ambil HANYA ID dari Intent
        val orderId = intent.getIntExtra(EXTRA_ORDER_ID, -1)

        // 4. Panggil ViewModel untuk mengambil data lengkap dari server
        if (orderId != -1) {
            viewModel.getTransactionById(orderId)
        } else {
            Toast.makeText(this, "ID Transaksi tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupBackButton()

        // 5. Panggil fungsi untuk mengamati LiveData
        observeViewModel()

        binding.tvinvoice.setOnClickListener {
            // Ambil ID dari intent yang diterima activity ini
            val orderId = intent.getIntExtra(EXTRA_ORDER_ID, -1)
            if (orderId != -1) {
                val intent = Intent(this, InvoiceDetailActivity::class.java).apply {
                    putExtra(InvoiceDetailActivity.EXTRA_INVOICE_ID, orderId)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "ID Transaksi tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 6. FUNGSI BARU UNTUK MENGAMATI VIEWMODEL
    private fun observeViewModel() {
        // Observer untuk data detail transaksi
        viewModel.transactionDetail.observe(this) { order ->
            order?.let {
                bindOrderInfo(it)
                setupRecyclerView(it.orderDetails)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty() && viewModel.transactionDetail.value == null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupBackButton() {
        binding.imgBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }



    private fun bindOrderInfo(order: HistoryItem) {
        if (order.paymentStatus.equals("success", ignoreCase = true)) {

            binding.tvinvoice.visibility = View.VISIBLE
        } else {
            binding.tvinvoice.visibility = View.GONE
        }
        binding.statushistoryOrder.text = mapStatus(order.paymentStatus)
        binding.orderCodeo.text = order.code
        binding.historyDateOrder.text = formatOrderDate(order.createdAt)
        binding.shippingCourirName.text = order.shippingMethod
        binding.nameReceiptOrder.text = order.customerName
        binding.phoneReceiptOrder.text = order.phoneNumber ?: "-"
        binding.adressReceiptOrder.text = order.address
        binding.totalShippingCostHistory.text = formatRupiah(order.shippingCost.toDoubleOrNull() ?: 0.0)
        binding.totalPaymentHistory.text = formatRupiah(order.totalPrice.toDoubleOrNull() ?: 0.0)

        val totalHargaBarang = order.orderDetails.sumOf { it.price * it.qty }
        binding.totalProductCostHistory.text = formatRupiah(totalHargaBarang.toDouble())

        val jumlahProduk = order.orderDetails.size
        binding.statusProduck.text = "Detail Produk ($jumlahProduk) "

        binding.totalhargaBarang.text = "Total Harga Barang ($jumlahProduk Produk)"
    }

    private fun setupRecyclerView(orderDetails: List<OrderDetail>) {
        val adapter = OrderDetailAdapter(orderDetails)
        binding.rvHistoryDetail.apply {
            layoutManager = LinearLayoutManager(this@HistoryDetailActivity)
            this.adapter = adapter
        }
    }

    private fun mapStatus(status: String): String {
        return when (status) {
            "pending" -> "Menunggu Pembayaran"
            "success" -> "Selesai"
            "cancel" -> "Dibatalkan"
            "expired" -> "Kedaluwarsa"
            "failure" -> "Gagal"
            else -> "Status Tidak Diketahui"
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
            dateString
        }
    }

    private fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp ${formatter.format(amount)}"
    }

    companion object {
        const val EXTRA_ORDER_ID = "extra_order_id"
    }
}
