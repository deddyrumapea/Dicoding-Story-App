package com.romnan.dicodingstory.core.layers.presentation.util

import com.romnan.dicodingstory.core.util.UIText

sealed class UIEvent {
    data class ShowSnackbar(val uiText: UIText) : UIEvent()
}