import { memo } from 'react'
import TrayTile from './TrayTile.jsx'

import './Tray.module.css'

export const TrayTilePreview = memo(function TrayTilePreview({ id, index, letter, swapTile }) {
    return (
        <div>
            <TrayTile id={id} index={index} letter={letter} swapTile={swapTile} preview />
        </div>
    )
})
