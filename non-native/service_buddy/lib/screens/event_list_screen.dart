import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:provider/provider.dart';
import 'package:service_buddy/providers/events_provider.dart';
import 'package:service_buddy/screens/add_edit_event_screen.dart';
import 'package:service_buddy/widgets/event_card.dart';

class EventListScreen extends StatefulWidget {
  const EventListScreen({super.key});

  @override
  State<EventListScreen> createState() => _EventListScreenState();
}

class _EventListScreenState extends State<EventListScreen> {
  String _searchQuery = "";
  final ScrollController _scrollController = ScrollController();
  bool _isFabExtended = true;

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(() {
      if (_scrollController.position.userScrollDirection == ScrollDirection.reverse) {
        if (_isFabExtended) setState(() => _isFabExtended = false);
      } else if (_scrollController.position.userScrollDirection == ScrollDirection.forward) {
        if (!_isFabExtended) setState(() => _isFabExtended = true);
      }
    });
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<EventsProvider>(context);

    final displayedEvents = provider.events.where((event) {
      final query = _searchQuery.toLowerCase();
      return event.title.toLowerCase().contains(query) ||
          event.vehicleIdentifier.toLowerCase().contains(query);
    }).toList();

    return Scaffold(
      appBar: AppBar(
        title: const Text("Service Buddy 🚗"),
        centerTitle: true,
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: TextField(
              decoration: InputDecoration(
                hintText: "Search (e.g., Oil, CJ 01 XYZ...)",
                hintStyle: const TextStyle(color: Colors.grey),
                prefixIcon: const Icon(Icons.search, color: Colors.grey),
                filled: true,
                fillColor: Colors.white,
                contentPadding: const EdgeInsets.all(12),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: const BorderSide(color: Color(0xFFCCCCCC), width: 1),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: const BorderSide(color: Color(0xFF0061A4), width: 2),
                ),
              ),
              onChanged: (value) => setState(() => _searchQuery = value),
            ),
          ),

          Expanded(
            child: ListView.builder(
              controller: _scrollController,
              itemCount: displayedEvents.length,
              itemBuilder: (context, index) {
                return EventCard(
                  event: displayedEvents[index],
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => AddEditEventScreen(event: displayedEvents[index]),
                      ),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),

      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => const AddEditEventScreen()),
          );
        },
        backgroundColor: const Color(0xFF0061A4),
        foregroundColor: Colors.white,
        icon: const Icon(Icons.add),
        label: const Text("Add Event"),
        isExtended: _isFabExtended,
      ),
    );
  }
}