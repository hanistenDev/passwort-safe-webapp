import React, { useState } from "react";
import axios from "./api";

export default function PasswordReset({ onSuccess }) {
    const [username, setUsername] = useState("");
    const [token, setToken] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [message, setMessage] = useState("");
    const [step, setStep] = useState("request"); // "request" or "confirm"

    const getErrorMessage = (error) => {
        if (error.response) {
            // The server responded with an error
            if (typeof error.response.data === 'string') {
                return error.response.data;
            }
            if (error.response.data && error.response.data.message) {
                return error.response.data.message;
            }
            if (error.response.data && error.response.data.error) {
                return error.response.data.error;
            }
            return `Fehler: ${error.response.status}`;
        }
        if (error.request) {
            // The request was made but no response was received
            return "Keine Antwort vom Server erhalten";
        }
        // Something happened in setting up the request
        return error.message || "Ein unbekannter Fehler ist aufgetreten";
    };

    const requestReset = async () => {
        try {
            const res = await axios.post("/api/reset/request", { username });
            setToken(res.data);
            setMessage("Reset-Token wurde erstellt. Bitte geben Sie Ihr neues Passwort ein.");
            setStep("confirm");
        } catch (error) {
            setMessage(getErrorMessage(error));
        }
    };

    const confirmReset = async () => {
        if (newPassword !== confirmPassword) {
            setMessage("Die Passwörter stimmen nicht überein.");
            return;
        }

        if (newPassword.length < 8) {
            setMessage("Das Passwort muss mindestens 8 Zeichen lang sein.");
            return;
        }

        try {
            await axios.post("/api/reset/confirm", {
                token,
                newPassword
            });
            setMessage("Passwort erfolgreich zurückgesetzt");
            
            // Clear form
            setToken("");
            setNewPassword("");
            setConfirmPassword("");

            // Call success callback if provided
            if (onSuccess) {
                setTimeout(() => {
                    onSuccess();
                }, 2000);
            }
        } catch (error) {
            setMessage(getErrorMessage(error));
        }
    };

    const getMessageStyle = () => {
        const baseStyle = {
            padding: "10px",
            borderRadius: "4px",
            marginTop: "10px"
        };

        if (!message) return baseStyle;

        return {
            ...baseStyle,
            backgroundColor: message.toLowerCase().includes("erfolgreich") ? "#e8f5e9" : "#ffebee"
        };
    };

    return (
        <div style={{ maxWidth: 600, margin: "auto", padding: 20 }}>
            <h2>Passwort zurücksetzen</h2>

            {step === "request" && (
                <div style={{ marginBottom: 10 }}>
                    <input
                        type="text"
                        placeholder="Benutzername"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        style={{ width: "100%", padding: 8, marginBottom: 10 }}
                    />
                    <button 
                        onClick={requestReset} 
                        disabled={!username}
                        style={{ padding: "10px 20px" }}
                    >
                        Reset-Token anfordern
                    </button>
                </div>
            )}

            {step === "confirm" && (
                <div style={{ marginBottom: 10 }}>
                    <input
                        type="password"
                        placeholder="Neues Passwort"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        style={{ width: "100%", padding: 8, marginBottom: 10 }}
                    />
                    <input
                        type="password"
                        placeholder="Neues Passwort bestätigen"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        style={{ width: "100%", padding: 8, marginBottom: 10 }}
                    />
                    <button 
                        onClick={confirmReset}
                        disabled={!newPassword || !confirmPassword}
                        style={{ padding: "10px 20px" }}
                    >
                        Passwort zurücksetzen
                    </button>
                </div>
            )}

            {message && (
                <p style={getMessageStyle()}>
                    {message}
                </p>
            )}
        </div>
    );
}
