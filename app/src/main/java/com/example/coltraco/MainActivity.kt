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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coltraco.ui.theme.ColtracoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ColtracoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BasicUI()
                }
            }
        }
    }
}

@Composable
fun BasicUI() {

    // state variables
    var name by remember { mutableStateOf("") }
    var nameDisplay by remember { mutableStateOf("") }
    var isButtonClicked by remember { mutableStateOf(false) }
    var showHelloMessage by remember { mutableStateOf(false) }
    val isValidName = name.length >= 2
                   && name.matches(Regex("^[a-zA-Z\\s]*$"))
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
        OutlinedTextField(
            value = name,
            label = { Text(text = stringResource(R.string.nameEnter)) },
            singleLine = true,
            onValueChange = { text -> name = text
                            isButtonClicked = false},
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

        // "Say Hello" button
        Button(
            onClick = {
                isButtonClicked = true
                if (isValidName) { nameDisplay = name
                    showHelloMessage = true }
            },
            enabled = name.isNotBlank()
        ) {
            Text(text = stringResource(R.string.buttonLabel))
        }

        // show the "Hello" message if name input is valid
        if (showHelloMessage) {
            Text(text = "Hello, ${nameDisplay}!")
        }

    }
}

@Composable
@Preview(showBackground = true)
fun PreviewUI() {
    ColtracoTheme {
        BasicUI()
    }
}