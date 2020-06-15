(function () {

    // Page components
    var accountList, outgoingList, incomingList, newTransfer, pageOrchestrator = new PageOrchestrator();

    // Event on window load
    window.addEventListener('load', () => {
        pageOrchestrator.start(); // Initialize components
        pageOrchestrator.refresh() // Show initial components
    }, false);

    // Constructors of view components
    function AccountList(_error, _listContainer, _listBody ) {

        this.error = _error;
        this.listContainer = _listContainer;
        this.listBody = _listBody;

        this.show = function (next) {
            var self =  this;
            makeCall("GET", "GetAccounts", null, function (request) {
                if (request.readyState === 4) {
                    var message = request.responseText;
                    if (request.status === 200) { // Ok
                        self.update(JSON.parse(message)) // Update view with accounts
                        if (next) next();
                    } else {
                        self.error.textContent = message;
                    }
                }
            });
        };

        this.update = function () {
            // TODO: fill with received json values
        };

        this.reset = function () {
            this.listContainer.style.visibility = "hidden";
        };
    }

    function PageOrchestrator() {

        this.start = function () {
            accountList = new AccountList(
                document.getElementById("accountEmpty"),
                document.getElementById("accountContainer"),
                document.getElementById("accountBody")
            );
        };

        this.refresh = function () {

        };
    }

})();