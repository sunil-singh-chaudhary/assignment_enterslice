import 'package:assignment_enterslice/controller/NativeCommunicationController.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

Future<void> checkLocationPermissionAndStartService() async {
  final status = await Permission.location.status;
  final backgroundStatus = await Permission.locationAlways.status;

  if (status.isGranted && backgroundStatus.isGranted) {
    // Location and ACCESS_BACKGROUND_LOCATION permissions are granted, start the service
    try {
      await NativeCommunicationController.to.startLocationService();
    } on PlatformException catch (e) {
      debugPrint('Error: $e');
    }
  } else {
    // Request location permissions including ACCESS_BACKGROUND_LOCATION
    final results = await [
      Permission.location,
      Permission.locationAlways,
    ].request();

    if (results[Permission.location]!.isGranted &&
        results[Permission.locationAlways]!.isGranted) {
      // Location and ACCESS_BACKGROUND_LOCATION permissions are now granted, start the service
      try {
        await NativeCommunicationController.to.startLocationService();
      } on PlatformException catch (e) {
        debugPrint('Error: $e');
      }
    } else {
      // Location permissions denied by the user
      // You can handle this case as needed (e.g., show a message)
    }
  }
}
