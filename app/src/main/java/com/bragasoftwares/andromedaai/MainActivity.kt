package com.bragasoftwares.andromedaai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bragasoftwares.andromedaai.sampledata.bottomAppBarItems
import com.bragasoftwares.andromedaai.ui.theme.AndromedaAITheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.bragasoftwares.andromedaai.menus.BottomAppBarItem
import com.bragasoftwares.andromedaai.menus.BottomAppBarMain
import com.bragasoftwares.andromedaai.menus.TopBarMain
import com.bragasoftwares.andromedaai.screens.HomeScreen
import com.bragasoftwares.andromedaai.screens.MensagensScreen
import com.bragasoftwares.andromedaai.screens.NoticiasScreen


var itemSelecionado : String = "Home"
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndromedaAITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    var selectedItem by remember {
                        val item = bottomAppBarItems.first()
                        mutableStateOf(item)
                    }




                    AndromedaApp(
                        bottomAppBarItemSelected = selectedItem,
                        onBottomAppBarItemSelectedChange = {
                            selectedItem = it
                            //   screens.add(it.label)
                        },
                        onFabClick = {
                            //floateactionbutton  screens.add("Pedido")
                        }) {


                    }


                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AndromedaApp(
        bottomAppBarItemSelected: BottomAppBarItem = bottomAppBarItems.first(),
        onBottomAppBarItemSelectedChange: (BottomAppBarItem) -> Unit = {},
        onFabClick: () -> Unit = {},
        content: @Composable () -> Unit

    ) {

        Scaffold(
            topBar = TopBarMain(),
            bottomBar = {
                BottomAppBarMain(
                    item = bottomAppBarItemSelected,
                    items = bottomAppBarItems,
                    onItemChange = onBottomAppBarItemSelectedChange,
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onFabClick
                ) {
                    Icon(
                        Icons.Filled.Call,
                        contentDescription = null
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier.padding(it)
            ) {
                //              content()
                //  InicialScreen().invoke()
                //       SegundaScreen().invoke()
                TelaAtual(itemSelecionado).invoke()
            }
        }
    }


    @Composable
    fun TelaAtual(tela: String): @Composable () -> Unit {
        return when (tela) {
            "Home" -> HomeScreen()
            "Mensagens" -> MensagensScreen()
            "Notícias" -> NoticiasScreen()
            else -> NoticiasScreen()
        }
    }


}

