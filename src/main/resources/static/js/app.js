const API_URL = "http://localhost:8082/api";

window.onload = function () {
  loadTasks();
};

function loadTasks() {
  fetch(API_URL + "/tasks", {
    method: "GET",
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  })
    .then((res) => res.json())
    // .then((data) => {
    //   const list = document.getElementById("taskList");

    //   data.forEach((task) => {
    //     const li = document.createElement("li");
    //     li.classList.add("list-group-item");
    //     li.innerText = task.title;
    //     list.append(li);
    //   });
    // })

    .then((data) => {
      console.log("PAGE RESPONSE:", data);

      const tasks = data.content; // 👈 Page structure

      const list = document.getElementById("taskList");
      list.innerHTML = "";

      tasks.forEach((task) => {
        const li = document.createElement("li");
        li.classList.add("list-group-item");
        li.innerText = task.title;
        list.appendChild(li);
      });
    });
}

function createTask() {
  const input = document.getElementById("taskInput");

  fetch(API_URL + "/tasks", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
    body: JSON.stringify({
      title: input.value,
    }),
  }).then(() => {
    input.value = "";
    loadTasks();
  });
}
