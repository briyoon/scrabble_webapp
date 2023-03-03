/** @type {import('tailwindcss').Config} */
const colors = require('tailwindcss/colors')
module.exports = {
  mode: "jit",
  darkMode: 'class',
  purge: [
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  content: [
    // Or if using `src` directory:
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    fontFamily: {
      display: ["Quicksand", "sans-serif"]
    },
    extend: {
      colors: {
        primaryLight: "#F3E7D2",
        secondaryLight: "#6A4D33",
        triLight: "#BBAE98",
        melon: "#FFaacf",
        airBlue: "#6D98BA",
        tan: "#D2B48C",

        primaryDark: "#242424",
        secondaryDark: "#C492B1",
        // triDark: "#343434"
        triDark: "#494949"
      }
    },
  },
  plugins: [],
}
