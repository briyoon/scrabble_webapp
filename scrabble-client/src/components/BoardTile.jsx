import React from 'react';
import { useRef } from 'react';
import { useDrag, useDrop } from 'react-dnd';

import DnDTypes from '../DnDTypes';

import './Tile.module.css'

function BoardTile({ id, value, placeTile, moveTileToBoard, ogBoard }) {
    let cssClass;
    let letter;
    const ref = useRef(null)

    switch (value) {
        case ")":
            cssClass = "qw";
            letter = "Quad\nWord"
            break;
        case "}":
            cssClass = "tw";
            letter = "TW"
            break;
        case "]":
            cssClass = "dw";
            letter = "DW"
            break;
        case "(":
            cssClass = "ql";
            letter = "Quad\nLetter"
            break;
        case "{":
            cssClass = "tl";
            letter = "TL"
            break;
        case "[":
            cssClass = "dl";
            letter = "DL"
            break;
        case ".":
            cssClass = "blank";
            letter = ""
            break;
        default:
            // if on ogBoard, tile cant be noved
            if (ogBoard.tiles[Math.floor(id / ogBoard.size)][id % ogBoard.size] === value) {
                cssClass = "filled"
            }
            else {
                cssClass = "moveable";
            }
            letter = value.toLowerCase()
            break;
    }

    const [{ isDragging }, drag] = useDrag({
        type: DnDTypes.BoardTile,
        item: () => { return { id, letter }},
        collect: (monitor) => ({
            isDragging: !!monitor.isDragging(),
        })
    })

    const [, drop] = useDrop({
        accept: [
            DnDTypes.TrayTile,
            DnDTypes.BoardTile,
        ],
        drop: (item, monitor) => {
            switch (monitor.getItemType()) {
                case DnDTypes.TrayTile:
                    placeTile(id, item.id);
                    break;
                case DnDTypes.BoardTile:
                    moveTileToBoard(id, item.id)
                    break;
                default:
                    console.log("deafult: ", monitor.getItemType())
                    break;
            }
        },
        canDrop: () => checkEmpty(),
    }, [placeTile])

    const checkEmpty = (() => {
        return cssClass !== "filled"
    })

    if (cssClass === "moveable") {
        drag(drop(ref))
    }
    else {
        drop(ref)
    }

    return (
        <div
            className={"tile " + cssClass}
            ref={ref}
        >
            {letter}
        </div>
    )
}

export default BoardTile