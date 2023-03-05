import { useEffect } from "react";
import useThemeToggle from "@/useThemeToggle";
import { SunIcon, MoonIcon } from "@heroicons/react/24/solid"

export default function Header() {
    const [theme, setTheme] = useThemeToggle();

    const themeToggle = () => {setTheme(theme === "dark" ? "light" : "dark")}
    const toggleClass = "fixed top-0 right-0 transition-all duration-500 text-primaryDark dark:text-primaryLight text-2xl rounded-lg w-12 h-12 hover:cursor-pointer"

    useEffect(() => {}, [theme])

    const renderToggleTheme = () => {
        if (theme === "dark") {
            return (
                <SunIcon className={toggleClass} onClick={() => themeToggle()} />
            )
        }
        return (
            <MoonIcon className={toggleClass} onClick={() => themeToggle()} />
        )
    }


    return (
        <header className="fixed top-0 left-0 w-screen h-16 m-0 bg-transparent">
            <div className="flex h-full justify-center items-center">
                {renderToggleTheme()}
                <button className="main-button h-12 w-24 text-xl m-0 border-4 hover:rounded-xl">Home</button>
            </div>
        </header>
    )
}