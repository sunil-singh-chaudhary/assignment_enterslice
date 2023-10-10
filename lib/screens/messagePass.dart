import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../controller/NativeCommunicationController.dart';
import 'location.dart';

class messagePass extends StatefulWidget {
  const messagePass({super.key});

  @override
  State<messagePass> createState() => _messagePassState();
}

class _messagePassState extends State<messagePass> {
  @override
  void initState() {
    super.initState();
    NativeCommunicationController.to.fetchLastKnowLocation();
  }

  @override
  void didChangeDependencies() {
    NativeCommunicationController.to.fetchLastKnowLocation();
    super.didChangeDependencies();
  }

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Method Channel Example'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Obx(
                () => Text(
                  'Longitude From DB is- ${NativeCommunicationController.to.longitudeAndroid.value}',
                  style: const TextStyle(fontSize: 18),
                ),
              ),
              Obx(
                () => Text(
                  'latitude From DB is- ${NativeCommunicationController.to.latitudeAndroid.value}',
                  style: const TextStyle(fontSize: 18),
                ),
              ),
              Obx(
                () => Text(
                  'TimeStamp is- ${NativeCommunicationController.to.timeStampAndroid.value}',
                  style: const TextStyle(fontSize: 18),
                ),
              ),
              Column(
                children: [
                  MyChildWidget(context),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class MyChildWidget extends StatelessWidget {
  final BuildContext context;

  MyChildWidget(this.context);

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => const LocatioinScreen(),
          ),
        );
      },
      child: const Text('Go to Location Screen'),
    );
  }
}
