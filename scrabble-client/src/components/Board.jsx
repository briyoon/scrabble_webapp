import BoardTile from './BoardTile';

export default function Board({ board, placeTile, moveTileToBoard, ogBoard }) {
    let size = board.size
    let tiles = board.tiles

    return (
        <div className="my-auto justify-center items-center">
            <table>
                <tbody>
                    {tiles.map((row, i) => {
                        return (
                            <tr key={i}>
                                {row.map((col, j) => {
                                    return (
                                        <td className="bg-transparent" key={j}>
                                            {<BoardTile key={(i * size) + j} id={(i * size) + j} value={col} placeTile={placeTile} moveTileToBoard={moveTileToBoard} ogTile={ogBoard.tiles[Math.floor(((i * size) + j) / ogBoard.size)][((i * size) + j) % ogBoard.size]}/>}
                                        </td>
                                    )
                                })}
                            </tr>
                        )
                    })}
                </tbody>
            </table>
        </div>
    )
}