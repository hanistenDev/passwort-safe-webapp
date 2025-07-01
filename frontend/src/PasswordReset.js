import React, { useState } from "react";
import axios from "./api";

export default function PasswordReset() {
    const [username, setUsername] = useState("");
    const [token, setToken] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [message, setMessage] = useState("");

    const requestReset = async () => {
        try {
            const res = await axios.post("/reset/request", null, { params: { username } });
            setMessage(res.data);
        } catch (error) {
            setMessage("Fehler beim Anfordern des Tokens");
        }
    };

    const confirmReset = async () => {
        try {
            const res = await axios.post("/reset/confirm", null, { params: { token, newPassword } });
            setMessage(res.data);
        } catch (error) {
            setMessage("Fehler beim Zurücksetzen");
        }
    };

    return (
        <div style={{ maxWidth: 600, margin: "auto", padding: 20 }}>
            <h2>Passwort zurücksetzen</h2>

            <div style={{ marginBottom: 10 }}>
                <input
                    type="text"
                    placeholder="Benutzername"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    style={{ width: "100%", padding: 8, marginBottom: 10 }}
                />
                <button onClick={requestReset} style={{ padding: "10px 20px" }}>
                    Reset-Token anfordern
                </button>
            </div>

            <div style={{ marginBottom: 10 }}>
                <input
                    type="text"
                    placeholder="Reset-Token"
                    value={token}
                    onChange={(e) => setToken(e.target.value)}
                    style={{ width: "100%", padding: 8, marginBottom: 10 }}
                />
                <input
                    type="password"
                    placeholder="Neues Masterpasswort"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    style={{ width: "100%", padding: 8, marginBottom: 10 }}
                />
                <button onClick={confirmReset} style={{ padding: "10px 20px" }}>
                    Passwort zurücksetzen
                </button>
            </div>

            {message && <p>{message}</p>}
        </div>
    );
}
