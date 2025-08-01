package app.maul.koperasi.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import app.maul.koperasi.MainActivity
import app.maul.koperasi.R
import app.maul.koperasi.adapter.ImageSliderAdapter
import app.maul.koperasi.databinding.ActivityDetailProductBinding
import app.maul.koperasi.model.product.Product
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.augmentedReality.ProductArActivity
import app.maul.koperasi.presentation.ui.detailProduct.DetailProductViewModel
import app.maul.koperasi.presentation.ui.wishlist.WishlistViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.text.DecimalFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailProductBinding
    private val wishlistViewModel by viewModels<WishlistViewModel>()
    private val detailProductViewModel by viewModels<DetailProductViewModel>()
    var tempProduct: Product? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backToHome()
        attachDetailProduct()
        addToCart()
        observeCartResponse()
        observeUpdateFavorite()
        goToArActivity()

        binding.wishlistButton.setOnClickListener {
            if (tempProduct!!.isWishlisted == 1) {
                tempProduct?.let { it1 ->
                    wishlistViewModel.removeWishlist(
                        Preferences.getId(this@DetailProductActivity),
                        it1.id
                    )
                }
                Toast.makeText(this, "Berhasil menghapus wishlist", Toast.LENGTH_SHORT).show()
//                binding.wishlistButton.setImageResource(R.drawable.ic_favorite_border)
            }else {
                tempProduct?.let { it1 ->
                    wishlistViewModel.addWishlist(
                        it1.id,
                        Preferences.getId(this@DetailProductActivity)
                    )
                }
            }
        }
    }

    private fun observeUpdateFavorite(){
        wishlistViewModel.updateWishlistResponse.observe(this) { message ->
            getDetailProduct()
        }
    }

    private fun getDetailProduct() {
        val productId = intent.getIntExtra("product_id", 0)
        println("productId: ${productId}")
        detailProductViewModel.fetchProductDetail(
            productId,
            Preferences.getId(this@DetailProductActivity)
        )
    }

    private fun backToHome() {
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun attachDetailProduct() {
        getDetailProduct()
        detailProductViewModel.productDetail.observe(this, Observer { response ->
            response?.let {
                val product = it.data
                tempProduct = product
                println(product)
                val formatter = DecimalFormat("#,###")
                val formattedPrice = formatter.format(product.price).replace(',', '.')
                val prefix = "Rp. "
//                Glide.with(this).load(Constant.BASE_URL + product.images).into(binding.IvProduct)
                val imageAdapter = ImageSliderAdapter(product.images) { position ->
                    // Ini adalah aksi yang akan dijalankan saat gambar diklik
                    // Membuka FullscreenImageActivity yang sudah kita buat sebelumnya
                    val intent = FullscreenImageActivity.newIntent(this, product.images, position)
                    startActivity(intent)
                }
                binding.viewPagerProduct.adapter = imageAdapter

                TabLayoutMediator(binding.tabLayout, binding.viewPagerProduct) { tab, position ->
                }.attach()
                binding.ProductPrice.text = "Rp. $formattedPrice"
                binding.ProductName.text = product.name
//                binding.ProductSold.text = " ${product.soldCount} Terjual"
                binding.ProductDesc.text = product.description
                binding.tvstockProduct.text = product.quantity.toString()

                if (product.isWishlisted == 1) {
                    binding.wishlistButton.setImageResource(R.drawable.ic_favorite_red)
                } else {
                    binding.wishlistButton.setImageResource(R.drawable.ic_favorite_border)
                }

            }
        })
    }

    private fun addToCart() {
        binding.btnAddCart.setOnClickListener {
            val userId = Preferences.getId(this)
            val quantity = 1
            detailProductViewModel.productDetail.value?.data?.let { product ->
                detailProductViewModel.addCart(product.id, userId, quantity)
            }
        }
    }

    private fun observeCartResponse() {
        detailProductViewModel.cartResponse.observe(this) { response ->
            if (response.status == 201) {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToArActivity() {
        binding.arButton.setOnClickListener {
            detailProductViewModel.productDetail.value?.data?.let { product ->
                startActivity(Intent(this, ProductArActivity::class.java).apply {
                    putExtra("urlGlbFile", product.model?.urlname)
                    putExtra("productName", product.name)
                })
            }
        }
    }
}