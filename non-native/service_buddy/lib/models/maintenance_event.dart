import 'package:uuid/uuid.dart';

class MaintenanceEvent {
  String id;
  String vehicleIdentifier;
  String title;
  String? description;
  String category;
  DateTime dueDate;
  String status;
  double price;

  MaintenanceEvent({
    String? id,
    required this.vehicleIdentifier,
    required this.title,
    this.description,
    required this.category,
    required this.dueDate,
    required this.status,
    required this.price,
  }) : id = id ?? const Uuid().v4();
}