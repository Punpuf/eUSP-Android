package com.punpuf.e_usp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.punpuf.e_usp.R
import com.punpuf.e_usp.model.LibraryViewModel
import com.punpuf.e_usp.ui_more.BookUserRecyclerAdapter
import com.punpuf.e_usp.util.BookUserClickInterface
import com.punpuf.e_usp.util.Utils
import com.punpuf.e_usp.vo.BookUser
import com.punpuf.e_usp.vo.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.layout_card_login.*
import timber.log.Timber.d

@AndroidEntryPoint
class LibraryFragment : Fragment(), BookUserClickInterface {

    private val model: LibraryViewModel by viewModels()
    private val loanAdapter = BookUserRecyclerAdapter(this)
    private val reservationAdapter = BookUserRecyclerAdapter(this)
    private val historyAdapter = BookUserRecyclerAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_library, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryBorrowList.adapter = loanAdapter
        libraryReservationList.adapter = reservationAdapter
        libraryHistoryList.adapter = historyAdapter

        // Click -> Login Button
        loginCardButton.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToLoginFragment()
            val extras = FragmentNavigatorExtras(it to getString(R.string.transition_to_login_main))
            view.findNavController().navigate(action, extras)
        }

        // Click -> Settings
        libraryToolbarSettingsBtn.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToSettingsFragment()
            val extras = FragmentNavigatorExtras(it to getString(R.string.transition_to_settings_main))
            findNavController().navigate(action, extras)
        }

        // Adding Tooltips for Toolbar's Buttons
        TooltipCompat.setTooltipText(libraryToolbarSearchBtn, getString(R.string.toolbar_search_btn_description))
        TooltipCompat.setTooltipText(libraryToolbarSettingsBtn, getString(R.string.toolbar_settings_btn_description))
    }

    override fun onResume() {
        super.onResume()

        model.userInfo.observe(viewLifecycleOwner) { user ->
            if (user == null) {
                Utils.makeViewsVisible(libraryLoginCardLayout)
                Utils.makeViewsGone(libraryBorrowLayout, libraryReservationLayout, libraryHistoryLayout)
            } else {
                Utils.makeViewsGone(libraryLoginCardLayout)
                Utils.makeViewsVisible(libraryBorrowLayout, libraryReservationLayout, libraryHistoryLayout)
            }
        }

        model.libraryLoans.observe(viewLifecycleOwner) { data: Resource<List<BookUser>>? ->
            if (data?.status == Resource.Status.LOADING) Utils.makeViewsVisible(libraryBorrowProgressBar)
            else Utils.makeViewsGone(libraryBorrowProgressBar)

            loanAdapter.updateData(data?.data ?: emptyList())
        }

        model.libraryReservations.observe(viewLifecycleOwner) { data: Resource<List<BookUser>>? ->
            d("reservations ||| status: ${data?.status}; data: ${data?.data}")
            if (data?.status == Resource.Status.LOADING) Utils.makeViewsVisible(libraryReservationProgressBar)
            else Utils.makeViewsGone(libraryReservationProgressBar)

            reservationAdapter.updateData(data?.data ?: emptyList())
        }

        model.libraryHistory.observe(viewLifecycleOwner) { data: Resource<List<BookUser>>? ->
            if (data?.status == Resource.Status.LOADING) Utils.makeViewsVisible(libraryHistoryProgressBar)
            else Utils.makeViewsGone(libraryHistoryProgressBar)

            historyAdapter.updateData(data?.data ?: emptyList())
        }

    }

    override fun notifyBookUserClicked(bookUser: BookUser) {
        d("book clicked $bookUser")
    }

}