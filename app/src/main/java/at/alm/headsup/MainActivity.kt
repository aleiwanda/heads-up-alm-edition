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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.alm.headsup.response.DeepPlaylistResponse
import at.alm.headsup.response.PlaylistTrackResponse
import at.alm.headsup.response.ShallowPlaylistResponse
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
        val currentPlaylist = viewModel.uiState.selectedPlaylist
//        var color by remember { mutableStateOf(Green) }
        if (currentPlaylist == null || viewModel.uiState.timeLeft <= 0) {
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
                viewModel.uiState.timeLeft
            ) { viewModel.stop() }
//            }
        }
    }

    @Composable
    fun SinglePlaylistScreen(
        currentPlaylist: DeepPlaylistResponse,
        timeLeft: Int,
        goBack: () -> Unit
    ) {
        val items: List<PlaylistTrackResponse> = currentPlaylist.items.items
        Column(modifier = Modifier.padding(30.dp)) {
            Button(onClick = { goBack() }) {
                Text(text = "Back")
            }
            Text(text = "%d:%02d".format(timeLeft / 60, timeLeft % 60))
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
                Button(onClick = { viewModel.start() }) {
                    Text("Start")
                }
            }
            if (viewModel.uiState.playlistsReloading) {
                LinearProgressIndicator()
            }
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val duration = viewModel.uiState.duration.inWholeSeconds
                Text(text = "%d:%02d".format(duration / 60, duration % 60))
                Slider(
                    value = duration / 60f,
                    onValueChange = {
                        viewModel.updateDuration(
                            (it * 60).toInt().toDuration(DurationUnit.SECONDS)
                        )
                    },
                    valueRange = (0.5f..5f),
                    steps = 8
                )
            }
            PlayListList(viewModel.uiState.playlists, viewModel)
        }
    }

    /**
     * Extra composable so the state gets updated if the list updates
     */
    @Composable
    private fun PlayListList(playlists: List<ShallowPlaylistResponse>, viewModel: MainViewModel) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalAlignment = Alignment.Start
        ) {
            items(items = playlists) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (it.id == viewModel.uiState.selectedPlaylist?.id),
                            onClick = {
                                val tokenResponse =
                                    spotifyConnector.tokenResponseContent.getOrNull()
                                if (tokenResponse != null) {
                                    viewModel.setSelectedPlaylist(it.id, tokenResponse)
                                }
                            },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (it.id == viewModel.uiState.selectedPlaylist?.id),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(text = it.name)
                }
            }
        }
    }
}
