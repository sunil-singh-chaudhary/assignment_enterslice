import 'package:assignment_enterslice/controller/LocationController.dart';
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

  void fetchUpdatedLocation() async {
    try {
      final result = await platform.invokeMethod('updateLocation');

      if (result != null) {
        final Map<dynamic, dynamic> data = result;
        final double lat = data[COLUMN_LATITUDE];
        final double long = data[COLUMN_LONGITUDE];

        // Use the LocationController to update the values
        LocationController.to.updateLocation(lat, long);

        // Handle the result if needed
        debugPrint('Received Updated data from Android: $result');
      } else {
        debugPrint('No Updated data received from Android.');
      }
    } catch (e) {
      // Handle any errors
      debugPrint('Error fetching Updated_data from Android: $e');
    }
  }
}
