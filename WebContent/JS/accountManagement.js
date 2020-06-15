(function () {

    // Page components
    var accountList, accountStatus, outgoingList, incomingList, newTransfer, pageOrchestrator = new PageOrchestrator();

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
            var that =  this;
            makeCall("GET", "GetAccounts", null, function (request) {
                if (request.readyState === 4) {
                    var message = request.responseText;
                    if (request.status === 200) { // Ok
                        that.update(JSON.parse(message)) // Update view with accounts
                        if (next) next();
                    } else {
                        that.error.textContent = message;
                    }
                }
            });
        };

        this.update = function (arrayAccount) {
            // TODO: fill with received json values
            var length = arrayAccount.length, element, i, row, idCell, balanceCell, linkCell, anchor, linkText;
            if (length == 0) {
                this.error.textContent = "No account yet";
            } else {
                this.listBody.innerHTML = ""; // Empty the table body
                // Build updated list
                var that = this;
                arrayAccount.forEach(function (account) {
                    row = document.createElement("tr"); // Create row

                    // ID cell
                    idCell = document.createElement("td"); // Create ID cell
                    idCell.textContent = account.id; // Populate ID cell
                    row.appendChild(idCell); // Add ID cell to the row

                    // Balance cell
                    balanceCell = document.createElement("td"); // Create Balance cell
                    balanceCell.textContent = account.balance; // Populate Balance cell
                    row.appendChild(balanceCell); // Add Balance cell to row

                    // Link cell
                    linkCell = document.createElement("td"); // Create link cell
                    anchor = document.createElement("a"); // Create anchor element
                    linkCell.appendChild(anchor); // Add anchor to link cell
                    linkText = document.createTextNode("Account status"); // Create text for anchor
                    anchor.appendChild(linkText); // Add text to anchor
                    anchor.setAttribute('accountId', account.id); // Add account ID as attribute to anchor
                    anchor.addEventListener('click', (e) => { // Add event on anchor click
                        // TODO: accountStatus.show(e.target.getAttribute("accountId"));
                    }, false);
                    anchor.href = "#"; // Add fake href
                    row.appendChild(linkCell); // Add link cell to row

                    // Row
                    self.listBody.appendChild(row); // Add row to table body
                });
                this.listContainer.style.visibility = "visible"; // Set div visible
            }
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
