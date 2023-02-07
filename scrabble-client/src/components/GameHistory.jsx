
import './GameHistory.css'

const GameHistory = (({ msgArray }) => {
    return (
        <div className="game-history">
            <textarea value={msgArray.join('\n')} readOnly/>
        </div>
    );
});

export default GameHistory;