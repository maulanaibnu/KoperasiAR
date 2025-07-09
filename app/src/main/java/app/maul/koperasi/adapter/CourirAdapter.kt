package app.maul.koperasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.ListItemCourierBinding
import app.maul.koperasi.model.ongkir.ShippingOption
import java.text.NumberFormat
import java.util.Locale

class CourirAdapter(
    val data : List<ShippingOption>,
    val onItemSelected: (ShippingOption) -> Unit
) : RecyclerView.Adapter<CourirAdapter.CourirViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CourirViewHolder {
        return CourirViewHolder(ListItemCourierBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: CourirViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            tvCourirNinja.text = data[position].shippingName
            tvPriceNinja.text = "( ${formatRupiah(data[position].shippingCost)} )"

            radioNinja.isChecked = position == selectedPosition


            radioNinja.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                onItemSelected(data[position]) // Callback
            }


            root.setOnClickListener {
                radioNinja.performClick()
            }
        }
    }

    override fun getItemCount(): Int = data.size

    inner class CourirViewHolder(val binding : ListItemCourierBinding): RecyclerView.ViewHolder(binding.root)

    fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp.${formatter.format(amount)}"
    }
}