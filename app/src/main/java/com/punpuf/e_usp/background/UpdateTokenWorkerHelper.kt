package com.punpuf.e_usp.background

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.*
import com.punpuf.e_usp.Const.Companion.WORKER_UPDATE_TOKEN_ID
import com.punpuf.e_usp.Const.Companion.WORKER_UPDATE_TOKEN_INITIAL_RETRY_DELAY_MINUTES
import com.punpuf.e_usp.Const.Companion.WORKER_UPDATE_TOKEN_REQUEST_INTERVAL
import com.punpuf.e_usp.Const.Companion.WORKER_UPDATE_TOKEN_START_HOUR_REQUEST
import com.punpuf.e_usp.R
import timber.log.Timber.d
import java.util.*
import java.util.concurrent.TimeUnit

class UpdateTokenWorkerHelper (
    private val context: Context
) {

    fun enqueueUpdateWorkerRequest(existingWorkPolicy: ExistingWorkPolicy) {
        d("Enqueuing work")
        val preference = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.prefs_qr_code_update_key), context.getString(R.string.prefs_qr_code_update_default_value))
        var networkType = NetworkType.CONNECTED

        when (preference) {
            // wifi only
            context.getString(R.string.prefs_qr_code_update_value_wifi) -> {
                networkType = NetworkType.UNMETERED
            }
            // any network
            context.getString(R.string.prefs_qr_code_update_value_any_connection) -> {
                networkType = NetworkType.CONNECTED
            }
            // deactivated
            context.getString(R.string.prefs_qr_code_update_value_deactivated) -> {
                return
            }
        }

        d("Will use the following network type $networkType")

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(networkType)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<UpdateTokenWorker>()
            .setInitialDelay(getInitialDelayDurationMillis(), TimeUnit.MILLISECONDS)
            .setConstraints(workConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WORKER_UPDATE_TOKEN_INITIAL_RETRY_DELAY_MINUTES, TimeUnit.MINUTES)
            .build()

        val result = WorkManager.getInstance(context)
            .enqueueUniqueWork(WORKER_UPDATE_TOKEN_ID, existingWorkPolicy, workRequest)
        d("Result of Enqueuing work is: $result")
    }

    fun cancelUpdateWorker() {
        d("Cancelling work")
        WorkManager.getInstance(context).cancelUniqueWork(WORKER_UPDATE_TOKEN_ID)
    }

    private fun getInitialDelayDurationMillis(): Long {
        val currentDate = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        // will start tomorrow
        if (currentDate.get(Calendar.HOUR_OF_DAY) >= WORKER_UPDATE_TOKEN_START_HOUR_REQUEST) {
            startDate.add(Calendar.DAY_OF_YEAR, 1)
        }
        startDate.set(Calendar.HOUR_OF_DAY, WORKER_UPDATE_TOKEN_START_HOUR_REQUEST)
        startDate.set(Calendar.MINUTE, 0)
        startDate.set(Calendar.SECOND, 0)

        val randomDelayAdditionMinutes = (0..(WORKER_UPDATE_TOKEN_REQUEST_INTERVAL * 60)).random()
        startDate.add(Calendar.MINUTE, randomDelayAdditionMinutes)

        d("Delay was of $randomDelayAdditionMinutes minutes; Start date will be $startDate")
        return startDate.timeInMillis - currentDate.timeInMillis
    }

}