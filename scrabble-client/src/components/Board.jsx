import styles from './Board.module.css';
import BoardTile from './BoardTile';

function Board({ board, placeTile, moveTileToBoard, ogBoard }) {
    let size = board.size
    let tiles = board.tiles

    return (
        <div className={styles.board}>
            <table className={styles.table}>
                <tbody className={styles.tableBody}>
                    {tiles.map((row, i) => {
                        return (
                            <tr key={i}>
                                {row.map((col, j) => {
                                    return (
                                        <td className={styles.td} key={j}>
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

export default Board;