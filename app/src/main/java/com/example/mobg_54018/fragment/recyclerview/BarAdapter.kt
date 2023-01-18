package com.example.mobg_54018.fragment.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobg_54018.databinding.BarItemBinding
import com.example.mobg_54018.dto.Bar


/**
 * It's a RecyclerView.Adapter that takes a BarDetailListener and a BarLocalisationListener as
 * parameters, and uses them to bind the data to the view
*/
class BarAdapter(val clickDetailListener: BarDetailListener, val clickLocationListener: BarLocalisationListener) : ListAdapter<Bar, BarAdapter.ViewHolder>(BarLDiffCallback()) {

    /**
     * It binds the data to the view holder.
     *
     * @param holder ViewHolder - The ViewHolder which should be updated to represent the contents of
     * the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickDetailListener, clickLocationListener)
    }

    /**
     * The function takes in a parent view and a view type and returns a ViewHolder
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     * adapter position.
     * @param viewType This is the type of view that will be created.
     * @return A ViewHolder object
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /* It's a class that takes a BarItemBinding object as a parameter and binds it to a Bar object */
    class ViewHolder private constructor(val binding: BarItemBinding) : RecyclerView.ViewHolder(binding.root){

        /**
         * It binds the data to the view.
         *
         * @param item Bar is the object that will be used to populate the view.
         * @param clickDetailListener This is the listener that will be called when the user clicks on
         * the detail button.
         * @param clickLocationListener This is the listener that will be called when the user clicks
         * on the location button.
         */
        fun bind(item: Bar, clickDetailListener: BarDetailListener, clickLocationListener : BarLocalisationListener) {
            binding.bar = item
            binding.barDetailClickListener = clickDetailListener
            binding.barLocationClickListener = clickLocationListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BarItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


/* The BarLDiffCallback class is a Kotlin class that extends the DiffUtil.ItemCallback class and
overrides the areItemsTheSame and areContentsTheSame methods. */
class BarLDiffCallback : DiffUtil.ItemCallback<Bar>() {

    override fun areItemsTheSame(oldItem: Bar, newItem: Bar): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Bar, newItem: Bar): Boolean {
        return oldItem == newItem
    }
}


/* `BarLocalisationListener` is a class that takes a lambda as a constructor parameter and calls it
when the `onClick` method is called */
class BarLocalisationListener(val clickListener: (bar: String) -> Unit) {
    fun onClick(bar: Bar) = clickListener(bar.address)
}

/* "BarDetailListener is a class that takes a lambda as a constructor parameter and has a method that
calls the lambda."

The class is called BarDetailListener because it's a listener for the bar detail view. The class
takes a lambda as a constructor parameter. The lambda is called clickListener. The class has a
method called onClick that takes a bar as a parameter and calls the lambda */
class BarDetailListener(val clickListener: (bar: Bar) -> Unit) {
    fun onClick(bar: Bar) = clickListener(bar)
}
