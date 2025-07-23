package app.maul.koperasi.presentation.ui.augmentedReality

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityProductArBinding
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch

class ProductArActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductArBinding


    private var urlGlbFile: String? = null
    private var productName: String? = null

    var isLoading = false
        set(value) {
            field = value
            binding.loadingView.isGone = !value
        }

    var anchorNode: AnchorNode? = null
        set(value) {
            if (field != value) {
                field = value
                updateInstructions()
            }
        }

    var trackingFailureReason: TrackingFailureReason? = null
        set(value) {
            if (field != value) {
                field = value
                updateInstructions()
            }
        }

    fun updateInstructions() {
        binding.instructionText.text = trackingFailureReason?.let {
            it.getDescription(this)
        } ?: if (anchorNode == null) {
            getString(R.string.point_your_phone_down)
        } else {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductArBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        goBack()


        urlGlbFile = intent.getStringExtra("urlGlbFile")
        productName = intent.getStringExtra("productName")
        binding.productName.text = productName ?: getString(R.string.productName)


        if (urlGlbFile == null || urlGlbFile!!.isBlank()) {
            Log.e("ProductArActivity", "URL model 3D tidak ditemukan atau kosong.")
            Toast.makeText(this, "Model 3D tidak tersedia untuk produk ini.", Toast.LENGTH_LONG).show()
        }

        setupSceneview()
    }

    private fun setupSceneview(){
        binding.sceneView.apply {
            lifecycle = this@ProductArActivity.lifecycle
            planeRenderer.isEnabled = true
            configureSession { session, config ->
                config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    true -> Config.DepthMode.AUTOMATIC
                    else -> Config.DepthMode.DISABLED
                }
                config.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }

            onSessionUpdated = { _, frame ->
                if (anchorNode == null) {
                    frame.getUpdatedPlanes()
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let { plane ->
                            addAnchorNode(plane.createAnchor(plane.centerPose))
                        }
                }
            }
            onTrackingFailureChanged = { reason ->
                this@ProductArActivity.trackingFailureReason = reason
            }
        }
    }

    fun addAnchorNode(anchor: Anchor) {
        binding.sceneView.addChildNode(
            AnchorNode(binding.sceneView.engine, anchor)
                .apply {
                    isEditable = true
                    this@ProductArActivity.anchorNode = this
                    lifecycleScope.launch {
                        isLoading = true
                        try {

                            if (urlGlbFile != null && urlGlbFile!!.isNotBlank()) {
                                binding.sceneView.modelLoader.loadModelInstance(urlGlbFile!!)?.let { modelInstance ->

                                    addChildNode(
                                        ModelNode(
                                            modelInstance = modelInstance,
                                            scaleToUnits = 0.5f,
                                            centerOrigin = Position(y = -0.5f)
                                        ).apply {
                                            isEditable = true
                                        }
                                    )
                                    Log.d("ProductArActivity", "Model berhasil dimuat dari URL: $urlGlbFile")
                                } ?: run {

                                    Log.e("ProductArActivity", "Gagal memuat instance model dari URL: $urlGlbFile (mengembalikan null)")
                                    Toast.makeText(this@ProductArActivity, "Gagal memuat model 3D. Periksa format file atau URL.", Toast.LENGTH_LONG).show()
                                }
                            } else {

                                Log.e("ProductArActivity", "URL model 3D tidak ditemukan atau kosong saat mencoba memuat.")
                                Toast.makeText(this@ProductArActivity, "Model 3D tidak tersedia untuk produk ini.", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {

                            Log.e("ProductArActivity", "Terjadi kesalahan saat memuat model 3D dari URL: $urlGlbFile", e)
                            Toast.makeText(this@ProductArActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isLoading = false
                        }
                    }
                }
        )
    }

    private fun goBack(){
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}