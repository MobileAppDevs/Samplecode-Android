package com.ongraph.whatsappclone.ui.messageListScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ongraph.whatsappclone.R
import com.ongraph.whatsappclone.remote.model.Chat
import com.ongraph.whatsappclone.remote.model.ChatList
import com.ongraph.whatsappclone.ui.theme.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageListScreen(navController: NavController) {

    val isDeleteStatus = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(10.dp)
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    // Assuming you are using jetpack compose navigation
                    navController.popBackStack()
                }
                true
            }
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
                .verticalScroll(rememberScrollState())
        ) {

            val list = arrayListOf<Chat>()
            list.add(Chat("Hi", 1))
            list.add(Chat("How Are You?", 1))
            list.add(Chat("I am good, How are you.", 2))
            list.add(Chat("I am fine too.", 1))
            list.add(Chat("What are you doing?\nHave you left your job?", 2))
            list.add(Chat("yes, I have to. I mean there were no reason for me to stay...", 1))
            list.add(Chat("Is everything ok with you and ...", 2))
            list.add(
                Chat(
                    "Please don't.\n I'll tell you tomorrow at our favorite spot at same time.",
                    1
                )
            )
            list.add(Chat("Ok, fine. I'll be there on time.\nSee ya.", 2))
            list.add(Chat("bye :)", 1))

            isDeleteStatus.value = ChatList(
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 20.dp), ChatList(list), navController
            ).value

            Spacer(modifier = Modifier.height(10.dp))
        }

        BottomChatUi()
    }
    if(isDeleteStatus.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.4f)
                .background(Color.Black)
        ) {}
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            DeleteMessageScreen()
        }
    }
}

@Composable
fun ChatList(
    modifier: Modifier,
    chatList: ChatList? = null,
    navController: NavController
): MutableState<Boolean> {
    var itemSelectedCount by remember { mutableStateOf(0) }
    val isDeleteStatus = remember { mutableStateOf(false) }

    isDeleteStatus.value = TopActionBarUi(
        personOrGroupName = "Name",
        onBackClick = {
             if(itemSelectedCount>0){
                 itemSelectedCount=0
                 chatList?.list?.map {
                     it.isSelected=false
                 }
             }else{
                 navController.popBackStack()
             }
        }, itemSelectedCount, navController
    ).value

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BgGrey)
    )

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(modifier = modifier) {
        chatList?.list?.let {
            items(it) { item ->
                if (item.from == 1)
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(
                                if (true) RoundedCornerShape(10.dp)
                                else RoundedCornerShape(
                                    topEnd = 10.dp,
                                    bottomEnd = 10.dp,
                                    bottomStart = 10.dp
                                )
                            )
                            .background(BgGrey)
                            .clickable {
                                item.isSelected = !item.isSelected
                                if (item.isSelected) itemSelectedCount += 1
                                else itemSelectedCount -= 1
                                println("Selection Items : $itemSelectedCount")
                            }
                    ) {
                        item.message?.let { it1 ->
                            Text(
                                modifier = Modifier.padding(10.dp),
                                text = it1,
                                color = Color.Black,
                                fontFamily = FontFamily(Font(R.font.comfortaa_regular))
                            )
                        }
                    }
                else
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .clip(
                                    if (true) RoundedCornerShape(10.dp)
                                    else RoundedCornerShape(
                                        topEnd = 10.dp,
                                        bottomEnd = 10.dp,
                                        bottomStart = 10.dp
                                    )
                                )
                                .background(BgGreen)
                                .clickable {
                                    item.isSelected = !item.isSelected
                                    if (item.isSelected) itemSelectedCount += 1
                                    else itemSelectedCount -= 1
                                    println("Selection Items : $itemSelectedCount")
                                }
                        ) {
                            item.message?.let { it1 ->
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = it1,
                                    color = Color.Black,
                                    fontFamily = comfortaaRegular
                                )
                            }
                        }
                    }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    return isDeleteStatus
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BottomChatUi() {
    var chat by remember { mutableStateOf("") }
    var chatLabel by remember { mutableStateOf("Type message ...") }
    var sendIcon by remember { mutableStateOf(R.drawable.drawable_mic_ffffff) }
    var attachedStatus by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
    ) {
        if (attachedStatus)
            AttachedItemsUi()

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clickable {

                    }
                    .clip(RoundedCornerShape(20.dp))
                    .wrapContentHeight()
                    .border(BorderStroke(1.dp, BgGrey), RoundedCornerShape(20.dp))
                    .weight(1F),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Image(
                            painter = painterResource(
                                id = R.drawable.drawable_smile_707070
                            ),
                            contentDescription = null
                        )
                    }

                    val keyboardController = LocalSoftwareKeyboardController.current

                    /*sendIcon = if (true) R.drawable.drawable_send_ffffff
                    else R.drawable.drawable_mic_ffffff*/

                    TextField(
                        modifier = Modifier
                            .weight(1F)
                            .heightIn(40.dp, 200.dp),
                        value = chat,
                        onValueChange = {
                            chat = it
                            chatLabel = if (it.isNotBlank()) ""
                            else "Type message ..."
                        },
                        label = {
                            Text(
                                text = chatLabel,
                                fontSize = 14.sp,
                                color = textColor,
                                fontFamily = comfortaaRegular
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = comfortaaRegular
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                sendIcon = R.drawable.drawable_mic_ffffff
                                keyboardController?.hide()
                            }
                        )
                    )

                    IconButton(
                        onClick = {
                            attachedStatus = !attachedStatus
                        }) {
                        Image(
                            painter = painterResource(
                                id = R.drawable.drawable_attach_707070
                            ),
                            contentDescription = null
                        )
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Image(
                            painter = painterResource(
                                id = R.drawable.drawable_camera_707070
                            ),
                            contentDescription = null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .clickable {

                    }
                    .clip(RoundedCornerShape(10.dp))
                    .height(40.dp)
                    .width(40.dp)
                    .background(GreenTheme),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(20.dp, 30.dp),
                    painter = painterResource(
                        id = sendIcon
                    ),
                    contentDescription = null
                )
            }
        }
    }
}