# 🚗 Service Buddy - Project Documentation

## 1. Project Vision & Goals

### The Problem
Every car owner knows the feeling of suddenly remembering an overdue task: "When was the last oil change?" or "Is my periodic inspection still valid?". Tracking vehicle maintenance and document deadlines is often a disorganized process involving stickers on the windshield, scattered papers, or unreliable memory. This can lead to neglected maintenance, which compromises vehicle safety and resale value, and even legal issues from expired documents.

### Our Solution
**Service Buddy** is a modern mobile application designed to be the perfect companion for any vehicle owner. It acts as a proactive digital glovebox, centralizing all important service tasks and document renewals for **one or more vehicles**. The application is built around three core principles:

* **Simplicity & Organization:** The interface is clean and intuitive, allowing anyone to add or view events quickly. It provides a single, organized dashboard for all vehicle-related deadlines, eliminating clutter and transforming car ownership from a chore into a stress-free experience.
* **Proactivity:** Instead of relying on memory, the app provides smart, timely reminders, ensuring a user never misses an important service or renewal.
* **Reliability (Offline-First):** The app is designed to be 100% functional without an internet connection. Users can add, view, and complete tasks anywhere, with data syncing automatically in the background when connectivity is restored.

## 2. Core Data Model: The `MaintenanceEvent` Entity

To maintain focus and simplicity, the entire application logic is built around a single, powerful entity: `MaintenanceEvent`. This model is versatile enough to represent everything from a physical service to a document renewal for any vehicle.

| Field Name          | Data Type     | Description                                                                                                                                                             | Example                                        |
| ------------------- | ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------- |
| **`id`** | **`String`** | **A [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier) (Universally Unique Identifier). This prevents ID conflicts when items are created offline.** | **`"f47ac10b-58cc-4372-a567-0e02b2c3d479"`** |
| `vehicleIdentifier` | `String`      | An identifier for the vehicle (e.g., license plate or a nickname). Allows grouping and filtering events per vehicle.                                                    | `"B 123 ABC"` or `"My Duster"`                 |
| `title`             | `String`      | A concise, user-friendly name for the event. This is the main text displayed in lists.                                                                                  | `"Summer/Winter Tire Change"`                  |
| `description`       | `String`      | An optional field for extra details, such as policy numbers, specific parts used, or notes.                                                                             | `"Michelin CrossClimate 2 tires"`              |
| `category`          | `String`      | Defines the event type for filtering and icons. Limited to predefined values.                                                                                           | `'SERVICE'` or `'DOCUMENT'`                    |
| `dueDate`           | `Date`        | The deadline for the event. This date is used for sorting and triggering notifications.                                                                                 | `2025-11-15`                                   |
| `status`            | `String`      | The current state of the event, which dictates its visual representation.                                                                                               | `'UPCOMING'`, `'COMPLETED'`, `'OVERDUE'`       |

## 3. Core Application Functionality (CRUD Operations)

The user's interaction with their maintenance data is handled through four fundamental operations (Create, Read, Update, Delete).

* **➕ Create:** Users can add a new `MaintenanceEvent` via a prominent "+" button. This opens a form where they fill in the event's details, including the **vehicle identifier**. Upon saving, the event is instantly added to the main list.

* **📖 Read:** The main screen presents a chronologically sorted list of all `MaintenanceEvent`s. Each item clearly displays the event title and the associated **vehicle identifier**. Tapping an item opens a dedicated details screen.

* **✍️ Update:** Users can modify any event from its details screen. This includes changing the event's status, date, or even moving it to a different **vehicle** by editing the identifier field.

* **🗑️ Delete:** An event can be removed by swiping it in the list or via a "Delete" button. A confirmation dialog (`"Are you sure?"`) prevents accidental deletions.

## 4. Data Persistence Strategy: Local-First Synchronization

To ensure a fast, reliable, and seamless user experience, **Service Buddy** is architected using a **local-first** approach. The UI interacts exclusively with a local on-device database, making the app feel instantaneous.

**The Flow for Each Operation:**
1.  **Local Write:** Any change (create, update, delete) is first committed to the **local database**.
2.  **Instant UI Update:** The application's UI immediately reflects this change.
3.  **Background Sync:** The change is added to a queue. When a network connection is available, the sync process sends the queued changes to the **remote server**.

This model guarantees that the app is 100% functional offline.

## 5. 📶❌ Offline-First Scenarios

The local-first architecture ensures that the user's workflow is never interrupted by a lack of internet connectivity.

* **Create (Offline):** A user adds a new event for their second car ("BV 99 ABC") while offline. The event is saved locally (with a newly generated `UUID`) and appears in the app. It will be synced automatically once connectivity is restored.

* **Read (Offline):** All existing events for all vehicles are always available for browsing. The user can check details for any of their cars at any time.

* **Update (Offline):** A user marks the "Annual Inspection" for car "B 123 ABC" as `'COMPLETED'` while offline. The change is saved locally, and the update is queued for the next sync.

* **Delete (Offline):** The user deletes an old service record. The app removes it from the local database and UI instantly and queues the delete command to be sent to the server later.

## 6. UI/UX Mockups
<table>
  <tr align="center">
    <td>
      <img width="250" alt="Mockup - Listă Evenimente" src="https://github.com/user-attachments/assets/9a17757e-4c9a-4663-80e5-6287ed0bdadd">
    </td>
    <td>
      <img width="250" alt="Mockup - Detalii Eveniment" src="https://github.com/user-attachments/assets/96d860b0-ab73-4d72-b888-3a8b7b2a855b">
    </td>
    <td>
      <img width="250" alt="Mockup - Adăugare Eveniment" src="https://github.com/user-attachments/assets/0c986a8b-367a-4f58-9af3-753bba3da22f">
    </td>
  </tr>
</table>

