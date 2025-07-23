package app.maul.koperasi.presentation.ui.historyorder

import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityInvoiceDetailBinding
import app.maul.koperasi.model.order.HistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class InvoiceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInvoiceDetailBinding
    private var currentInvoiceData: HistoryItem? = null

    companion object {
        const val EXTRA_INVOICE_DATA = "extra_invoice_data"
        private const val STORAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupClickListeners()

        val invoiceData: HistoryItem? = getInvoiceDataFromIntent()

        if (invoiceData != null) {
            currentInvoiceData = invoiceData
            displayInvoiceData(invoiceData)
        } else {
            Toast.makeText(this, "Gagal memuat data invoice", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // --- FUNGSI UTAMA YANG SUDAH DI-REFACTOR ---
    private fun displayInvoiceData(invoice: HistoryItem) {
        bindHeaderInfo(invoice)
        bindProductDetails(invoice)
        bindPaymentSummary(invoice)
    }

    // --- HELPER UNTUK MENGISI UI ---
    private fun bindHeaderInfo(invoice: HistoryItem) {
        binding.apply {
            tvInvoiceNumber.text = invoice.code
            tvPurchaseDate.text = getString(R.string.purchase_date_label, formatOrderDate(invoice.createdAt))
            tvBuyerName.text = getString(R.string.buyer_label, invoice.customerName)
            tvShippingAddress.text = invoice.address
        }
    }

    private fun bindProductDetails(invoice: HistoryItem) {
        binding.apply {
            // Karena HistoryItem hanya untuk 1 produk
            tvProductName.text = invoice.nameProduct
            tvProductQuantity.text = invoice.quantity.toString()

            val productTotalPrice = (invoice.price.toDoubleOrNull() ?: 0.0) * invoice.quantity
            tvProductTotal.text = formatRupiah(productTotalPrice)
        }
    }

    private fun bindPaymentSummary(invoice: HistoryItem) {
        binding.apply {
            val productTotalPrice = (invoice.price.toDoubleOrNull() ?: 0.0) * invoice.quantity
            val shippingCost = invoice.shippingCost.toDoubleOrNull() ?: 0.0
            val grandTotal = invoice.totalPrice.toDoubleOrNull() ?: 0.0

            valueSubtotal.text = formatRupiah(productTotalPrice)
            valueShipping.text = formatRupiah(shippingCost)
            valueGrandTotal.text = formatRupiah(grandTotal)

            // Logika untuk menampilkan nama bank
            var paymentMethodText = formatPaymentType(invoice.paymentType)
            if (invoice.paymentType.equals("bank_transfer", ignoreCase = true) && !invoice.bankName.isNullOrEmpty()) {
                paymentMethodText = invoice.bankName // Asumsi 'bankName' ada di HistoryItem
            }
            bankMethod.text = paymentMethodText
        }
    }

    // --- FUNGSI SETUP DAN HELPER LAINNYA ---

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.ivDownload.setOnClickListener {
            checkPermissionAndDownloadPdf()
        }
    }

    private fun checkPermissionAndDownloadPdf() {
        // Untuk Android 10 (API 29) ke atas, tidak perlu izin runtime untuk menyimpan ke folder publik
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createAndSavePdf()
            return
        }

        // Untuk Android 9 (API 28) ke bawah, kita perlu cek izin
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            createAndSavePdf()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createAndSavePdf()
            } else {
                Toast.makeText(this, "Izin ditolak. Tidak dapat menyimpan PDF.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAndSavePdf() {
        if (currentInvoiceData == null) {
            Toast.makeText(this, "Data invoice belum siap untuk di-download.", Toast.LENGTH_SHORT).show()
            return
        }

        // Lakukan operasi file di background thread
        lifecycleScope.launch(Dispatchers.IO) {
            val viewToPrint = binding.printableContentLayout // ID dari ConstraintLayout utama

            // Buat dokumen PDF
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(viewToPrint.width, viewToPrint.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            // Gambar view ke canvas PDF
            viewToPrint.draw(page.canvas)
            pdfDocument.finishPage(page)

            // Tentukan lokasi dan nama file di folder Downloads
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val fileName = "Invoice-${currentInvoiceData?.code}.pdf"
            val file = File(downloadsDir, fileName)

            try {
                // Tulis dokumen ke file
                FileOutputStream(file).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                pdfDocument.close()

                // Tampilkan pesan sukses di Main thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InvoiceDetailActivity, "PDF berhasil disimpan di folder Downloads", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InvoiceDetailActivity, "Gagal menyimpan PDF: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getInvoiceDataFromIntent(): HistoryItem? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_INVOICE_DATA, HistoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_INVOICE_DATA)
        }
    }

    private fun setupWindowInsets(){
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun formatPaymentType(type: String): String {
        return type.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    private fun formatOrderDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    private fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        format.maximumFractionDigits = 0
        return format.format(amount)
    }
}