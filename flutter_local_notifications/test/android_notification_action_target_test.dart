import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter_local_notifications/src/platform_specifics/android/method_channel_mappers.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('AndroidNotificationActionTarget', () {
    test('maps a broadcast receiver target', () {
      const AndroidNotificationDetails details = AndroidNotificationDetails(
        'channelId',
        'channelName',
        actions: <AndroidNotificationAction>[
          AndroidNotificationAction(
            'actionId',
            'Action',
            target: AndroidNotificationActionTarget.broadcastReceiver(
              className: 'com.example.ActionReceiver',
              action: 'com.example.ACTION_NOTIFICATION',
            ),
          ),
        ],
      );

      final List<Object?> actions =
          details.toMap()['actions']! as List<Object?>;
      final Map<String, Object?> action =
          actions.single! as Map<String, Object?>;

      expect(action['target'], <String, Object?>{
        'type': 'broadcastReceiver',
        'className': 'com.example.ActionReceiver',
        'action': 'com.example.ACTION_NOTIFICATION',
      });
    });

    test('cannot also show the user interface', () {
      expect(
        () => AndroidNotificationAction(
          'actionId',
          'Action',
          showsUserInterface: true,
          target: const AndroidNotificationActionTarget.broadcastReceiver(
            className: 'com.example.ActionReceiver',
          ),
        ),
        throwsAssertionError,
      );
    });
  });
}
