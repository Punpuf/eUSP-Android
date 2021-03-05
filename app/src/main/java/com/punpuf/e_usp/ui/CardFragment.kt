package com.punpuf.e_usp.ui

import android.graphics.*
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.Const.Companion.SHORTCUT_BARCODE_INTENT_DATA
import com.punpuf.e_usp.Const.Companion.SHORTCUT_QRCODE_INTENT_DATA
import com.punpuf.e_usp.util.Utils
import com.punpuf.e_usp.model.CardViewModel
import com.punpuf.e_usp.vo.ProfilePictureInfo
import com.punpuf.e_usp.vo.Resource
import com.punpuf.e_usp.vo.UserInfo
import com.punpuf.e_usp.vo.UserProfile
import com.google.android.material.shape.CornerFamily
import com.google.zxing.BarcodeFormat
import com.punpuf.e_usp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_card.*
import kotlinx.android.synthetic.main.layout_card_login.*
import timber.log.Timber.d
import timber.log.Timber.e
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CardFragment : Fragment() {

    private val model: CardViewModel by viewModels()

    private var userInfo: UserInfo? = null
    private var userProfileListResource: Resource<List<UserProfile>>? = null
    private var profilePictureInfoResource: Resource<ProfilePictureInfo?>? = null

    private var currentProfileId = 0 // used for users with multiple profiles available

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_card, container, false)

    // Configuring views
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Shortcuts
        val intentData = activity?.intent?.data
        if (intentData.toString() == SHORTCUT_QRCODE_INTENT_DATA) model.setAutoOpenQrcode(true)
        else if (intentData.toString() == SHORTCUT_BARCODE_INTENT_DATA) model.setAutoOpenBarcode(true)
        activity?.intent?.data = null

        // Click -> QR Code Card
        cardQrcodeLayout.setOnClickListener {
            val codeType = BarcodeFormat.QR_CODE
            val code = userProfileListResource?.data?.get(currentProfileId)?.qrCodeToken
            if (code.isNullOrBlank()) {
                Toast.makeText(context, R.string.card_qrcode_toast_unavailable, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val action = CardFragmentDirections.actionCardFragmentToCodeDisplayDialogFragment(codeType, code)
            view.findNavController().navigate(action)
        }

        // Click -> Barcode Card
        cardBarcodeLayout.setOnClickListener {
            val codeType = BarcodeFormat.ITF
            val code = userInfo?.numberUSP
            if (code.isNullOrBlank()) return@setOnClickListener

            val action = CardFragmentDirections.actionCardFragmentToCodeDisplayDialogFragment(codeType, code)
            view.findNavController().navigate(action)
        }

        // Click -> Login Button
        loginCardButton.setOnClickListener {
            val action = CardFragmentDirections.actionCardFragmentToLoginFragment()
            val extras = FragmentNavigatorExtras(it to getString(R.string.transition_to_login_main))
            view.findNavController().navigate(action, extras)
        }

        // Click -> Settings
        cardToolbarSettingsBtn.setOnClickListener {
            val action = CardFragmentDirections.actionCardFragmentToSettingsFragment()
            val extras = FragmentNavigatorExtras(it to getString(R.string.transition_to_settings_main))
            findNavController().navigate(action, extras)
        }

        // Give profile picture's top right corner a radius
        val cardRadius = resources.getDimension(R.dimen.card_display_radius)
        val cardNoRadius = resources.getDimension(R.dimen.card_no_radius)
        val appearanceModel = cardEcardProfilePicIv.shapeAppearanceModel.toBuilder()
            .setTopRightCorner(CornerFamily.ROUNDED, cardRadius)
            .setTopLeftCorner(CornerFamily.ROUNDED, cardNoRadius)
            .setBottomRightCorner(CornerFamily.ROUNDED, cardNoRadius)
            .setBottomLeftCorner(CornerFamily.ROUNDED, cardNoRadius)
            .build()
        cardEcardProfilePicIv.shapeAppearanceModel = appearanceModel

        // Adding Tooltips for Toolbar's Buttons
        TooltipCompat.setTooltipText(cardToolbarProfileSwapBtn, getString(R.string.toolbar_profile_change_btn_description))
        TooltipCompat.setTooltipText(cardToolbarSettingsBtn, getString(R.string.toolbar_settings_btn_description))
    }

    // Adding listeners to view model
    override fun onResume() {
        super.onResume()

        // Feed -> User Info
        model.userInfo.observe(viewLifecycleOwner) {
            d("User Info -> UI Update: $it")
            userInfo = it
            updateUserInfoUI()
        }

        // Feed -> Resource of Profile List
        model.userProfileList.observe(viewLifecycleOwner) { profileListData: Resource<List<UserProfile>>? ->
            d("Profile List -> UI Update: ${profileListData?.data}")
            userProfileListResource = profileListData
            updateUserProfileUI()
        }

        // Feed -> Resource of Profile Picture
        model.userProfilePicture.observe(viewLifecycleOwner) { profilePictureData: Resource<ProfilePictureInfo?>? ->
            d("Profile Picture -> UI Update: ${profilePictureData?.data}")
            profilePictureInfoResource = profilePictureData
            updateProfilePictureUI()
        }
    }

    // Updates logged-in UI state, card, and barcode card
    private fun updateUserInfoUI() {

        // User not logged in
        if (userInfo == null || userInfo?.numberUSP == "") {
            Utils.makeViewsVisible(
                cardLoginLayout,
            )
            Utils.makeViewsGone(
                cardToolbarProfileSwapBtn,
                cardBarcodeLayout,
                cardQrcodeLayout,
            )
            cardEcardUserNameTv.text = ""
            cardEcardUserDepartmentTv.text = ""
            cardQrcodeStatusTv.text = ""

            return
        }

        // Shortcut -> open Dialog
        if (model.getAutoOpenBarcode() == true) {
            model.setAutoOpenBarcode(false)
            val delay = System.currentTimeMillis() - model.getAutoOpenSetTime()
            if (delay < Const.SHORTCUT_MAX_OPEN_DELAY) {
                cardBarcodeLayout.performClick()
            }
        }

        // Populating views with info
        cardLoginLayout.visibility = View.GONE

        cardEcardUserNameTv.text = userInfo?.name
        cardEcardUserDepartmentTv.text = userInfo?.departmentName

        cardQrcodeLayout.visibility = View.VISIBLE // added here as well to avoid layout misconfiguration
        cardQrcodeQrcodeIv.setImageResource(R.color.app_card_profile_picture_background)

        cardBarcodeLayout.visibility = View.VISIBLE
        cardBarcodeCodeTv.text = getString(R.string.card_barcode_code, userInfo?.numberUSP)
        cardBarcodeBarcodeIv.setImageBitmap(Utils.getBarcodeBitmap(userInfo?.numberUSP))
    }

    //Updates qrcode, card with the user's profiles
    private fun updateUserProfileUI() {

        // User not logged in
        if (userInfo == null || userInfo?.numberUSP.isNullOrEmpty()) {
            cardQrcodeLayout.visibility = View.GONE
            cardEcardUserGroupTv.text = ""
            cardEcardUserTypeTv.text = ""
            cardEcardUserExpirationDateTv.text = ""
            cardEcardGradientBackground.setBackgroundResource(R.drawable.card_card_gradient_yellow)
            return
        }

        // Configure Switch current profile button
        val numProfiles = userProfileListResource?.data?.size ?: 0
        if (numProfiles > 1) {
            cardToolbarProfileSwapBtn.visibility = View.VISIBLE
            cardToolbarProfileSwapBtn.setOnClickListener {
                // todo test this
                currentProfileId += 1
                if (currentProfileId >= numProfiles) currentProfileId = 0
                updateUserProfileUI()
            }
        } else { // only one profile
            cardToolbarProfileSwapBtn.visibility = View.GONE
        }

        // User logged in, but maybe no user profile available
        val currentProfile = userProfileListResource?.data?.getOrNull(currentProfileId)
        val currentState = userProfileListResource?.status

        cardEcardUserGroupTv.text = currentProfile?.userGroup ?: ""
        cardEcardUserTypeTv.text = currentProfile?.userType ?: ""
        cardEcardUserExpirationDateTv.text =
            if(!currentProfile?.cardExpirationDate.isNullOrEmpty()) {
                getString(R.string.card_ecard_expiration_date, currentProfile?.cardExpirationDate?.replace("-", "/"))
            } else { "" }

        // Set gradient background color
        if (!currentProfile?.userTypeCode.isNullOrEmpty() &&
            currentProfile?.userTypeCode != "101" && currentProfile?.userTypeCode != "102") {
            cardEcardGradientBackground.setBackgroundResource(R.drawable.card_card_gradient_blue)
        } else {
            cardEcardGradientBackground.setBackgroundResource(R.drawable.card_card_gradient_yellow)
        }

        cardQrcodeLayout.visibility = View.VISIBLE
        cardQrcodeQrcodeIv.setImageBitmap(Utils.getQrcodeBitmap(currentProfile?.qrCodeToken))

        // Get structured date of token expiration
        val tokenExpirationDate: Date? =
            try { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .parse(currentProfile?.qrCodeTokenExpiration ?: "")
            } catch (exception: Exception) { null }
        val nowCalendar = Calendar.getInstance()
        val tokenExpirationDateCalendar = Calendar.getInstance().apply {
            timeInMillis = tokenExpirationDate?.time ?: 0
        }
        val isTokenValid = nowCalendar.before(tokenExpirationDateCalendar)

        // token valid
        if (isTokenValid) {
            if (DateUtils.isToday(tokenExpirationDate?.time ?: 0)) {
                cardQrcodeStatusTv.text = getString(R.string.card_qrcode_expiration_date_msg_today_valid, DateFormat.format("HH:mm", tokenExpirationDate))
            } else {
                cardQrcodeStatusTv.text = getString(R.string.card_qrcode_expiration_date_msg_future_valid, DateFormat.format("dd/MM/yyyy", tokenExpirationDate), DateFormat.format("HH:mm", tokenExpirationDate))
            }
        }

        // token expired, but already updated
        else {
            when (currentState) {
                Resource.Status.SUCCESS -> cardQrcodeStatusTv.text = getString(R.string.card_qrcode_expiration_date_state_invalid_successful)
                Resource.Status.LOADING -> cardQrcodeStatusTv.text = getString(R.string.card_qrcode_expiration_date_state_invalid_loading)
                Resource.Status.ERROR -> cardQrcodeStatusTv.text = getString(R.string.card_qrcode_expiration_date_state_invalid_error)
                null -> cardQrcodeStatusTv.text = getString(R.string.card_qrcode_expiration_date_state_invalid_successful)
            }

            cardQrcodeStatusTv.text =
                if (tokenExpirationDate != null) getString(R.string.card_qrcode_expiration_date_msg_invalid, DateFormat.format("dd/MM/yyyy", tokenExpirationDate), DateFormat.format("HH:mm", tokenExpirationDate))
                else ""
        }

        // Shortcut -> open QRCode Dialog
        if (!currentProfile?.qrCodeToken.isNullOrEmpty() && isTokenValid && model.getAutoOpenQrcode() == true) {
            model.setAutoOpenQrcode(false)
            val delay = System.currentTimeMillis() - model.getAutoOpenSetTime()
            if (delay < Const.SHORTCUT_MAX_OPEN_DELAY) {
                cardQrcodeLayout.performClick()
            }
        }

    }

    // Tries to set ecard profile picture to iv if reference is not null
    private fun updateProfilePictureUI() {
        if (profilePictureInfoResource?.data == null ||
            profilePictureInfoResource?.data?.location.isNullOrEmpty()) {
            cardEcardProfilePicIv.setImageResource(R.color.app_card_profile_picture_background)
            return
        }

        try {
            val profilePictureInfo = profilePictureInfoResource?.data
            val inputStream = context?.openFileInput(profilePictureInfo?.location)
            val profilePicBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (profilePicBitmap != null) cardEcardProfilePicIv.setImageBitmap(profilePicBitmap)
            else { cardEcardProfilePicIv.setImageResource(R.color.app_card_profile_picture_background) }

        } catch (exception: Exception) { e("Profile Picture UI -> $exception") }
    }

}