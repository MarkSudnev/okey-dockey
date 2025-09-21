import './App.css';
import SearchResult from "./SearchResult";
import {useState} from "react";

function App() {

    const [keyword, setKeyword] = useState("");
    const [loading, setLoading] = useState(false);
    const [searchResults, setSearchResults] = useState([]);
    const [searchExecuted, setSearchExecuted] = useState(false);
    const [fetchFailed, setFetchFailed] = useState(false);
    const apiUrl = "http://localhost:8095/api/v1/documents"
    const examples = [
        "First album after break",
        "City found in a desert"
    ].map(item => <span key={item} className="badge accent m-5" onClick={() => onExampleClicked(item)}>{item}</span>);

    const searchDocuments = url => {
        setLoading(true);
        setFetchFailed(false);
        fetch(url)
            .then(res => res.json())
            .then(data => {
                console.log(data);
                setSearchResults(data);
            })
            .catch(err => {
                setFetchFailed(true);
                console.log(err);
            })
            .finally(() => {
                setLoading(false);
                setSearchExecuted(true);
            });
    }

    const onSearchButtonClicked = () => {
        const url = `${apiUrl}?q=${keyword}`;
        searchDocuments(url);
    }

    const onExampleClicked = (_keyword) => {
        setKeyword(_keyword);
        const url = `${apiUrl}?q=${keyword}`;
        searchDocuments(url);
    }

    return (
        <div className="App">
            <div className="container">
                <div className="row">
                    <div className="col-1">
                        <div className="form-group">
                            <label className="form-label" htmlFor="search-document-input">Semantic Search</label>
                            <div className="input-group">
                                <input
                                    className="input"
                                    id="search-document-input"
                                    type="text"
                                    onChange={ev => setKeyword(ev.target.value)}
                                    placeholder="What is this document about?.."/>
                                <span className="input-addon-btn">
                                    <button className="btn" onClick={() => onSearchButtonClicked()}>
                                        <i className="petalicon petalicon-search"/>
                                        Search
                                    </button>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="row mv-15"></div>
                {searchResults.length > 0 && searchExecuted &&
                    <div className="row">
                        <SearchResult
                            results={searchResults}/>
                    </div>
                }
                {searchExecuted && !fetchFailed && searchResults.length === 0 &&
                    <div className="alert">
                        <p><strong>Notice:</strong> Nothing is found</p>
                    </div>
                }
                {!searchExecuted &&
                    <div className="alert accent">
                        <p>
                            <strong>Heads up:</strong> Try to find document by semantic.
                        </p>
                    </div>
                }
                {!searchExecuted &&
                    <div className="panel pv-15">
                        <ul>
                            {examples}
                        </ul>
                    </div>

                }
                {!loading && fetchFailed &&
                    <div className="alert danger">
                        <p><strong>Error:</strong> Failed to fetch search results</p>
                    </div>
                }
            </div>
        </div>
    );
}

export default App;
