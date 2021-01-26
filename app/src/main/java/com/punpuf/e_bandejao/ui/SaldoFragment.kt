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
import androidx.navigation.fragment.findNavController
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.model.SaldoViewModel
import com.punpuf.e_bandejao.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_card.*
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saldoToolbarSettingsBtn.setOnClickListener { model.logoutUser() }

        loginCardButton.setOnClickListener {
            val action = SaldoFragmentDirections.actionSaldoFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        model.userInfo.observe(viewLifecycleOwner) { user ->
            if (user == null) {
                saldoToolbarSettingsBtn.visibility = View.GONE
                saldoToolbarRefreshBtn.visibility = View.GONE
                saldoLoginCardConstraintLayout.visibility = View.VISIBLE
            }
            else {
                saldoToolbarSettingsBtn.visibility = View.VISIBLE
                saldoToolbarRefreshBtn.visibility = View.VISIBLE
                saldoLoginCardConstraintLayout.visibility = View.GONE
            }
        }

        model.accountBalance.observe(viewLifecycleOwner) {
            saldoAccountBalanceTv.text = if (it?.data == null) getString(R.string.saldo_title) else getString(R.string.saldo_reais_title_template, it.data)
        }

        model.ongoingBoleto.observe(viewLifecycleOwner) {
            // user not logged in
            if (it == null) {
                saldoBoletoLayout.visibility = View.GONE
                saldoDepositLayout.visibility = View.GONE
                return@observe
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

            val boleto = it.data

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
        TooltipCompat.setTooltipText(saldoToolbarRefreshBtn, getString(R.string.toolbar_refresh_btn_description))
        TooltipCompat.setTooltipText(saldoToolbarSettingsBtn, getString(R.string.toolbar_settings_btn_description))
    }
}