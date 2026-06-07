package com.windowsphonelauncher.tilecore

private const val GridColumns = 4

enum class TileSize(
    val columnSpan: Int,
    val rowSpan: Int,
) {
    OneByOne(columnSpan = 1, rowSpan = 1),
    TwoByTwo(columnSpan = 2, rowSpan = 2),
    FourByTwo(columnSpan = 4, rowSpan = 2),
}

data class StartTileRecord(
    val id: String,
    val label: String,
    val size: TileSize,
    val colorArgb: Long,
    val order: Int,
)

data class GridPosition(
    val row: Int,
    val column: Int,
)

data class PositionedStartTile(
    val record: StartTileRecord,
    val position: GridPosition,
)

fun positionStartTiles(
    records: List<StartTileRecord>,
    columns: Int = GridColumns,
): List<PositionedStartTile> {
    require(columns > 0) { "Grid columns must be positive." }
    validateTileRecords(records)
    val occupiedCells = mutableSetOf<GridPosition>()
    var scanStart = GridPosition(row = 0, column = 0)

    return records
        .sortedWith(compareBy<StartTileRecord> { it.order }.thenBy { it.id })
        .map { record ->
            require(record.size.columnSpan in 1..columns) {
                "Tile ${record.id} has invalid column span ${record.size.columnSpan} for $columns columns."
            }
            val position = firstAvailablePosition(
                size = record.size,
                columns = columns,
                occupiedCells = occupiedCells,
                scanStart = scanStart,
            )
            record.size.cellsFrom(position).forEach { occupiedCells.add(it) }
            scanStart = nextScanStart(position, record.size, columns)
            PositionedStartTile(record = record, position = position)
        }
}

fun List<PositionedStartTile>.hasOverlappingCells(): Boolean {
    val occupiedCells = mutableSetOf<GridPosition>()
    for (tile in this) {
        for (cell in tile.record.size.cellsFrom(tile.position)) {
            if (!occupiedCells.add(cell)) {
                return true
            }
        }
    }
    return false
}

fun List<PositionedStartTile>.occupiedRowCount(): Int =
    flatMap { tile -> tile.record.size.cellsFrom(tile.position) }
        .maxOfOrNull { it.row + 1 }
        ?: 0

private fun firstAvailablePosition(
    size: TileSize,
    columns: Int,
    occupiedCells: Set<GridPosition>,
    scanStart: GridPosition,
): GridPosition {
    var row = scanStart.row
    var columnStart = scanStart.column
    while (true) {
        for (column in columnStart..(columns - size.columnSpan)) {
            val candidate = GridPosition(row = row, column = column)
            if (size.cellsFrom(candidate).none { it in occupiedCells }) {
                return candidate
            }
        }
        row += 1
        columnStart = 0
    }
}

private fun validateTileRecords(records: List<StartTileRecord>) {
    val ids = mutableSetOf<String>()
    records.forEach { record ->
        require(record.id.isNotBlank()) { "Tile ID must not be blank." }
        require(ids.add(record.id)) { "Tile ID must be unique: ${record.id}." }
    }
}

private fun nextScanStart(
    position: GridPosition,
    size: TileSize,
    columns: Int,
): GridPosition {
    val nextColumn = position.column + size.columnSpan
    return if (nextColumn >= columns) {
        GridPosition(row = position.row + 1, column = 0)
    } else {
        GridPosition(row = position.row, column = nextColumn)
    }
}

private fun TileSize.cellsFrom(position: GridPosition): List<GridPosition> =
    buildList {
        repeat(rowSpan) { rowOffset ->
            repeat(columnSpan) { columnOffset ->
                add(
                    GridPosition(
                        row = position.row + rowOffset,
                        column = position.column + columnOffset,
                    ),
                )
            }
        }
    }
