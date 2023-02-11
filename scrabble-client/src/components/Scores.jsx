import React from 'react'
import './Scores.module.css'

const Scores = (({ scores }) => {
    return (
        <div className="scores">
            <div className="container">
                <h3 className="score" >CPU: {scores.cpu}</h3>
                <h3 className="score ">You: {scores.player}</h3>
            </div>
        </div>
    )
});

export default Scores;