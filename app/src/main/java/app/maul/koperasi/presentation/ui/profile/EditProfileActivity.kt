package app.maul.koperasi.presentation.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityEditProfileBinding
import app.maul.koperasi.model.user.User
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.activity.AddAddressActivity
import app.maul.koperasi.viewmodel.AddressViewModel
import app.maul.koperasi.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    private val viewModel: UserViewModel by viewModels()

    private val REQUEST_CAMERA = 100
    private val REQUEST_GALLERY = 101

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        val token = Preferences.getToken(this)
        if (!token.isNullOrEmpty()) {
            viewModel.getUserProfile("Bearer $token")
        }

        observeProfileData()

    }

    private fun initView(){
        val genderOptions = arrayOf("Laki-laki", "Perempuan")
        val paddingStartPx = 16.dpToPx() // Dapatkan padding dalam piksel

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            genderOptions
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                if (view is TextView) {
                    view.setPadding(paddingStartPx, view.paddingTop, view.paddingRight, view.paddingBottom)
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                if (view is TextView) {

                    view.setPadding(paddingStartPx, 24, 0, 24) // Contoh padding vertikal 24dp
                }
                return view
            }
        }
        binding.edtTextGender.setAdapter(adapter)

        binding.apply {
            imgBack.setOnClickListener {
                onBackPressedCallback.handleOnBackPressed()
            }
            onBackPressedDispatcher.addCallback(onBackPressedCallback)
            btnChangeImage.setOnClickListener {
                showBottomSheet()
            }
            btnEditProfile.setOnClickListener {
                if(edtTextName.text.toString().isNotEmpty() && edtTextGender.text.toString().isNotEmpty() && edtPhone.text.toString().isNotEmpty()){
                    doUpdateUser()
                }
            }
            edtTextGender.setOnClickListener {
                edtTextGender.showDropDown() // Memaksa dropdown untuk tampil
            }
        }
    }
    private fun observeProfileData() {
        lifecycleScope.launchWhenStarted {
            viewModel.userProfile.collect { user ->
                // Pastikan user datanya tidak null
                user?.let {
                    populateUserData(it)
                }
            }
        }
    }
    private fun populateUserData(user: User) {
        binding.apply {
            edtTextName.setText(user.name)
            edtTextGender.setText(user.gender ?: "", false)
            edtPhone.setText(user.phone ?: "")

            if (!user.profile_image.isNullOrEmpty()) {
                Glide.with(this@EditProfileActivity)
                    .load(user.profile_image)
                    .placeholder(R.drawable.img_1)
                    .error(R.drawable.img_1)
                    .into(profileImage)
            }
        }
    }
    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()


    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_pick_image, null)

        val btnCamera = view.findViewById<Button>(R.id.btnCamera)
        val btnGallery = view.findViewById<Button>(R.id.btnGallery)
        val btnClose = view.findViewById<Button>(R.id.btnClose)

        btnCamera.setOnClickListener {
            bottomSheetDialog.dismiss()
            openCamera()
        }

        btnGallery.setOnClickListener {
            bottomSheetDialog.dismiss()
            openGallery()
        }

        btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = File.createTempFile("IMG_", ".jpg", externalCacheDir)
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", photoFile)
        imageUri = uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    imageUri?.let {
                        binding.profileImage.setImageURI(it)
                    }
                }
                REQUEST_GALLERY -> {
                    val selectedImage = data?.data
                    imageUri = selectedImage
                    binding.profileImage.setImageURI(selectedImage)
                }
            }
        }
    }

    private fun doUpdateUser(){
        val name = binding.edtTextName.text.toString()
        val gender = binding.edtTextGender.text.toString()
        val phone = binding.edtPhone.text.toString()

        val namePart = createPartFromString(name)
        val genderPart = createPartFromString(gender)
        val phonePart = createPartFromString(phone)

        val imagePart = imageUri?.let { uri ->
            val imageFile = uriToFile(this, uri)
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("profile_image", imageFile.name, requestFile)
        }

        viewModel.updateUser(
            name = namePart,
            gender = genderPart,
            phone = phonePart,
            image = imagePart
        )
        observe()
    }

    private fun observe(){
        lifecycleScope.launchWhenStarted {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(this@EditProfileActivity, "Success $it", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(this@EditProfileActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val result = cursor.getString(idx)
            cursor.close()
            result
        } else {
            uri.path!!
        }
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile("image", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    fun createPartFromString(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
}