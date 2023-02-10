import React from 'react';
import { useRef } from 'react';
import { useDrag, useDrop } from 'react-dnd';

import DnDTypes from '../DnDTypes'

import './Tile.css'

const TrayTile = (({ id, index, letter, swapTile, preview, moveTileToTray }) => {
    let cssClass = "moveable"
    if (letter === "") {
        cssClass = "blank"
    }

    const ref = useRef(null)

    const [{ isDragging }, drag] = useDrag({
        type: DnDTypes.TrayTile,
        item: () => { return { id, index }},
        collect: (monitor) => ({
            isDragging: monitor.isDragging(),
        })
    });

    const [{ handlerId }, drop] = useDrop({
        accept: [
            DnDTypes.TrayTile,
            DnDTypes.BoardTile
        ],
        collect: (monitor) => ({
            handlerId: monitor.getHandlerId()
        }),
        hover: (item, monitor) => {
            if (monitor.getItemType() !== DnDTypes.TrayTile) {
                return
            }
            if (!ref.current) {
                return
            }

            const dragIndex = item.index
            const hoverIndex = index

            if (dragIndex === hoverIndex) {
                return
            }

            // Determine rectangle on screen
            const hoverBoundingRect = ref.current?.getBoundingClientRect()
            // Get horizontal middle
            const hoverMiddleX = hoverBoundingRect.width / 2
            // Determine mouse position
            const clientOffset = monitor.getClientOffset()
            // Get pixels to the left
            const hoverClientX = clientOffset.x - hoverBoundingRect.left

            // check right
            if (dragIndex < hoverIndex && hoverClientX < hoverMiddleX) {
                return
            }
            // check left
            if (dragIndex > hoverIndex && hoverClientX > hoverMiddleX) {
                return
            }

            // Time to actually perform the action
            swapTile(dragIndex, hoverIndex)
            item.index = hoverIndex
        },
        drop: (item, monitor) => {
            switch (monitor.getItemType()) {
                case DnDTypes.BoardTile:
                    moveTileToTray(id, item.id)
                    break;
                default:
                    console.log("deafult: ", monitor.getItemType())
                    break;
            }
        }
    }, [letter]);

    if (cssClass === "moveable") {
        drop(drag(ref))
    }
    else {
        drop(ref)
    }

    return (
        <div
            className={"tile " + (isDragging ? "blank" : cssClass)}
            ref={ref}
            data-handler-id={handlerId}
            role={preview ? 'TrayTilePreview' : 'TrayTile'}
        >
            {isDragging ? "" : letter}
        </div>
    )
});

export default TrayTile;