import 'package:flutter/material.dart';
import 'package:service_buddy/models/maintenance_event.dart';

class EventsProvider with ChangeNotifier {
  final List<MaintenanceEvent> _events = [];

  final Map<String, int> _statusOrder = {
    "OVERDUE": 0,
    "PENDING": 1,
    "UPCOMING": 2,
    "FUTURE": 3,
    "COMPLETED": 4
  };

  EventsProvider() {
    _loadDemoData();
  }

  List<MaintenanceEvent> get events => _events;

  void _sortList() {
    _events.sort((a, b) {
      int statusCompare = (_statusOrder[a.status] ?? 99).compareTo(_statusOrder[b.status] ?? 99);
      if (statusCompare != 0) return statusCompare;
      return a.dueDate.compareTo(b.dueDate);
    });
    notifyListeners();
  }

  void addEvent(MaintenanceEvent event) {
    _events.add(event);
    _sortList();
  }

  void updateEvent(String id, MaintenanceEvent updatedEvent) {
    int index = _events.indexWhere((e) => e.id == id);
    if (index != -1) {
      _events[index] = updatedEvent;
      _sortList();
    }
  }

  void deleteEvent(String id) {
    _events.removeWhere((e) => e.id == id);
    notifyListeners();
  }

  void regenerateEvents() {
    _events.clear();
    _loadDemoData();
    notifyListeners();
  }

  void _loadDemoData() {
    _events.addAll([
      MaintenanceEvent(
          vehicleIdentifier: "CJ 01 XYZ",
          title: "Insurance Renewal (RCA)",
          description: "Policy expire soon, need to renew urgently.",
          category: "DOCUMENT",
          dueDate: DateTime(2025, 11, 20),
          status: "OVERDUE",
          price: 150.0
      ),
      MaintenanceEvent(
          vehicleIdentifier: "B 999 CAR",
          title: "Transmission Fluid Change",
          description: "Shifting is a bit rough.",
          category: "SERVICE",
          dueDate: DateTime(2025, 10, 15),
          status: "OVERDUE",
          price: 300.0
      ),

      MaintenanceEvent(
          vehicleIdentifier: "AB 12 CDE",
          title: "ITP Inspection",
          description: "Technical inspection appointment needed.",
          category: "DOCUMENT",
          dueDate: DateTime(2025, 12, 01),
          status: "PENDING",
          price: 50.0
      ),
      MaintenanceEvent(
          vehicleIdentifier: "CJ 01 XYZ",
          title: "Check Engine Light",
          description: "Yellow light appeared on dash.",
          category: "SERVICE",
          dueDate: DateTime(2025, 12, 05),
          status: "PENDING",
          price: 0.0
      ),

      MaintenanceEvent(
          vehicleIdentifier: "CJ 01 XYZ",
          title: "Wiper Blade Replacement",
          description: null,
          category: "SERVICE",
          dueDate: DateTime(2025, 12, 10),
          status: "UPCOMING",
          price: 25.5
      ),
      MaintenanceEvent(
          vehicleIdentifier: "B 999 CAR",
          title: "Brake Pad Replacement",
          description: "Front pads are wearing thin.",
          category: "SERVICE",
          dueDate: DateTime(2025, 12, 12),
          status: "UPCOMING",
          price: 120.0
      ),
      MaintenanceEvent(
          vehicleIdentifier: "TM 55 WOW",
          title: "Winter Tires Swap",
          description: "Change to winter set.",
          category: "SERVICE",
          dueDate: DateTime(2025, 12, 15),
          status: "UPCOMING",
          price: 40.0
      ),

      MaintenanceEvent(
          vehicleIdentifier: "TM 55 WOW",
          title: "Annual Road Tax (Rovinieta)",
          description: "Valid until Feb 2026.",
          category: "DOCUMENT",
          dueDate: DateTime(2026, 02, 15),
          status: "FUTURE",
          price: 28.0
      ),
      MaintenanceEvent(
          vehicleIdentifier: "CJ 01 XYZ",
          title: "Air Filter Change",
          description: "Routine maintenance.",
          category: "SERVICE",
          dueDate: DateTime(2026, 03, 10),
          status: "FUTURE",
          price: 30.0
      ),
      MaintenanceEvent(
          vehicleIdentifier: "B 999 CAR",
          title: "CASCO Insurance",
          description: "Full coverage renewal.",
          category: "DOCUMENT",
          dueDate: DateTime(2026, 05, 20),
          status: "FUTURE",
          price: 450.0
      ),
      MaintenanceEvent(
          vehicleIdentifier: "AB 12 CDE",
          title: "Coolant Flush",
          description: "Prevent overheating in summer.",
          category: "SERVICE",
          dueDate: DateTime(2026, 06, 01),
          status: "FUTURE",
          price: 80.0
      ),

      MaintenanceEvent(
          vehicleIdentifier: "B 999 CAR",
          title: "Annual Service",
          description: "Oil change and general checkup.",
          category: "SERVICE",
          dueDate: DateTime(2025, 07, 01),
          status: "COMPLETED",
          price: 250.0
      ),
      MaintenanceEvent(
          vehicleIdentifier: "AB 12 CDE",
          title: "Spark Plug Replacement",
          description: "Engine was misfiring.",
          category: "SERVICE",
          dueDate: DateTime(2025, 08, 15),
          status: "COMPLETED",
          price: 90.0
      ),
      MaintenanceEvent(
          vehicleIdentifier: "CJ 01 XYZ",
          title: "Battery Replacement",
          description: "Old battery died.",
          category: "SERVICE",
          dueDate: DateTime(2025, 09, 10),
          status: "COMPLETED",
          price: 110.0
      ),
    ]);
    _sortList();
  }
}