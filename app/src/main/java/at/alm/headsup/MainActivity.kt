package at.alm.headsup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.alm.headsup.response.DeepPlaylistResponse
import at.alm.headsup.response.PlaylistTrackResponse
import at.alm.headsup.response.ShallowPlaylistResponse
import java.util.stream.Collectors
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MainActivity : ComponentActivity() {
    val TAG = this.javaClass.simpleName

    var spotifyLoginCallback: (resultCode: Int, data: Intent?) -> Unit = { a, b ->
        val c = Log.e(TAG, "Spotify login callback not initialized")
    }

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
        if (requestCode == SpotifyConnector.requestCode) {
            spotifyLoginCallback(resultCode, data)
        } else {
            Log.e(TAG, "Got activity result for unknown activity, $requestCode, $resultCode, $data")
        }
    }

    @Composable
    fun App(viewModel: MainViewModel = viewModel()) {
        viewModel.setActivity(this)
        val currentPlaylist = viewModel.uiState.selectedPlaylist
        if (currentPlaylist == null || viewModel.uiState.timeLeft <= 0) {
            PlaylistOverviewScreen(viewModel)
        } else {
            SinglePlaylistScreen(
                currentPlaylist, viewModel.uiState.timeLeft
            ) { viewModel.stop() }
        }
    }
}

@Composable
fun SinglePlaylistScreen(
    currentPlaylist: DeepPlaylistResponse, timeLeft: Int, goBack: () -> Unit
) {
    val items: List<PlaylistTrackResponse> by rememberSaveable {
        mutableStateOf(
            currentPlaylist.items?.items?.shuffled() ?: emptyList()
        )
    }
    var itemIndex: Int by rememberSaveable { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = { itemIndex += 1 }),
//                .focusable()
//                .onKeyEvent { event ->
//                    if (event.type == KeyEventType.KeyUp) {
//                        if (event.key == Key.VolumeUp || event.key == Key.VolumeDown || event.key == Key.Back) {
//                            itemIndex += 1
//                            return@onKeyEvent true
//                        }
//                    }
//                    false
//                },
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { goBack() }) {
            Text(text = "Back")
        }
        if (itemIndex < items.size) {
            Text(
                fontSize = 30.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                text = "%d:%02d".format(timeLeft / 60, timeLeft % 60)
            )
            Text(text = "$itemIndex / ${items.size}")
            Text(fontSize = 40.sp, lineHeight = 50.sp, text = items[itemIndex].item.name)
            Text(
                fontStyle = FontStyle.Italic,
                text = items[itemIndex].item.artists.stream().map { a -> a.name }
                    .collect(Collectors.joining(",")) + " - " + items[itemIndex].item.album.name + " - " + items[itemIndex].item.album.releaseDate)
        } else {
            Text(fontSize = 40.sp, lineHeight = 50.sp, text = "Finished")
        }
    }
}

@Composable
fun PlaylistOverviewScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                enabled = viewModel.spotifyConnector.getLoginState() != LoginState.LOGGING_IN,
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = {
                    when (viewModel.spotifyConnector.getLoginState()) {
                        LoginState.LOGGED_OUT -> viewModel.spotifyConnector.openLoginWindow()
                        LoginState.LOGGING_IN -> {}
                        LoginState.LOGGED_IN -> viewModel.spotifyConnector.logout()
                    }
                }) {
                when (viewModel.spotifyConnector.getLoginState()) {
                    LoginState.LOGGED_OUT -> Text("Logged out - Login")
                    LoginState.LOGGING_IN -> CircularProgressIndicator(color = Color.Black)
                    LoginState.LOGGED_IN -> Text("Logged in - Log out")
                }
            }
            Button(
                enabled = viewModel.spotifyConnector.getLoginState() == LoginState.LOGGED_IN,
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = {
                    viewModel.reloadPlaylists()
                }) {
                Text("Load Playlists")
            }
            Button(
                enabled = viewModel.uiState.selectedPlaylist != null,
                onClick = { viewModel.start() }) {
                Text("Start")
            }
        }
        if (viewModel.uiState.playlistsReloading) {
            LinearProgressIndicator()
        }
        Row(
            modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            val duration = viewModel.uiState.duration.inWholeSeconds
            Text(text = "%d:%02d".format(duration / 60, duration % 60))
            Slider(
                value = duration / 60f, onValueChange = {
                    viewModel.updateDuration(
                        (it * 60).toInt().toDuration(DurationUnit.SECONDS)
                    )
                }, valueRange = (0.5f..5f), steps = 8
            )
        }
        PlayListList(viewModel)
    }
}

/**
 * Extra composable so the state gets updated if the list updates
 */
@Composable
private fun PlayListList(
    viewModel: MainViewModel,
) {
    val playlists =
        viewModel.uiState.localPlaylists.fold(HashMap<String, Pair<ShallowPlaylistResponse?, ShallowPlaylistResponse?>>()) { map, e ->
            map[e.id] = Pair(e, null); map
        }
    for (rp in viewModel.uiState.remotePlaylists) {
        val lp = playlists[rp.id]?.first
        playlists[rp.id] = Pair(lp, rp)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(), horizontalAlignment = Alignment.Start
    ) {
        items(items = playlists.values.sortedBy { e -> e.first?.name?:e.second?.name }) {
            val first = it.first
            if (first == null) {
                val second = it.second
                if (second == null) {
                    TODO()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { viewModel.downloadPlaylist(second.id) })
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Download, contentDescription = "Download"
                    )
                    Text(text = "${second.name} - ${second.items?.total ?: 0}")
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (first.id == viewModel.uiState.selectedPlaylist?.id), onClick = {
                                viewModel.setSelectedPlaylist(first.id)
                            }, role = Role.RadioButton
                        )
                        .padding(10.dp), verticalAlignment = Alignment.CenterVertically

                ) {
                    RadioButton(
                        selected = (first.id == viewModel.uiState.selectedPlaylist?.id),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(text = "${first.name} - ${first.items?.total ?: 0}")
                }
            }
        }
    }
}
