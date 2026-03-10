package com.example.servicebuddy.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.servicebuddy.R
import com.example.servicebuddy.model.MaintenanceEvent
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditEventFragment : Fragment() {
    private val viewModel: EventViewModel by activityViewModels()
    private val args: EditEventFragmentArgs by navArgs()
    private var currentEvent: MaintenanceEvent? = null
    private val calendar = Calendar.getInstance()

    
    private lateinit var layoutId: TextInputLayout
    private lateinit var etId: TextInputEditText
    private lateinit var etTitle: TextInputEditText
    private lateinit var etVehicle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etPrice: TextInputEditText
    private lateinit var etDueDate: TextInputEditText
    private lateinit var actvStatus: AutoCompleteTextView
    private lateinit var toggleCategory: MaterialButtonToggleGroup
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)

        
        val statuses = resources.getStringArray(R.array.status_options)
        actvStatus.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses))

        
        viewModel.getEventById(args.eventId) { event ->
            currentEvent = event
            if (event == null) {
                Toast.makeText(context, "Not found", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                populate(event)
            }
        }

        view.findViewById<ImageView>(R.id.ivClose).setOnClickListener { findNavController().popBackStack() }
        etDueDate.setOnClickListener { showDatePicker() }

        btnSave.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch { validateAndSave() }
        }
        btnDelete.setOnClickListener { confirmDelete() }
    }

    private fun bindViews(view: View) {
        layoutId = view.findViewById(R.id.layout_id)
        etId = view.findViewById(R.id.et_id)
        etTitle = view.findViewById(R.id.et_title)
        etVehicle = view.findViewById(R.id.et_vehicle)
        etDescription = view.findViewById(R.id.et_description)
        etPrice = view.findViewById(R.id.et_price)
        etDueDate = view.findViewById(R.id.et_due_date)
        actvStatus = view.findViewById(R.id.actv_status)
        toggleCategory = view.findViewById(R.id.toggle_category)
        btnSave = view.findViewById(R.id.btn_save)
        btnDelete = view.findViewById(R.id.btn_delete)
    }

    private fun populate(e: MaintenanceEvent) {
        etId.setText(e.id)
        etTitle.setText(e.title)
        etVehicle.setText(e.vehicleIdentifier)
        etDescription.setText(e.description)
        etPrice.setText(e.price.toString())
        calendar.time = e.dueDate
        updateDateText()
        actvStatus.setText(e.status, false)
        toggleCategory.check(if (e.category == "SERVICE") R.id.btn_service else R.id.btn_document)
    }

    private fun showDatePicker() {
        DatePickerDialog(requireContext(), R.style.DatePicker, { _, y, m, d ->
            calendar.set(y, m, d)
            updateDateText()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateText() {
        etDueDate.setText(SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(calendar.time))
    }

    private suspend fun validateAndSave() {
        val newId = etId.text.toString().trim()

        // ID validation
        if (newId.isEmpty()) {
            layoutId.error = "Required"
            return
        }

        if (!isValidUUID(newId)) {
            layoutId.error = "Invalid UUID: '$newId'. Use 8-4-4-4-12 characters."
            return
        }

        if (newId != currentEvent?.id && viewModel.getEventByIdSuspend(newId) != null) {
            layoutId.error = "ID already exists"
            return
        }
        
        layoutId.error = null // Clear error if validation passes

        val updated = currentEvent!!.copy(
            id = newId,
            title = etTitle.text.toString(),
            vehicleIdentifier = etVehicle.text.toString(),
            description = etDescription.text.toString(),
            price = etPrice.text.toString().toDoubleOrNull() ?: 0.0,
            status = actvStatus.text.toString(),
            category = if (toggleCategory.checkedButtonId == R.id.btn_service) "SERVICE" else "DOCUMENT",
            dueDate = calendar.time
        )
        viewModel.updateEventById(currentEvent!!.id, updated)
        findNavController().popBackStack()
    }

    private fun isValidUUID(uuid: String): Boolean {
        val uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$".toRegex()
        return uuid.matches(uuidRegex)
    }

    private fun confirmDelete() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete?")
            .setPositiveButton("Yes") { _, _ ->
                currentEvent?.let { viewModel.deleteEvent(it.id) }
                findNavController().popBackStack()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
