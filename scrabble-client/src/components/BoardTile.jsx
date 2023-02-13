import React from 'react';
import { useRef, useEffect } from 'react';
import { useDrag, useDrop } from 'react-dnd';

import DnDTypes from '../DnDTypes';

import styles from './Tile.module.css'

function BoardTile({ id, value, placeTile, moveTileToBoard, ogTile }) {
    let cssClass;
    let letter;

    switch (value) {
        case ")":
            cssClass = styles.qw;
            letter = "Quad\nWord"
            break;
        case "}":
            cssClass = styles.tw;
            letter = "TW"
            break;
        case "]":
            cssClass = styles.dw;
            letter = "DW"
            break;
        case "(":
            cssClass = styles.ql;
            letter = "Quad\nLetter"
            break;
        case "{":
            cssClass = styles.tl;
            letter = "TL"
            break;
        case "[":
            cssClass = styles.dl;
            letter = "DL"
            break;
        case ".":
            cssClass = styles.blank;
            letter = ""
            break;
        default:
            // if on ogBoard, tile cant be moved
            if (ogTile === value) {
                cssClass = styles.filled
            }
            else {
                cssClass = styles.moveable;
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
        return (cssClass !== styles.filled) || (cssClass !== styles.moveable)
    })

    if (cssClass === styles.moveable) {
        return (
            <div
                className={`${styles.tile} ${cssClass}`}
                ref={drag}
            >
                {letter}
            </div>
        )
    }
    else if (cssClass !== styles.filled) {
        return (
            <div
                className={`${styles.tile} ${cssClass}`}
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
                className={`${styles.tile} ${cssClass}`}
            >
                {letter}
            </div>
        )
    }


}

export default BoardTile