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
    private val onSelect: (AddressData) -> Unit,
    private val onChange: (AddressData) -> Unit,

) : ListAdapter<AddressData, AddressAdapter.AddressViewHolder>(DIFF_CALLBACK) {

    private var selectedId: Int? = null // jika ingin highlight alamat yang dipilih
    fun setSelectedId(id: Int?) {
        selectedId = id
        notifyDataSetChanged()
    }

    inner class AddressViewHolder(val binding: ListItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AddressData) {
            val context = binding.root.context
            if (item.id == selectedId) {
                binding.card.setBackgroundResource(R.drawable.background_selected_size)
            } else {
                binding.card.setBackgroundResource(R.drawable.background_normal)
            }
            // Set data ke setiap view
            binding.tvLabelAddress.text = item.label ?: "-"
            binding.tvName.text = item.recipient_name
            binding.tvPhoneNumber.text = item.phone_number
            binding.tvAddress.text = item.street
            binding.tvKodepos.text = item.notes // Atau item.postalCode jika ada
            binding.tvPriority.text = if (item.id == selectedId) "Utama" else "Alamat"

            // Tampilkan icon check jika terpilih
            binding.imageView.visibility = if (item.id == selectedId) View.VISIBLE else View.INVISIBLE

            // Listener untuk memilih alamat
            binding.card.setOnClickListener {
                onSelect(item)
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
