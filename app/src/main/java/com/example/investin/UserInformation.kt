package com.example.investin

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.investin.databinding.ActivityUserInformationBinding
import com.example.investin.login.SignIn
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import java.util.Locale
import java.util.Objects

class UserInformation : AppCompatActivity() {

    private lateinit var dateOfBirthTextInputLayout: TextInputLayout
    private lateinit var cityTextInputLayout: TextInputLayout
    private lateinit var educationTextInputLayout: TextInputLayout
    private lateinit var etDateOfBirth: EditText
    private val calendar = Calendar.getInstance()

    private val cities = arrayOf(
        "Karachi",
        "Lahore",
        "Islamabad",
        "Rawalpindi",
        "Faisalabad"
    )
    private val education = arrayOf(
        "Primary School",
        "Middle School",
        "High School",
        "Intermediate",
        "Bachelor's Degree",
        "Master's Degree",
        "Doctorate (Ph.D.)"
    )

//    tIRCduYOqCK3hwqJNdcd  id

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var usersReference: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUserInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val city = findViewById<EditText>(R.id.etCity)
        val education = findViewById<EditText>(R.id.etEducation)

        // Initialize views
        dateOfBirthTextInputLayout = findViewById(R.id.dateOfBirth_text_input_layout)
        cityTextInputLayout = findViewById(R.id.city_text_input_layout)
        educationTextInputLayout = findViewById(R.id.education_text_input_layout)
        etDateOfBirth = findViewById(R.id.etDateOfBirth)


        // Attach OnClickListener to the DOB icon
        dateOfBirthTextInputLayout.setEndIconOnClickListener {
            showDatePicker()
        }

        etDateOfBirth.setOnClickListener {
            showDatePicker()
        }

        // Attach OnClickListener to the City icon
        cityTextInputLayout.setEndIconOnClickListener {
            showCitySelection()
        }

        city.setOnClickListener {
            showCitySelection()
        }

        // Attach OnClickListener to the Education Level icon
        educationTextInputLayout.setEndIconOnClickListener {
            showEducationSelection()
        }

        education.setOnClickListener {
            showEducationSelection()
        }

