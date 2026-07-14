package com.dexterous.flutter_local_notifications_example

import android.content.Context
import android.content.Intent
import android.util.Log
import com.dexterous.flutterlocalnotifications.FlutterLocalNotificationsActionReceiver

class CustomActionReceiver : FlutterLocalNotificationsActionReceiver() {
    override fun onReceiveNotificationAction(context: Context, intent: Intent) {
        Log.i(
            "CustomActionReceiver",
            "Received action ${FlutterLocalNotificationsActionReceiver.getActionId(intent)} " +
                "with payload ${FlutterLocalNotificationsActionReceiver.getPayload(intent)}",
        )
    }
}
