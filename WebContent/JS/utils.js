function makeCall(method, url, formElement, callBack, reset = true) {

    var request = new XMLHttpRequest(); // Visible by closure
    request.onreadystatechange = function() {

        callBack(request)

    }; // Closure

    request.open(method, url);
    if (formElement == null) {
        request.send();
    } else {
        request.send(new FormData(formElement));
    }
    if (formElement!==null && reset===true) {
        formElement.reset();
    }

}
