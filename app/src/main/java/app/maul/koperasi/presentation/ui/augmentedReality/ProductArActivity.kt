package app.maul.koperasi.presentation.ui.augmentedReality

import android.os.Bundle
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
        val urlGlbFIle = intent.getStringExtra("urlGlbFile")
        binding.sceneView.addChildNode(
            AnchorNode(binding.sceneView.engine, anchor)
                .apply {
                    isEditable = true
                    lifecycleScope.launch {
                        isLoading = true
                        if (urlGlbFIle != null) {
                            binding.sceneView.modelLoader.loadModelInstance(
                                urlGlbFIle
                            )?.let { modelInstance ->
                                addChildNode(
                                    ModelNode(
                                        modelInstance = modelInstance,
                                        // Scale to fit in a 0.5 meters cube
                                        scaleToUnits = 0.5f,
                                        // Bottom origin instead of center so the model base is on floor
                                        centerOrigin = Position(y = -0.5f)
                                    ).apply {
                                        isEditable = true
                                    }
                                )
                            }
                        }
                        isLoading = false
                    }
                    anchorNode = this
                }
        )
    }

    private fun goBack(){
        binding.backButton.setOnClickListener {
            finish()
        }
    }


}