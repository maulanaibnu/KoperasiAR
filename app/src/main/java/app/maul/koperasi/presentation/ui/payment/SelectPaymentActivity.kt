package app.maul.koperasi.presentation.ui.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.R
import app.maul.koperasi.adapter.PaymentAdapter
import app.maul.koperasi.databinding.ActivitySelectPaymentBinding
import app.maul.koperasi.model.payment.PaymentMethod

class SelectPaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.imgBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        // Membuat daftar metode pembayaran secara hardcode
        val paymentOptions = listOf(
            PaymentMethod("BCA Virtual Account", "bca", R.drawable.bcalogo),
            PaymentMethod("BRI Virtual Account", "bri", R.drawable.brikogo),
            PaymentMethod("Mandiri Virtual Account", "mandiri", R.drawable.mandirilogo),
            PaymentMethod("BNI Virtual Account", "bni", R.drawable.bnilogo),
            PaymentMethod("PERMATA Virtual Account", "permata", R.drawable.permata_logo),
            PaymentMethod("CIMB NIAGA Virtual Account", "cimb_niaga", R.drawable.cimb)
        )

        // Menginisialisasi adapter dan mengirim data yang dipilih ke CheckoutActivity
        val paymentAdapter = PaymentAdapter { selectedMethod ->
            // Mengirimkan data yang dipilih kembali ke CheckoutActivity
            val resultIntent = Intent().apply {
                putExtra("bank_code", selectedMethod.bankCode)
                putExtra("bank_name", selectedMethod.bankName)
                putExtra("bank_logo", selectedMethod.logoResId)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Mengirimkan list metode pembayaran ke adapter
        paymentAdapter.submitList(paymentOptions)

        // Mengatur RecyclerView dengan adapter
        binding.rvPaymentList.apply {
            layoutManager = LinearLayoutManager(this@SelectPaymentActivity)
            adapter = paymentAdapter
        }
    }

}