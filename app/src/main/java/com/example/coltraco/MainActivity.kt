package com.example.coltraco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.coltraco.ui.theme.ColtracoTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Initialize database instance
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "users-db"
            ).build()

            // Get the UserDao instance from the database
            val userDao = db.userDao()

            ColtracoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BasicUI(userDao)
                }
            }
        }
    }
}

@Composable
fun BasicUI(userDao: UserDao) {

    // state variables
    var name by remember { mutableStateOf("") }
    var nameDisplay by remember { mutableStateOf("") }
    var isButtonClicked by remember { mutableStateOf(false) }
    var showHelloMessage by remember { mutableStateOf(false) }
    val isValidName = name.length >= 2
            && name.matches(Regex("^[a-zA-Z\\s]*$"))
    val isValidAge = true
    val focusManager = LocalFocusManager.current

    // UI layout
    Column(modifier = Modifier.fillMaxSize()
        .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        // error message
        if (isButtonClicked && !isValidName) {
            Text(
                text = stringResource(R.string.errorMessage),
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            showHelloMessage = false
        }

        // name field

        Text(
            text = "Enter your name:",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = name,
            label = { Text(text = stringResource(R.string.nameEnter)) },
            singleLine = true,
            maxLines = 1,
            onValueChange = { text -> isButtonClicked = false
                if (!text.contains("\n"))
                    name = text},
            placeholder = { Text(text = stringResource(R.string.nameType)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.personIconDesc)
            ) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    // close the keyboard
                    focusManager.clearFocus()
                } )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // date of birth fields
        var day by remember { mutableStateOf("") }
        var month by remember { mutableStateOf("") }
        var year by remember { mutableStateOf("") }
        var age by remember { mutableStateOf(-1) }

        Text(
            text = "Enter your date of birth:",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        DateOfBirthFields(
            onDateEntered = { enteredDay, enteredMonth, enteredYear ->
                day = enteredDay.toString().padStart(2, '0')
                month = enteredMonth.toString().padStart(2, '0')
                year = enteredYear.toString()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // "Say Hello" button
        Button(
            onClick = {
                isButtonClicked = true
                if (isValidName && isValidAge) { nameDisplay = name
                    age = calculateAge(day.toInt(), month.toInt(), year.toInt())
                    showHelloMessage = true
                }
            },
            enabled = name.isNotBlank()
        ) {
            Text(text = stringResource(R.string.buttonLabel))
        }

        // show the "Hello" message if name input is valid
        if (showHelloMessage) {
            Text(text = "Hello, $nameDisplay! You are $age years old." +
                    " You were born in ${year}.",
                modifier = Modifier.padding(top = 8.dp))
            // insert into database
            val user = User(name = name, age = age, birthMonth = month, birthYear = year)
            LaunchedEffect(userDao) {
                userDao.insertUser(user)
            }
        }

    }
}

@Composable
fun DateOfBirthFields(onDateEntered: (Int, Int, Int) -> Unit) {

    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    // Requesters for the focus of the Month and Year fields
    val monthFocusRequester = remember { FocusRequester() }
    val yearFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Day field
        OutlinedTextField(
            value = day,
            onValueChange = { value ->
                val filteredValue = value.filter { it.isDigit() }
                day = filteredValue.take(2)

                // Move focus to the Month field when two digits have been entered
                if (day.length == 2) {
                    monthFocusRequester.requestFocus()
                }
            },
            label = { Text(stringResource(R.string.day)) },
            placeholder = { Text(text = stringResource(R.string.dayFormat)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    // move to month focus
                    monthFocusRequester.requestFocus()
                } ),
            modifier = Modifier.weight(1f)
        )

        // Month field
        OutlinedTextField(
            value = month,
            onValueChange = { value ->
                val filteredValue = value.filter { it.isDigit() }
                month = filteredValue.take(2)

                // Move focus to the Year field when two digits have been entered
                if (month.length == 2) {
                    yearFocusRequester.requestFocus()
                }
            },
            label = { Text(stringResource(R.string.month)) },
            placeholder = { Text(text = stringResource(R.string.monthFormat)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    // move to year focus
                    yearFocusRequester.requestFocus()
                } ),
            modifier = Modifier.weight(1f)
                .focusRequester(monthFocusRequester)

        )

        // Year field
        OutlinedTextField(
            value = year,
            onValueChange = { value ->
                val filteredValue = value.filter { it.isDigit() }
                year = filteredValue.take(4)

                // Clear focus once maximum number of digits is entered
                if (year.length == 4) {
                    focusManager.clearFocus()
                }
            },
            label = { Text(stringResource(R.string.year)) },
            placeholder = { Text(text = stringResource(R.string.yearFormat)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    // close the keyboard
                    focusManager.clearFocus()
                } ),
            modifier = Modifier.weight(1f)
                .focusRequester(yearFocusRequester)
        )
    }

    // Call the external function with the date values when all three fields have been filled
    LaunchedEffect(day, month, year) {
        if (day.isNotEmpty() && month.isNotEmpty() && year.isNotEmpty()) {
            onDateEntered(day.toInt(), month.toInt(), year.toInt())
        }
    }
}




//@Composable
//@Preview(showBackground = true)
//fun PreviewUI() {
//    ColtracoTheme {
//        BasicUI()
//    }
//}

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "birth_year") val birthYear: String?,
    @ColumnInfo(name = "birth_month") val birthMonth: String?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    fun delete(user: User)
}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}