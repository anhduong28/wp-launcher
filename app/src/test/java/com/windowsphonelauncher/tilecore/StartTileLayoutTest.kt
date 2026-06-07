package com.windowsphonelauncher.tilecore

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class StartTileLayoutTest {
    @Test
    fun tileSizesExposeMvpGridSpans() {
        assertEquals(1, TileSize.OneByOne.columnSpan)
        assertEquals(1, TileSize.OneByOne.rowSpan)
        assertEquals(2, TileSize.TwoByTwo.columnSpan)
        assertEquals(2, TileSize.TwoByTwo.rowSpan)
        assertEquals(4, TileSize.FourByTwo.columnSpan)
        assertEquals(2, TileSize.FourByTwo.rowSpan)
    }

    @Test
    fun layoutPlacesMixedTilesWithoutOverlap() {
        val positionedTiles = positionStartTiles(
            listOf(
                StartTileRecord(
                    id = "weather",
                    label = "Weather",
                    size = TileSize.FourByTwo,
                    colorArgb = 0xFF0078D7,
                    order = 0,
                ),
                StartTileRecord(
                    id = "phone",
                    label = "Phone",
                    size = TileSize.OneByOne,
                    colorArgb = 0xFF00A300,
                    order = 1,
                ),
                StartTileRecord(
                    id = "messages",
                    label = "Messages",
                    size = TileSize.TwoByTwo,
                    colorArgb = 0xFFE51400,
                    order = 2,
                ),
            ),
        )

        assertFalse(positionedTiles.hasOverlappingCells())
        assertEquals(GridPosition(row = 0, column = 0), positionedTiles[0].position)
        assertEquals(GridPosition(row = 2, column = 0), positionedTiles[1].position)
        assertEquals(GridPosition(row = 2, column = 1), positionedTiles[2].position)
    }

    @Test
    fun layoutWrapsTileWhenItDoesNotFitRemainingColumns() {
        val positionedTiles = positionStartTiles(
            listOf(
                StartTileRecord(
                    id = "one",
                    label = "One",
                    size = TileSize.OneByOne,
                    colorArgb = 0xFF00BCF2,
                    order = 0,
                ),
                StartTileRecord(
                    id = "two",
                    label = "Two",
                    size = TileSize.OneByOne,
                    colorArgb = 0xFF0078D7,
                    order = 1,
                ),
                StartTileRecord(
                    id = "three",
                    label = "Three",
                    size = TileSize.OneByOne,
                    colorArgb = 0xFF00A300,
                    order = 2,
                ),
                StartTileRecord(
                    id = "wide",
                    label = "Wide",
                    size = TileSize.TwoByTwo,
                    colorArgb = 0xFFF7630C,
                    order = 3,
                ),
            ),
        )

        assertTrue(positionedTiles.none { it.record.id == "wide" && it.position.column == 3 })
        assertEquals(GridPosition(row = 1, column = 0), positionedTiles.last().position)
        assertFalse(positionedTiles.hasOverlappingCells())
    }

    @Test
    fun layoutDoesNotBackfillLaterTilesBeforeEarlierOrderedTiles() {
        val positionedTiles = positionStartTiles(
            listOf(
                StartTileRecord(
                    id = "weather",
                    label = "Weather",
                    size = TileSize.FourByTwo,
                    colorArgb = 0xFF0078D7,
                    order = 0,
                ),
                StartTileRecord(
                    id = "phone",
                    label = "Phone",
                    size = TileSize.OneByOne,
                    colorArgb = 0xFF00A300,
                    order = 1,
                ),
                StartTileRecord(
                    id = "messages",
                    label = "Messages",
                    size = TileSize.TwoByTwo,
                    colorArgb = 0xFFE51400,
                    order = 2,
                ),
                StartTileRecord(
                    id = "calendar",
                    label = "Calendar",
                    size = TileSize.OneByOne,
                    colorArgb = 0xFF800080,
                    order = 3,
                ),
                StartTileRecord(
                    id = "clock",
                    label = "Clock",
                    size = TileSize.TwoByTwo,
                    colorArgb = 0xFF00BCF2,
                    order = 4,
                ),
                StartTileRecord(
                    id = "settings",
                    label = "Settings",
                    size = TileSize.OneByOne,
                    colorArgb = 0xFFF7630C,
                    order = 5,
                ),
            ),
        )

        assertEquals(
            listOf("weather", "phone", "messages", "calendar", "clock", "settings"),
            positionedTiles
                .sortedWith(compareBy<PositionedStartTile> { it.position.row }.thenBy { it.position.column })
                .map { it.record.id },
        )
    }

    @Test
    fun layoutRejectsBlankTileIds() {
        try {
            positionStartTiles(
                listOf(
                    StartTileRecord(
                        id = " ",
                        label = "Blank",
                        size = TileSize.OneByOne,
                        colorArgb = 0xFF0078D7,
                        order = 0,
                    ),
                ),
            )
            fail("Blank tile IDs must be rejected.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }

    @Test
    fun layoutRejectsDuplicateTileIds() {
        try {
            positionStartTiles(
                listOf(
                    StartTileRecord(
                        id = "phone",
                        label = "Phone",
                        size = TileSize.OneByOne,
                        colorArgb = 0xFF00A300,
                        order = 0,
                    ),
                    StartTileRecord(
                        id = "phone",
                        label = "Phone Copy",
                        size = TileSize.OneByOne,
                        colorArgb = 0xFFE51400,
                        order = 1,
                    ),
                ),
            )
            fail("Duplicate tile IDs must be rejected.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
