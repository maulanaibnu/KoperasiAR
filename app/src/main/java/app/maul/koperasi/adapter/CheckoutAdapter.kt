package app.maul.koperasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ListItemCheckoutBinding
import app.maul.koperasi.model.order.OrderDetail
import app.maul.koperasi.utils.Constant
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class CheckoutAdapter(
    private val data: List<OrderDetail>,
    private val onQtyChanged: () -> Unit
) : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

    inner class CheckoutViewHolder(val binding: ListItemCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCheckoutBinding.inflate(inflater, parent, false)
        return CheckoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        val item = data[position]
        holder.binding.apply {
            tvCOProductName.text = item.name_product
            tvCoProductPrice.text = formatRupiah(item.price)
            tvQuantityProduct.text = item.qty.toString()
            Glide.with(root.context)
                .load(Constant.BASE_URL + item.image_url)
                .placeholder(R.drawable.product)
                .error(R.drawable.baseline_error_outline_24)
                .into(imgCoPRoduct)

            btnPlus.setOnClickListener {
                item.qty += 1
                tvQuantityProduct.text = item.qty.toString()
                onQtyChanged.invoke()
            }

            btnMinus.setOnClickListener {
                if (item.qty > 1) {
                    item.qty -= 1
                    tvQuantityProduct.text = item.qty.toString()
                    onQtyChanged.invoke()
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size

    private fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp. ${formatter.format(amount)}"
    }
}
