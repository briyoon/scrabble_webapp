import { useCallback, useEffect } from "react";
import { useState } from "react";
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from "react-dnd-html5-backend";
import { useRouter } from "next/router";

import Board from "../../components/Board";
import Scores from "../../components/Scores";
import GameHistory from "../../components/GameHistory";
import Tray from "../../components/Tray";

// import { CustomDragLayer } from "../components/CustomDragLayer";

const Game = (() => {
    const [loading, setLoading] = useState(true);
    const [board, setBoard, getBoard] = useState(null);
    const [hand, setHand] = useState(null);
    const [ogBoard, setogBoard] = useState(null);
    const [ogHand, setogHand] = useState(null);
    const [scores, setScores] = useState(null);
    const [msgArray, setMsgArray] = useState(null);

    const params = useRouter().query;

    const submitBoard = async () => {
        console.log("Submitting move")
        let boardString = board.tiles.reduce((acc, row) => {return acc.concat(row)}).join('')
        console.log(boardString)
        console.log(process.env.NEXT_PUBLIC_SERVER_ADDR + "/api/games?" + new URLSearchParams({gameID: params.gameID, newBoard: boardString}))
        const res = await fetch(
            process.env.NEXT_PUBLIC_SERVER_ADDR + "/api/games?" + new URLSearchParams({gameID: params.gameID, newBoard: boardString}),
            {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                cache: 'default',
                mode: 'no-cors'
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
              process.env.NEXT_PUBLIC_SERVER_ADDR + "/api/games?" + new URLSearchParams({gameID: gameID}),
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
        // console.log(params.gameID)
        getGame(params.gameID);
    }, [params, loading]);

    const placeTile = useCallback((boardIndex, trayId) => {
        console.log("placing tile");
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

        tmpBoard.tiles[iDrop][jDrop] = tmpBoard.tiles[iDrag][jDrag]
        tmpBoard.tiles[iDrag][jDrag] = ogBoard.tiles[iDrag][jDrag]

        setBoard(tmpBoard);
    }, [board, ogBoard]);

    const moveTileToTray = useCallback((dropIndex, dragIndex) => {
        console.log("moving tile to tray");

        console.log(dropIndex, dragIndex)

        let tmpBoard = JSON.parse(JSON.stringify(board));
        let tmpHand = JSON.parse(JSON.stringify(hand));

        let iDrag = Math.floor(dragIndex / tmpBoard.size)
        let jDrag = dragIndex % tmpBoard.size

        tmpHand[dropIndex].letter = tmpBoard.tiles[iDrag][jDrag]
        console.log(ogBoard.tiles[iDrag][jDrag])
        tmpBoard.tiles[iDrag][jDrag] = ogBoard.tiles[iDrag][jDrag]

        setBoard(tmpBoard);
        setHand(tmpHand);
    }, [hand, board, ogBoard]);

    // loading page
    if (board === null || hand === null) {
        return (
            <div className="flex justify-center items-center w-screen h-screen">
                <h1 className="text-3xl">Loading</h1>
            </div>
        )
    }
    else {
        return (
            <DndProvider backend={HTML5Backend}>
                <div className="flex justify-center items-center h-screen">
                    <Board board={board} placeTile={placeTile} moveTileToBoard={moveTileToBoard} ogBoard={ogBoard} />
                    <div className="flex flex-col items-center my-auto">
                        <h3 className="text-[calc(var(--tile-size)/1.5)]">{params.gameID}</h3>
                        <Scores scores={scores} />
                        <GameHistory msgArray={msgArray} />
                        <Tray hand={hand} setHand={setHand} moveTileToTray={moveTileToTray} />
                        <div>
                            <button className="game-button" onClick={() => submitBoard()}>Submit</button>
                            <button className="game-button" onClick={() => resetState()}>Reset</button>
                        </div>
                    </div>
                </div>
                {/* <CustomDragLayer /> */}
            </DndProvider>
        );
    }
});

export default Game;
