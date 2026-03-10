import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:service_buddy/models/maintenance_event.dart';
import 'package:service_buddy/providers/events_provider.dart';
import 'package:uuid/uuid.dart';

class AddEditEventScreen extends StatefulWidget {
  final MaintenanceEvent? event;

  const AddEditEventScreen({super.key, this.event});

  @override
  State<AddEditEventScreen> createState() => _AddEditEventScreenState();
}

class _AddEditEventScreenState extends State<AddEditEventScreen> {
  final _formKey = GlobalKey<FormState>();

  late TextEditingController _idController;
  late TextEditingController _titleController;
  late TextEditingController _vehicleController;
  late TextEditingController _descController;
  late TextEditingController _priceController;
  late TextEditingController _dateController;

  String _category = "SERVICE";
  String _status = "UPCOMING";
  DateTime? _selectedDate;

  final List<String> _statusOptions = ['OVERDUE', 'PENDING', 'UPCOMING', 'FUTURE', 'COMPLETED'];

  @override
  void initState() {
    super.initState();
    final e = widget.event;

    _idController = TextEditingController(text: e?.id ?? const Uuid().v4());
    _titleController = TextEditingController(text: e?.title ?? "");
    _vehicleController = TextEditingController(text: e?.vehicleIdentifier ?? "");
    _descController = TextEditingController(text: e?.description ?? "");
    _priceController = TextEditingController(text: e?.price.toString() ?? "");
    _category = e?.category ?? "SERVICE";
    _status = e?.status ?? "UPCOMING";
    _selectedDate = e?.dueDate ?? DateTime.now();
    _dateController = TextEditingController(
      text: DateFormat('MMMM dd, yyyy').format(_selectedDate!),
    );
  }

  @override
  void dispose() {
    _idController.dispose();
    _titleController.dispose();
    _vehicleController.dispose();
    _descController.dispose();
    _priceController.dispose();
    _dateController.dispose();
    super.dispose();
  }

