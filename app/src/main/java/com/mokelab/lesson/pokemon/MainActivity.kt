package com.mokelab.lesson.pokemon

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mokelab.lesson.pokemon.ui.theme.PokemonTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch(Dispatchers.Main) {
            // 結果をUIスレッドで更新
            try {
                val result = getDataFromURL("https://moke-battle-log.web.app/poke-ja.json")
                // 結果をUIスレッドで更新
                println("APIから取得した結果: $result")
            } catch (e: Exception) {
                // エラーハンドリング
                println("エラー: ${e.message}")
            }
        }
        setContent {
            PokemonTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }

    // API取得処理
    private suspend fun getDataFromURL(urlString: String){
        return withContext(Dispatchers.IO) {
            // ネットワークリクエストをバックグラウンドスレッドで実行
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val buffer = StringBuffer()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
                line?.let { Log.d("MainActivity", it) }
            }
            buffer.toString()  // 取得したJSONデータを返す
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PokemonTheme {
        Greeting("Android")
    }
}