package app.maul.koperasi.presentation.ui.historyorder

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityHistoryDetailBinding
import app.maul.koperasi.model.order.HistoryItem
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imgBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val orderHistory: HistoryItem? = intent.getParcelableExtra(EXTRA_ORDER_HISTORY)

        orderHistory?.let { order ->
            val statusText = when (order.paymentStatus) {
                "0" -> "Pesanan: Menunggu Pembayaran"
                "1" -> "Pesanan: Diproses"
                "2" -> "Pesanan: Selesai"
                else -> "Pesanan: Status Tidak Diketahui"
            }
            binding.statushistoryOrder.text = statusText
            binding.orderCodeo.text = order.code

            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.isLenient = false
                val date = inputFormat.parse(order.createdAt)
                val outputFormat = SimpleDateFormat("dd MMMM<x_bin_67>", Locale("id", "ID"))
                binding.historyDateOrder.text = outputFormat.format(date)
            } catch (e: Exception) {
                binding.historyDateOrder.text = order.createdAt
                e.printStackTrace()
            }

            binding.historyProductName.text = order.nameProduct
            Glide.with(this)
                .load(order.product.images)
                .placeholder(R.drawable.product)
                .error(R.drawable.product)
                .into(binding.historyImgProduct)

            binding.totalQuantityOrder.text = order.quantity.toString()
            binding.priceProductHistory.text = "Rp ${order.price}"

            binding.shippingCourirName.text = order.shippingMethod
            binding.nameReceiptOrder.text = order.customerName
            binding.phoneReceiptOrder.text = order.phoneNumber
            binding.adressReceiptOrder.text = order.address

            binding.totalShippingCostHistory.text = "Rp ${order.shippingCost}"

            val totalProductCost = order.price.toDouble() * order.quantity
            binding.totalProductCostHistory.text = "Rp ${String.format(Locale("id", "ID"), "%,.0f", totalProductCost)}"

            binding.totalPaymentHistory.text = "Rp ${order.totalPrice}"

        } ?: run {
            Toast.makeText(this, "Detail transaksi tidak dapat dimuat.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        const val EXTRA_ORDER_HISTORY = "extra_order_history"
    }
}