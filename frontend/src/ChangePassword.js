import React, { useState } from 'react';
import axios from './api';
import { logout } from './api';
import { validatePassword, getPasswordStrength } from './utils/passwordValidation';
import './ChangePassword.css';

function ChangePassword({ onSuccess }) {
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const [validationErrors, setValidationErrors] = useState([]);

    const getErrorMessage = (error) => {
        if (error.response) {
            if (typeof error.response.data === 'string') {
                return error.response.data;
            }
            if (error.response.data && error.response.data.message) {
                return error.response.data.message;
            }
            if (error.response.data && error.response.data.error) {
                return error.response.data.error;
            }
            return 'Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.';
        } else if (error.request) {
            return 'Keine Antwort vom Server erhalten. Bitte überprüfen Sie Ihre Internetverbindung.';
        } else {
            return 'Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.';
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess(false);
        setValidationErrors([]);

        // Check if passwords match
        if (newPassword !== confirmPassword) {
            setError('Die Passwörter stimmen nicht überein');
            return;
        }

        // Validate new password
        const errors = validatePassword(newPassword);
        if (errors.length > 0) {
            setValidationErrors(errors);
            return;
        }

        try {
            await axios.post('/passwords/change-password', {
                oldPassword,
                newPassword
            });
            setSuccess(true);
            setOldPassword('');
            setNewPassword('');
            setConfirmPassword('');
            
            // Log out after successful password change
            setTimeout(() => {
                logout(); // First logout to clear credentials
                onSuccess(); // Then call onSuccess which will trigger App's handleLogout
            }, 2000);
        } catch (err) {
            setError(getErrorMessage(err));
        }
    };

    return (
        <div className="change-password-container">
            <h2>Passwort ändern</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Aktuelles Passwort:</label>
                    <input
                        type="password"
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Neues Passwort:</label>
                    <input
                        type="password"
                        value={newPassword}
                        onChange={(e) => {
                            setNewPassword(e.target.value);
                            setValidationErrors(validatePassword(e.target.value));
                        }}
                        required
                    />
                    {newPassword && (
                        <div className={`password-strength ${getPasswordStrength(newPassword).replace(' ', '-')}`}>
                            Passwortstärke: {getPasswordStrength(newPassword)}
                        </div>
                    )}
                </div>
                <div className="form-group">
                    <label>Neues Passwort bestätigen:</label>
                    <input
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                </div>
                
                {validationErrors.length > 0 && (
                    <div className="validation-errors">
                        <ul>
                            {validationErrors.map((err, index) => (
                                <li key={index}>{err}</li>
                            ))}
                        </ul>
                    </div>
                )}
                
                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">Passwort erfolgreich geändert! Sie werden in Kürze abgemeldet...</div>}
                
                <div className="button-group">
                    <button type="button" onClick={onSuccess}>Zurück</button>
                    <button type="submit">Passwort ändern</button>
                </div>
            </form>
        </div>
    );
}

export default ChangePassword; 