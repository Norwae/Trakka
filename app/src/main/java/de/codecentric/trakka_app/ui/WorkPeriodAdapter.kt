package de.codecentric.trakka_app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.codecentric.trakka_app.R
import de.codecentric.trakka_app.model.Workperiod

class WorkPeriodAdapter(private val actions: WorkPeriodActions) : RecyclerView.Adapter<WorkPeriodViewHolder>() {
    private var model = mutableListOf<Workperiod>()

    private var count = 1L
    private val idMap = mutableMapOf<String, Long>()

    init {
        setHasStableIds(true)
    }

    fun updateContents(nextModel: List<Workperiod>) {
        val descending = nextModel.sortedByDescending { it.start }

        model.clear()
        model.addAll(descending)

        for (next in descending) {
            if (!idMap.containsKey(next.rootId)) {
                idMap[next.rootId] = count++
            }
        }

        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long = idMap[model[position].rootId]!!

    override fun getItemCount(): Int = model.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkPeriodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_contents,
            parent,
            false
        )

        return WorkPeriodViewHolder(view, actions)
    }

    override fun onBindViewHolder(holder: WorkPeriodViewHolder, position: Int) {
        holder.data = model[position]
    }
}