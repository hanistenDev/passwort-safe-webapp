export const validatePassword = (password) => {
    const errors = [];
    
    if (password.length < 12) {
        errors.push('Das Passwort muss mindestens 12 Zeichen lang sein');
    }
    if (!/[A-Z]/.test(password)) {
        errors.push('Das Passwort muss mindestens einen Großbuchstaben enthalten');
    }
    if (!/[a-z]/.test(password)) {
        errors.push('Das Passwort muss mindestens einen Kleinbuchstaben enthalten');
    }
    if (!/\d/.test(password)) {
        errors.push('Das Passwort muss mindestens eine Zahl enthalten');
    }
    if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
        errors.push('Das Passwort muss mindestens ein Sonderzeichen enthalten (!@#$%^&*(),.?":{}|<>)');
    }
    
    // Check for repeating characters
    if (/(.)\\1\\1/.test(password)) {
        errors.push('Das Passwort darf keine sich wiederholenden Zeichen enthalten');
    }
    
    // Check for sequential characters
    const sequences = 'abcdefghijklmnopqrstuvwxyz0123456789';
    const lowerPassword = password.toLowerCase();
    for (let i = 0; i < sequences.length - 2; i++) {
        const forward = sequences.slice(i, i + 3);
        const reverse = forward.split('').reverse().join('');
        if (lowerPassword.includes(forward) || lowerPassword.includes(reverse)) {
            errors.push('Das Passwort darf keine sequenziellen Zeichen enthalten');
            break;
        }
    }
    
    return errors;
};

export const getPasswordStrength = (password) => {
    if (!password) return '';
    
    let strength = 0;
    if (password.length >= 12) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/\d/.test(password)) strength++;
    if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength++;
    
    switch (strength) {
        case 0: return 'sehr schwach';
        case 1: return 'schwach';
        case 2: return 'mittel';
        case 3: return 'gut';
        case 4: return 'stark';
        case 5: return 'sehr stark';
        default: return '';
    }
}; 