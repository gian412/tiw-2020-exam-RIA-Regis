(function() {

    document.getElementById("loginButton").addEventListener('click', (e)=> {

        // Get login form
        let form = e.target.closest("form");

        if (form.checkValidity()){ // If the form is valid, makeCall
            makeCall("POST", 'CheckLogin', e.target.closest("form"), 
                function(request) {

                    if (request.readyState == XMLHttpRequest.DONE){
                        let message = request.responseText;
                        let errorElements
                        switch (request.status) {
                            case 200:
                                sessionStorage.setItem('id', message);
                                window.location.href = "Home.html";
                                break;
                            case 400: // Bad request
                                switch (message) {
                                    case "Username and password cannot be empty":
                                        document.getElementById("loginErrorMessage").textContent = message;
                                        errorElements = document.getElementsByClassName("loginErrorMessage");
                                        errorElements.forEach(function (item) {
                                            item.style.display = 'block';
                                        });
                                        break;
                                    case "Username can't be empty":
                                        document.getElementById("usernameError").textContent = message;
                                        errorElements = document.getElementsByClassName("usernameError");
                                        errorElements.forEach(function (item) {
                                            item.style.display = 'block';
                                        });
                                        break;
                                    case "Password can't be empty":
                                        document.getElementById("passwordError").textContent = message;
                                        errorElements = document.getElementsByClassName("passwordError");
                                        errorElements.forEach(function (item) {
                                            item.style.display = 'block';
                                        });
                                        break;
                                }
                                break;
                            case 401: // Unauthorized
                                if (message === "Wrong username") {
                                    document.getElementById("usernameError").textContent = message;
                                    errorElements = document.getElementsByClassName("usernameError");
                                    errorElements.forEach(function (item) {
                                        item.style.display = 'block';
                                    });
                                } else {
                                    document.getElementById("passwordError").textContent = message;
                                    errorElements = document.getElementsByClassName("passwordError");
                                    errorElements.forEach(function (item) {
                                        item.style.display = 'block';
                                    });
                                }
                                break;
                            case 500: // Internal server error
                                document.getElementById("usernameError").textContent = message;
                                errorElements = document.getElementsByClassName("usernameError");
                                errorElements.forEach(function (item) {
                                    item.style.display = 'block';
                                });
                        }
                    }

                }
            );
        }

    });

})();
