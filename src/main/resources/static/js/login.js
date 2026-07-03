const API_URL = "http://localhost:8082/api/auth/login";

function login() {
  console.log("LOGIN BUTTON CLICKED");
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username,
      password,
    }),
  })
    .then(async (response) => {
      const text = await response.text(); // 👈 SAFE FIRST

      if (!response.ok) {
        throw new Error(text || "Login failed");
      }

      return JSON.parse(text); // 👈 only parse if valid JSON
    })
    .then((data) => {
      console.log("LOGIN SUCCESS:", data);

      localStorage.setItem("token", data.token); // ⚠ FIX THIS TOO

      window.location.href = "../index.html";
    })
    .catch((err) => {
      console.error("LOGIN ERROR:", err);
      document.getElementById("error").innerHTML = "Login failed";
    });
}
