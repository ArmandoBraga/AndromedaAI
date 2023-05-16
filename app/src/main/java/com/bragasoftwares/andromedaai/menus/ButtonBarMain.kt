package com.bragasoftwares.andromedaai.menus

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.bragasoftwares.andromedaai.itemSelecionado
import com.bragasoftwares.andromedaai.sampledata.bottomAppBarItems
import com.bragasoftwares.andromedaai.ui.theme.AndromedaAITheme

class BottomAppBarItem(
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomAppBarMain(
    item: BottomAppBarItem,
    modifier: Modifier = Modifier,
    items: List<BottomAppBarItem> = emptyList(),
    onItemChange: (BottomAppBarItem) -> Unit = {}
) {
    NavigationBar(modifier) {
        items.forEach {
            val label = it.label
            val icon = it.icon
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = item.label == label,
                onClick = {
                    onItemChange(it)
                    itemSelecionado=label
                }
            )
        }
    }
}

@Preview
@Composable
fun BottomAppBarMainPreview() {
    AndromedaAITheme {
        BottomAppBarMain(
            item = bottomAppBarItems.first(),
            items = bottomAppBarItems
        )
    }
}