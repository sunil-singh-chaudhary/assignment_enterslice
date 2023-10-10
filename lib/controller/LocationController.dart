import 'package:get/get.dart';

class LocationController extends GetxController {
  static LocationController get to => Get.find();

  RxDouble latitude = 0.0.obs;
  RxDouble longitude = 0.0.obs;

  void updateLocation(double lat, double long) {
    latitude.value = lat;
    longitude.value = long;
  }
}
