import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:service_buddy/providers/events_provider.dart';
import 'package:service_buddy/screens/event_list_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => EventsProvider(),
      child: MaterialApp(
        title: 'Service Buddy 🚗',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF0061A4)),
          useMaterial3: true,
          appBarTheme: const AppBarTheme(
            backgroundColor: Colors.white,
            surfaceTintColor: Colors.transparent,
            elevation: 0,
            iconTheme: IconThemeData(color: Colors.black),
            titleTextStyle: TextStyle(color: Colors.black, fontSize: 22),
          ),
          scaffoldBackgroundColor: const Color(0xFFFDFCFF),
        ),
        home: const EventListScreen(),
      ),
    );
  }
}