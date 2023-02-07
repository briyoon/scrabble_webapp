import update from 'immutability-helper'
import { useCallback } from "react"

import TrayTile from "./TrayTile"

import './Tray.css'
import { useEffect } from 'react'

function Tray({ hand, setHand, resetState}) {
    const swapTile = useCallback((dragIndex, hoverIndex) => {
        setHand((prevHand) =>
            update(prevHand, {
                $splice: [
                    [dragIndex, 1],
                    [hoverIndex, 0, prevHand[dragIndex]],
                ],
            }),
        )
    }, [hand, setHand])

    const renderTile = useCallback((tile, index) => {
        return (
            <TrayTile key={tile.id} id={tile.id} index={index} letter={tile.letter} swapTile={swapTile}/>
        )
    }, [hand])

    return (
        <div className="tray">
            <div className="tray-tiles">
                {hand.map((tile, index) => renderTile(tile, index))}
            </div>
        </div>
    )
}

export default Tray