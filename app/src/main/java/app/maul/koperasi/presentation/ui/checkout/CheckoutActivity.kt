package app.maul.koperasi.presentation.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import app.maul.koperasi.model.ongkir.ShippingOption
import app.maul.koperasi.model.ongkir.TariffData
import app.maul.koperasi.model.order.OrderDetail
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.activity.AddressActivity
import app.maul.koperasi.presentation.ui.courir.SelectCourirActivity
import app.maul.koperasi.presentation.ui.order.OrderViewModel
import app.maul.koperasi.viewmodel.AddressViewModel
import app.maul.koperasi.viewmodel.RajaOngkirViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var orderDetails: List<OrderDetail>
    private val orderViewModel by viewModels<OrderViewModel>()
    private var userId: Int = 0
    private var total: Double = 0.0

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private val viewModel: AddressViewModel by viewModels()

    private val kingViewModel: RajaOngkirViewModel by viewModels()

    var street : String? = ""
    var name : String? = ""
    var label : String? = ""
    var idDestination : Int = 0

    var selectShipping : ShippingOption? = null

    private var isFromShippingSelection = false

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


                val shippingOption = result.data?.getParcelableExtra<ShippingOption>("shippingOption")

                if (shippingOption != null) {
                    isFromShippingSelection = true
                    binding.tvCourier.text = "${shippingOption.shippingName} (${shippingOption.serviceName})"
                    binding.tvEstimation.text = formatRupiah(shippingOption.shippingCost)
                    binding.tvTotalShipping.text = formatRupiah(shippingOption.shippingCost)
                    viewModel.getAddresses()
                    selectShipping = shippingOption
                }else{
                    street = result.data?.getStringExtra("street") ?: ""
                    name = result.data?.getStringExtra("name") ?: ""
                    label = result.data?.getStringExtra("label") ?: ""
                    binding.tvDetailAlamat.text = street
                    binding.tvReceiptName.text = name
                    binding.tvLabelAlamat.text = label
                }

            }
        }

        userId = Preferences.getId(this@CheckoutActivity)
        // Retrieve data from Intent
        total = intent.getDoubleExtra("total", 0.0)
        orderDetails =
            intent.getParcelableArrayListExtra<OrderDetail>("orderDetails") ?: emptyList()

        binding.tvTotalItemCo.text = formatRupiah(orderDetails.sumOf { it.price })

        viewModel.getAddresses()
        setupObservers()

        observeOrderResponse()
        observerCalculate()

        binding.cardShippingOption.setOnClickListener {
            resultLauncher.launch(Intent(this, SelectCourirActivity::class.java).also{
                it.putExtra("receiver_id",idDestination)
                it.putExtra("price",orderDetails.sumOf { it.price }.toLong())
            })
        }
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

    private fun setupObservers() {
        // Mengamati perubahan pada daftar alamat
        lifecycleScope.launch {
            viewModel.addresses.collect { addresses ->
                idDestination = addresses?.firstOrNull { it.isDefault }?.id_destination ?: 0
                binding.tvDetailAlamat.text = addresses?.firstOrNull { it.isDefault }?.street ?: ""
                binding.tvReceiptName.text = addresses?.firstOrNull { it.isDefault }?.recipient_name ?: ""
                binding.tvLabelAlamat.text = addresses?.firstOrNull { it.isDefault }?.label ?: ""
                if (!isFromShippingSelection) {
                    kingViewModel.calculateTariff(
                        shipperId = 73209,
                        receiverId = idDestination,
                        weight = 1,
                        itemValue = orderDetails.sumOf { it.price }.toLong(),
                        cod = "no"
                    )
                }

                isFromShippingSelection = false
            }
        }

        // Mengamati pesan error
        lifecycleScope.launch {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(this@CheckoutActivity, it, Toast.LENGTH_LONG).show()
                    viewModel.resetError() // Reset agar toast tidak muncul lagi
                }
            }
        }

        // Mengamati pesan sukses
        lifecycleScope.launch {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@CheckoutActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetSuccess() // Reset agar toast tidak muncul lagi
                }
            }
        }
    }

    private fun observerCalculate(){
        kingViewModel.tariffResult.observe(this) {
            val data = getCheapestShippingOption(it)
            binding.tvCourier.text = data?.shippingName ?: "Gagal mendapatkan nama shipping"
            binding.tvEstimation.text = formatRupiah(data?.shippingCost ?: 0)
            selectShipping = data
        }
        kingViewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error ?: "Error apa kek", Toast.LENGTH_SHORT).show()
        }
    }

    fun getCheapestShippingOption(tariffData: TariffData): ShippingOption? {
        return (tariffData.reguler)
            .minByOrNull { it.shippingCost }
    }

    fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp.${formatter.format(amount)}"
    }

    override fun onResume() {
        super.onResume()
    }
}