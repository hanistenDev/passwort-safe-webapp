import React, { useState } from 'react';
import { login, registerUser } from './api';
import { validatePassword, getPasswordStrength } from './utils/passwordValidation';
import './Login.css';

function Login({ onLogin }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isRegistering, setIsRegistering] = useState(false);
    const [error, setError] = useState('');
    const [validationErrors, setValidationErrors] = useState([]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setValidationErrors([]);

        // Only validate password for registration
        if (isRegistering) {
            const errors = validatePassword(password);
            if (errors.length > 0) {
                setValidationErrors(errors);
                return;
            }
        }

        try {
            if (isRegistering) {
                await registerUser(username, password);
                // After registration, automatically log in
                await login(username, password);
            } else {
                await login(username, password);
            }
            onLogin();
        } catch (error) {
            setError(error.message);
        }
    };

    return (
        <div className="login-container">
            <h2>{isRegistering ? 'Registrieren' : 'Anmelden'}</h2>
            
            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Benutzername:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Passwort:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => {
                            setPassword(e.target.value);
                            if (isRegistering) {
                                setValidationErrors(validatePassword(e.target.value));
                            }
                        }}
                        required
                    />
                    {isRegistering && password && (
                        <div className={`password-strength ${getPasswordStrength(password).replace(' ', '-')}`}>
                            Passwortstärke: {getPasswordStrength(password)}
                        </div>
                    )}
                </div>

                {isRegistering && validationErrors.length > 0 && (
                    <div className="validation-errors">
                        <ul>
                            {validationErrors.map((err, index) => (
                                <li key={index}>{err}</li>
                            ))}
                        </ul>
                    </div>
                )}
                
                <div className="button-group">
                    <button type="submit">
                        {isRegistering ? 'Registrieren' : 'Anmelden'}
                    </button>
                    <button 
                        type="button" 
                        onClick={() => {
                            setIsRegistering(!isRegistering);
                            setValidationErrors([]);
                            setError('');
                        }}
                    >
                        {isRegistering ? 'Zur Anmeldung' : 'Zur Registrierung'}
                    </button>
                </div>
            </form>
        </div>
    );
}

export default Login; 