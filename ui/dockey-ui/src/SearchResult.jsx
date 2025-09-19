
function SearchResult({results}) {

    const lines = results.map(result => <SearchResultLine result={result} />)

    return (
        <div className="col-1">
            <h2>Found documents</h2>
            <table className="table">
                <thead>
                <tr>
                    <th scope="col">Name</th>
                    <th scope="col">Content</th>
                </tr>
                </thead>
                <tbody>
                {lines}
                </tbody>
            </table>
        </div>
    )
}

function SearchResultLine({result}) {

    return (
        <tr key={result.filename}>
            <td>{result.filename}</td>
            <td>{result.content}</td>
        </tr>
    )
}

export default SearchResult;