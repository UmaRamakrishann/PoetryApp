package com.udacity.capstone.poetry.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.udacity.capstone.poetry.R
import com.udacity.capstone.poetry.databinding.PoetryItemBinding
import com.udacity.capstone.poetry.domain.Poem

class PoetryAdapter(val callback: PoemClick) :
    RecyclerView.Adapter<PoetryAdapter.PoetryViewHolder>() {

    /**
     * The poems that our Adapter will show
     */
    var poems: List<Poem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoetryViewHolder {
        val withDataBinding: PoetryItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            PoetryViewHolder.LAYOUT,
            parent,
            false
        )
        return PoetryViewHolder(withDataBinding)
    }

    override fun getItemCount() = poems.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: PoetryViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.poem = poems[position]
            it.poemCallback = callback
        }
        //   holder.viewDataBinding.clickableOverlay.contentDescription = "Poem ${position}: " +
        //     "${poems[position].author}, poem title ${poems[position].title} "
    }

    class PoetryViewHolder(val viewDataBinding: PoetryItemBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {

        companion object {
            @LayoutRes
            val LAYOUT = R.layout.poetry_item
        }
    }

    class PoemClick(val block: (Poem) -> Unit) {
        /**
         * Called when a poem is clicked
         *
         * @param poem selected
         */
        fun onClick(poem: Poem) = block(poem)
    }

}