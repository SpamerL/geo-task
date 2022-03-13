package com.spamerl.geo_task.presentation.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.spamerl.geo_task.R
import java.util.*

class PlacesPredictionAdapter(
    context: Context,
    resourceId: Int,
) : ArrayAdapter<AutocompletePrediction>(context, resourceId) {
    private val predictions: MutableList<AutocompletePrediction>
    private val rId = resourceId

    init {
        predictions = ArrayList()
    }

    fun setData(list: List<AutocompletePrediction>) {
        predictions.clear()
        predictions.addAll(list)
    }

    override fun getCount(): Int {
        return predictions.size
    }

    override fun getItem(position: Int): AutocompletePrediction {
        return predictions[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(rId, parent, false)
        }
        val prediction = getItem(position)
        val first = view!!.findViewById<TextView>(R.id.address_tv)
        val second = view.findViewById<TextView>(R.id.secondary_tv)
        first.text = prediction.getPrimaryText(null)
        second.text = prediction.getSecondaryText(null)
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    filterResults.values = predictions
                    filterResults.count = predictions.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
