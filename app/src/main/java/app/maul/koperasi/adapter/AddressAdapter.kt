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
    private val onSetDefault: (AddressData) -> Unit,
    private val onChange: (AddressData) -> Unit,

) : ListAdapter<AddressData, AddressAdapter.AddressViewHolder>(DIFF_CALLBACK) {

    inner class AddressViewHolder(val binding: ListItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AddressData) {
            // Set data ke setiap view
            binding.tvLabelAddress.text = item.label ?: "-"
            binding.tvName.text = item.recipient_name
            binding.tvPhoneNumber.text = item.phone_number
            binding.tvAddress.text = item.street
            binding.tvKodepos.text = item.notes // Atau item.postalCode jika ada


            if (item.isDefault) {
                // Tampilan jika alamat ini adalah alamat utama
                binding.card.setBackgroundResource(R.drawable.background_selected_size)
                binding.tvPriority.visibility = View.VISIBLE
                binding.imageView.visibility = View.VISIBLE
            } else {
                // Tampilan untuk alamat biasa
                binding.card.setBackgroundResource(R.drawable.background_normal)
                binding.tvPriority.visibility = View.GONE
                binding.imageView.visibility = View.GONE
            }

            // Listener untuk memilih alamat
            binding.card.setOnClickListener {
                android.util.Log.d("AdapterClick", "Item DIKLIK! Data: $item")
                if (!item.isDefault) {
                    android.util.Log.d("AdapterClick", "Item belum default, memanggil onSetDefault...")
                    onSetDefault(item)
                }else {
                    android.util.Log.d("AdapterClick", "Item sudah default, tidak melakukan apa-apa.")
                }
            }
            // Listener untuk tombol ubah alamat
            binding.btnChangeAddress.setOnClickListener {
                onChange(item)
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
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AddressData, newItem: AddressData): Boolean {
                return oldItem == newItem
            }
        }
    }
}
