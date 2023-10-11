import 'package:assignment_enterslice/controller/LocationController.dart';
import 'package:assignment_enterslice/controller/NativeCommunicationController.dart';
import 'package:assignment_enterslice/controller/locationpermission.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';

final eventChannel = const EventChannel('EVENT_CHANNEL_SUB');

final COLUMN_LATITUDE = "latitude";
final COLUMN_LONGITUDE = "longitude";

class LocatioinScreen extends StatefulWidget {
  const LocatioinScreen({super.key});

  @override
  State<LocatioinScreen> createState() => _LocatioinScreenState();
}

class _LocatioinScreenState extends State<LocatioinScreen> {
  @override
  void initState() {
    super.initState();
    // NativeCommunicationController.to
    //     .fetchUpdatedLocation((latitude, longitude) {
    //   debugPrint('getting lat long in init $latitude - $longitude');
    //   LocationController.to.latitude.value = latitude;
    //   LocationController.to.longitude.value = longitude;
    // });
    eventHandles();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
          ElevatedButton(
            onPressed: () {
              checkLocationPermissionAndStartService();
            },
            child: const Text('Start Location Fetching'),
          ),
          ElevatedButton(
            onPressed: () {
              NativeCommunicationController.to.stopLocationService();
            },
            child: const Text('Stop Location Fetching'),
          ),
          Obx(
            () => Text(
              'Live Latitude:--> ${LocationController.to.latitude.value}\nLive Longitude:--> ${LocationController.to.longitude.value}',
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
          )
        ]),
      ),
    );
  }

  void eventHandles() {
    eventChannel.receiveBroadcastStream().listen((location) {
      double latitude = location[COLUMN_LATITUDE];
      double longitude = location[COLUMN_LONGITUDE];

      debugPrint('Updated latitude Flutter getting: $latitude');
      debugPrint('listening Updated longitude: $longitude');
      LocationController.to.latitude.value = latitude;
      LocationController.to.longitude.value = longitude;
    });
  }
}
