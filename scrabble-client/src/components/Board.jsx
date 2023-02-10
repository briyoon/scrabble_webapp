import './Board.css'
import BoardTile from './BoardTile';

function Board({ board, placeTile, moveTileToBoard, ogBoard }) {
    let size = board.size
    let tiles = board.tiles

    console.log(tiles)

    return (
        <div className='board'>
            <table className='table'>
                <tbody className='table-body'>
                    {tiles.map((row, i) => {
                        return (
                            <tr key={i}>
                                {row.map((col, j) => {
                                    return (
                                        <td key={j}>
                                            {<BoardTile key={i * size + j} id={i * size + j} value={col} placeTile={placeTile} moveTileToBoard={moveTileToBoard} ogBoard={ogBoard}/>}
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

export default Board;