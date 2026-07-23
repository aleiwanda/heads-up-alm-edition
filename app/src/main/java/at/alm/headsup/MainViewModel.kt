package at.alm.headsup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.alm.headsup.repository.MainRepository
import at.alm.headsup.response.TokenResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class MainViewModel(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    private var context: Context? = null
    val repository = MainRepository { context }

    private val _uiState = MutableMainUIState()
    val uiState: MainUIState = _uiState

    fun setContext(context: Context) {
        val initiaized = this.context != null;
        this.context = context
        if (!initiaized) {
            _uiState.localPlaylists = repository.getLocalPlaylists()
        }
    }

    fun requestLogin() {
        _uiState.loginState = LoginProcess.REQUESTED
    }

    fun markLoginAsInitiated() {
        _uiState.loginState = LoginProcess.INITIATED
    }

    fun reloadPlaylists(tokenResponse: TokenResponse) {
        viewModelScope.launch(defaultDispatcher) {
            // Emit a new state indicating that login is in progress
            _uiState.playlistsReloading = true
            _uiState.remotePlaylists = repository.getRemotePlaylists(tokenResponse)
            _uiState.playlistsReloading = false
        }
    }

    fun downloadPlaylist(id: String, tokenResponse: TokenResponse) {
        viewModelScope.launch(defaultDispatcher) {
            repository.fetchAndStoreRemotePlaylist(id, tokenResponse)
            _uiState.localPlaylists = repository.getLocalPlaylists().toList()
        }
    }

    fun setSelectedPlaylist(id: String?, tokenResponse: TokenResponse?) {
        if (id == null) {
            _uiState.selectedPlaylist = null
            return
        }
        val playlist = repository.getPlaylist(id)
        if (playlist == null && tokenResponse != null) {
            repository.fetchAndStoreRemotePlaylist(id, tokenResponse)
        }
        _uiState.selectedPlaylist = repository.getPlaylist(id)
    }

    fun updateDuration(duration: Duration) {
        _uiState.duration = duration
    }

    fun start() {
        if (_uiState.selectedPlaylist == null) {
            return
        }
        _uiState.timeLeft = _uiState.duration.inWholeSeconds.toInt()
        viewModelScope.launch(defaultDispatcher) {
            while (_uiState.timeLeft > 0) {
                delay(1.seconds)
                _uiState.timeLeft--
            }
        }
    }

    fun stop() {
        _uiState.timeLeft = 0
    }
}
