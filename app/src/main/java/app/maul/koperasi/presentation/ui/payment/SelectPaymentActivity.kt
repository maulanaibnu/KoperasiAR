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
        val currentBankCode = intent.getStringExtra("CURRENT_BANK_CODE")
        setupRecyclerView(currentBankCode)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.imgBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView(currentBankCode: String?) {
        val paymentOptions = listOf(
            PaymentMethod("BCA Virtual Account", "bca", R.drawable.bcalogo),
            PaymentMethod("BRI Virtual Account", "bri", R.drawable.brikogo),
            PaymentMethod("Mandiri Virtual Account", "mandiri", R.drawable.mandirilogo),
            PaymentMethod("BNI Virtual Account", "bni", R.drawable.bnilogo),
            PaymentMethod("PERMATA Virtual Account", "permata", R.drawable.permata_logo),
            PaymentMethod("CIMB NIAGA Virtual Account", "cimb_niaga", R.drawable.cimb)

        )

        // Kirim currentBankCode ke constructor Adapter
        val paymentAdapter = PaymentAdapter(currentBankCode) { selectedMethod ->
            // Bagian ini sudah benar, tidak perlu diubah
            val resultIntent = Intent().apply {
                putExtra("bank_code", selectedMethod.bankCode)
                putExtra("bank_name", selectedMethod.bankName)
                putExtra("bank_logo", selectedMethod.logoResId)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        paymentAdapter.submitList(paymentOptions)

        binding.rvPaymentList.apply {
            layoutManager = LinearLayoutManager(this@SelectPaymentActivity)
            adapter = paymentAdapter
        }
    }


}