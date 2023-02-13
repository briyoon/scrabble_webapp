import { useRouter } from "next/router";

function App() {
    const router = useRouter();

    return (
        <div className="App">
            <button className="menubutton" onClick={() => postGame(router)}>Create Game</button>
            <button className="menubutton" disabled={true}>Resume Game</button>
            <button className="menubutton" disabled={true}>Solver</button>
        </div>
    )
}

async function postGame(router) {
    console.log("Creating game")

    const res = await fetch(
        "/api/games",
        {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        cache: 'default'
        }
    );

    console.log(res);

    if (res.status == 200) {
        let body = await res.json();
        console.log(body);

        // Nav to new games page
        router.push(`/game/${body.gameID}`)
    }
}

export default App
