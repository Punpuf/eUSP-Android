package com.punpuf.e_bandejao.ui

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.util.Utils
import kotlinx.android.synthetic.main.dialog_barcode.*
import kotlinx.android.synthetic.main.dialog_qrcode.*

class CodeDisplayDialogFragment : DialogFragment() {

    private val args: CodeDisplayDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val codeType = args.codeType
        val code = args.code

        return Dialog(requireContext(), R.style.AppDialogTheme).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            if (codeType == BarcodeFormat.QR_CODE) {
                setContentView(R.layout.dialog_qrcode)
                this.dialogQrcodeQrcodeIv.setImageBitmap(getCodeBitmap(codeType, code, false))
            }
            else if (codeType == BarcodeFormat.ITF) {
                setContentView(R.layout.dialog_barcode)
                this.dialogBarcodeBarcodeIv.setImageBitmap(
                    getCodeBitmap(
                        codeType,
                        code,
                        code.length > 20
                    )
                )
                if (code.length > 20) {
                    val set = ConstraintSet()
                    set.clone(this.dialogBarcodeConstraintLayout)
                    set.setDimensionRatio(this.dialogBarcodeBarcodeIv.id, "8:1")
                    set.applyTo(this.dialogBarcodeConstraintLayout)

                }
            }

            this.window?.setLayout(
                resources.getInteger(R.integer.code_dialog_width_type), resources.getInteger(
                    R.integer.code_dialog_height_type
                )
            )
            this.window?.attributes = this.window?.attributes?.apply { screenBrightness = 1.0f }
            show()
        }
    }


    private fun getCodeBitmap(codeType: BarcodeFormat, code: String, isLong: Boolean): Bitmap {
        var width = 0; var height= 0
        when {
            isLong -> { width = 800; height = 100 }
            codeType == BarcodeFormat.QR_CODE -> { width = 500; height = 500 }
            codeType == BarcodeFormat.ITF -> { width = 600; height = 450 }
        }

        val qrcodeBitMatrix = MultiFormatWriter().encode(
            code,
            codeType,
            width,
            height,
            Utils.noMarginEncodeHint
        )
        return BarcodeEncoder().createBitmap(qrcodeBitMatrix)
    }


}