  void _pickDate() async {
    DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate!,
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
    );
    if (picked != null) {
      setState(() {
        _selectedDate = picked;
        _dateController.text = DateFormat('MMMM dd, yyyy').format(picked);
      });
    }
  }

  void _saveForm() {
    if (_formKey.currentState!.validate()) {
      final provider = Provider.of<EventsProvider>(context, listen: false);
      final inputId = _idController.text.trim();

      final bool idExists = provider.events.any((e) => e.id == inputId);
      final bool isSameIdAsOriginal = widget.event != null && widget.event!.id == inputId;

      if (idExists && !isSameIdAsOriginal) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Error: ID already exists. Please use a unique ID.")),
        );
        return;
      }

      showDialog(
        context: context,
        builder: (ctx) => AlertDialog(
          title: Text(widget.event == null ? "Create Event?" : "Save Changes?"),
          content: Text(widget.event == null
              ? "Are you sure you want to add this new event?"
              : "Are you sure you want to update this event?"),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(ctx),
              child: const Text("Cancel"),
            ),
            FilledButton(
              onPressed: () {
                final newEvent = MaintenanceEvent(
                  id: inputId,
                  title: _titleController.text,
                  vehicleIdentifier: _vehicleController.text,
                  description: _descController.text,
                  category: _category,
                  dueDate: _selectedDate!,
                  status: _status,
                  price: double.parse(_priceController.text),
                );

                if (widget.event == null) {
                  provider.addEvent(newEvent);
                } else {
                  provider.updateEvent(widget.event!.id, newEvent);
                }

                Navigator.pop(ctx);
                Navigator.pop(context);
              },
              child: const Text("Confirm"),
            ),
          ],
        ),
      );
    }
  }

  void _deleteEvent() {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text("Delete Event"),
        content: const Text("Are you sure you want to delete this event? This cannot be undone."),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text("Cancel")),
          TextButton(
            onPressed: () {
              Provider.of<EventsProvider>(context, listen: false).deleteEvent(widget.event!.id);
              Navigator.pop(ctx);
              Navigator.pop(context);
            },
            child: const Text("Delete", style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.event == null ? "Add Event" : "Edit Event"),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),

      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              TextFormField(
                controller: _idController,
                decoration: const InputDecoration(
                  labelText: "ID",
                  border: OutlineInputBorder(),
                  helperText: "Unique Identifier (UUID)",
                ),
                validator: (val) {
                  if (val == null || val.isEmpty) return "ID is required";
                  final uuidRegex = RegExp(r'^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$');
                  if (!uuidRegex.hasMatch(val)) {
                    return "Invalid UUID format (e.g. xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)";
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),

              TextFormField(
                controller: _titleController,
                decoration: const InputDecoration(labelText: "Title", border: OutlineInputBorder()),
                validator: (val) => val!.isEmpty ? "Title is required" : null,
              ),
              const SizedBox(height: 16),

              TextFormField(
                controller: _vehicleController,
                decoration: const InputDecoration(labelText: "Vehicle Identifier", border: OutlineInputBorder()),
                validator: (val) => val!.isEmpty ? "Vehicle is required" : null,
              ),
              const SizedBox(height: 16),

              TextFormField(
                controller: _descController,
                decoration: const InputDecoration(labelText: "Description (Optional)", border: OutlineInputBorder()),
                maxLines: 3,
              ),
              const SizedBox(height: 16),

              TextFormField(
                controller: _priceController,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(labelText: "Price", border: OutlineInputBorder()),
                validator: (val) {
                  if (val!.isEmpty) return "Price is required";
                  if (double.tryParse(val) == null) return "Invalid number";
                  return null;
                },
              ),
              const SizedBox(height: 16),

              TextFormField(
                controller: _dateController,
                readOnly: true,
                onTap: _pickDate,
                decoration: const InputDecoration(
                  labelText: "Due Date",
                  border: OutlineInputBorder(),
                  suffixIcon: Icon(Icons.calendar_today),
                ),
              ),
              const SizedBox(height: 24),

              const Text("Category", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              const SizedBox(height: 8),
              Row(
                children: [
                  Expanded(
                    child: RadioListTile(
                      title: const Text("Service"),
                      value: "SERVICE",
                      groupValue: _category,
                      onChanged: (val) => setState(() => _category = val.toString()),
                      contentPadding: EdgeInsets.zero,
                    ),
                  ),
                  Expanded(
                    child: RadioListTile(
                      title: const Text("Document"),
                      value: "DOCUMENT",
                      groupValue: _category,
                      onChanged: (val) => setState(() => _category = val.toString()),
                      contentPadding: EdgeInsets.zero,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),

              DropdownButtonFormField(
                value: _status,
                items: _statusOptions.map((s) => DropdownMenuItem(value: s, child: Text(s))).toList(),
                onChanged: (val) => setState(() => _status = val.toString()),
                decoration: const InputDecoration(labelText: "Status", border: OutlineInputBorder()),
              ),
              const SizedBox(height: 100),
            ],
          ),
        ),
      ),

      bottomNavigationBar: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
            color: Theme.of(context).scaffoldBackgroundColor,
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                offset: const Offset(0, -4),
                blurRadius: 10,
              )
            ]
        ),
        child: SafeArea(
          child: Row(
            children: [
              if (widget.event != null)
                Expanded(
                  child: OutlinedButton(
                    onPressed: _deleteEvent,
                    style: OutlinedButton.styleFrom(
                      foregroundColor: Colors.red,
                      side: const BorderSide(color: Colors.red, width: 1.5),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(50)),
                      padding: const EdgeInsets.symmetric(vertical: 16),
                    ),
                    child: const Text("Delete"),
                  ),
                ),
              if (widget.event != null) const SizedBox(width: 16),
              Expanded(
                child: FilledButton(
                  onPressed: _saveForm,
                  style: FilledButton.styleFrom(
                    backgroundColor: const Color(0xFF0061A4),
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(50)),
                  ),
                  child: const Text("Save Changes"),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}