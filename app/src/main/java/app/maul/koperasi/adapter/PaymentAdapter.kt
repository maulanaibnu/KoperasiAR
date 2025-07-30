package app.maul.koperasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.PaymentOrderListBinding
import app.maul.koperasi.model.payment.PaymentMethod



class PaymentAdapter(
    private val currentBankCode: String?,
    private val onItemSelected: (PaymentMethod) -> Unit
) : ListAdapter<PaymentMethod, PaymentAdapter.PaymentViewHolder>(PaymentMethodDiffCallback()) {

    private var selectedPosition = -1

    override fun submitList(list: List<PaymentMethod>?) {
        super.submitList(list)
        if (selectedPosition == -1 && currentBankCode != null) {
            selectedPosition = list?.indexOfFirst { it.bankCode == currentBankCode } ?: -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = PaymentOrderListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val paymentMethod = getItem(position) // Mengambil item dari ListAdapter
        holder.bind(paymentMethod)
    }

    override fun getItemCount(): Int = currentList.size // Mengambil jumlah item dari ListAdapter

    inner class PaymentViewHolder(private val binding: PaymentOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(paymentMethod: PaymentMethod) {
            binding.imgBankLogo.setImageResource(paymentMethod.logoResId)
            binding.tvBankName.text = paymentMethod.bankName

            binding.radioButtonPayment.isChecked = adapterPosition == selectedPosition

            itemView.setOnClickListener {
                if (selectedPosition != adapterPosition) {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onItemSelected(getItem(selectedPosition))
                }
            }
        }
    }
}

class PaymentMethodDiffCallback : DiffUtil.ItemCallback<PaymentMethod>() {
    override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
        return oldItem.bankCode == newItem.bankCode
    }

    override fun areContentsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
        return oldItem == newItem
    }
}


