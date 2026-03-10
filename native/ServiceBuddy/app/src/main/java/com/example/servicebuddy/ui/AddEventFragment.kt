package com.example.servicebuddy.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.servicebuddy.R
import com.example.servicebuddy.model.MaintenanceEvent
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddEventFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()
    private val calendar: Calendar = Calendar.getInstance()

    private lateinit var layoutId: TextInputLayout
    private lateinit var etId: TextInputEditText
    private lateinit var layoutTitle: TextInputLayout
    private lateinit var etTitle: TextInputEditText
    private lateinit var layoutVehicle: TextInputLayout
    private lateinit var etVehicle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var layoutPrice: TextInputLayout
    private lateinit var etPrice: TextInputEditText
    private lateinit var etDueDate: TextInputEditText
    private lateinit var toggleCategory: MaterialButtonToggleGroup
    private lateinit var actvStatus: AutoCompleteTextView
    private lateinit var btnSave: Button
    private lateinit var ivClose: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(view)

        
        val statuses = resources.getStringArray(R.array.status_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        actvStatus.setAdapter(adapter)
        actvStatus.setText(statuses[2], false) 

        
        etId.setText(UUID.randomUUID().toString())
        updateDateInView()
        toggleCategory.check(R.id.btn_service)

        ivClose.setOnClickListener { findNavController().popBackStack() }
        etDueDate.setOnClickListener { showDatePickerDialog() }

        
        btnSave.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                validateInputAndSave()
            }
        }
    }

    private fun findViews(view: View) {
        layoutId = view.findViewById(R.id.layout_id)
        etId = view.findViewById(R.id.et_id)
        layoutTitle = view.findViewById(R.id.layout_title)
        etTitle = view.findViewById(R.id.et_title)
        layoutVehicle = view.findViewById(R.id.layout_vehicle)
        etVehicle = view.findViewById(R.id.et_vehicle)
        etDescription = view.findViewById(R.id.et_description)
        layoutPrice = view.findViewById(R.id.layout_price)
        etPrice = view.findViewById(R.id.et_price)
        etDueDate = view.findViewById(R.id.et_due_date)
        toggleCategory = view.findViewById(R.id.toggle_category)
        actvStatus = view.findViewById(R.id.actv_status)
        btnSave = view.findViewById(R.id.btn_save)
        ivClose = view.findViewById(R.id.ivClose)
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        DatePickerDialog(
            requireContext(),
            R.style.DatePicker, 
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView() {
        val myFormat = "MMMM dd, yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etDueDate.setText(sdf.format(calendar.time))
    }

    private suspend fun validateInputAndSave() {
        var isValid = true
        val id = etId.text.toString()

        if (id.isEmpty()) {
            layoutId.error = "ID is required"
            isValid = false
        } else {
            if (!isValidUUID(id)) {
                layoutId.error = "Invalid UUID: '$id'. Use 8-4-4-4-12 characters."
                isValid = false
            } else {
                layoutId.error = null
            }
        }

        if (etTitle.text.isNullOrEmpty()) { layoutTitle.error = "Required"; isValid = false }
        if (etVehicle.text.isNullOrEmpty()) { layoutVehicle.error = "Required"; isValid = false }
        if (etPrice.text.isNullOrEmpty()) { layoutPrice.error = "Required"; isValid = false }

        if (!isValid) return

        
        val existing = viewModel.getEventByIdSuspend(id)
        if (existing != null) {
            layoutId.error = "ID already exists"
            return
        }

        saveEvent()
    }

    private fun saveEvent() {
        val newEvent = MaintenanceEvent(
            id = etId.text.toString(),
            title = etTitle.text.toString(),
            vehicleIdentifier = etVehicle.text.toString(),
            description = etDescription.text.toString(),
            category = if (toggleCategory.checkedButtonId == R.id.btn_service) "SERVICE" else "DOCUMENT",
            dueDate = calendar.time,
            status = actvStatus.text.toString(),
            price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
        )

        
        viewModel.addEvent(newEvent)
        Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun isValidUUID(uuid: String): Boolean {
        val uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$".toRegex()
        return uuid.matches(uuidRegex)
    }
}
