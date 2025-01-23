package com.mokelab.lesson.pokemon

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.mokelab.lesson.pokemon.network.Pokemon
import com.mokelab.lesson.pokemon.network.PokemonData
import com.mokelab.lesson.pokemon.ui.theme.PokemonTheme
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PokemonTheme {
                // ポケモンデータ
                var pokemons by remember { mutableStateOf<List<Pokemon>>(emptyList()) }
                // 読み込みステータス
                var isLoading by remember { mutableStateOf(true) }
                var isError by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    val result = fetchPokemonData("https://moke-battle-log.web.app/poke-ja.json")

                    if (result != null) {
                        pokemons = result.pokemons
                    }else{
                        isError = true
                    }
                    // ローディング終了
                    isLoading = false;
                }

                // isLoadingの状態による表示の切り替え
                if(isLoading){
                    LoadingIndicator()
                }else if(isError){
                    ErrorLoading()
                }else{
                    PokemonList(pokemons)
                }
            }
        }
    }

    // JSON取得処理
    private suspend fun fetchPokemonData(url: String): PokemonData? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null

        try {
            // HTTP 接続を準備
            val apiUrl = URL(url)
            connection = apiUrl.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            // レスポンスを取得
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { reader ->
                    val json = reader.readText()
                    // JSONをデシリアライズ
                    val gson = Gson()
                    return@withContext gson.fromJson(json, PokemonData::class.java)
                }
            } else {
                println("HTTP error: $responseCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
        return@withContext null
    }
}

// リスト全体
@Composable
fun PokemonList(pokemons: List<Pokemon>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // 縦幅を画面いっぱいに
    ) {
        items(pokemons){
                pokemon -> PokemonItem(pokemon=pokemon)
        }
    }
}

// リストの項目
@SuppressLint("DefaultLocale")
@Composable
fun PokemonItem(pokemon: Pokemon){
    Row(
        modifier = Modifier
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#${String.format("%04d", pokemon.id)}", // 0埋め4桁表示
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = pokemon.name,
            textAlign = TextAlign.Center
        )
    }
    Divider() // 線は横並びにしないので外
}

// 読込中表示
@Composable
fun LoadingIndicator(){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ){
        CircularProgressIndicator()
    }
}

// エラー時の表示
@Composable
fun ErrorLoading(){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ){
       Text("読み込みに失敗しました")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PokemonTheme {
        PokemonTheme {
            val samplePokemons = listOf(
                Pokemon(1, "フシギダネ"),
                Pokemon(2, "フシギソウ"),
                Pokemon(3, "フシギバナ")
            )
            PokemonList(samplePokemons)
        }
    }
}