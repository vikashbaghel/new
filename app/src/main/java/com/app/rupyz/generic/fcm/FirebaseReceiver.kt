package com.app.rupyz.generic.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.rupyz.R
import com.app.rupyz.InitialActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant.FCM_TOKEN
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.sales.customer.CustomerFeedbackDetailActivity
import com.app.rupyz.sales.lead.LeadDetailsActivity
import com.app.rupyz.sales.orderdispatch.OrderDispatchHistoryActivity
import com.app.rupyz.sales.orders.OrderDetailActivity
import com.app.rupyz.sales.payment.PaymentDetailsActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseReceiver : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        SharedPref.getInstance().putString(FCM_TOKEN, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Second case when notification payload is
        // received.
        if (remoteMessage.notification != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            showNotification(
                remoteMessage.notification!!.title,
                remoteMessage.notification!!.body, remoteMessage
            )
        }
    }

    // Method to display the notifications
    private fun showNotification(title: String?, message: String?, remoteMessage: RemoteMessage?) {
        // Pass the intent to switch to the MainActivity
        val intent: Intent? = getCallIntent(remoteMessage)

        // Assign channel ID
        val channelId = "2"

        // Pass the intent to PendingIntent to start the
        // next Activity
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this,
            channelId)
            .setSmallIcon(R.mipmap.ic_rupyz_white_logo)
            .setAutoCancel(true)
            .setGroup(getString(R.string.rupyz_work_group_id))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(false)
            .setGroupSummary(true)
            .setContentIntent(pendingIntent)

        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSmallIcon(R.mipmap.ic_rupyz_white_logo)

        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(mChannel)
        }
        notificationManager.notify(0, builder.build())
    }

    private fun getCallIntent(remoteMessage: RemoteMessage?): Intent? {
        var intent: Intent? = null
        val moduleUid = remoteMessage?.data?.get("module_uid")?.toInt()
        if (remoteMessage?.data?.get("module_name") != null) {

            when (remoteMessage.data["module_name"]) {
                "ORDER-DISPATCH" -> {
                    intent = Intent(this, OrderDispatchHistoryActivity::class.java)
                    if (remoteMessage.data["parent_module_uid"] != null) {
                        intent.putExtra(AppConstant.ORDER_ID, remoteMessage.data["parent_module_uid"]?.toInt())
                    }

                    intent.putExtra(AppConstant.DISPATCH_ID, moduleUid)
                    intent.putExtra(AppConstant.ORDER_CLOSE, false)
                }

                "ORDER" -> {
                    intent = Intent(this, OrderDetailActivity::class.java)
                    intent.putExtra(AppConstant.ORDER_ID, moduleUid)
                }

                "CUSTOMER-PAYMENT" -> {
                    intent = Intent(this, PaymentDetailsActivity::class.java)
                    intent.putExtra(AppConstant.PAYMENT_ID, moduleUid)
                }

                "LEAD" -> {
                    intent = Intent(this, LeadDetailsActivity::class.java)
                    intent.putExtra(AppConstant.LEAD_ID, moduleUid)
                }

                "LEAD-FEEDBACK", "CUSTOMER-FEEDBACK", "FOLLOWUP-REMINDERS" -> {
                    intent = Intent(this, CustomerFeedbackDetailActivity::class.java)
                    if (remoteMessage.data["parent_module_uid"] != null) {
                        intent.putExtra(AppConstant.ACTIVITY_ID, remoteMessage.data["parent_module_uid"]?.toInt())
                    }

                    if (remoteMessage.data["org_id"] != null) {
                        intent.putExtra(AppConstant.ORGANIZATION, remoteMessage.data["org_id"]?.toInt())
                    }
                }
                else -> {
                    intent = Intent(this, InitialActivity::class.java)
                }
            }
        }

        Log.e("DEBUG", "CALLING FROM PUSH NOTIFICATION")
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        return intent
    }
}