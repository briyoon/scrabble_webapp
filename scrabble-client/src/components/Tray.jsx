import update from 'immutability-helper'
import { useCallback } from "react"

import TrayTile from "./TrayTile"

import styles from './Tray.module.css'

function Tray({ hand, setHand, resetState, moveTileToTray}) {
    const swapTile = useCallback((dragIndex, hoverIndex) => {
        setHand((prevHand) =>
            update(prevHand, {
                $splice: [
                    [dragIndex, 1],
                    [hoverIndex, 0, prevHand[dragIndex]],
                ],
            }),
        )
        // let tmpHand = JSON.parse(JSON.stringify(hand))
        // let dragLetter = tmpHand[dragIndex].letter
        // tmpHand[dragIndex].letter = tmpHand[hoverIndex].letter
        // tmpHand[hoverIndex].letter = dragLetter
        // setHand(tmpHand)
    }, [])

    const renderTile = useCallback((tile, index) => {
        return (
            <TrayTile key={tile.id} id={tile.id} index={index} letter={tile.letter} swapTile={swapTile} moveTileToTray={moveTileToTray} />
        )
    }, [hand])

    return (
        <div className={styles.tray}>
            <div className={styles.trayTiles}>
                {hand.map((tile, index) => renderTile(tile, index))}
            </div>
        </div>
    )
}

export default Tray