
import './GameHistory.module.css'

const GameHistory = (({ msgArray }) => {
    return (
        <div className="game-history">
            <textarea className="textarea" value={msgArray.join('\n')} readOnly/>
        </div>
    );
});

export default GameHistory;