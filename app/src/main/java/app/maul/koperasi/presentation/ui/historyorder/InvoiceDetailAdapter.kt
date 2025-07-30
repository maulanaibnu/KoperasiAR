package app.maul.koperasi.presentation.ui.historyorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.DetailInvoiceItemBinding // Pastikan import ini benar
import app.maul.koperasi.model.order.OrderDetail
import java.text.NumberFormat
import java.util.Locale

class InvoiceDetailAdapter(private val items: List<OrderDetail>) :
    RecyclerView.Adapter<InvoiceDetailAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: DetailInvoiceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DetailInvoiceItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvInvoiceProductName.text = item.name_product
            tvInvoiceProductQuantity.text = item.qty.toString()
            tvInvoiceProductPrice.text = formatRupiah(item.price.toDouble())
        }
    }

    override fun getItemCount(): Int = items.size

    private fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        format.maximumFractionDigits = 0
        return format.format(amount)
    }
}