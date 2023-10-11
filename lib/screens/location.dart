import 'package:assignment_enterslice/controller/LocationController.dart';
import 'package:assignment_enterslice/controller/NativeCommunicationController.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class LocatioinScreen extends StatefulWidget {
  const LocatioinScreen({super.key});

  @override
  State<LocatioinScreen> createState() => _LocatioinScreenState();
}

class _LocatioinScreenState extends State<LocatioinScreen> {
  @override
  void initState() {
    super.initState();
    fetchForEvery2Seconds();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
          ElevatedButton(
            onPressed: () async {
              debugPrint('starting locationfetching click in flutter');
              await NativeCommunicationController.to.startLocationService();
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

  void fetchForEvery2Seconds() {
    NativeCommunicationController.to
        .fetchUpdatedLocation((latitude, longitude) {
      debugPrint('getting lat long in init $latitude - $longitude');
      LocationController.to.latitude.value = latitude;
      LocationController.to.longitude.value = longitude;
    });
  }
}
