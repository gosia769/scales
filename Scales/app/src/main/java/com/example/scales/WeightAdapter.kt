package com.example.scales

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class WeightAdapter(private val context: Context,
                    private val dataSource: ArrayList<Weight>) : BaseAdapter() {

    private class ViewHolder(row: View?) {
        var date: TextView? = null
        var value: TextView? = null
        init {
            this.date = row?.findViewById<TextView>(R.id.date)
            this.value = row?.findViewById<TextView>(R.id.value)
        }
    }

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View = inflater.inflate(R.layout.listview_item, parent, false)
        val viewHolder: ViewHolder = ViewHolder(rowView)
        rowView.tag = viewHolder
        val data = getItem(position) as Weight
        viewHolder.date?.text = data.date.toString()
        viewHolder.value?.text = data.value.toString() + " kg"

        return rowView
    }

}