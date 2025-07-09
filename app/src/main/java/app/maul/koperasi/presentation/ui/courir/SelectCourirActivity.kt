package app.maul.koperasi.presentation.ui.courir

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.R
import app.maul.koperasi.adapter.CourirAdapter
import app.maul.koperasi.databinding.ActivitySelectCourirBinding
import app.maul.koperasi.viewmodel.RajaOngkirViewModel

class SelectCourirActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySelectCourirBinding

    private val kingViewModel by viewModels<RajaOngkirViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCourirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("receiver_id",0)
        val price = intent.getLongExtra("price",0L)

        Toast.makeText(this, "$id $price", Toast.LENGTH_SHORT).show()
        kingViewModel.calculateTariff(
            shipperId = 73209,
            receiverId = id,
            weight = 1,
            itemValue = price.toLong(),
            cod = "no"
        )
        setObserver()
    }

    private fun setObserver(){
        kingViewModel.tariffResult.observe(this) {
            Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            val courierAdapter = CourirAdapter(it.reguler) { selectedShipping ->
                val resultIntent = Intent()
                resultIntent.putExtra("shippingOption", selectedShipping)
                setResult(RESULT_OK, resultIntent)
                finish()
            }

            binding.rvCourier.apply {
                adapter = courierAdapter
                layoutManager = LinearLayoutManager(this@SelectCourirActivity)
            }
        }
    }
}