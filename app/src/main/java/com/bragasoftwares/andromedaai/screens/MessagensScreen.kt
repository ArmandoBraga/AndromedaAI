package com.bragasoftwares.andromedaai.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bragasoftwares.andromedaai.ui.theme.Purple40

@Composable

fun MensagensScreen (): @Composable () -> Unit {
    return { Text( text = "Tela Mensagens",color = Purple40)
    }

}


@Composable
@Preview
fun SegundaScreenPreview() {

    MensagensScreen().invoke()
}