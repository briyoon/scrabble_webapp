import { useCallback, useEffect } from "react";
import { useState } from "react";
import { useParams } from "react-router";
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from "react-dnd-html5-backend";

import Board from "../components/Board";
import Scores from "../components/Scores";
import GameHistory from "../components/GameHistory";
import Tray from "../components/Tray";

import './Game.css';
import { CustomDragLayer } from "../components/CustomDragLayer";

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
        const res = await fetch(
            "http://localhost:8080/api/games?" + new URLSearchParams({gameID: params.gameID, newBoard: JSON.stringify(board.tiles.join(''))}),
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

        tmpBoard.tiles[boardIndex] = tmpHand[trayIndex].letter;
        tmpHand[trayIndex].letter = "";
        setHand(tmpHand);
        setBoard(tmpBoard);
    }, [hand, board]);

    const moveTile = useCallback((dropIndex, dragIndex) => {
        console.log("moving tile");
        let tmpBoard = JSON.parse(JSON.stringify(board));
        if (/[A-Za-z]/.test(tmpBoard.tiles[dropIndex])) {
            return;
        }

        tmpBoard.tiles[dropIndex] = tmpBoard.tiles[dragIndex];
        if (dragIndex in ogBoard.tiles) {
            tmpBoard.tiles[dragIndex] = ogBoard.tiles[dragIndex]
        }
        else {
            tmpBoard.tiles[dragIndex] = ".";
        }

        setBoard(tmpBoard);
    }, [board]);

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
                        <Board board={board} placeTile={placeTile} moveTile={moveTile} />
                    </div>
                    <div className="element right">
                        <h3>{params.gameID}</h3>
                        <Scores scores={scores} />
                        <GameHistory msgArray={msgArray} />
                        <Tray hand={hand} setHand={setHand} />
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