        binding.btFinish.setOnClickListener {
            saveUserInformation()
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun saveUserInformation() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        if (userId.isNotEmpty()) {
            val dateOfBirthTextInputLayout =
                findViewById<TextInputLayout>(R.id.dateOfBirth_text_input_layout)
            val nameTextInputLayout = findViewById<TextInputLayout>(R.id.username_text_input_layout)
            val cnicTextInputLayout = findViewById<TextInputLayout>(R.id.cnic_text_input_layout)
            val numberTextInputLayout =
                findViewById<TextInputLayout>(R.id.phoneNumber_text_input_layout)
            val cityTextInputLayout = findViewById<TextInputLayout>(R.id.city_text_input_layout)
            val permanentAddressTextInputLayout =
                findViewById<TextInputLayout>(R.id.address_text_input_layout)
            val educationTextInputLayout =
                findViewById<TextInputLayout>(R.id.education_text_input_layout)
            val genderRadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)

            val dateOfBirth = findViewById<EditText>(R.id.etDateOfBirth).text.toString().trim()
            val name = findViewById<EditText>(R.id.etName).text.toString().trim()
            val cnic = findViewById<EditText>(R.id.etCNIC).text.toString().trim()
            val number = findViewById<EditText>(R.id.etPhoneNumber).text.toString().trim()
            val city = findViewById<EditText>(R.id.etCity).text.toString().trim()
            val permanentAddress = findViewById<EditText>(R.id.etAddress).text.toString().trim()
            val education = findViewById<EditText>(R.id.etEducation).text.toString().trim()

            // Check for valid CNIC, name, and phone number format
            if (isValidCNIC(cnic) && isValidPhoneNumber(number) &&
                dateOfBirth.isNotEmpty() && name.isNotEmpty() && city.isNotEmpty() &&
                permanentAddress.isNotEmpty() && education.isNotEmpty()
            ) {
                val selectedGenderId = genderRadioGroup.checkedRadioButtonId
                val gender = when (selectedGenderId) {
                    R.id.maleRadioButton -> "Male"
                    R.id.femaleRadioButton -> "Female"
                    else -> "" // Handle default case or no selection
                }

                val userInformation = hashMapOf(
                    "dateOfBirth" to dateOfBirth,
                    "name" to name,
                    "gender" to gender,
                    "cnic" to cnic,
                    "number" to number,
                    "city" to city,
                    "permanentAddress" to permanentAddress,
                    "education" to education
                    // Add other user information fields similarly
                )
//                var hashMap=HashMap<String,Objects>()


                val userDocRef = firebaseFirestore.collection("InvestIn")
                    .document("profile")
                    .collection(userId)
                    .document(userId)

                    userDocRef.set(userInformation)
                    .addOnSuccessListener {
                        // Data saved successfully
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "User information saved!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Failed to save data
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Set errors for invalid format or empty fields
                if (!isValidPhoneNumber(number)) {
                    numberTextInputLayout.error = "Invalid phone number format!"
                } else {
                    numberTextInputLayout.error = null
                }

                if (!isValidCNIC(cnic)) {
                    cnicTextInputLayout.error = "Invalid CNIC format!"
                } else {
                    cnicTextInputLayout.error = null
                }

                if (dateOfBirth.isEmpty()) {
                    dateOfBirthTextInputLayout.error = "Please enter date of birth!"
                } else {
                    dateOfBirthTextInputLayout.error = null
                }

                if (name.isEmpty()) {
                    nameTextInputLayout.error = "Please enter name!"
                } else {
                    nameTextInputLayout.error = null
                }

                if (city.isEmpty()) {
                    cityTextInputLayout.error = "Please enter city!"
                } else {
                    cityTextInputLayout.error = null
                }

                if (permanentAddress.isEmpty()) {
                    permanentAddressTextInputLayout.error = "Please enter permanent address!"
                } else {
                    permanentAddressTextInputLayout.error = null
                }

                if (education.isEmpty()) {
                    educationTextInputLayout.error = "Please enter education!"
                } else {
                    educationTextInputLayout.error = null
                }
            }
        } else {
            // Handle case when userId is empty or null
            Toast.makeText(this, "User ID is empty!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidCNIC(cnic: String): Boolean {
        val cnicPattern = Regex("\\d{5}-\\d{7}-\\d")

        return cnicPattern.matches(cnic)
    }

    private fun isValidPhoneNumber(number: String): Boolean {
        // Check if the number starts with "+92" and has a total length of 12
        return number.startsWith("+92") && number.length == 13
    }

    private fun showCitySelection() {
        val popupMenu = PopupMenu(this, findViewById(R.id.etCity))
        cities.forEach { city ->
            popupMenu.menu.add(city)
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            findViewById<EditText>(R.id.etCity).setText(menuItem.title)
            true
        }
        popupMenu.show()
    }

    private fun showEducationSelection() {
        val popupMenu = PopupMenu(this, findViewById(R.id.etEducation))
        education.forEach { education ->
            popupMenu.menu.add(education)
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            findViewById<EditText>(R.id.etEducation).setText(menuItem.title)
            true
        }
        popupMenu.show()
    }

    // Shows a date picker dialog to select a date and updates the EditText with the selected date
    private fun showDatePicker() {
        // Create a Calendar instance for 15 years ago from the current date
        val minDateCalendar = Calendar.getInstance()
        minDateCalendar.add(Calendar.YEAR, -15)

        // Create a DatePickerDialog to allow the user to select a date
        val datePicker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Check if the selected date is at least 15 years ago
                if (calendar.timeInMillis <= minDateCalendar.timeInMillis) {
                    updateDateInView() // Update the selected date in the EditText
                } else {
                    // Show a toast indicating the user must be 15+
                    Toast.makeText(this, "Must be 15 years or older!", Toast.LENGTH_SHORT).show()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum date to 15 years ago
        datePicker.datePicker.maxDate = minDateCalendar.timeInMillis
        datePicker.show() // Display the DatePickerDialog
    }

    // Updates the EditText with the selected date from the calendar
    private fun updateDateInView() {
        // Format the selected date and set it in the EditText
        val dateFormat = "dd/MM/yyyy" // Define your desired date format here
        val simpleDateFormat = java.text.SimpleDateFormat(dateFormat, Locale.getDefault())
        etDateOfBirth.setText(simpleDateFormat.format(calendar.time))
    }

    fun showDatePicker(view: View) {}
}