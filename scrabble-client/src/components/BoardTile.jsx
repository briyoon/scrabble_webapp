import React from 'react';
import { useRef, useEffect } from 'react';
import { useDrag, useDrop } from 'react-dnd';

import DnDTypes from '../DnDTypes';

function BoardTile({ id, value, placeTile, moveTileToBoard, ogTile }) {
    let cssClass;
    let letter;

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
            // if on ogBoard, tile cant be moved
            if (ogTile === value) {
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
        console.log(cssClass)
        return (cssClass !== "filled") || (cssClass !== "moveable")
    })

    if (cssClass === "moveable") {
        return (
            <div
                className={`tile ${cssClass}`}
                ref={drag}
            >
                {letter}
            </div>
        )
    }
    else if (cssClass !== "filled") {
        return (
            <div
                className={`tile ${cssClass}`}
                ref={drop}
            >
                {letter}
            </div>
        )
    }
    else {
        return (
            <div
                id={id}
                className={`tile ${cssClass}`}
            >
                {letter}
            </div>
        )
    }


}

export default BoardTile