import { useState, useEffect } from 'react';
import axios from './api';

function App() {
  const [entries, setEntries] = useState([]);
  const [url, setUrl] = useState('');
  const [username, setUsername] = useState('');
  const [passwort, setPasswort] = useState('');

  useEffect(() => {
    axios.get('/passwords')
      .then(res => setEntries(res.data));
  }, []);

  const addEntry = async () => {
    const res = await axios.post('/passwords', {
      url,
      username,
      password: passwort
    });
    setEntries([...entries, res.data]);
  };

  return (
    <div className="App">
      <h1>Passwort Safe</h1>
      <input placeholder="URL" value={url} onChange={e => setUrl(e.target.value)} />
      <input placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} />
      <input placeholder="Passwort" value={passwort} onChange={e => setPasswort(e.target.value)} />
      <button onClick={addEntry}>Hinzufügen</button>

      <ul>
        {entries.map(entry => (
          <li key={entry.id}>
            {entry.url} | {entry.username} | {entry.password}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
