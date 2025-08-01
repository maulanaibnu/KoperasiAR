package app.maul.koperasi.presentation.ui.checkout


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.MainActivity
import app.maul.koperasi.R
import app.maul.koperasi.adapter.CheckoutAdapter
import app.maul.koperasi.databinding.ActivityCheckoutBinding
import app.maul.koperasi.model.cart.CartItem
import app.maul.koperasi.model.ongkir.ShippingOption
import app.maul.koperasi.model.ongkir.TariffData
import app.maul.koperasi.model.order.OrderDetail
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.model.order.OrderRequestList
import app.maul.koperasi.model.order.ProductRequest
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

    private var loadingDialog: Dialog? = null

    var street : String? = ""
    var name : String? = ""
    var label : String? = ""
    var idDestination : Int = 0

    var selectShipping : ShippingOption? = null
    private var selectedBank: Pair<String, String>? = null
    var countProduct = 1

    private var isFromShippingSelection = false

    private lateinit var checkoutAdapter : CheckoutAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = showSimpleLoadingDialog(this)
        binding.cardAlamatPengiriman.setOnClickListener {
            resultLauncher.launch(Intent(this, AddressActivity::class.java))
        }




        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                val bankCode = data?.getStringExtra("bank_code")
                if (bankCode != null) {
                    val bankName = data.getStringExtra("bank_name") ?: ""
                    val bankLogo = data.getIntExtra("bank_logo", 0)
                    selectedBank = Pair(bankCode, bankName)

                    binding.llSelectedPaymentDisplay.visibility = View.VISIBLE
                    binding.tvSelectedBankName.text = bankName
                    if (bankLogo != 0) binding.imgSelectedBankLogo.setImageResource(bankLogo)
                }
                else {
                    val shippingOption = data?.getParcelableExtra<ShippingOption>("shippingOption")
                    if (shippingOption != null) {
                        isFromShippingSelection = true
                        selectShipping = shippingOption
                        binding.tvCourier.text = "${shippingOption.shippingName} (${shippingOption.serviceName})"
                        binding.tvEstimation.text = formatRupiah(shippingOption.shippingCost)
                        binding.tvTotalShipping.text = formatRupiah(shippingOption.shippingCost)
                        updateTotalPrice()
                    }

                    else {
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
        binding.selectPaymentMethod.setOnClickListener {
            val intent = Intent(this, SelectPaymentActivity::class.java)
            resultLauncher.launch(intent)
        }
        total = intent.getDoubleExtra("total", 0.0)
        orderDetails =
            intent.getParcelableArrayListExtra<OrderDetail>("orderDetails") ?: emptyList()
        if(orderDetails.isNotEmpty()){
            showRecycler(orderDetails)
        }

        viewModel.getAddresses()
        setupObservers()
        setupPaymentSelection()
        observeOrderResponse()
        observerCalculate()


        binding.cardShippingOption.setOnClickListener {
            resultLauncher.launch(Intent(this, SelectCourirActivity::class.java).also{
                it.putExtra("receiver_id", idDestination)
                val totalProductPrice = (orderDetails.getOrNull(0)?.price ?: 0) * countProduct
                it.putExtra("price", totalProductPrice.toLong())
            })
        }

        binding.btnPayNow.setOnClickListener {
            var paymentType = "bank_transfer"
            val shippingMethod = selectShipping?.let { it.shippingName }
            val userId = Preferences.getId(this)


            val productRequest = mutableListOf<ProductRequest>()
            orderDetails.forEach {
                productRequest.add(ProductRequest(it.id_product,it.name_product, it.qty,it.price))
            }


            // Create OrderRequest
            val orderRequest = OrderRequestList(
                user_id = userId,
                payment_type = paymentType,
                bank = (selectedBank?.first ?: "bca"),
                shipping_method = shippingMethod ?: "SICEPAT",
                shipping_cost = selectShipping?.shippingCost ?: 0,  // Assuming this is the cost
                address = binding.tvDetailAlamat.text.toString(),
                products = productRequest
            )
            Log.d("TESTED","$orderRequest")

            // Call ViewModel to create the order
            orderViewModel.createOrderList(orderRequest)


        }

        binding.selectAllPayment.setOnClickListener {
            val intent = Intent(this, SelectPaymentActivity::class.java)

            intent.putExtra("CURRENT_BANK_CODE", selectedBank?.first)

            resultLauncher.launch(intent)
        }

        observeOrderResponse()

    }

    fun observeOrderResponse() {
        orderViewModel.orderResponseList.observe(this) { response ->
            if (response!!.status == "CREATED") {
                Toast.makeText(this, "Order submitted successfully!", Toast.LENGTH_SHORT).show()

                // You can send additional info to PaymentActivity, like virtual account, expiration, etc.
                val transactionData = response.data

                val intent = Intent(this, PaymentActivity::class.java).apply {
                    putExtra("virtual_account", transactionData?.virtual_account)
                    putExtra("expired", transactionData?.expired)
                    putExtra("total_payment", transactionData?.price)
                    putExtra("order_id", transactionData?.code)
                }
                startActivity(intent)  // Navigate to PaymentActivity for further processing

                setResult(RESULT_OK, null)
                finish()
            } else {
                // Handle error scenario
                Toast.makeText(this, "Failed to create order: ${response.message}", Toast.LENGTH_SHORT).show()
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


    private fun observerCalculate() {
        kingViewModel.tariffResult.observe(this) {
            val data = getCheapestShippingOption(it)
            binding.tvCourier.text = "${data?.shippingName} ( ${data?.serviceName} )" ?: "Gagal mendapatkan nama shipping"
            binding.tvEstimation.text = formatRupiah(data?.shippingCost ?: 0)
            selectShipping = data
            binding.tvTotalShipping.text = formatRupiah(data?.shippingCost ?: 0)
            if(!binding.tvCourier.text.toString().contains("null")){
                loadingDialog?.dismiss()
                loadingDialog = null
            }

            // PANGGIL FUNGSI YANG SUDAH BENAR
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

    private fun setupPaymentSelection() {
        if (selectedBank == null) {
            // 1. Definisikan data default untuk BCA
            val defaultBankCode = "bca"
            val defaultBankName = "BCA Virtual Account"
            // Ganti R.drawable.logo_bca dengan nama resource logo Anda yang benar
            val defaultBankLogo = R.drawable.bcalogo

            // 2. Atur variabel state dengan data BCA
            selectedBank = Pair(defaultBankCode, defaultBankName)

            // 3. Perbarui tampilan UI
            // Sembunyikan tombol "Pilih Metode Pembayaran"
            binding.selectPaymentMethod.visibility = View.GONE

            // Tampilkan layout yang berisi detail bank yang sudah terpilih
            binding.llSelectedPaymentDisplay.visibility = View.VISIBLE
            binding.tvSelectedBankName.text = defaultBankName
            binding.imgSelectedBankLogo.setImageResource(defaultBankLogo)
        }

    }

    fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp. ${formatter.format(amount)}"
    }

    private fun updateTotalPrice() {
        val shippingCost = selectShipping?.shippingCost ?: 0
        val totalProductPrice = orderDetails.sumOf { it.qty * it.price }
        binding.tvTotalItemCo.text = formatRupiah(totalProductPrice)
        binding.tvPriceTotal.text = formatRupiah(totalProductPrice + shippingCost)
    }

    private fun showRecycler(data: List<OrderDetail>) {
        checkoutAdapter = CheckoutAdapter(data) {
            updateTotalPrice()
        }
        binding.rvItem.apply {
            adapter = checkoutAdapter
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
        }
    }

    fun showSimpleLoadingDialog(context: Context, cancelable: Boolean = true): Dialog {
        val progressBar = ProgressBar(context).apply {
            isIndeterminate = true
        }

        val dialog = Dialog(context)
        dialog.setContentView(progressBar)
        dialog.setCancelable(cancelable)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        dialog.show()
        return dialog
    }
}