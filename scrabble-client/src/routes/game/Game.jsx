import { useCallback, useEffect } from "react";
import { useState } from "react";
import { useParams } from "react-router";
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from "react-dnd-html5-backend";

import Board from "../../components/Board";
import Scores from "../../components/Scores";
import GameHistory from "../../components/GameHistory";
import Tray from "../../components/Tray";

import './Game.css';
// import { CustomDragLayer } from "../components/CustomDragLayer";

const Game = (() => {
    const [loading, setLoading] = useState(true);
    const [board, setBoard] = useState(null);
    const [hand, setHand] = useState(null);
    const [ogBoard, setogBoard] = useState(null);
    const [ogHand, setogHand] = useState(null);
    const [scores, setScores] = useState(null);
    const [msgArray, setMsgArray] = useState(null);

    const params = useParams();

    const submitBoard = async () => {
        console.log("Submitting move")
        let boardString = board.tiles.reduce((acc, row) => {return acc.concat(row)}).join('')
        console.log(boardString)
        const res = await fetch(
            "http://localhost:8080/api/games?" + new URLSearchParams({gameID: params.gameID, newBoard: boardString}),
            {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                cache: 'default'
            }
        );

        let body = await res.json();

        switch (res.status) {
            case 200:
                setBoard(body.board);
                setogBoard(body.board);
                setHand((body.hand.map((letter, index) => {
                    return {
                        id: index,
                        letter: letter
                    }
                })));
                setogHand((body.hand.map((letter, index) => {
                    return {
                        id: index,
                        letter: letter
                    }
                })));
                setScores(body.scores);
                setMsgArray(body.msgArray);
                break;
            case 422:
                setMsgArray(body.msgArray);
                break;
            default:
                console.log(body)
                break;
        }

        return;
    }

    const resetState = (() => {
        console.log("Reseting state");
        setBoard(ogBoard);
        setHand(ogHand);
    });

    useEffect(() => {
        const getGame = async (gameID) => {
            console.log("getting game info");
            const res = await fetch(
              "http://localhost:8080/api/games?" + new URLSearchParams({gameID: gameID}),
              {
                method: 'GET',
                headers: {
                  'Content-Type': 'application/json'
                },
                // mode: 'cors',
                cache: 'default'
              }
            );

            if (res.status == 200) {
                let body = await res.json();
                console.log(body)
                setBoard(body.board);
                setogBoard(body.board);
                setHand((body.hand.map((letter, index) => {
                    return { id: index, letter: letter }
                })));
                setogHand((body.hand.map((letter, index) => {
                    return { id: index, letter: letter }
                })));
                setScores(body.scores);
                setMsgArray(body.msgArray);
            }
            setLoading(false);
            return;
        }

        getGame(params.gameID);
    }, [params]);

    const placeTile = useCallback((boardIndex, trayId) => {
        console.log("placing tile");
        console.log(boardIndex, trayId);
        let tmpBoard = JSON.parse(JSON.stringify(board));
        let tmpHand = JSON.parse(JSON.stringify(hand));
        let trayIndex = tmpHand.map(e => e.id).indexOf(trayId);

        tmpBoard.tiles[Math.floor(boardIndex / tmpBoard.size)][boardIndex % tmpBoard.size] = tmpHand[trayIndex].letter;
        tmpHand[trayIndex].letter = "";
        setHand(tmpHand);
        setBoard(tmpBoard);
    }, [hand, board]);

    const moveTileToBoard = useCallback((dropIndex, dragIndex) => {
        console.log("moving tile to board");

        let tmpBoard = JSON.parse(JSON.stringify(board));

        let iDrag = Math.floor(dragIndex / tmpBoard.size)
        let jDrag = dragIndex % tmpBoard.size
        let iDrop = Math.floor(dropIndex / tmpBoard.size)
        let jDrop = dropIndex % tmpBoard.size

        if (/[A-Za-z]/.test(tmpBoard.tiles[iDrop][jDrop])) {
            return;
        }

        tmpBoard.tiles[iDrop][jDrop] = tmpBoard.tiles[iDrag][jDrag]
        tmpBoard.tiles[iDrag][jDrag] = ogBoard.tiles[iDrag][jDrag]

        setBoard(tmpBoard);
    }, [board]);

    const moveTileToTray = useCallback((dropIndex, dragIndex) => {
        console.log("moving tile to tray");

        let tmpBoard = JSON.parse(JSON.stringify(board));
        let tmpHand = JSON.parse(JSON.stringify(hand));

        let iDrag = Math.floor(dragIndex / tmpBoard.size)
        let jDrag = dragIndex % tmpBoard.size

        if (/[A-Za-z]/.test(tmpHand[dropIndex].letter)) {
            return;
        }

        tmpHand[dropIndex].letter = tmpBoard.tiles[iDrag][jDrag]
        tmpBoard.tiles[iDrag][jDrag] = ogBoard.tiles[iDrag][jDrag]

        setBoard(tmpBoard);
        setHand(tmpHand);
    }, [hand, board]);

    if (loading) {
        return (
            <div>
                <h1>Loading game...</h1>
            </div>
        )
    }
    // temp holder for loading board / getting permissions
    else if (board === null || hand === null || scores === null || msgArray === null) {
        return (
            <div>
                <h1>Game not found</h1>
            </div>
        )
    }
    else {
        return (
            <DndProvider backend={HTML5Backend}>
                <div className="container">
                    <div className="element left">
                        <Board board={board} placeTile={placeTile} moveTileToBoard={moveTileToBoard} ogBoard={ogBoard} />
                    </div>
                    <div className="element right">
                        <h3>{params.gameID}</h3>
                        <Scores scores={scores} />
                        <GameHistory msgArray={msgArray} />
                        <Tray hand={hand} setHand={setHand} moveTileToTray={moveTileToTray} />
                        <div>
                            <button onClick={() => submitBoard()}>Submit</button>
                            <button onClick={() => resetState()}>Reset</button>
                        </div>
                    </div>
                </div>
                {/* <CustomDragLayer /> */}
            </DndProvider>
        );
    }
});

export default Game;