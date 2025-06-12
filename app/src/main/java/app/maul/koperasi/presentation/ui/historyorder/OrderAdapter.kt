package app.maul.koperasi.presentation.ui.historyorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.OrderListItemBinding
import app.maul.koperasi.model.order.Order

class OrderAdapter(
    private var orderList: List<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: OrderListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            OrderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position] // Store the order to avoid repeated indexing

        var status = ""
        if(order.status == "0"){
            status = "Status: Pesanan diproses"
        }else if(order.status == "1"){
            status = "Status: Pesanan selesai"
        }

        holder.binding.apply {
            textOrderId.text = "Order ID: ${order.code}"
            textOrderTotal.text = "Total: Rp. ${order.total_price}"
            textOrderStatus.text = status
            textOrderDate.text = "Created At: ${order.createdAt}"

            // Display order details
            textOrderDetail.text = order.order_details.joinToString("\n") {
                "${it.name_product} - ${it.qty}x @ Rp. ${it.price}"
            }
        }

        holder.itemView.setOnClickListener {
//            listener.onItemClick(order)
        }
    }

    override fun getItemCount(): Int = orderList.size
}

interface OrderItemListener {
    fun onItemClick(order: Order)
}
