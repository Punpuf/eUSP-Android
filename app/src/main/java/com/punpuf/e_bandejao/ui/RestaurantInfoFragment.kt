package com.punpuf.e_bandejao.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.google.android.material.shape.CornerFamily
import com.google.gson.Gson
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.vo.Restaurant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_restaurant_info.*

@AndroidEntryPoint
class RestaurantInfoFragment : Fragment() {

    private val args: RestaurantInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_restaurant_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationUI.setupWithNavController(restaurantInfoToolbar, NavHostFragment.findNavController(this))
        restaurantInfoToolbar.title = ""

        val cardRadius = resources.getDimension(R.dimen.card_display_radius)
        val appearanceModel = restaurantInfoThumbnailIv.shapeAppearanceModel.toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, cardRadius)
            .build()
        restaurantInfoThumbnailIv.shapeAppearanceModel = appearanceModel

        val restaurant = Gson().fromJson(args.restaurant, Restaurant::class.java)

        restaurantInfoNameTv.text = restaurant.name
        restaurantInfoCampusTv.text = restaurant.campusName

        // hours
        var hours = ""
        if (restaurant.workingHours_weekday?.isNotBlank() == true) hours += getString(R.string.restaurant_info_hours_weekdays, restaurant.workingHours_weekday) + "\n"
        if (restaurant.workingHours_saturday?.isNotBlank() == true) hours += getString(R.string.restaurant_info_hours_saturday, restaurant.workingHours_saturday) + "\n"
        if (restaurant.workingHours_sunday?.isNotBlank() == true) hours += getString(R.string.restaurant_info_hours_sunday, restaurant.workingHours_sunday) + "\n"
        if (hours.isNotBlank()) {
            restaurantInfoHoursTitleTv.visibility = View.VISIBLE
            restaurantInfoHoursContentTv.visibility = View.VISIBLE
            restaurantInfoHoursContentTv.text = hours.removeSuffix("\n")
        }
        else {
            restaurantInfoHoursTitleTv.visibility = View.GONE
            restaurantInfoHoursContentTv.visibility = View.GONE
        }


        // location
        var location = restaurant.address
        if (restaurant.latitude?.isNotBlank() == true && restaurant.longitude?.isNotBlank() == true) {
            location += "(${restaurant.latitude} ${restaurant.longitude})"
        }
        if (location?.isNotBlank() == true) {
            restaurantInfoLocationTitleTv.visibility = View.VISIBLE
            restaurantInfoLocationContentTv.visibility = View.VISIBLE
            restaurantInfoLocationContentTv.text = location
        }
        else {
            restaurantInfoLocationTitleTv.visibility = View.GONE
            restaurantInfoLocationContentTv.visibility = View.GONE
        }

        // cashier
        if (restaurant.cashierInfo?.isNotBlank() == true) {
            restaurantInfoCashierTitleTv.visibility = View.VISIBLE
            restaurantInfoCashierContentTv.visibility = View.VISIBLE
            restaurantInfoCashierContentTv.text = restaurant.cashierInfo
        }
        else {
            restaurantInfoCashierTitleTv.visibility = View.GONE
            restaurantInfoCashierContentTv.visibility = View.GONE
        }

        // phone
        if (restaurant.phoneNumber?.isNotBlank() == true) {
            restaurantInfoPhoneTitleTv.visibility = View.VISIBLE
            restaurantInfoPhoneContentTv.visibility = View.VISIBLE
            restaurantInfoPhoneContentTv.text = restaurant.phoneNumber
        }
        else {
            restaurantInfoPhoneTitleTv.visibility = View.GONE
            restaurantInfoPhoneContentTv.visibility = View.GONE
        }

        Glide.with(this).load(restaurant.thumbnailUrl).into(restaurantInfoThumbnailIv)
        
    }

}