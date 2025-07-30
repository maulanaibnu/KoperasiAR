package app.maul.koperasi.presentation.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.ActivityFullscreenImageBinding
import app.maul.koperasi.databinding.ItemFullscreenImageBinding
import app.maul.koperasi.utils.Constant
import com.bumptech.glide.Glide

class FullscreenImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenImageBinding

    companion object {
        const val EXTRA_IMAGES = "EXTRA_IMAGES"
        const val EXTRA_POSITION = "EXTRA_POSITION"

        fun newIntent(context: Context, images: List<String>, position: Int): Intent {
            return Intent(context, FullscreenImageActivity::class.java).apply {
                putStringArrayListExtra(EXTRA_IMAGES, ArrayList(images))
                putExtra(EXTRA_POSITION, position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val images = intent.getStringArrayListExtra(EXTRA_IMAGES) ?: arrayListOf()
        val position = intent.getIntExtra(EXTRA_POSITION, 0)

        val fullscreenAdapter = FullscreenImageAdapter(images)
        binding.fullscreenViewPager.adapter = fullscreenAdapter
        binding.fullscreenViewPager.setCurrentItem(position, false)

        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}

// Adapter untuk ViewPager2 di Fullscreen
class FullscreenImageAdapter(private val images: List<String>) :
    RecyclerView.Adapter<FullscreenImageAdapter.ImageViewHolder>() {

    // Pastikan ViewHolder ini menggunakan ItemFullscreenImageBinding
    inner class ImageViewHolder(val binding: ItemFullscreenImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemFullscreenImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]

        val fullUrl = Constant.BASE_URL + imageUrl

        Glide.with(holder.itemView.context)
            .load(fullUrl)
            .fitCenter()
            .into(holder.binding.productImageView)
    }

    override fun getItemCount(): Int = images.size
}