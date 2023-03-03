import { useEffect, useState } from 'react';
import { useTheme } from "next-themes";
import{ SunIcon, MoonIcon } from "@heroicons/react/solid";


export default function Header() {
    const { systemTheme, theme, setTheme } = useTheme();
    const [ mounted, setMounted ] = useState(false);

    useEffect(() =>{
        setMounted(true);
    },[])

    const renderThemeChanger = () => {
        console.log(mounted)
        if (!mounted) return null;

        const currentTheme = theme === "system" ? systemTheme : theme;
        console.log(currentTheme)

        if (currentTheme == "dark") {
            return (<SunIcon className="w-16 h-16" role="button" onClick={() => setTheme('light')} />)
        }
        return (<MoonIcon className="w-16 h-16" role="button" onClick={() => setTheme('dark')} />)
    }

    return (
        <header className="fixed top-0 left-0 w-screen h-16 m-0">
            <div>
                {renderThemeChanger()}
            </div>
        </header>
    )
}