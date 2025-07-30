package app.maul.koperasi.presentation.ui.checkout


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import app.maul.koperasi.MainActivity
import app.maul.koperasi.databinding.ActivityPaymentBinding
import app.maul.koperasi.presentation.ui.order.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale


@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private val viewModel: OrderViewModel by viewModels()
    private var virtualAccount: String? = null
    private var expiredTime: String? = null
    private var totalPayment: String? = null
    private var bankName: String? = null
    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiveAndDisplayPaymentData()
        setupClickListeners()
        setupObservers()
    }
    private fun setupObservers() {
        viewModel.cancelStatus.observe(this) { result ->
            result.onSuccess { message ->

                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }.onFailure {

                Toast.makeText(this, "Gagal membatalkan: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun receiveAndDisplayPaymentData() {
        virtualAccount = intent.getStringExtra("virtual_account")
        expiredTime = intent.getStringExtra("expired")
        totalPayment = intent.getStringExtra("total_payment")
        bankName = intent.getStringExtra("bank_name")
        orderId = intent.getStringExtra("order_id")
        binding.vaNumber.text = virtualAccount ?: "N/A"

        val formattedTotalPayment = totalPayment?.toDoubleOrNull()?.let {
            formatRupiah(it)
        } ?: "Rp. 0"
        binding.totalPaymentOrder.text = formattedTotalPayment
        binding.namePaymentType.text = bankName ?: "Virtual Account"

        if (!expiredTime.isNullOrEmpty()) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm z", Locale("in", "ID")) // Tambah WIB atau zona waktu
                val date = inputFormat.parse(expiredTime)
                binding.dateExpiredOrder.text = date?.let { outputFormat.format(it) } ?: "N/A"
            } catch (e: Exception) {
                binding.dateExpiredOrder.text = "Waktu kedaluwarsa tidak valid"
                e.printStackTrace()
            }
        } else {
            binding.dateExpiredOrder.text = "N/A"
        }

        Toast.makeText(this, "Jangan lupa salin Nomor Virtual Account Anda!", Toast.LENGTH_LONG).show()
    }

    private fun setupClickListeners() {
        binding.imgBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        binding.tvTapToCopy.setOnClickListener {
            copyTextToClipboard(virtualAccount)
            Toast.makeText(this, "Nomor Virtual Account disalin!", Toast.LENGTH_SHORT).show()
        }
        binding.btnSelesai.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        binding.btnCancelOrder.setOnClickListener {
            showCancelConfirmationDialog()
        }
    }
    private fun showCancelConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Pembatalan")
        builder.setMessage("Apakah kamu ingin membatalkan pesanan?")

        builder.setPositiveButton("Iya") { dialog, _ ->
            // Jika "Iya" diklik, panggil ViewModel untuk membatalkan
            orderId?.let {
                viewModel.cancelOrder(it)
            } ?: run {
                Toast.makeText(this, "Error: Order ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Tidak") { dialog, _ ->
            // Jika "Tidak" diklik, tutup dialog saja
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun copyTextToClipboard(text: String?) {
        if (!text.isNullOrEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Nomor Virtual Account", text)
            clipboard.setPrimaryClip(clip)
        }
    }

    fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp. ${formatter.format(amount)}"
    }
}