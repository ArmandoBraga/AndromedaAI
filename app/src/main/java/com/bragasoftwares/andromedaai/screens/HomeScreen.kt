package com.bragasoftwares.andromedaai.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bragasoftwares.andromedaai.ui.theme.Purple40


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HomeScreen (): @Composable () -> Unit {
    return {


        Column( modifier = Modifier
            .fillMaxSize(),
            //  horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "Tela Inicial2",color = Purple40)

            Text(
                text = "Tela Inicial",color = Purple40)


            Row(

            ) {

                var text by rememberSaveable { mutableStateOf("") }

                TextField(
                    modifier = Modifier.weight(1f),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Faça uma pergunta") },
                    //   leadingIcon = { Icon(Icons.Filled.Favorite, contentDescription = "Localized description") },
                    //   trailingIcon = { Icon(Icons.Filled.Send, contentDescription = "Localized description") }
                )
                FloatingActionButton(
                    onClick = { /* do something */ },
                ) {
                    Icon(Icons.Filled.Send, "Localized description")
                }


            }


        }



    }
}




@Composable
@Preview
fun InicialScreenPreview() {

    HomeScreen().invoke()
}