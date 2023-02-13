
import styles from './GameHistory.module.css'

const GameHistory = (({ msgArray }) => {
    return (
        <div className={styles.gameHistory}>
            <textarea className={styles.textarea} value={msgArray.join('\n')} readOnly/>
        </div>
    );
});

export default GameHistory;