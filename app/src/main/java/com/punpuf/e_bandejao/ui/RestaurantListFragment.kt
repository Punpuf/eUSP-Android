package com.punpuf.e_bandejao.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import com.google.android.material.transition.MaterialContainerTransform
import com.google.gson.Gson
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.model.RestaurantListViewModel
import com.punpuf.e_bandejao.util.RestaurantListItemClickListener
import com.punpuf.e_bandejao.vo.Restaurant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_restaurant_list.*
import kotlinx.android.synthetic.main.list_item_select_restaurant.view.*
import timber.log.Timber.d

@AndroidEntryPoint
class RestaurantListFragment : Fragment(), RestaurantListItemClickListener {

    private val model: RestaurantListViewModel by viewModels()
    private val adapter = RestaurantListRecyclerAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            drawingViewId = R.id.mainNavHostFragment
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
        }

        return inflater.inflate(R.layout.fragment_restaurant_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationUI.setupWithNavController(restaurantListToolbar, NavHostFragment.findNavController(this))
        restaurantListToolbar.title = ""
        findNavController().addOnDestinationChangedListener { _, _, _ ->
            restaurantListToolbar?.title = ""
        }

        restaurantListRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        model.getRestaurantList().observe(viewLifecycleOwner) {
            if (it.data != null) adapter.updateData(it.data)
        }
    }

    override fun notifyRestaurantSelected(restaurantId: Int) {
        model.setSelectedRestaurant(restaurantId)
        findNavController().navigateUp()
    }

    override fun openRestaurantInfo(restaurant: Restaurant) {
        val action = RestaurantListFragmentDirections.actionRestaurantListFragmentToRestaurantInfoFragment(Gson().toJson(restaurant))
        findNavController().navigate(action)
    }


    private class RestaurantListRecyclerAdapter(
        val clickInterface: RestaurantListItemClickListener,
    ) : RecyclerView.Adapter<RestaurantListRecyclerViewHolder>() {

        private var data: List<Restaurant> = emptyList()

        fun updateData (newData: List<Restaurant>) {
            data = newData
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantListRecyclerViewHolder {
            return RestaurantListRecyclerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_select_restaurant, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RestaurantListRecyclerViewHolder, position: Int) {
            val restaurant = data[position]
            holder.titleTv.text = restaurant.name
            holder.descriptionTv.text = restaurant.campusName
            holder.layout.setOnClickListener { clickInterface.notifyRestaurantSelected(restaurant.id) }
            holder.infoBtn.setOnClickListener { clickInterface.openRestaurantInfo(restaurant) }
        }

        override fun getItemCount(): Int = data.size

    }

    private class RestaurantListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout: View = itemView.restaurantListItemLayout
        val titleTv: TextView = itemView.restaurantListItemTitleTv
        val descriptionTv: TextView = itemView.restaurantListItemDescriptionTv
        val infoBtn: Button = itemView.restaurantListItemInfoBtn
    }

}


