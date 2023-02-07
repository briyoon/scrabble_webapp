import { useDragLayer } from 'react-dnd'
import { TrayTilePreview } from './TrayTilePreview.jsx'

import DnDTypes from '../DnDTypes.js'

const layerStyles = {
  position: 'fixed',
  pointerEvents: 'none',
  zIndex: 100,
  left: 0,
  top: 0,
  width: '100%',
  height: '100%',
}
function getItemStyles(initialOffset, currentOffset) {
    if (!initialOffset || !currentOffset) {
        return {
            display: 'none',
        }
    }
    let { x, y } = currentOffset

    const transform = `translate(${x}px, ${y}px)`
    return {
        transform,
        WebkitTransform: transform,
    }
}

export const CustomDragLayer = (props) => {
    const { itemType, isDragging, item, initialOffset, currentOffset } =
        useDragLayer((monitor) => ({
            item: monitor.getItem(),
            itemType: monitor.getItemType(),
            initialOffset: monitor.getInitialSourceClientOffset(),
            currentOffset: monitor.getSourceClientOffset(),
            isDragging: monitor.isDragging(),
        }))
    function renderItem() {
        switch (itemType) {
            case DnDTypes.TrayTile:
                return <TrayTilePreview id={item.id} index={item.index} letter={item.letter} swapTile={item.swapTile} preview  />
            default:
                return null
        }
    }
    if (!isDragging) {
        return null
    }
    return (
        <div style={layerStyles}>
            <div
                style={getItemStyles(initialOffset, currentOffset, props.snapToGrid)}>
                {renderItem()}
            </div>
        </div>
    )
}
