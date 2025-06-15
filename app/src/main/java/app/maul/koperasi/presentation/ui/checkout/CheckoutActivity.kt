package app.maul.koperasi.presentation.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.MainActivity
import app.maul.koperasi.databinding.ActivityCheckoutBinding
import app.maul.koperasi.model.order.OrderDetail
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.activity.AddressActivity
import app.maul.koperasi.presentation.ui.order.OrderViewModel
import app.maul.koperasi.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var orderDetails: List<OrderDetail>
    private val orderViewModel by viewModels<OrderViewModel>()
    private var userId: Int = 0
    private var total: Double = 0.0

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cardAlamatPengiriman.setOnClickListener {
            resultLauncher.launch(Intent(this, AddressActivity::class.java))
        }

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val street = result.data?.getStringExtra("street")
                val name = result.data?.getStringExtra("name")
                val label = result.data?.getStringExtra("label")
                binding.tvDetailAlamat.text = street
                binding.tvReceiptName.text = name
                binding.tvLabelAlamat.text = label

            }
        }

        userId = Preferences.getId(this@CheckoutActivity)
        // Retrieve data from Intent
        total = intent.getDoubleExtra("total", 0.0)
        orderDetails =
            intent.getParcelableArrayListExtra<OrderDetail>("orderDetails") ?: emptyList()

        observeOrderResponse()
        // Set up Submit Button
//        binding.btnSubmitOrder.setOnClickListener {
//            var paymentType = binding.spinnerPaymentType.selectedItem.toString()
//            val bankTransfer = binding.spinnerBankTransfer.selectedItem.toString()
//            val shippingMethod = binding.spinnerShippingMethod.selectedItem.toString()
//            val customerName = binding.etCustomerName.text.toString()
//            val phoneNumber = binding.etPhoneNumber.text.toString()
//            val address = binding.etAddress.text.toString()
//
//            // Validate inputs
//            if (customerName.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
//                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            if(paymentType == "Bank Transfer"){
//                paymentType = "bank_transfer"
//            }
//
//            // Create OrderRequest
//            val orderRequest = OrderRequest(
//                id_user = userId,
//                total = total,
//                payment_type = paymentType,
//                shipping_method = shippingMethod,
//                bank_transfer = bankTransfer,
//                customer_name = customerName,
//                phone_number = phoneNumber,
//                address = address,
//                orderDetails = orderDetails
//            )
//
//            // Call ViewModel to create the order
//            orderViewModel.createOrder(orderRequest)
//
//            // Show success message or navigate back
//
//        }

    }

    fun observeOrderResponse() {
        orderViewModel.orderResponse.observe(this) { response ->
            if (response!!.status == "CREATED") {
                Toast.makeText(this, "Order submitted successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                setResult(RESULT_OK, null)
                finish()
            }

        }
    }
}