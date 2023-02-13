import React from 'react'
import styles from './Scores.module.css'

const Scores = (({ scores }) => {
    return (
        <div className={styles.scores}>
            <div className={styles.container}>
                <h3 className={styles.score} >CPU: {scores.cpu}</h3>
                <h3 className={styles.score} >You: {scores.player}</h3>
            </div>
        </div>
    )
});

export default Scores;