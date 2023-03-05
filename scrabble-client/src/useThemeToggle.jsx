import { useEffect, useState } from "react";


export default function useThemeToggle() {
    const [theme, setTheme] = useState(
        typeof window !== "undefined" ? localStorage.getItem("theme") : null
    )

    useEffect(() => {
        // if no window then return
        if (typeof window === "undefined") {
            return
        }

        // if theme is not null on intitial load, set it to storage
        if (theme) {
            setTheme(theme)
        }
        // if null, grab system preference
        else {
            setTheme(window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light")
        }

        localStorage.setItem("theme", theme)

        // replace previous with current
        const root = window.document.documentElement;
        const previousTheme = theme === "dark" ? "light" : "dark";
        root.classList.remove(previousTheme);
        root.classList.add(theme);
    }, [theme])

    return [ theme, setTheme ]
}