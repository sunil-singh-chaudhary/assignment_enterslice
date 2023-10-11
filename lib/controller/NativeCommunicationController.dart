import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';

class NativeCommunicationController extends GetxController {
  static NativeCommunicationController get to => Get.find();

  final message = ''.obs;

  final latitudeAndroid = ''.obs;
  final longitudeAndroid = ''.obs;
  final timeStampAndroid = ''.obs;

  final COLUMN_LATITUDE = "latitude";
  final COLUMN_LONGITUDE = "longitude";
  final COLUMN_TIMESTAMP = "timestamp";
  final platform = const MethodChannel('location_service');
  final eventChannel = const EventChannel('EVENT_CHANNEL_SUB');

  Future<void> startLocationService() async {
    debugPrint('Start click');

    try {
      await platform.invokeMethod('startLocationService');
    } on PlatformException catch (e) {
      debugPrint('Error starting location service: $e');
    }
  }

  Future<void> stopLocationService() async {
    try {
      await platform.invokeMethod('stopLocationService');
    } on PlatformException catch (e) {
      debugPrint('Error stopping location service: $e');
    }
  }

  Future<void> fetchLastKnowLocation() async {
    try {
      final locationData = await platform.invokeMethod('fetchDataFromNative');
      if (locationData != null) {
        debugPrint('Data found. $locationData');
        for (var location in locationData) {
          double latitude = location[COLUMN_LATITUDE];
          double longitude = location[COLUMN_LONGITUDE];
          int timestamp = location[COLUMN_TIMESTAMP];

          //Update Statae Management USING GETX
          latitudeAndroid.value = latitude.toString();
          longitudeAndroid.value = longitude.toString();
          timeStampAndroid.value = timestamp.toString();
        }
      } else {
        debugPrint('No previous data found.');
      }
    } on PlatformException catch (e) {
      debugPrint('Error stopping location service: $e');
    }
  }

  void fetchUpdatedLocation(
      Function(double latitude, double longitude) onsuccesscallback) async {
    eventChannel.receiveBroadcastStream().listen((location) {
      double latitude = location[COLUMN_LATITUDE];
      double longitude = location[COLUMN_LONGITUDE];

      debugPrint('Updated latitude Flutter getting: $latitude');
      debugPrint('listening Updated longitude: $longitude');

      onsuccesscallback(latitude, longitude);
    });
  }
}
