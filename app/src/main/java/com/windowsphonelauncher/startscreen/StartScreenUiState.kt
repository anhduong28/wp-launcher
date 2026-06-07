package com.windowsphonelauncher.startscreen

import com.windowsphonelauncher.tilecore.StartTileRecord
import com.windowsphonelauncher.tilecore.TileSize

data class StartScreenUiState(
    val tiles: List<StartTileRecord>,
)

fun defaultStartScreenUiState(): StartScreenUiState =
    StartScreenUiState(
        tiles = defaultStartTileRecords,
    )

internal object WindowsPhoneTileColors {
    const val Cyan = 0xFF00BCF2
    const val Blue = 0xFF0078D7
    const val Green = 0xFF00A300
    const val Red = 0xFFE51400
    const val Purple = 0xFF800080
    const val Orange = 0xFFF7630C
}

private val defaultStartTileRecords = listOf(
    StartTileRecord(
        id = "weather",
        label = "Weather",
        size = TileSize.FourByTwo,
        colorArgb = WindowsPhoneTileColors.Blue,
        order = 0,
    ),
    StartTileRecord(
        id = "phone",
        label = "Phone",
        size = TileSize.OneByOne,
        colorArgb = WindowsPhoneTileColors.Green,
        order = 1,
    ),
    StartTileRecord(
        id = "messages",
        label = "Messages",
        size = TileSize.TwoByTwo,
        colorArgb = WindowsPhoneTileColors.Red,
        order = 2,
    ),
    StartTileRecord(
        id = "calendar",
        label = "Calendar",
        size = TileSize.OneByOne,
        colorArgb = WindowsPhoneTileColors.Purple,
        order = 3,
    ),
    StartTileRecord(
        id = "clock",
        label = "Clock",
        size = TileSize.TwoByTwo,
        colorArgb = WindowsPhoneTileColors.Cyan,
        order = 4,
    ),
    StartTileRecord(
        id = "settings",
        label = "Settings",
        size = TileSize.OneByOne,
        colorArgb = WindowsPhoneTileColors.Orange,
        order = 5,
    ),
)
