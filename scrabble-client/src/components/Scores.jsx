import React from 'react'

export default function Scores({ scores }) {
    return (
        <div className="flex flex-row flex-1 justify-center items-center">
            <h3 className="m-1 text-[calc(var(--tile-size)/2.5)]" >CPU: {scores.cpu}</h3>
            <h3 className="m-1 text-[calc(var(--tile-size)/2.5)]" >You: {scores.player}</h3>
        </div>
    )
}