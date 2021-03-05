package com.punpuf.e_bandejao.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.punpuf.e_bandejao.Const.Companion.FRAG_ARG_DAILY_MENU
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.vo.DailyMenu
import kotlinx.android.synthetic.main.inner_frag_daily_menu.*
import timber.log.Timber.d

class DailyMenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.inner_frag_daily_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf { it.containsKey(FRAG_ARG_DAILY_MENU) }?.apply {
            val dailyMenu = Gson().fromJson(getString(FRAG_ARG_DAILY_MENU), DailyMenu::class.java)
            // d("Hello from DailyMenuFragment, I have a arg $dailyMenu")

            //d("lunch not blank ${dailyMenu.lunchMenu}")
            if (dailyMenu.lunchMenu.isNotBlank()) {
                dailyMenuLunchContentTv.text = dailyMenu.lunchMenu
                if (dailyMenu.lunchCalories.isNotBlank() && dailyMenu.lunchCalories != "0") {
                    dailyMenuLunchTitleTv.text = getString(R.string.bandejao_lunch_calories, dailyMenu.lunchCalories)
                }
            }
            else dailyMenuLunchContentTv.text = getString(R.string.bandejao_content_empty)


            //d("dinner ${dailyMenu.dinnerMenu}")
            if (dailyMenu.dinnerMenu.isNotBlank()) {
                //d("dinner not blank")
                dailyMenuDinnerContentTv.text = dailyMenu.dinnerMenu
                if (dailyMenu.dinnerCalories.isNotBlank() && dailyMenu.dinnerCalories != "0") {
                    dailyMenuDinnerTitleTv.text = getString(R.string.bandejao_dinner_calories, dailyMenu.dinnerCalories)
                }
            }
            else dailyMenuDinnerContentTv.text = getString(R.string.bandejao_content_empty)

        }
    }
}