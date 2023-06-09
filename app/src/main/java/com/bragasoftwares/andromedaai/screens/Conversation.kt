package com.bragasoftwares.andromedaai.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bragasoftwares.andromedaai.R
import com.bragasoftwares.andromedaai.data.exampleUiState
import com.bragasoftwares.andromedaai.ui.theme.AndromedaAITheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Shape
import com.example.myapplicationai5.OpenAI
import org.json.JSONException
import org.json.JSONObject


const val ConversationTestTag = "ConversationTestTag"

@Composable
fun Messages(
    messages: List<Message>,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {

        val authorMe = stringResource(id = R.string.author_me)
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize()
        ) {
            for (index in messages.indices) {
                val prevAuthor = messages.getOrNull(index - 1)?.author
                val nextAuthor = messages.getOrNull(index + 1)?.author
                val content = messages[index]
                val isFirstMessageByAuthor = prevAuthor != content.author
                val isLastMessageByAuthor = nextAuthor != content.author

                // Hardcode day dividers for simplicity
                if (index == messages.size - 1) {
                    item {
                        DayHeader("20 Aug")
                    }
                } else if (index == 2) {
                    item {
                        DayHeader("Today")
                    }
                }

                item {
                    Message(
                        onAuthorClick = { name -> navigateToProfile(name) },
                        msg = content,
                        isUserMe = content.author == authorMe,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor
                    )
                }
            }
        }
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun Message(
    onAuthorClick: (String) -> Unit,
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    Row(modifier = spaceBetweenAuthors) {
        if (isLastMessageByAuthor) {
            // Avatar
            Image(
                modifier = Modifier
                    .clickable(onClick = { onAuthorClick(msg.author) })
                    .padding(horizontal = 16.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                painter = painterResource(id = msg.authorImage),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            // Space under avatar
            Spacer(modifier = Modifier.width(74.dp))
        }
        AuthorAndTextMessage(
            msg = msg,
            isUserMe = isUserMe,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f)
        )
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (isLastMessageByAuthor) {
            AuthorNameTimestamp(msg)
        }
        ChatItemBubble(msg, isUserMe, authorClicked = authorClicked)
        if (isFirstMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun AuthorNameTimestamp(msg: Message) {
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = msg.author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = msg.timestamp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    Divider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
fun ChatItemBubble(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {

    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Column {
        Surface(
            color = backgroundBubbleColor,
            shape = ChatBubbleShape
        ) {
            ClickableMessage(
                message = message,
                isUserMe = isUserMe,
                authorClicked = authorClicked
            )
        }

        message.image?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = backgroundBubbleColor,
                shape = ChatBubbleShape
            ) {
                Image(
                    painter = painterResource(it),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(160.dp),
                    contentDescription = stringResource(id = R.string.attached_image)
                )
            }
        }
    }
}

@Composable
fun ClickableMessage(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    val styledMessage = messageFormatter(
        text = message.content,
        primary = isUserMe
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier.padding(16.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                        else -> Unit
                    }
                }
        }
    )
}









@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Conversa(
    uiState: ConversationUiState,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { }
): @Composable () -> Unit {
    return {
        val authorMe = stringResource(R.string.author_me)
        val timeNow = stringResource(id = R.string.now)

        val scrollState = rememberLazyListState()
        val topBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
        val scope = rememberCoroutineScope()

        var resultAI by remember { mutableStateOf("") }



        Column(
            Modifier.fillMaxSize()
            // .padding(paddingValues)
        ) {
            Messages(
                messages = uiState.messages,
                navigateToProfile = navigateToProfile,
                modifier = Modifier.weight(1f),
                scrollState = scrollState
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {

                val textFieldValue = remember { mutableStateOf("") }
                val isEnabled = remember { mutableStateOf(false) }

                var resultAI by remember { mutableStateOf("") }

                val openAI = OpenAI("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",textFieldValue.value)

                SideEffect {
                    openAI.callAPI { apiResult ->
                        try {
                            val jsonObject = JSONObject(apiResult)

                            val jsonArray = jsonObject.getJSONArray("choices")

                            val result = jsonArray.getJSONObject(0).getString("text")

                            //   val result = jsonObject.getJSONObject("choices")
                            //       val text = result.getString("texto")
                            resultAI= result.trim { it <= ' ' };
                            // processar a resposta aqui
                        } catch (e: JSONException) {
                            e.printStackTrace();
                            // lidar com o erro de análise JSON
                        }


                    }
                }


                TextField(
                    modifier = Modifier.weight(1f),
                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                    enabled = true,
                    value = textFieldValue.value,
                    shape= MaterialTheme.shapes.large,
                    onValueChange = {
                        textFieldValue.value = it
                        isEnabled.value = textFieldValue.value.isNotBlank()
                    },

                    label = { Text("Faça uma pergunta.") }
                )
                Button(
                    modifier = Modifier.padding(start = 16.dp),
                    enabled = isEnabled.value,
                    onClick = {
                        uiState.addMessage(Message(authorMe, textFieldValue.value, timeNow))
                        textFieldValue.value = ""
                        isEnabled.value = false


                       uiState.addMessage(Message("Andromeda AI", resultAI, timeNow))

                    }
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}



@Composable
fun RespostaOpenAi(openAI: OpenAI, uiState: ConversationUiState)  {

    var resultAI by remember { mutableStateOf("") }
    val authorMe = stringResource(R.string.author_me)
    val timeNow = stringResource(id = R.string.now)


    SideEffect {
        openAI.callAPI { apiResult ->
            try {
                val jsonObject = JSONObject(apiResult)

                val jsonArray = jsonObject.getJSONArray("choices")

                val result = jsonArray.getJSONObject(0).getString("text")

                //   val result = jsonObject.getJSONObject("choices")
                //       val text = result.getString("texto")
                resultAI= result.trim { it <= ' ' };
                // processar a resposta aqui
            } catch (e: JSONException) {
                e.printStackTrace();
                // lidar com o erro de análise JSON
            }


        }
    }
    uiState.addMessage(Message(authorMe, resultAI, timeNow))
   // Text(text = resultAI)
}



@Preview
@Composable
fun ConversaPreview() {
    val openAI = OpenAI("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","Qual a versao do chate gpt usa")

    AndromedaAITheme {
        Conversa(
            uiState = exampleUiState,
            navigateToProfile = { },

        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AlignedSurface() {
    val cardElevation: Dp = 8.dp
    Surface( modifier = Modifier.fillMaxSize(),
    ){
        Column() {
            Text(text = "Mensagem 1")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 3")
            Text(text = "Mensagem 4")
            Text(text = "Mensagem 5")
            Text(text = "Mensagem 6")
            Text(text = "Mensagem 7")
            Text(text = "Mensagem 8")
            Text(text = "Mensagem 9")
            Text(text = "Mensagem 10")
            Text(text = "Mensagem 11")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 1")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 1")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 3")
            Text(text = "Mensagem 4")
            Text(text = "Mensagem 5")
            Text(text = "Mensagem 6")
            Text(text = "Mensagem 7")
            Text(text = "Mensagem 8")
            Text(text = "Mensagem 9")
            Text(text = "Mensagem 10")
            Text(text = "Mensagem 11")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 1")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 1")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 3")
            Text(text = "Mensagem 4")
            Text(text = "Mensagem 5")
            Text(text = "Mensagem 6")
            Text(text = "Mensagem 7")
            Text(text = "Mensagem 8")
            Text(text = "Mensagem 9")
            Text(text = "Mensagem 10")
            Text(text = "Mensagem 11")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 1")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 1")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 3")
            Text(text = "Mensagem 4")
            Text(text = "Mensagem 5")
            Text(text = "Mensagem 6")
            Text(text = "Mensagem 7")
            Text(text = "Mensagem 8")
            Text(text = "Mensagem 9")
            Text(text = "Mensagem 10")
            Text(text = "Mensagem 11")
            Text(text = "Mensagem 2")
            Text(text = "Mensagem 1")
            Text(text = "Mensagem 2")


        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),

        //   color = MaterialTheme.colors.primary,

    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {

            val textFieldValue = remember { mutableStateOf("") }

            TextField(
                modifier = Modifier.weight(1f),
                textStyle= LocalTextStyle.current.copy(color = LocalContentColor.current),
                enabled = true,
                value = textFieldValue.value,
                shape= MaterialTheme.shapes.extraLarge,
                onValueChange = { textFieldValue.value = it },
                label = { Text("Faça uma perguntannn") }
            )
            Button(
                modifier = Modifier.padding(start = 16.dp),
                onClick = { /* Lógica do botão */ }
            ) {
                Text("Enviar")
            }
        }
    }
}

private val JumpToBottomThreshold = 56.dp