(function() {

    document.getElementById("loginButton").addEventListener('click', (e)=> {

        // Get login form
        let form = e.target.closest("form");

        if (form.checkValidity()){ // If the form is valid, makeCall
            makeCall("POST", 'CheckLogin', e.target.closest("form"), 
                function(request) {

                    if (request.readyState === XMLHttpRequest.DONE){
                        let message = request.responseText;
                        let errorElements
                        switch (request.status) {
                            case 200:
                                sessionStorage.setItem('id', message);
                                window.location.href = "home.html";
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
                                document.getElementById("loginErrorMessage").textContent = message;
                                errorElements = document.getElementsByClassName("loginErrorMessage");
                                errorElements.forEach(function (item) {
                                    item.style.display = 'block';
                                });
                        }
                    }
                    
                }
            );
        } else {
            form.reportValidity();
        }

    });

    document.getElementById("signUpButton").addEventListener('click', (e)=>{

        let pwd1 = document.getElementById('sign-up-password1');
        let pwd2 = document.getElementById('sign-up-password2');

        if (pwd1.value !== pwd2.value) {
            pwd1.style.borderColor = '#ff0000';
            pwd2.style.borderColor = '#ff0000';

        } else {
            pwd1.style.borderColor = '#cccccc';
            pwd2.style.borderColor = '#cccccc';
            let signUpButton = document.getElementById('signUpButton');

            // Get sign up form
            let form = e.target.closest("form");

            if (form.checkValidity()){ // If the form is valid, makeCall
                makeCall("POST", 'CreateUser', e.target.closest("form"),
                    function(request) {

                        if (request.readyState === XMLHttpRequest.DONE) {
                            // Reset errors
                            let errorElements;
                            errorElements = document.getElementsByClassName("error");
                            errorElements.forEach(function (item) {
                                item.style.display = 'none';
                            });

                            let message = request.responseText;
                            switch (request.status) {
                                case 200:
                                    sessionStorage.setItem('id', message);
                                    window.location.href = "home.html";
                                    break;
                                case 400: // Bad request
                                    switch (message) {
                                        case "All fields must be filled":
                                            document.getElementById("signUpErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("signUpErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                        case "First name can't be empty":
                                            document.getElementById("fnErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("fnErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                        case "Last name can't be empty":
                                            document.getElementById("lnErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("lnErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                        case "Username can't be empty":
                                            document.getElementById("unErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("unErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                        case "Username already in use, choose another one":
                                            document.getElementById("unErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("unErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                        case "Email can't be empty":
                                            document.getElementById("emErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("emErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                        case "Email not valid":
                                            document.getElementById("emErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("emErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                        case "Email already associated to another account, try another email":
                                            document.getElementById("emErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("emErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                        case "Password can't be empty":
                                            document.getElementById("pwErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("pwErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                        case "Second password can't be empty":
                                            document.getElementById("spwErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("spwErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                        case "Passwords must be equals":
                                            document.getElementById("spwErrorMessage").textContent = message;
                                            errorElements = document.getElementsByClassName("spwErrorMessage");
                                            errorElements.forEach(function (item) {
                                                item.style.display = 'block';
                                            });
                                            break;
                                    }
                                    break;
                                case 500: // Internal server error
                                    document.getElementById("signUpErrorMessage").textContent = message;
                                    errorElements = document.getElementsByClassName("signUpErrorMessage");
                                    errorElements.forEach(function (item) {
                                        item.style.display = 'block';
                                    });
                                    break;
                            }
                        }

                    }
                );
            } else {
                form.reportValidity();
            }

        }


    });

})();
