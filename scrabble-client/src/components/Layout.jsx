import Header from "./Header"


export default function Layout({ children }) {
    return (
        <div className="flex flex-col w-screen h-screen">
            <Header />
            <>{children}</>
        </div>
    )
}