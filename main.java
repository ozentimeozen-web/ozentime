// Elements
const loginBtn = document.getElementById('login-btn');
const adminPass = document.getElementById('admin-pass');
const userInfo = document.getElementById('user-info');
const chatBox = document.getElementById('chat-box');
const input = document.getElementById('chat-input');
const sendBtn = document.getElementById('send-btn');
const groupSelect = document.getElementById('group-select');
const createGroupBtn = document.getElementById('create-group-btn');
const fileContent = document.getElementById('file-content');

let currentUser = null;
let currentGroup = "general";
let isAdmin = false;

// Fake login (no Firebase)
loginBtn.addEventListener('click', () => {
  const email = prompt("Enter your email:");
  if (email) {
    currentUser = { email };
    userInfo.textContent = "Logged in as: " + currentUser.email;
    if (adminPass.value === "ozentime1239") {
      isAdmin = true;
      userInfo.innerHTML += " <span class='admin'>(Admin)</span>";
    }
    loadMessages();
  }
});

// Create new group
createGroupBtn.addEventListener('click', () => {
  const newGroup = prompt("Enter new group name:");
  if (newGroup) {
    const option = document.createElement("option");
    option.value = newGroup;
    option.textContent = newGroup;
    groupSelect.appendChild(option);
    groupSelect.value = newGroup;
    currentGroup = newGroup;
    chatBox.innerHTML = "";
    loadMessages();
  }
});

// Send message (stored in LocalStorage)
sendBtn.addEventListener('click', () => {
  if (!currentUser) { alert("Login first!"); return; }
  const msg = input.value.trim();
  if (msg !== '') {
    const now = new Date().toLocaleString();
    const stored = JSON.parse(localStorage.getItem(currentGroup)) || [];
    stored.push({ text: msg, sender: currentUser.email, timestamp: now, admin: isAdmin });
    localStorage.setItem(currentGroup, JSON.stringify(stored));
    input.value = '';
    showMessage(stored[stored.length - 1]);
  }
});

// Load messages from LocalStorage
function loadMessages() {
  chatBox.innerHTML = "";
  const stored = JSON.parse(localStorage.getItem(currentGroup)) || [];
  stored.forEach(showMessage);
}

// Show a single message
function showMessage(data) {
  const messageDiv = document.createElement('div');
  messageDiv.className = 'message';
  let sender = data.sender;
  if (data.admin) sender = "<span class='admin'>" + sender + "</span>";
  if (data.text.startsWith("```") && data.text.endsWith("```")) {
    const code = data.text.slice(3, -3);
    messageDiv.innerHTML = `<strong>${sender}:</strong><pre><code class="language-javascript">${code}</code></pre>
      <div class="timestamp">${data.timestamp}</div>`;
    Prism.highlightAll();
  } else {
    messageDiv.innerHTML = `<strong>${sender}:</strong> ${data.text}
      <div class="timestamp">${data.timestamp}</div>`;
  }
  chatBox.appendChild(messageDiv);
}

// Fetch and show main.java file
async function loadJavaFile() {
  try {
    const response = await fetch("main.java"); // root file
    if (!response.ok) throw new Error("File not found");
    const text = await response.text();
    fileContent.innerHTML = `<pre><code class="language-java">${text}</code></pre>`;
    Prism.highlightAll();
  } catch (err) {
    fileContent.innerHTML = "<p style='color:red'>Could not load main.java</p>";
  }
}

// Load file on page start
loadJavaFile();
