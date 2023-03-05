

export default function GameHistory({ msgArray }) {
    return (
        <textarea
            className="bg-tan dark:bg-triDark rounded-lg resize-none
            h-[calc(9*var(--tile-size))] w-[calc(8*var(--tile-size))]
            text-[calc(var(--tile-size)/3)]
            border-secondaryLight dark:border-secondaryDark border-2 px-1 py-0.5 transition-colors duration-300"
            value={msgArray.join('\n')}
            readOnly
        />
    );
}