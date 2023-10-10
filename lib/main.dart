import 'package:flutter/material.dart';
import 'package:get/get.dart';

import 'controller/LocationController.dart';
import 'controller/NativeCommunicationController.dart';
import 'screens/messagePass.dart';

void main() {
  Get.put(NativeCommunicationController());
  final LocationController locationController = Get.put(LocationController());

  runApp(const messagePass());
}
