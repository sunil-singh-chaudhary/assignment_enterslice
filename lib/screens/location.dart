import 'package:assignment_enterslice/controller/LocationController.dart';
import 'package:assignment_enterslice/controller/NativeCommunicationController.dart';
import 'package:assignment_enterslice/controller/locationpermission.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'dart:async';

class LocatioinScreen extends StatefulWidget {
  const LocatioinScreen({super.key});

  @override
  State<LocatioinScreen> createState() => _LocatioinScreenState();
}

class _LocatioinScreenState extends State<LocatioinScreen> {
  late Timer _timer;

  @override
  void initState() {
    super.initState();
    _timer = Timer.periodic(const Duration(seconds: 3), (timer) {
      debugPrint('requesting from flutter Timer tick');
      NativeCommunicationController.to.fetchUpdatedLocation();
    });
  }

  @override
  void dispose() {
    super.dispose();

    // Cancel the timer when the widget is disposed
    _timer.cancel();
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
}
