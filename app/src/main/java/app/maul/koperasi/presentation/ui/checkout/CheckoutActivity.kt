package app.maul.koperasi.presentation.ui.checkout


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.MainActivity
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityCheckoutBinding
import app.maul.koperasi.model.ongkir.ShippingOption
import app.maul.koperasi.model.ongkir.TariffData
import app.maul.koperasi.model.order.OrderDetail
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.model.payment.PaymentMethod
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.activity.AddressActivity
import app.maul.koperasi.presentation.ui.courir.SelectCourirActivity
import app.maul.koperasi.presentation.ui.order.OrderViewModel
import app.maul.koperasi.presentation.ui.payment.SelectPaymentActivity
import app.maul.koperasi.viewmodel.AddressViewModel
import app.maul.koperasi.viewmodel.RajaOngkirViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

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
    private var selectedPaymentMethod: PaymentMethod? = null
    var countProduct = 1
    private var isFromShippingSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                val bankCode = data?.getStringExtra("bank_code")
                val shippingOption = data?.getParcelableExtra<ShippingOption>("shippingOption")
                when {
                    bankCode != null -> {
                        val bankName = data.getStringExtra("bank_name") ?: ""
                        val bankLogoResId = data.getIntExtra("bank_logo", 0) ?: 0

                        val newlySelectedMethod = PaymentMethod(
                            bankName = bankName,
                            bankCode = bankCode,
                            logoResId = bankLogoResId,
                            isSelected = true
                        )
                        selectedPaymentMethod = newlySelectedMethod

                        binding.selectPaymentMethod.visibility = View.GONE
                        binding.llSelectedPaymentDisplay.visibility = View.VISIBLE
                        updateSelectedPaymentDisplay()
                        Log.d("CheckoutActivity", "Bank default diinisialisasi: ${selectedPaymentMethod?.bankName} (${selectedPaymentMethod?.bankCode})")
                    }
                    shippingOption != null -> {
                        isFromShippingSelection = true
                        selectShipping = shippingOption
                        binding.tvCourier.text = "${shippingOption.shippingName} (${shippingOption.serviceName})"
                        binding.tvEstimation.text = formatRupiah(shippingOption.shippingCost)
                        binding.tvTotalShipping.text = formatRupiah(shippingOption.shippingCost)
                        updateTotalPrice()
                    }
                    else -> {
                        val street = data?.getStringExtra("street") ?: ""
                        val name = data?.getStringExtra("name") ?: ""
                        val label = data?.getStringExtra("label") ?: ""
                        binding.tvDetailAlamat.text = street
                        binding.tvReceiptName.text = name
                        binding.tvLabelAlamat.text = label
                    }
                }
            }
        }

        userId = Preferences.getId(this@CheckoutActivity)
        total = intent.getDoubleExtra("total", 0.0)
        orderDetails = intent.getParcelableArrayListExtra<OrderDetail>("orderDetails") ?: emptyList()

        Log.d("CheckoutActivity", "OrderDetail diterima dari Intent: $orderDetails")
        if (orderDetails.isNotEmpty()) {
            Log.d("CheckoutActivity", "ID Produk pertama di OrderDetails: ${orderDetails[0].id}")
        } else {
            Log.d("CheckoutActivity", "OrderDetails kosong setelah diterima dari Intent.")
        }
        if (orderDetails.isNotEmpty()) {
            val firstItem = orderDetails[0]

            binding.tvCOProductName.text = firstItem.name_product
            binding.tvCoProductPrice.text = formatRupiah(firstItem.price)

            countProduct = firstItem.qty
            binding.tvQuantityProduct.text = countProduct.toString()

            updateTotalPrice()
            val baseUrl = "https://koperasi.simagang.my.id/"
            val fullImageUrl = baseUrl + firstItem.image_url
            Glide.with(this)
                .load(fullImageUrl)
                .placeholder(R.drawable.product)
                .error(R.drawable.baseline_error_outline_24)
                .into(binding.imgCoPRoduct)

            binding.btnPlus.setOnClickListener {
                countProduct++
                binding.tvQuantityProduct.text = countProduct.toString()
                updateTotalPrice()
            }

            binding.btnMinus.setOnClickListener {
                if (countProduct > 1) {
                    countProduct--
                    binding.tvQuantityProduct.text = countProduct.toString()
                    updateTotalPrice()
                }
            }
        } else {
            Toast.makeText(this, "Terjadi kesalahan produk", Toast.LENGTH_SHORT).show()

        }

        viewModel.getAddresses()
        setupObservers()
        setupPaymentSelection()
        observerCalculate()

        binding.cardAlamatPengiriman.setOnClickListener {
            resultLauncher.launch(Intent(this, AddressActivity::class.java))
        }
        binding.cardShippingOption.setOnClickListener {
            resultLauncher.launch(Intent(this, SelectCourirActivity::class.java).also{
                it.putExtra("receiver_id", idDestination)
                val totalProductPrice = (orderDetails.getOrNull(0)?.price ?: 0) * countProduct
                it.putExtra("price", totalProductPrice.toLong())
            })
        }
        binding.btnPayNow.setOnClickListener {

            if (selectShipping == null) {
                Toast.makeText(this, "Mohon pilih opsi pengiriman terlebih dahulu.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val currentAddressDetail = binding.tvDetailAlamat.text.toString()
            val receiptNameFromAddress = binding.tvReceiptName.text.toString()
            val labelFromAddress = binding.tvLabelAlamat.text.toString()

            if (currentAddressDetail.isNullOrEmpty() ||
                receiptNameFromAddress.isNullOrEmpty() ||
                labelFromAddress.isNullOrEmpty()) {
                Toast.makeText(this, "Mohon lengkapi alamat pengiriman.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentType = "bank_transfer"
            val bankCode = selectedPaymentMethod?.bankCode ?: ""

            val shippingMethodName = selectShipping?.shippingName ?: (selectShipping?.serviceName ?: "")


            val addressForOrder = currentAddressDetail


            val orderRequest = OrderRequest(
                userId = userId,
                idProduct = orderDetails[0].id_product,
                quantity = countProduct,
                paymentType = paymentType,
                bank = bankCode,
                shippingMethod = shippingMethodName,
                shippingCost = selectShipping?.shippingCost ?: 0,
                address = addressForOrder
            )
            lifecycleScope.launch {
                orderViewModel.createOrder(orderRequest)
            }
        }

    }
    private fun setupPaymentSelection() {
        val initialDefaultBank = PaymentMethod(
            bankName = "BCA Virtual Account",
            bankCode = "bca",
            logoResId = R.drawable.bcalogo,
            isSelected = true
        )

        selectedPaymentMethod = initialDefaultBank
        Log.d("CheckoutActivity", "Bank default diinisialisasi: ${selectedPaymentMethod?.bankName} (${selectedPaymentMethod?.bankCode})")
        binding.selectPaymentMethod.visibility = View.VISIBLE
        binding.llSelectedPaymentDisplay.visibility = View.GONE
        binding.imgVA.setImageResource(initialDefaultBank.logoResId)
        binding.tvVA.text = initialDefaultBank.bankName
        binding.radioButtonPayment.isChecked = true // Centang RadioButton ini
    }

    private fun updateSelectedPaymentDisplay() {
        selectedPaymentMethod?.let { bank ->
            binding.llSelectedPaymentDisplay.visibility = View.VISIBLE
            binding.tvSelectedBankName.text = bank.bankName
            if (bank.logoResId != 0) {
                binding.imgSelectedBankLogo.setImageResource(bank.logoResId)
            } else {
                binding.imgSelectedBankLogo.setImageDrawable(null) // Atau default image
            }
        } ?: run {
            binding.llSelectedPaymentDisplay.visibility = View.GONE
        }
        updatePayButtonState()
    }

    private fun updatePayButtonState() {
        binding.btnPayNow.isEnabled = selectedPaymentMethod != null
    }

    fun observeOrderResponse() {
        orderViewModel.orderResponse.observe(this) { response ->
            if (response != null) {
                if (response.status == "CREATED") {
                    Toast.makeText(this, "Order submitted successfully!", Toast.LENGTH_SHORT).show()
                    val transactionData = response.data

                    val intent = Intent(this, PaymentActivity::class.java).apply {
                        putExtra("virtual_account", transactionData?.virtualAccount)
                        putExtra("expired", transactionData?.expired)
                        putExtra("total_payment", transactionData?.price)
                        putExtra("bank_name", selectedPaymentMethod?.bankName)
                    }
                    startActivity(intent)

                    setResult(RESULT_OK, null)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to create order: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Failed to create order: Response is null", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setupObservers() {
        observeOrderResponse()
        lifecycleScope.launch {
            viewModel.addresses.collect { addresses ->
                idDestination = addresses?.firstOrNull { it.isDefault }?.id_destination ?: 0
                binding.tvDetailAlamat.text = addresses?.firstOrNull { it.isDefault }?.street ?: ""
                binding.tvReceiptName.text = addresses?.firstOrNull { it.isDefault }?.recipient_name ?: ""
                binding.tvLabelAlamat.text = addresses?.firstOrNull { it.isDefault }?.label ?: ""

                if (!isFromShippingSelection && idDestination != 0) {
                    val totalProductPrice = (orderDetails.getOrNull(0)?.price ?: 0) * countProduct
                    kingViewModel.calculateTariff(
                        shipperId = 73209,
                        receiverId = idDestination,
                        weight = 1,
                        itemValue = totalProductPrice.toLong(),
                        cod = "no"
                    )
                }
                isFromShippingSelection = false
            }
        }
        lifecycleScope.launch {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(this@CheckoutActivity, it, Toast.LENGTH_LONG).show()
                    viewModel.resetError()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@CheckoutActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetSuccess()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.cardAlamatPengiriman.setOnClickListener {
            resultLauncher.launch(Intent(this, AddressActivity::class.java))
        }
        binding.cardShippingOption.setOnClickListener {
            resultLauncher.launch(Intent(this, SelectCourirActivity::class.java).also{
                it.putExtra("receiver_id", idDestination)
                val totalProductPrice = (orderDetails.getOrNull(0)?.price ?: 0) * countProduct
                it.putExtra("price", totalProductPrice.toLong())
            })
        }
        binding.selectAllPayment.setOnClickListener {
            val intent = Intent(this, SelectPaymentActivity::class.java)
            resultLauncher.launch(intent)
        }
        binding.selectPaymentMethod.setOnClickListener {
            val intent = Intent(this, SelectPaymentActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun observerCalculate() {
        kingViewModel.tariffResult.observe(this) {
            val data = getCheapestShippingOption(it)
            binding.tvCourier.text = "${data?.shippingName} ( ${data?.serviceName} )"
            binding.tvEstimation.text = formatRupiah(data?.shippingCost ?: 0)
            selectShipping = data
            binding.tvTotalShipping.text = formatRupiah(data?.shippingCost ?: 0)
            updateTotalPrice()
        }
        kingViewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error ?: "Error menghitung ongkir", Toast.LENGTH_SHORT).show()
        }
    }
    fun getCheapestShippingOption(tariffData: TariffData): ShippingOption? {
        return (tariffData.reguler)
            .minByOrNull { it.shippingCost }
    }
    fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp. ${formatter.format(amount)}"
    }
    private fun updateTotalPrice() {
        val shippingCost = selectShipping?.shippingCost ?: 0
        val totalProductPrice = if (orderDetails.isNotEmpty()) orderDetails[0].price * countProduct else 0
        binding.tvTotalItemCo.text = formatRupiah(totalProductPrice)
        binding.tvPriceTotal.text = formatRupiah(shippingCost + totalProductPrice)
    }
}