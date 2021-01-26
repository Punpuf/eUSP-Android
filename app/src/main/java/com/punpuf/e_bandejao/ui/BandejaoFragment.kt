package com.punpuf.e_bandejao.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.punpuf.e_bandejao.Const.Companion.FRAG_ARG_DAILY_MENU
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.model.BandejaoViewModel
import com.punpuf.e_bandejao.vo.WeeklyMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bandejao.*
import timber.log.Timber.d
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class BandejaoFragment : Fragment() {

    private val model: BandejaoViewModel by viewModels()
    private val tabDateFormatter = SimpleDateFormat("EE\n(dd/MM)", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_bandejao, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        d("BANDECO FOR LIVE ------------------------------")

        // Observes Selected Restaurant
        model.currentRestaurant.observe(viewLifecycleOwner) {
            d("onViewCreated/ currentRestaurant --> $it")
            bandejaoSelectRestaurantNameTv.text = it?.data?.name
            bandejaoSelectRestaurantCampusNameTv.text = it?.data?.campusName
        }

        // Observes Menu of Selected Restaurant
        model.weeklyMenu.observe(viewLifecycleOwner) {
            val weeklyMenu = it.data

            d("$it; restaurant is ${weeklyMenu?.restaurantId}; 5 dinner is ${weeklyMenu?.dailyMenus?.get(5)?.dinnerMenu}")

            bandejaoViewPager.adapter = DailyMenuFragmentAdapter(this, weeklyMenu)

            // configure titles for the tabs
            if (weeklyMenu != null) {
                TabLayoutMediator(bandejaoTabLayout, bandejaoViewPager) { tab, position ->
                    val date = Date(weeklyMenu.dailyMenus[position].dateProcessedStart)
                    tab.text = tabDateFormatter.format(date)
                }.attach()
            }

            d("tab layout pos ${model.tabLayoutPosition}")

            // set initial page to $today
            if (weeklyMenu != null && model.tabLayoutPosition == null) {
                var initPage = weeklyMenu.dailyMenus.size - 1
                val currentTime = System.currentTimeMillis()
                weeklyMenu.dailyMenus.forEachIndexed { index, dailyMenu ->
                    if (dailyMenu.dateProcessedStart <= currentTime &&
                        currentTime <= dailyMenu.dateProcessedEnd) {
                        initPage = index
                        d("daily menu found!!! $index: $dailyMenu")
                        return@forEachIndexed
                    }
                }
                bandejaoViewPager.setCurrentItem(initPage, false)
                model.tabLayoutPosition = initPage
            }

            else if (weeklyMenu != null && model.tabLayoutPosition != null) {
                bandejaoViewPager.setCurrentItem(model.tabLayoutPosition ?: 0, false)
            }

        }

        bandejaoSelectRestaurantLayout.setOnClickListener {
            val action = BandejaoFragmentDirections.actionBandejaoFragmentToRestaurantListFragment()
            findNavController().navigate(action)
        }

        // Adding Tooltips for Toolbar's Buttons
        TooltipCompat.setTooltipText(bandejaoToolbarSettingsBtn, getString(R.string.toolbar_settings_btn_description))

    }

    override fun onStop() {
        super.onStop()
        model.tabLayoutPosition = bandejaoTabLayout.selectedTabPosition
    }

    class DailyMenuFragmentAdapter(fragment: Fragment, val data: WeeklyMenu?) : FragmentStateAdapter(fragment) {

        /*private var data: WeeklyMenu? = null
        fun updateDate(newData: WeeklyMenu?) {
            data = newData
            notifyDataSetChanged()
        }*/

        override fun getItemCount(): Int = data?.dailyMenus?.size ?: 0

        override fun createFragment(position: Int): Fragment {
            val fragment = DailyMenuFragment()
            fragment.arguments = Bundle().apply { putString(FRAG_ARG_DAILY_MENU, Gson().toJson(data?.dailyMenus?.get(position))) }
            return fragment
        }

        /*override fun getItemId(position: Int): Long = ((data?.restaurantId ?: 0)
            * (data?.dailyMenus?.get(position)?.dateProcessedStart ?: 0)).toLong()

        override fun containsItem(itemId: Long): Boolean {
            if (data?.restaurantId == null) return false
            val mealStart = itemId / data!!.restaurantId

            for (meal in data?.dailyMenus ?: emptyList()) {
                if (meal.dateProcessedStart == mealStart) return true
            }
            return false
        }*/
    }



}