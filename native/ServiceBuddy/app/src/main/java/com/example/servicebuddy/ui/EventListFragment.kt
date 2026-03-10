package com.example.servicebuddy.ui

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.servicebuddy.R
import com.example.servicebuddy.model.MaintenanceEvent

class EventListFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        eventAdapter = EventAdapter { event ->
            val action = EventListFragmentDirections.actionToEditEventFragment(event.id)
            findNavController().navigate(action)
        }
        recyclerView.adapter = eventAdapter
        recyclerView.itemAnimator = null

        viewModel.events.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }

        val searchEditText = view.findViewById<EditText>(R.id.etSearch)
        searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.setSearchQuery(text.toString())
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.event_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    
                    R.id.action_refresh -> {
                        viewModel.refreshData()
                        Toast.makeText(context, "Checking connection...", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_add -> {
                        findNavController().navigate(R.id.action_to_addEventFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}
