package app.maul.koperasi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ListItemAddressBinding
import app.maul.koperasi.model.address.AddressData
import app.maul.koperasi.model.address.AddressResponse


class AddressAdapter(
    private val onChange: (AddressData) -> Unit // Hanya pertahankan listener untuk perubahan/edit
) : ListAdapter<AddressData, AddressAdapter.AddressViewHolder>(DIFF_CALLBACK) {

    inner class AddressViewHolder(val binding: ListItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AddressData) {
            // Set data alamat ke tampilan
            binding.tvLabelAddress.text = item.label ?: "-"
            binding.tvName.text = item.recipient_name
            binding.tvPhoneNumber.text = item.phone_number
            binding.tvAddress.text = item.street
            binding.tvKodepos.text = item.notes // Sesuaikan jika ini seharusnya kode pos


            // Logika tampilan berdasarkan status isDefault
            if (item.isDefault) {
                // Jika alamat ini adalah alamat utama
                binding.card.setBackgroundResource(R.drawable.background_selected_size) // Ganti background CardView
                binding.tvPriority.visibility = View.VISIBLE // Tampilkan label "Alamat Utama"
                binding.imageView.visibility = View.VISIBLE // Tampilkan ikon cek

                // Atur teks dan warna untuk label "Alamat Utama"
                binding.tvPriority.text = itemView.context.getString(R.string.home_adress) // Pastikan string ini ada di strings.xml
                binding.tvPriority.setBackgroundResource(R.drawable.background_rounded) // Sesuaikan drawable
                // Untuk warna backgroundTint dan textColor, gunakan ColorStateList atau getColor
                binding.tvPriority.backgroundTintList = itemView.context.getColorStateList(R.color.semi_gray)
                binding.tvPriority.setTextColor(itemView.context.getColor(R.color.second_gray))

                // Atur tint untuk ikon cek
                binding.imageView.setColorFilter(itemView.context.getColor(R.color.primary_color)) // Sesuaikan dengan warna yang Anda inginkan

            } else {
                // Jika alamat ini bukan alamat utama (biasa)
                binding.card.setBackgroundResource(R.drawable.background_normal) // Kembalikan background CardView
                binding.tvPriority.visibility = View.GONE // Sembunyikan label "Alamat Utama"
                binding.imageView.visibility = View.GONE // Sembunyikan ikon cek
            }

            // Hapus listener untuk memilih alamat default dari klik card.
            // Sekarang klik card tidak akan mengubah status default.
            binding.card.setOnClickListener {
                android.util.Log.d("AdapterClick", "Item DIKLIK! (Aksi default address tidak dilakukan dari sini)")
                // Anda bisa menambahkan aksi lain di sini jika diperlukan,
                // misalnya membuka detail alamat tanpa maksud mengubah default.
            }

            // Listener untuk tombol ubah alamat
            binding.btnChangeAddress.setOnClickListener {
                onChange(item) // Panggil callback untuk membuka halaman edit
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ListItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AddressData>() {
            override fun areItemsTheSame(oldItem: AddressData, newItem: AddressData): Boolean {
                // Membandingkan item berdasarkan ID unik mereka
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AddressData, newItem: AddressData): Boolean {
                // Membandingkan konten item untuk mendeteksi perubahan.
                // Sangat PENTING untuk menyertakan 'isDefault' di sini agar perubahan status default terlihat.
                return oldItem.recipient_name == newItem.recipient_name &&
                        oldItem.phone_number == newItem.phone_number &&
                        oldItem.street == newItem.street &&
                        oldItem.notes == newItem.notes &&
                        oldItem.label == newItem.label &&
                        oldItem.city == newItem.city && // Pastikan ini ada di model AddressData
                        oldItem.id_destination == newItem.id_destination && // Pastikan ini ada di model AddressData
                        oldItem.isDefault == newItem.isDefault // Ini adalah bagian kunci!
            }
        }
    }
}
