package com.punpuf.e_bandejao.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.model.SaldoViewModel
import com.punpuf.e_bandejao.util.Utils
import com.punpuf.e_bandejao.vo.Boleto
import com.punpuf.e_bandejao.vo.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_card.*
import kotlinx.android.synthetic.main.fragment_restaurant_list.*
import kotlinx.android.synthetic.main.fragment_saldo.*
import kotlinx.android.synthetic.main.layout_card_login.*
import timber.log.Timber.d
import timber.log.Timber.e

@AndroidEntryPoint
class SaldoFragment : Fragment() {

    private val model: SaldoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_saldo, container, false)

    // Configuring views: button clicks, edit text input
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().addOnDestinationChangedListener { _, _, _ ->
            restaurantListToolbar?.title = ""
        }

        // Toolbar buttons: Settings & Refresh
        libraryToolbarSettingsBtn.setOnClickListener {
            val settingsAction = SaldoFragmentDirections.actionSaldoFragmentToSettingsFragment()
            val extras = FragmentNavigatorExtras(it to "trans_dest_settings_main")
            findNavController().navigate(settingsAction, extras)
        }

        libraryToolbarSearchBtn.setOnClickListener { model.refreshBoleto() }

        // Click -> Login card button
        loginCardButton.setOnClickListener {
            val action = SaldoFragmentDirections.actionSaldoFragmentToLoginFragment()
            val extras = FragmentNavigatorExtras(it to "trans_dest_login_main")
            findNavController().navigate(action, extras)
        }

        // Processing deposit amount input edit text
        saldoDepositEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(text: Editable?) {
                var digits = Utils.stringToDigitsNoLeadingZero(text.toString())
                if (digits.isBlank()) digits = "0"
                val num = (digits.toInt() / 100.0)

                if (num > 200 || num < 20) saldoDepositSubmitBtn.isEnabled = false
                else {
                    saldoDepositSubmitBtn.isEnabled = true
                    saldoDepositSubmitBtn.setOnClickListener {
                        d("Button clicked when deposit amount is $num")
                        model.generateBoleto(num)
                    }
                }
                
                when (digits.length) {
                    0 -> digits = "000"
                    1 -> digits = "00$digits"
                    2 -> digits = "0$digits"
                }

                digits = digits.substring(0, digits.length - 2) + "," + digits.substring(
                    digits.length - 2,
                    digits.length
                )

                d("output: $digits")

                saldoDepositEditText.removeTextChangedListener(this)
                saldoDepositEditText.setText(digits)
                try {
                    saldoDepositEditText.setSelection(digits.length)
                } catch (exception: Exception) {
                    e(exception)
                }
                saldoDepositEditText.addTextChangedListener(this)
            }
        })

        // Adding Tooltips for Toolbar's Buttons
        TooltipCompat.setTooltipText(libraryToolbarSearchBtn, getString(R.string.toolbar_refresh_btn_description))
        TooltipCompat.setTooltipText(libraryToolbarSettingsBtn, getString(R.string.toolbar_settings_btn_description))
    }

    // Model data updates
    override fun onResume() {
        super.onResume()

        // indicates if user is logged in
        model.getUserInfo().observe(viewLifecycleOwner) { user ->
            if (user == null) {
                libraryToolbarSearchBtn.visibility = View.GONE
                libraryToolbarSearchBtn.isEnabled = false
                saldoLoginCardConstraintLayout.visibility = View.VISIBLE
            }
            else {
                libraryToolbarSearchBtn.visibility = View.VISIBLE
                saldoLoginCardConstraintLayout.visibility = View.GONE
            }
        }

        // account balance
        model.accountBalance.observe(viewLifecycleOwner) {
            libraryTitleTv.text = if (it?.data == null) getString(R.string.saldo_title) else getString(R.string.saldo_reais_title_template, it.data)
        }

        // ongoing boleto data
        model.ongoingBoleto.observe(viewLifecycleOwner) {
            // user not logged in
            if (it == null) {
                saldoBoletoLayout.visibility = View.GONE
                saldoDepositLayout.visibility = View.GONE
                return@observe
            }

            // disable buttons while loading
            if (it.status == Resource.Status.LOADING) {
                Utils.makeViewsVisible(saldoProgressBar)
                Utils.disableButtons(
                    libraryToolbarSearchBtn,
                    saldoDepositSubmitBtn,
                    saldoBoletoCopyCodeBtn,
                    saldoBoletoDeleteBtn,
                )
            }
            // enable them afterwards
            else {
                Utils.makeViewsGone(saldoProgressBar)
                Utils.enableButtons(
                    libraryToolbarSearchBtn,
                    saldoBoletoCopyCodeBtn,
                    saldoBoletoDeleteBtn,
                )
                setSubmitBtnState(saldoDepositEditText.text)
            }

            // no ongoing boleto
            if (it.data == null) {
                saldoBoletoLayout.visibility = View.GONE
                saldoDepositLayout.visibility = View.VISIBLE
                return@observe
            }

            // ongoing boleto available
            saldoBoletoLayout.visibility = View.VISIBLE
            saldoDepositLayout.visibility = View.GONE

            configureBoletoView(it.data)

        }
    }

    // Configures the view with info of the open boleto
    private fun configureBoletoView(boleto: Boleto) {
        saldoBoletoTitleTv.text = getString(R.string.saldo_boleto_ongoing, boleto.amount)
        saldoBoletoExpirationDateTv.text = getString(
            R.string.saldo_boleto_expiration_date,
            boleto.expirationDate
        )
        saldoBoletoCodeTv.text = boleto.barcode?. replace(" ", "\n")

        saldoBoletoCopyCodeBtn.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            clipboard?.setPrimaryClip(ClipData.newPlainText("", boleto.barcode))
            Toast.makeText(context, R.string.saldo_boleto_code_copied, Toast.LENGTH_SHORT).show()
        }

        saldoBoletoDeleteBtn.setOnClickListener {
            model.deleteBoleto(boleto.id)
        }
    }

    private fun setSubmitBtnState(text: Editable?) {
        var digits = Utils.stringToDigitsNoLeadingZero(text.toString())
        if (digits.isBlank()) digits = "0"
        val num = (digits.toInt() / 100.0)

        saldoDepositSubmitBtn.isEnabled = !(num > 200 || num < 20)
    }

}