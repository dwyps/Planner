package com.example.planner.ui.main.taskform

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.DatePicker
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.example.planner.R
import com.example.planner.data.model.Task
import com.example.planner.util.ColorDialog
import com.example.planner.util.GridViewAdapter
import com.example.planner.util.IconDialog
import com.example.planner.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_task_form.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class TaskFormFragment : Fragment(R.layout.fragment_task_form),
    IconDialog.OnItemListener,
    ColorDialog.OnItemListener,
    OnSelectDateListener{

    @Inject
    lateinit var taskFormViewModel: TaskFormViewModel

    private lateinit var icons : List<Int>
    private lateinit var colors : List<Int>

    private var icon = R.drawable.ic_call
    private var color = R.color.colorPrimary
    private var dateFrom: Long = Calendar.getInstance().timeInMillis
    private var dateTo: Long = Calendar.getInstance().timeInMillis
    private var repeat: Int = 0

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initListeners()
        initUI()
    }

    @ExperimentalCoroutinesApi
    private fun initViewModel() {

        taskFormViewModel.addTaskLiveData.observe(viewLifecycleOwner, {


            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility =
                    View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    if (it.data?.id != null) {

                        taskFormViewModel.addTask(it.data)
                    } else
                        findNavController().popBackStack()

                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    @ExperimentalCoroutinesApi
    private fun initListeners() {

        initSpinners()
        initButtons()
    }

    private fun initUI() {

        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)

        task_form_edit_text_date.text = formattedDate

    }

    private fun initSpinners() {

        val repeatList = arrayOf("One time", "Daily", "Monthly", "Yearly")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, repeatList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        task_form_spinner_repeat_options.adapter = spinnerAdapter
        task_form_spinner_repeat_options.dropDownVerticalOffset = 10

        task_form_spinner_repeat_options.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                repeat = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                repeat = 0
            }
        }

    }

    @ExperimentalCoroutinesApi
    private fun initButtons() {

        task_form_btn_cancel.setOnClickListener {
            findNavController().popBackStack()
        }

        task_form_btn_icon.setOnClickListener {

            showIconPickerDialog()
        }

        task_form_btn_color.setOnClickListener {

            showColorPickerDialog()
        }

        task_form_edit_text_date.setOnClickListener {

            showDatePickerDialog()
        }

        task_form_btn_save.setOnClickListener {

            val title = task_form_edit_text_title.text.toString()
            val description = task_form_edit_text_description.text.toString()

            if (title.isEmpty()) {

                Toast.makeText(requireContext(), "Title can't be empty!", Toast.LENGTH_SHORT).show()
            } else {

                val task = Task(
                    null,
                    false,
                    title,
                    description,
                    icon,
                    color,
                    dateFrom,
                    dateTo,
                    task_form_switch.isChecked,
                    repeat
                )

                Timber.e(task.toString())

                taskFormViewModel.getTaskID(task)
            }
        }
    }

    private fun showIconPickerDialog() {

        icons = listOf(
            R.drawable.ic_call,
            R.drawable.ic_email,
            R.drawable.ic_schedule,
            R.drawable.ic_buy,
            R.drawable.ic_learn,
            R.drawable.ic_read,
            R.drawable.ic_drink,
            R.drawable.ic_exercise,
            R.drawable.ic_visit
        )

        IconDialog(requireActivity(), GridViewAdapter(icons), this).show()
    }

    private fun showColorPickerDialog() {

        colors = listOf(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark,
            R.color.colorSecondary,
            R.color.colorTaskBackground
        )

        ColorDialog(requireActivity(), GridViewAdapter(colors), this).show()
    }

    private fun showDatePickerDialog() {

        val builder = DatePickerBuilder(requireContext(), this)
            .pickerType(CalendarView.RANGE_PICKER)

        val datePicker: DatePicker = builder.build()

        datePicker.show()
    }

    override fun onIconClick(position: Int) {

        icon = icons[position]
        task_form_btn_icon.setImageResource(icon)
    }

    override fun onColorClick(position: Int) {

        color = colors[position]
        task_form_btn_color.setImageResource(color)
    }

    override fun onSelect(calendar: List<Calendar>) {

        dateFrom = calendar.first().timeInMillis
        dateTo = calendar.last().timeInMillis

        val c = dateFrom
        val df = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)

        task_form_edit_text_date.text = formattedDate
    }
}