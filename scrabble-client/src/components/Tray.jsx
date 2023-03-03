import update from 'immutability-helper'
import { useCallback } from "react"

import TrayTile from "./TrayTile"


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
        <div className="flex flex-row mx-16 my-4 justify-center">
            {hand.map((tile, index) => renderTile(tile, index))}
        </div>
    )
}

export default Tray