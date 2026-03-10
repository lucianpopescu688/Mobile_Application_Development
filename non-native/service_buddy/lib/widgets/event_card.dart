import 'package:flutter/material.dart';
import 'package:service_buddy/models/maintenance_event.dart';

class EventCard extends StatelessWidget {
  final MaintenanceEvent event;
  final VoidCallback onTap;

  const EventCard({super.key, required this.event, required this.onTap});

  Color getStatusColor(String status) {
    switch (status.toUpperCase()) {
      case 'OVERDUE': return const Color(0xFFD32F2F);
      case 'UPCOMING': return const Color(0xFFF57C00);
      case 'FUTURE': return const Color(0xFF03A9F4);
      case 'COMPLETED': return const Color(0xFF388E3C);
      default: return const Color(0xFF1976D2);
    }
  }

  @override
  Widget build(BuildContext context) {
    final diff = event.dueDate.difference(DateTime.now()).inDays;
    String dateText;
    if (diff < 0) dateText = "${diff.abs()} days ago";
    else if (diff == 0) dateText = "Today";
    else dateText = "in $diff days";

    return Card(
      elevation: 4,
      margin: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: IntrinsicHeight(
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Container(
                width: 4,
                color: getStatusColor(event.status),
              ),

              Expanded(
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Icon(
                        event.category == 'SERVICE' ? Icons.build : Icons.description,
                        size: 24,
                        color: Colors.black,
                      ),
                      const SizedBox(width: 16),

                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Expanded(
                                  child: Text(
                                    event.title,
                                    style: const TextStyle(
                                      fontSize: 16,
                                      fontWeight: FontWeight.w500,
                                    ),
                                  ),
                                ),
                                const SizedBox(width: 8),
                                Text(
                                  event.status,
                                  style: TextStyle(
                                    fontSize: 16,
                                    color: getStatusColor(event.status),
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 4),

                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(
                                  event.vehicleIdentifier,
                                  style: const TextStyle(
                                    fontSize: 14,
                                    color: Colors.grey,
                                  ),
                                ),
                                Text(
                                  dateText,
                                  style: const TextStyle(
                                    fontSize: 14,
                                    color: Colors.grey,
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 2),

                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              crossAxisAlignment: CrossAxisAlignment.end,
                              children: [
                                Expanded(
                                  child: Text(
                                    "ID: ${event.id.substring(0, 8)}...",
                                    style: const TextStyle(
                                      fontSize: 12,
                                      color: Colors.grey,
                                    ),
                                  ),
                                ),
                                Padding(
                                  padding: const EdgeInsets.only(top: 8.0),
                                  child: Text(
                                    "\$${event.price.toStringAsFixed(2)}",
                                    style: const TextStyle(
                                      fontSize: 16,
                                      fontWeight: FontWeight.bold,
                                      color: Colors.black,
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}