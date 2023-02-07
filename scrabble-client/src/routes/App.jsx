import './App.css'

function App() {
  return (
    <div className="App">
        <button className={"menubutton"} onClick={postGame}>Create Game</button>
        <button className={"menubutton"} >Resume Game</button>
    </div>
  )
}

async function postGame() {
  console.log("Creating game")
  const res = await fetch(
    "http://localhost:8080/api/games",
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      // mode: 'cors',
      cache: 'default'
    }
  );

  console.log(res);

  if (res.status == 200) {
    let body = await res.json();
    console.log(body);

    // Nav to new games page
    window.location.href = `/games/${body.gameID}`
  }
}

export default App
