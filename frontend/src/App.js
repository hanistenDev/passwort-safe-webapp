import React, { useState, useEffect } from "react";
import axios from "./api";
import PasswordReset from "./PasswordReset";

function App() {
    const [entries, setEntries] = useState([]);
    const [label, setLabel] = useState("");
    const [url, setUrl] = useState("");
    const [username, setUsername] = useState("");
    const [passwort, setPasswort] = useState("");
    const [view, setView] = useState("list"); // 'list' oder 'reset'

    useEffect(() => {
        if (view === "list") {
            axios
                .get("/passwords")
                .then((res) => setEntries(res.data))
                .catch(() => setEntries([]));
        }
    }, [view]);

    const addEntry = async () => {
        if (!label || !url || !username || !passwort) {
            alert("Bitte alle Felder ausfüllen!");
            return;
        }
        try {
            const res = await axios.post("/passwords", {
                label,
                url,
                username,
                password: passwort,
            });
            setEntries([...entries, res.data]);
            setLabel("");
            setUrl("");
            setUsername("");
            setPasswort("");
        } catch (error) {
            alert("Fehler beim Hinzufügen: " + error.message);
        }
    };

    return (
        <div style={{ maxWidth: 600, margin: "auto", padding: 20 }}>
            <h1>Passwort Safe</h1>

            <nav style={{ marginBottom: 20 }}>
                <button
                    onClick={() => setView("list")}
                    disabled={view === "list"}
                    style={{ marginRight: 10, padding: "8px 16px" }}
                >
                    Passwort-Liste
                </button>
                <button
                    onClick={() => setView("reset")}
                    disabled={view === "reset"}
                    style={{ padding: "8px 16px" }}
                >
                    Passwort zurücksetzen
                </button>
            </nav>

            {view === "list" && (
                <>
                    <input
                        placeholder="Label"
                        value={label}
                        onChange={(e) => setLabel(e.target.value)}
                        style={{ display: "block", marginBottom: 10, width: "100%", padding: 8 }}
                    />
                    <input
                        placeholder="URL"
                        value={url}
                        onChange={(e) => setUrl(e.target.value)}
                        style={{ display: "block", marginBottom: 10, width: "100%", padding: 8 }}
                    />
                    <input
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        style={{ display: "block", marginBottom: 10, width: "100%", padding: 8 }}
                    />
                    <input
                        placeholder="Passwort"
                        value={passwort}
                        onChange={(e) => setPasswort(e.target.value)}
                        type="password"
                        style={{ display: "block", marginBottom: 10, width: "100%", padding: 8 }}
                    />

                    <button onClick={addEntry} style={{ padding: "10px 20px", fontSize: 16 }}>
                        Hinzufügen
                    </button>

                    <ul style={{ marginTop: 20, listStyleType: "none", paddingLeft: 0 }}>
                        {entries.map((entry) => (
                            <li
                                key={entry.id}
                                style={{ marginBottom: 10, borderBottom: "1px solid #ddd", paddingBottom: 10 }}
                            >
                                <strong>{entry.label}</strong> | {entry.url} | {entry.username} | {entry.password}
                            </li>
                        ))}
                    </ul>
                </>
            )}

            {view === "reset" && <PasswordReset />}
        </div>
    );
}

export default App;
