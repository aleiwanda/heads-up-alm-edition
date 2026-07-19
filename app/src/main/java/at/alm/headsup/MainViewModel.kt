package at.alm.headsup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.alm.headsup.repository.MainRepository
import at.alm.headsup.response.TokenResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
}
