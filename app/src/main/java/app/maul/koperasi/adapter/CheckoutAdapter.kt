package app.maul.koperasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.adapter.CheckoutAdapter.CheckoutViewHolder
import app.maul.koperasi.databinding.ListItemCheckoutBinding
import app.maul.koperasi.model.order.OrderDetail
import app.maul.koperasi.utils.Constant
import com.bumptech.glide.Glide

class CheckoutAdapter(
    private val data : List<OrderDetail>
): RecyclerView.Adapter<CheckoutViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckoutViewHolder {
        return CheckoutViewHolder(ListItemCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: CheckoutViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            tvCOProductName.text = data[position].name_product
            tvCoProductPrice.text = data[position].price.toString()
            tvQuantity.text = "|  ${data[position].qty}x"
            Glide.with(root.context).load(Constant.BASE_URL + data[position].image_url).into(imgCoPRoduct)
        }
    }

    override fun getItemCount(): Int = data.size

    inner class CheckoutViewHolder(val binding : ListItemCheckoutBinding): RecyclerView.ViewHolder(binding.root)
}