package com.dexterous.flutterlocalnotifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FlutterLocalNotificationsActionReceiverTest {
  @Test
  public void accessors_readStandardActionExtras() {
    Intent intent =
        new Intent()
            .putExtra(FlutterLocalNotificationsPlugin.NOTIFICATION_ID, 42)
            .putExtra(FlutterLocalNotificationsPlugin.NOTIFICATION_TAG, "tag")
            .putExtra(FlutterLocalNotificationsPlugin.ACTION_ID, "action-id")
            .putExtra(FlutterLocalNotificationsPlugin.PAYLOAD, "payload");

    assertEquals(42, FlutterLocalNotificationsActionReceiver.getNotificationId(intent));
    assertEquals("tag", FlutterLocalNotificationsActionReceiver.getNotificationTag(intent));
    assertEquals("action-id", FlutterLocalNotificationsActionReceiver.getActionId(intent));
    assertEquals("payload", FlutterLocalNotificationsActionReceiver.getPayload(intent));
    assertNull(FlutterLocalNotificationsActionReceiver.getInput(intent));
  }
}
