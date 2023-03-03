import { useRouter } from "next/router";
// import

function App() {
    const router = useRouter();

    return (
        <div className="flex flex-col justify-center items-center w-screen h-screen">
            <button className="main-button" onClick={() => postGame(router)}>Create Game</button>
            <button className="main-button" disabled={true}>Resume Game</button>
            <button className="main-button" disabled={true}>Puzzle Mode</button>
            <button className="main-button" disabled={true}>Solver</button>
            <a className="w-24 h-24 dark:invert my-4" href="https://github.com/briyoon/scrabble_webapp">
                <img src="https://upload.wikimedia.org/wikipedia/commons/9/91/Octicons-mark-github.svg" alt="Link to Github repo" />
            </a>
        </div>
    )
}

async function postGame(router) {
    console.log("Creating game")

    console.log(process.env.NEXT_PUBLIC_SERVER_ADDR + "/api/games")

    const res = await fetch(
        process.env.NEXT_PUBLIC_SERVER_ADDR + "/api/games",
        {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        cache: 'default',
        }
    );

    console.log(res);

    if (res.status == 200) {
        let body = await res.json();
        console.log(body);

        // Nav to new games page
        router.push(`/game/${body.gameID}`)
    }
    else {
        console.log("invalid response")
    }
}

export default App
