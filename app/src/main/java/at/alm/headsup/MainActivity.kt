package at.alm.headsup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.alm.headsup.response.DeepPlaylistResponse
import at.alm.headsup.response.PlaylistTrackResponse
import at.alm.headsup.response.ShallowPlaylistResponse

class MainActivity : ComponentActivity() {
    val TAG = this.javaClass.simpleName

    val spotifyConnector: SpotifyConnector = SpotifyConnector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                App()
            }
        }
    }

    @Deprecated("Deprecated but used because of Spotify API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == spotifyConnector.requestCode) {
            spotifyConnector.continueAuthorization(resultCode, data)
        } else {
            Log.e(TAG, "Got activity result for unknown activity, $requestCode, $resultCode, $data")
        }
    }

    @Composable
    fun App(modifier: Modifier = Modifier, viewModel: MainViewModel = viewModel()) {
        val currentPlaylist = viewModel.uiState.currentPlaylist
//        var color by remember { mutableStateOf(Green) }
        if (currentPlaylist == null) {
            PlaylistOverviewScreen(modifier, viewModel)
        } else {
//            Box(
//                modifier = Modifier
//                    .focusable()
//                    .fillMaxHeight()
//                    .background(color)
//                    .onFocusChanged { color = if (it.isFocused) Blue else Green }
//                    .onKeyEvent({ event ->
//                        if (event.type == KeyEventType.KeyUp) {
//                            if (event.key == Key.VolumeUp || event.key == Key.VolumeDown || event.key == Key.Back) {
//                                viewModel.setCurrentPlaylist("a", null)
//                            }
//                        }
//                        true
//                    })
//            ) {
            SinglePlaylistScreen(
                currentPlaylist,
                { viewModel.setCurrentPlaylist(null, null) })
//            }
        }
    }

    @Composable
    fun SinglePlaylistScreen(
        currentPlaylist: DeepPlaylistResponse,
        goBack: () -> Unit
    ) {
        val items: List<PlaylistTrackResponse> = currentPlaylist.items.items
        Column(modifier = Modifier.padding(30.dp)) {
            Button(onClick = { goBack() }) {
                Text(text = "Back")
            }
            LazyColumn(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {

                items(items = items) {
                    Text(text = it.item.name)
                }
            }
        }
    }

    @Composable
    fun PlaylistOverviewScreen(modifier: Modifier = Modifier, viewModel: MainViewModel) {
        val activity = this
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    onClick = {
                        viewModel.requestLogin()
                    }
                ) {
                    when (viewModel.uiState.loginState) {
                        LoginProcess.LOGGED_OUT -> Text("Login")
                        LoginProcess.REQUESTED -> {
                            Text("Login")
                            spotifyConnector.openLoginWindow(activity)
                            viewModel.markLoginAsInitiated()
                        }

                        LoginProcess.INITIATED -> CircularProgressIndicator(color = Color.Black)
                        LoginProcess.LOGGED_IN -> Text("Logged in")
                    }
                }
                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    onClick = {
                        val tokenResponse = spotifyConnector.tokenResponseContent.getOrNull()
                        if (tokenResponse != null) {
                            viewModel.reloadPlaylists(tokenResponse)
                        }
                    }
                ) {
                    Text("Load Playlists")
                }
            }
            if (viewModel.uiState.playlistsReloading) {
                LinearProgressIndicator()
            }
            PlayListList(viewModel.uiState.playlists) { id ->
                val tokenResponse = spotifyConnector.tokenResponseContent.getOrNull()
                if (tokenResponse != null) {
                    viewModel.setCurrentPlaylist(id, tokenResponse)
                }
            }
        }
    }

    /**
     * Extra composable so the state gets updated if the list updates
     */
    @Composable
    private fun PlayListList(playlists: List<ShallowPlaylistResponse>, onClick: (String) -> Unit) {
        LazyColumn(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            items(items = playlists) {
                TextButton(onClick = { onClick(it.id) }) {
                    Text(text = it.name)
                }
            }
        }
    }
}
