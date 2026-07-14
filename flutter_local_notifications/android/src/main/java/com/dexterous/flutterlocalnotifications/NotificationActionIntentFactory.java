package com.dexterous.flutterlocalnotifications;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;

import com.dexterous.flutterlocalnotifications.models.NotificationAction;
import com.dexterous.flutterlocalnotifications.models.NotificationAction.NotificationActionTarget;
import com.dexterous.flutterlocalnotifications.models.NotificationDetails;

final class NotificationActionIntentFactory {
  private static final String BROADCAST_RECEIVER = "broadcastReceiver";

  private NotificationActionIntentFactory() {}

  @SuppressLint("UnspecifiedImmutableFlag")
  static PendingIntent create(
      Context context,
      NotificationDetails notificationDetails,
      NotificationAction action,
      int requestCode) {
    if (action.target != null
        && action.showsUserInterface != null
        && action.showsUserInterface) {
      throw new IllegalArgumentException(
          "A native notification action target cannot be combined with showsUserInterface");
    }

    boolean startsActivity =
        action.target == null
            && action.showsUserInterface != null
            && action.showsUserInterface;
    Intent actionIntent =
        action.target == null
            ? createDefaultIntent(context, startsActivity)
            : createTargetIntent(context, action.target);

    actionIntent
        .putExtra(FlutterLocalNotificationsPlugin.NOTIFICATION_ID, notificationDetails.id)
        .putExtra(FlutterLocalNotificationsPlugin.NOTIFICATION_TAG, notificationDetails.tag)
        .putExtra(FlutterLocalNotificationsPlugin.ACTION_ID, action.id)
        .putExtra(
            FlutterLocalNotificationsPlugin.CANCEL_NOTIFICATION, action.cancelNotification)
        .putExtra(FlutterLocalNotificationsPlugin.PAYLOAD, notificationDetails.payload);

    int flags = PendingIntent.FLAG_UPDATE_CURRENT;
    if (action.actionInputs == null || action.actionInputs.isEmpty()) {
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        flags |= PendingIntent.FLAG_IMMUTABLE;
      }
    } else if (VERSION.SDK_INT >= VERSION_CODES.S) {
      flags |= PendingIntent.FLAG_MUTABLE;
    }

    return startsActivity
        ? PendingIntent.getActivity(context, requestCode, actionIntent, flags)
        : PendingIntent.getBroadcast(context, requestCode, actionIntent, flags);
  }

  private static Intent createDefaultIntent(Context context, boolean startsActivity) {
    if (startsActivity) {
      Intent intent = FlutterLocalNotificationsPlugin.getLaunchIntent(context);
      intent.setAction(FlutterLocalNotificationsPlugin.SELECT_FOREGROUND_NOTIFICATION_ACTION);
      return intent;
    }

    Intent intent = new Intent(context, ActionBroadcastReceiver.class);
    intent.setAction(ActionBroadcastReceiver.ACTION_TAPPED);
    return intent;
  }

  static Intent createTargetIntent(Context context, NotificationActionTarget target) {
    if (!BROADCAST_RECEIVER.equals(target.type)) {
      throw new IllegalArgumentException(
          "Unsupported Android notification action target type: " + target.type);
    }
    if (TextUtils.isEmpty(target.className)) {
      throw new IllegalArgumentException(
          "Android notification action target className must not be empty");
    }

    String className = target.className;
    if (className.startsWith(".")) {
      className = context.getPackageName() + className;
    } else if (!className.contains(".")) {
      className = context.getPackageName() + "." + className;
    }

    Intent intent = new Intent();
    intent.setClassName(context.getPackageName(), className);
    if (!TextUtils.isEmpty(target.action)) {
      intent.setAction(target.action);
    }
    return intent;
  }
}
