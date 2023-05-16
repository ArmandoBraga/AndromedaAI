package com.bragasoftwares.andromedaai.sampledata

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.Info
import com.bragasoftwares.andromedaai.menus.BottomAppBarItem



val bottomAppBarItems = listOf(
    BottomAppBarItem(
        label = "Home",
        icon = Icons.Filled.Home
    ),
    BottomAppBarItem(
        label = "Mensagens",
        icon = Icons.Filled.MailOutline
    ),
    BottomAppBarItem(
        label = "Notícias",
        icon = Icons.Outlined.Info
    ),
)