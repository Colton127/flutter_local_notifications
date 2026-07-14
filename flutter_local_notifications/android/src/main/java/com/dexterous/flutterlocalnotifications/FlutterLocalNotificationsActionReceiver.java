package com.dexterous.flutterlocalnotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.core.app.RemoteInput;

/**
 * Base class for app-defined receivers targeted by Android notification actions.
 *
 * <p>This class preserves the plugin's {@code cancelNotification} behaviour before delegating to
 * {@link #onReceiveNotificationAction(Context, Intent)}. The original notification ID, tag, action
 * ID and payload are available through the static accessor methods.
 */
@Keep
public abstract class FlutterLocalNotificationsActionReceiver extends BroadcastReceiver {
  @Override
  public final void onReceive(Context context, Intent intent) {
    cancelNotificationIfNeeded(context, intent);
    onReceiveNotificationAction(context, intent);
  }

  /** Called after the plugin has applied the configured notification cancellation behaviour. */
  protected abstract void onReceiveNotificationAction(Context context, Intent intent);

  /** Applies the action's configured notification cancellation behaviour. */
  public static void cancelNotificationIfNeeded(Context context, Intent intent) {
    ActionBroadcastReceiver.cancelNotificationIfNeeded(context, intent);
  }

  /** Returns the ID of the notification containing the selected action. */
  public static int getNotificationId(Intent intent) {
    return intent.getIntExtra(FlutterLocalNotificationsPlugin.NOTIFICATION_ID, 0);
  }

  /** Returns the optional tag of the notification containing the selected action. */
  @Nullable
  public static String getNotificationTag(Intent intent) {
    return intent.getStringExtra(FlutterLocalNotificationsPlugin.NOTIFICATION_TAG);
  }

  /** Returns the ID of the selected notification action. */
  @Nullable
  public static String getActionId(Intent intent) {
    return intent.getStringExtra(FlutterLocalNotificationsPlugin.ACTION_ID);
  }

  /** Returns the notification payload. */
  @Nullable
  public static String getPayload(Intent intent) {
    return intent.getStringExtra(FlutterLocalNotificationsPlugin.PAYLOAD);
  }

  /** Returns text supplied through a notification action input, when present. */
  @Nullable
  public static CharSequence getInput(Intent intent) {
    Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
    return remoteInput == null
        ? null
        : remoteInput.getCharSequence(FlutterLocalNotificationsPlugin.INPUT_RESULT);
  }
}
