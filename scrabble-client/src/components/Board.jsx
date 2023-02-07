import './Board.css'
import BoardTile from './BoardTile';

function Board({ board, placeTile, moveTile }) {
    let size = board.size
    let tiles = board.tiles
    let tmpTile = []

    for (let i = 0; i < size; i++) {
        let row = []
        for (let j = 0; j < size; j++) {
            row.push(tiles[i * size + j])
        }
        tmpTile.push(row)
    }

    return (
        <div className='board'>
            <table className='table'>
                <tbody className='table-body'>
                    {tmpTile.map((row, i) => {
                        return (
                            <tr key={i}>
                                {row.map((col, j) => {
                                    return (
                                        <td key={j}>
                                            {<BoardTile key={i * size + j} id={i * size + j} value={col} placeTile={placeTile} moveTile={moveTile} />}
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