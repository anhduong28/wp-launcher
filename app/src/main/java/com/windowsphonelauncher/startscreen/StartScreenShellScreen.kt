package com.windowsphonelauncher.startscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.windowsphonelauncher.tilecore.PositionedStartTile
import com.windowsphonelauncher.tilecore.StartTileRecord
import com.windowsphonelauncher.tilecore.occupiedRowCount
import com.windowsphonelauncher.tilecore.positionStartTiles

private const val StartScreenGridColumns = 4
private val TileGap = 4.dp
private val ScreenEdgePadding = 8.dp
private val MinimumTileCellSize = 48.dp
private val MinimumGridWidth = (MinimumTileCellSize * StartScreenGridColumns) +
    (TileGap * (StartScreenGridColumns - 1))

object StartScreenShellTestTags {
    const val Root = "start-screen-shell-root"
    const val Grid = "start-screen-tile-grid"

    fun tile(id: String): String = "start-screen-tile-$id"
}

@Composable
fun StartScreenShellScreen(
    modifier: Modifier = Modifier,
    uiState: StartScreenUiState = defaultStartScreenUiState(),
) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag(StartScreenShellTestTags.Root),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeContent)
                .padding(ScreenEdgePadding)
                .horizontalScroll(horizontalScrollState),
        ) {
            val gridWidth = maxOf(maxWidth, MinimumGridWidth)
            StartTileGrid(
                positionedTiles = positionStartTiles(uiState.tiles),
                modifier = Modifier
                    .width(gridWidth)
                    .verticalScroll(verticalScrollState),
            )
        }
    }
}

@Composable
private fun StartTileGrid(
    positionedTiles: List<PositionedStartTile>,
    modifier: Modifier = Modifier,
) {
    val gap = TileGap
    Layout(
        modifier = modifier.testTag(StartScreenShellTestTags.Grid),
        content = {
            positionedTiles.forEach { positionedTile ->
                StartTile(
                    tile = positionedTile.record,
                    modifier = Modifier.testTag(StartScreenShellTestTags.tile(positionedTile.record.id)),
                )
            }
        },
    ) { measurables, constraints ->
        val gapPx = gap.roundToPx()
        val availableWidth = constraints.maxWidth
        val cellSize = ((availableWidth - (gapPx * (StartScreenGridColumns - 1))) / StartScreenGridColumns)
            .coerceAtLeast(0)
        val gridHeight = (cellSize * positionedTiles.occupiedRowCount()) +
            (gapPx * (positionedTiles.occupiedRowCount() - 1).coerceAtLeast(0))
        val constrainedHeight = gridHeight.coerceIn(constraints.minHeight, constraints.maxHeight)

        val placeables = measurables.mapIndexed { index, measurable ->
            val tile = positionedTiles[index].record
            val tileWidth = (cellSize * tile.size.columnSpan) + (gapPx * (tile.size.columnSpan - 1))
            val tileHeight = (cellSize * tile.size.rowSpan) + (gapPx * (tile.size.rowSpan - 1))
            measurable.measure(
                Constraints.fixed(
                    width = tileWidth,
                    height = tileHeight,
                ),
            )
        }

        layout(width = availableWidth, height = constrainedHeight) {
            placeables.forEachIndexed { index, placeable ->
                val positionedTile = positionedTiles[index]
                val x = positionedTile.position.column * (cellSize + gapPx)
                val y = positionedTile.position.row * (cellSize + gapPx)
                placeable.placeRelative(x = x, y = y)
            }
        }
    }
}

@Composable
private fun StartTile(
    tile: StartTileRecord,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(Color(tile.colorArgb))
            .padding(8.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        Text(
            text = tile.label,
            color = tile.labelColor(),
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun StartTileRecord.labelColor(): Color =
    when (colorArgb) {
        WindowsPhoneTileColors.Cyan,
        WindowsPhoneTileColors.Green,
        WindowsPhoneTileColors.Orange -> Color.Black
        else -> Color.White
    }

@Preview(showBackground = true)
@Composable
private fun StartScreenShellScreenPreview() {
    MaterialTheme {
        StartScreenShellScreen()
    }
}
