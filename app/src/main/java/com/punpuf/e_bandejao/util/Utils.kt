package com.punpuf.e_bandejao.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.widget.Button
import androidx.core.app.NotificationManagerCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import timber.log.Timber.e
import java.util.*

class Utils {
    companion object {

        // Provides a no margin config for code generation
        val noMarginEncodeHint: MutableMap<EncodeHintType, Any> =
            EnumMap<EncodeHintType, Any>(
                EncodeHintType::class.java
            ).apply {
                this[EncodeHintType.CHARACTER_SET] = "UTF-8"
                this[EncodeHintType.MARGIN] = 0
            }

        // Generate Bitmap of codes
        fun getQrcodeBitmap(code: String?, width: Int = 300, height: Int = 300): Bitmap? {
            if (code.isNullOrEmpty()) return null
            val qrcodeBitMatrix = MultiFormatWriter().encode(
                code, BarcodeFormat.QR_CODE,
                width, height, noMarginEncodeHint
            )
            return BarcodeEncoder().createBitmap(qrcodeBitMatrix)
        }

        fun getBarcodeBitmap(code: String?, width: Int = 500, height: Int = 375): Bitmap? {
            try {
                if (code.isNullOrEmpty()) return null

                var barcode = code
                if (barcode.length % 2 == 1) barcode = "0$barcode"

                val barcodeBitMatrix = MultiFormatWriter().encode(
                    barcode,
                    BarcodeFormat.ITF,
                    width,
                    height,
                    noMarginEncodeHint
                )
                return BarcodeEncoder().createBitmap(barcodeBitMatrix)
            } catch (e: Exception) { e("getBarcodeBitmap input: $code; $e") }
            return null
        }

        // Notification Helpers
        fun sendNotificationWithMsg(appContext: Context, message: String) {
            return
            /*createNotificationChannel(appContext)

            val builder = NotificationCompat.Builder(appContext, "work play sleep")
                .setSmallIcon(R.drawable.ic_account_circle)
                .setContentTitle("Working!")
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message)).priority =
                NotificationCompat.PRIORITY_DEFAULT

            NotificationManagerCompat.from(appContext).notify(System.currentTimeMillis().toInt(), builder.build())*/
        }

        /*private fun createNotificationChannel(appContext: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("work play sleep", "Soy un canal", importance).apply {
                    description = "Nosotros somos buenos"
                }
                // Register the channel with the system
                val notificationManager = NotificationManagerCompat.from(appContext)
                notificationManager.createNotificationChannel(channel)
            }
        }*/

        private fun stringToDigits(string: String?): String {
            return Regex("\\D").replace(string ?: "", "")
        }

        fun stringToDigitsNoLeadingZero(string: String?): String {
            if (string.isNullOrBlank()) return ""
            return Regex("^0+(?!\$)").replace(stringToDigits(string), "")
        }

        fun makeViewsVisible (vararg views: View) {
            for (view in views) view.visibility = View.VISIBLE
        }

        fun makeViewsGone (vararg views: View) {
            for (view in views ) view.visibility = View.GONE
        }

        fun enableButtons(vararg buttons: View) {
            for (btn in buttons) btn.isEnabled = true
        }

        fun disableButtons(vararg buttons: View) {
            for (btn in buttons) btn.isEnabled = false
        }
    }
}