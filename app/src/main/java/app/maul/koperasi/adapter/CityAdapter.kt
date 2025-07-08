package app.maul.koperasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.adapter.CityAdapter.CityViewHolder
import app.maul.koperasi.databinding.ListItemCityBinding
import app.maul.koperasi.model.ongkir.DestinationData

class CityAdapter(
    private val data : MutableList<DestinationData>,
    private val listener : onClickCity
) : RecyclerView.Adapter<CityViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityViewHolder {
        return CityViewHolder(ListItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: CityViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            tvCity.text = data[position].label
            card.setOnClickListener { listener.onSelectCity(data[position].label,data[position].id) }
        }
    }

    override fun getItemCount(): Int  = data.size

    inner class CityViewHolder(val binding : ListItemCityBinding): RecyclerView.ViewHolder(binding.root)

    interface onClickCity{

        fun onSelectCity(label : String,id : Int)
    }

    fun updateData(newList: List<DestinationData>) {
        this.data.clear()
        this.data.addAll(newList)
        notifyDataSetChanged()
    }
}