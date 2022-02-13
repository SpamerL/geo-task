package com.spamerl.geo_task.presentation.ui.path

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.spamerl.geo_task.R

class RvListAdapter : RecyclerView.Adapter<RvListAdapter.PlacePredictionViewHolder>() {
    private val predictions: MutableList<AutocompletePrediction> = ArrayList()
    var onPlaceClickListener: ((AutocompletePrediction) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePredictionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlacePredictionViewHolder(
            inflater.inflate(R.layout.place_prediction_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlacePredictionViewHolder, position: Int) {
        val place = predictions[position]
       /* val layoutParam = holder.itemView.layoutParams
        layoutParam.height = 20
        layoutParam.width = layoutParam.width

        */
        holder.setPrediction(place)
        holder.itemView.setOnClickListener {
            onPlaceClickListener?.invoke(place)
        }
    }

    fun setPredictions(prediction: List<AutocompletePrediction>?) {
        this.predictions.clear()
        if (prediction != null) {
            this.predictions.addAll(prediction)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int =
        predictions.size

    interface OnPlaceClickListener {
        fun onPlaceClicked(place: AutocompletePrediction)
    }

    class PlacePredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.text_view_title)
        // private val address: TextView = itemView.findViewById(R.id.text_view_address)
        fun setPrediction(prediction: AutocompletePrediction) {
            title.text = prediction.getFullText(null)
        }
    }
}
