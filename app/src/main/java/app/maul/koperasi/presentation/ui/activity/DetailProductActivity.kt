package app.maul.koperasi.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import app.maul.koperasi.MainActivity
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityDetailProductBinding
import app.maul.koperasi.model.product.Product
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.augmentedReality.ProductArActivity
import app.maul.koperasi.presentation.ui.detailProduct.DetailProductViewModel
import app.maul.koperasi.presentation.ui.wishlist.WishlistViewModel
import app.maul.koperasi.utils.Constant.Companion.BASE_URL
import com.bumptech.glide.Glide
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
                val prefix = "Rp. "
                Glide.with(this).load(BASE_URL + product.images).into(binding.IvProduct)
                binding.ProductPrice.text = "$prefix${product.price}"
                binding.ProductName.text = product.name
                binding.ProductSold.text = "Terjual ${product.terjual}"
                binding.ProductDesc.text = product.description

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
                })
            }
        }
    }

}