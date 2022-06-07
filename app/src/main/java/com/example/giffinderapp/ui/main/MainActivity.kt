package com.example.giffinderapp.ui.main

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.giffinderapp.R
import com.example.giffinderapp.ui.theme.GIFFinderAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    lateinit var stateVisibilityLayout: MutableState<Boolean>
    lateinit var gifUrl: MutableState<String>
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GIFFinderAppTheme {
                stateVisibilityLayout = remember { mutableStateOf(true) }
                gifUrl = remember { mutableStateOf("") }

                viewModel.randGifUrl.observe(this) {
                    gifUrl.value = it.split("?cid").first()
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = Color(0xFF0D1017)
                ) {

                    Column(modifier = Modifier.padding(15.dp)) {
                        if (stateVisibilityLayout.value)
                            MainLayout()
                        else
                            SearchLayout()
                    }
                }
            }
        }
    }

    @Composable
    fun SearchLayout() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackImage()
            SearchView()
        }
        ShowMessageText("Result")
        GifList(gifList = listOf("", "", "", "", "", ""))
    }

    @Composable
    fun MainLayout() {
        SearchBar()
        ShowMessageText("Random gif")
        CardGif()
        reloadGifDelay()
    }

    private fun runnable(): Runnable {
        return Runnable {
            //new request after 10s
            viewModel.getRandomGif()
            reloadGifDelay()
        }
    }

    private fun reloadGifDelay() = Handler(Looper.getMainLooper()).postDelayed(runnable(), 10000)

    @Composable
    private fun GifList(gifList: List<String>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(10.dp),
        ) {
            items(gifList.size) { index ->
                GifItem()
            }
        }
    }

    @Composable
    fun BackImage() {
        Image(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 5.dp)
                .clickable {
                    stateVisibilityLayout.value = true
                }
        )
    }

    @Composable
    fun ShowMessageText(text: String) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(top = 18.dp, bottom = 18.dp)
        )
    }

    @Composable
    fun SearchBar() {
        val source = remember { MutableInteractionSource() }

        if (source.collectIsPressedAsState().value) {
            stateVisibilityLayout.value = false
        }

        TextField(
            value = "Search",
            onValueChange = {
            },
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(15.dp)
                        .size(24.dp)
                )
            },
            singleLine = true,
            readOnly = true,
            interactionSource = source,
            shape = RoundedCornerShape(15.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                cursorColor = Color.White,
                leadingIconColor = Color.White,
                trailingIconColor = Color.White,
                backgroundColor = colorResource(id = R.color.lightBlack),
                placeholderColor = Color.White,
                focusedIndicatorColor = Color.Transparent
            )
        )
    }

    @Composable
    fun SearchView() {
        val textState = remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            value = textState.value,
            onValueChange = { value ->
                textState.value = value
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text("Search") },
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(15.dp)
                        .size(24.dp)
                )
            },
            trailingIcon = {
                if (textState.value != TextFieldValue("")) {
                    IconButton(
                        onClick = {
                            textState.value =
                                TextFieldValue("") // Remove text from TextField
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(15.dp)
                                .size(24.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(15.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                cursorColor = Color.White,
                leadingIconColor = Color.White,
                trailingIconColor = Color.White,
                backgroundColor = colorResource(id = R.color.lightBlack),
                placeholderColor = Color.White,
                focusedIndicatorColor = Color.Transparent
            )
        )
    }

    @Composable
    fun CardGif() {
        Card(
            modifier = Modifier,
            shape = RoundedCornerShape(15.dp)
        )
        {
            val imageLoader = ImageLoader.Builder(applicationContext)
                .components {
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()

            Image(
                painter = rememberImagePainter(
                    imageLoader = imageLoader,
                    data = gifUrl.value,
                    builder = {
                        error(R.drawable.ic_gif)
                    }
                ),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(Color(0xFF161B21))
            )
        }
    }

    @Composable
    fun GifItem() {
        Card(
            modifier = Modifier.padding(end = 5.dp, start = 5.dp, bottom = 8.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = 2.dp
        )
        {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp)
                    .background(Color(0xFF161B21))
                    .clickable {
                        Toast
                            .makeText(this, "item Click", Toast.LENGTH_SHORT)
                            .show()
                    }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        GIFFinderAppTheme {
            MainLayout()
        }
    }
}