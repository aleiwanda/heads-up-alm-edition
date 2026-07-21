package at.alm.headsup

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
    val TAG = this.javaClass.simpleName
    val repository = MainRepository()

    private val _uiState = MutableMainUIState()
    val uiState: MainUIState = _uiState

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
            repository.reloadPlaylists(tokenResponse)
            _uiState.playlists = repository.getPlaylists()
            _uiState.playlistsReloading = false
        }
    }

    fun setSelectedPlaylist(id: String?, tokenResponse: TokenResponse?) {
        if (id == null) {
            _uiState.selectedPlaylist = null
            return
        }
        val playlist = repository.getPlaylist(id)
        if (playlist == null && tokenResponse != null) {
            repository.reloadPlaylist(id, tokenResponse)
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
