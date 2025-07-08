import React, { useState, useEffect } from "react";
import axios from "./api";
import ChangePassword from "./ChangePassword";
import Login from "./Login";
import { logout } from "./api";
import CryptoJS from 'crypto-js';
import './App.css';

function App() {
    const [entries, setEntries] = useState([]);
    const [label, setLabel] = useState("");
    const [url, setUrl] = useState("");
    const [username, setUsername] = useState("");
    const [passwort, setPasswort] = useState("");
    const [email, setEmail] = useState("");
    const [note, setNote] = useState("");
    const [category, setCategory] = useState("Allgemein");
    const [view, setView] = useState("list");
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [visiblePasswords, setVisiblePasswords] = useState(new Set());

    // categories
    const categories = ["Allgemein", "Privat", "Geschäft", "Games", "Hobbies", "Social Media", "Banking"];

    const hashPassword = (password) => {
        return CryptoJS.SHA256(password).toString();
    };

    const togglePasswordVisibility = (id) => {
        setVisiblePasswords(prev => {
            const newSet = new Set(prev);
            if (newSet.has(id)) {
                newSet.delete(id);
            } else {
                newSet.add(id);
            }
            return newSet;
        });
    };

    const fetchPasswords = async () => {
        try {
            const res = await axios.get("/passwords");
            setEntries(res.data);
            setVisiblePasswords(new Set());
        } catch (error) {
            console.error("Error fetching passwords:", error);
            setEntries([]);
        }
    };

    useEffect(() => {
        if (isAuthenticated && view === "list") {
            fetchPasswords();
        }
    }, [view, isAuthenticated]);

    const handleLogout = () => {
        logout();
        setIsAuthenticated(false);
        setEntries([]);
        setVisiblePasswords(new Set());
        setView("list");
    };

    const handleViewChange = (newView) => {
        setView(newView);
        if (newView === "list" && isAuthenticated) {
            fetchPasswords();
        }
    };

    const handlePasswordChangeSuccess = () => {
        handleLogout();
    };

    const addEntry = async () => {
        if (!label || !url || !username || !passwort) {
            alert("Bitte alle Pflichtfelder ausfüllen!");
            return;
        }
        try {
            await axios.post("/passwords", {
                label,
                url,
                username,
                password: passwort,
                email,
                note,
                category
            });
            await fetchPasswords();
            setLabel("");
            setUrl("");
            setUsername("");
            setPasswort("");
            setEmail("");
            setNote("");
            setCategory("Allgemein");
        } catch (error) {
            alert("Fehler beim Hinzufügen: " + error.message);
        }
    };

    // Group entries by category
    const groupedEntries = entries.reduce((groups, entry) => {
        const category = entry.category || "Allgemein";
        if (!groups[category]) {
            groups[category] = [];
        }
        groups[category].push(entry);
        return groups;
    }, {});

    if (!isAuthenticated) {
        return <Login onLogin={() => setIsAuthenticated(true)} />;
    }

    return (
        <div className="app-container">
            <div className="app-header">
                <h1>Passwort Safe</h1>
                <button 
                    onClick={handleLogout}
                    className="logout-button"
                >
                    Abmelden
                </button>
            </div>

            <nav className="nav-buttons">
                <button
                    onClick={() => handleViewChange("list")}
                    disabled={view === "list"}
                    className="nav-button"
                >
                    Passwort-Liste
                </button>
                <button
                    onClick={() => handleViewChange("change-password")}
                    disabled={view === "change-password"}
                    className="nav-button"
                >
                    Passwort ändern
                </button>
            </nav>

            {view === "list" && (
                <>
                    <div className="password-form">
                        <select
                            className="form-input"
                            value={category}
                            onChange={(e) => setCategory(e.target.value)}
                        >
                            {categories.map(cat => (
                                <option key={cat} value={cat}>{cat}</option>
                            ))}
                        </select>
                        <input
                            className="form-input"
                            placeholder="Label *"
                            value={label}
                            onChange={(e) => setLabel(e.target.value)}
                        />
                        <input
                            className="form-input"
                            placeholder="URL *"
                            value={url}
                            onChange={(e) => setUrl(e.target.value)}
                        />
                        <input
                            className="form-input"
                            placeholder="Username *"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                        <input
                            className="form-input"
                            placeholder="Passwort *"
                            value={passwort}
                            onChange={(e) => setPasswort(e.target.value)}
                            type="password"
                        />
                        <input
                            className="form-input"
                            placeholder="Email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            type="email"
                        />
                        <textarea
                            className="form-input"
                            placeholder="Notizen"
                            value={note}
                            onChange={(e) => setNote(e.target.value)}
                            rows="3"
                        />
                        <button onClick={addEntry} className="add-button">
                            Hinzufügen
                        </button>
                    </div>

                    <div className="password-categories">
                        {Object.entries(groupedEntries).map(([category, categoryEntries]) => (
                            <div key={category} className="category-section">
                                <h2 className="category-title">{category}</h2>
                                <ul className="password-list">
                                    {categoryEntries.map((entry) => (
                                        <li key={entry.id} className="password-item">
                                            <div className="password-info">
                                                <strong>{entry.label}</strong>
                                                <span>{entry.url}</span>
                                                <span>{entry.username}</span>
                                                {entry.email && <span>{entry.email}</span>}
                                                <span style={{ fontFamily: 'monospace' }}>
                                                    {visiblePasswords.has(entry.id) ? hashPassword(entry.password) : "•".repeat(8)}
                                                </span>
                                                {entry.note && <span className="note-text">{entry.note}</span>}
                                            </div>
                                            <button
                                                onClick={() => togglePasswordVisibility(entry.id)}
                                                className={`toggle-button ${visiblePasswords.has(entry.id) ? 'visible' : 'hidden'}`}
                                            >
                                                {visiblePasswords.has(entry.id) ? "Verbergen" : "Anzeigen"}
                                            </button>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        ))}
                    </div>
                </>
            )}

            {view === "change-password" && <ChangePassword onSuccess={handlePasswordChangeSuccess} />}
        </div>
    );
}

export default App;
