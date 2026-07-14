package com.dexterous.flutterlocalnotifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.dexterous.flutterlocalnotifications.models.NotificationAction;
import com.dexterous.flutterlocalnotifications.models.NotificationAction.NotificationActionTarget;
import com.dexterous.flutterlocalnotifications.models.NotificationDetails;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowPendingIntent;

@RunWith(RobolectricTestRunner.class)
public class NotificationActionIntentFactoryTest {
  private Context context;

  @Before
  public void before() {
    context = ApplicationProvider.getApplicationContext();
  }

  @Test
  public void createTargetIntent_supportsFullyQualifiedClassNames() {
    NotificationActionTarget target =
        target("com.example.ActionReceiver", "com.example.ACTION_NOTIFICATION");

    Intent intent = NotificationActionIntentFactory.createTargetIntent(context, target);

    assertEquals(
        new ComponentName(context.getPackageName(), "com.example.ActionReceiver"),
        intent.getComponent());
    assertEquals("com.example.ACTION_NOTIFICATION", intent.getAction());
  }

  @Test
  public void createTargetIntent_expandsRelativeClassNames() {
    NotificationActionTarget target = target(".ActionReceiver", null);

    Intent intent = NotificationActionIntentFactory.createTargetIntent(context, target);

    assertEquals(
        new ComponentName(
            context.getPackageName(), context.getPackageName() + ".ActionReceiver"),
        intent.getComponent());
  }

  @Test
  public void createTargetIntent_rejectsUnsupportedTargetTypes() {
    Map<String, Object> raw = new HashMap<>();
    raw.put("type", "service");
    raw.put("className", "com.example.ActionService");
    NotificationActionTarget target = new NotificationActionTarget(raw);

    try {
      NotificationActionIntentFactory.createTargetIntent(context, target);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException exception) {
      assertTrue(exception.getMessage().contains("Unsupported"));
    }
  }

  @Test
  public void create_buildsBroadcastPendingIntentWithStandardExtras() {
    NotificationDetails notificationDetails = new NotificationDetails();
    notificationDetails.id = 42;
    notificationDetails.tag = "tag";
    notificationDetails.payload = "payload";

    NotificationAction action = action(false);
    PendingIntent pendingIntent =
        NotificationActionIntentFactory.create(context, notificationDetails, action, 99);
    ShadowPendingIntent shadowPendingIntent = Shadows.shadowOf(pendingIntent);
    Intent intent = shadowPendingIntent.getSavedIntent();

    assertTrue(shadowPendingIntent.isBroadcastIntent());
    assertFalse(shadowPendingIntent.isActivityIntent());
    assertEquals(99, shadowPendingIntent.getRequestCode());
    assertEquals("action-id", intent.getStringExtra(FlutterLocalNotificationsPlugin.ACTION_ID));
    assertEquals(42, intent.getIntExtra(FlutterLocalNotificationsPlugin.NOTIFICATION_ID, 0));
    assertEquals("tag", intent.getStringExtra(FlutterLocalNotificationsPlugin.NOTIFICATION_TAG));
    assertEquals("payload", intent.getStringExtra(FlutterLocalNotificationsPlugin.PAYLOAD));
    assertTrue(
        intent.getBooleanExtra(FlutterLocalNotificationsPlugin.CANCEL_NOTIFICATION, false));
    assertEquals(
        "com.example.ActionReceiver", intent.getComponent().getClassName());
  }

  @Test
  public void create_rejectsTargetThatAlsoShowsUserInterface() {
    NotificationDetails notificationDetails = new NotificationDetails();
    notificationDetails.id = 42;

    NotificationAction action = action(true);

    try {
      NotificationActionIntentFactory.create(context, notificationDetails, action, 99);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException exception) {
      assertTrue(exception.getMessage().contains("showsUserInterface"));
    }
  }

  private NotificationActionTarget target(String className, String action) {
    Map<String, Object> raw = new HashMap<>();
    raw.put("type", "broadcastReceiver");
    raw.put("className", className);
    raw.put("action", action);
    return new NotificationActionTarget(raw);
  }

  private NotificationAction action(boolean showsUserInterface) {
    Map<String, Object> raw = new HashMap<>();
    raw.put("id", "action-id");
    raw.put("title", "Action");
    raw.put("cancelNotification", true);
    raw.put("showsUserInterface", showsUserInterface);
    raw.put("target", targetMap());
    return new NotificationAction(raw);
  }

  private Map<String, Object> targetMap() {
    Map<String, Object> raw = new HashMap<>();
    raw.put("type", "broadcastReceiver");
    raw.put("className", "com.example.ActionReceiver");
    raw.put("action", "com.example.ACTION_NOTIFICATION");
    return raw;
  }
}
