(function () {

    // Page components
    var userMessage, accountList, accountStatus, accountDetails, outgoingList, incomingList, newTransfer, pageOrchestrator = new PageOrchestrator();

    // Event on window load
    window.addEventListener('load', () => {
        pageOrchestrator.start(); // Initialize components
        pageOrchestrator.refresh() // Show initial components
    }, false);

    // User id message in title
    function UserMessage(_userId, idContainer) {
        this.userId = _userId;
        this.show = function() {
            idContainer.textContent = this.userId;
        }
    }

    // List of user's account
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
            let length = arrayAccount.length, row, idCell, balanceCell, linkCell, anchor, linkText;
            if (length === 0) {
                this.error.textContent = "No account yet";
            } else {
                this.listBody.innerHTML = ""; // Empty the table body
                // Build updated list
                let that = this;
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
                        accountStatus.show(e.target.getAttribute("accountId"));
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

        this.autoclick = function (accountId) {
            let e = new Event('click');
            let selector = "a[accountId='" + accountId + "']";
            let anchorToClick = (accountId) ? document.querySelector(selector) : this.listBody.querySelector("a")[0];
            anchorToClick.dispatchEvent(e);
        };
    }

    // Element that control incoming and outgoing transfers's lists and new
    // transfer's form component
    function AccountStatus(options) {
        this.accountDetails = options['accountDetails'];
        this.accountDetailsError = options['accountDetailsError'];
        this.outgoingTransfers = options['outgoingTransfers'];
        this.outgoingMessage = options['outgoingMessage'];
        this.incomingTransfers = options['incomingTransfers'];
        this.incomingMessage = options['incomingMessage'];
        this.transferForm = options['transferForm'];
        this.transferFormError = options['transferFormError'];

        this.registerEvent = function(orchestrator) {
            this.transferForm.querySelector("input[type='button']").addEventListener('click', (e) => {
                var form = e.target.closest("form");
                if (form.checkValidity()) {
                    var that = this, originAccount = form.querySelector("input[type='hidden']").value;
                    makeCall("POST", "MakeTransfer", form, function (request) {
                        if (request.readyState === XMLHttpRequest.DONE) {
                            var message = request.responseText;
                            if (request.status === 200) { // Ok
                                orchestrator.refresh(originAccount);
                            } else {
                                that.transferFormError.textContent = message; // TODO: divide in different error
                            }
                        }
                    });
                } else {
                    form.reportValidity();
                }
            });
        };

        this.show = function(accountId) {
            var self = this;
            makeCall("GET", "AccountDetails?accountId=" + accountId, null, 
                function(request) {
                    if (request.readyState === XMLHttpRequest.DONE) {
                        var message = request.responseText;
                        if (request.status === 200) { // Ok
                            var account = JSON.parse(message);
                            self.accountDetails.update(account);
                        } else { // BadRequest and InternalServerError
                            self.accountDetailsError.textContent = message;
                        }
                    }
                }
            );
            makeCall("GET", "OutgoingTransfers?accountId=" + accountId, null,
                function(request) {
                    if (request.readyState === XMLHttpRequest.DONE) {
                        var message = request.responseText;
                        if (request.status === 200) { // Ok
                            var transfers = JSON.parse(message);
                            self.outgoingTransfers.update(transfers);
                        } else { // Bad request, Unauthorized and InternalServerError
                            self.outgoingMessage.textContent = message;
                        }
                    }
                }
            );
            makeCall("GET", "IncomingTransfers?accountId=" + accountId, null,
                function(request) {
                    if (request.readyState === XMLHttpRequest.DONE) {
                        var message = request.responseText;
                        if (request.status === 200) { // Ok
                            var transfers = JSON.parse(message);
                            self.incomingTransfers.update(transfers);
                        } else { // Bad request, Unauthorized and InternalServerError
                            self.incomingMessage.textContent = message;
                        }
                    }
                }
            );
        };

        this.reset = function() {
            this.accountDetails.reset();
            this.outgoingTransfers.reset();
            this.incomingTransfers.reset();
            this.newTransferForm.reset();
        };
    }

    // Element that controls account details' table
    function AccountDetails() {
        
        this.update = function(account) {
            // TODO: put account in account details table
        };

        this.reset = function() {
            // TODO: this.element.style.visibility = "hidden";
        };

    }

    // Element that control the outgoing transfer's list
    function TransferList() {

        this.update = function(transfers) {
            // TODO: put transfers in transfers table
        };

        this.reset = function() {
            // TODO: this.element.style.visibility = "hidden";
        };
    }

    // Element that control the from used to create a new transfer
    function NewTransfer() {

        this.reset = function() {
            // TODO: this.element.style.visibility = "hidden";
        };
    }

    // Element that control the flow 0of the entire page
    function PageOrchestrator() {

        this.start = function () {

            // Initialize and show user id message in title
            userMessage = new UserMessage(sessionStorage.getItem("userId"), document.getElementById("user_id"));
            userMessage.show();

            // Initialize account list component
            accountList = new AccountList(
                document.getElementById("accountEmpty"),
                document.getElementById("accountContainer"),
                document.getElementById("accountBody")
            );

            // Initialize outgoing list component
            outgoingList = new TransferList();

            // Initialize incoming list component
            incomingList = new TransferList();

            // Initialize new transfer form component 
            newTransfer = new NewTransfer();

            accountDetails = new AccountDetails();
            // Initialize transfers lists component
            accountStatus = new AccountStatus({
                accountDetails: accountDetails,
                accountDetailsError: document.getElementById("accountDetailsError"),
                outgoingTransfers: outgoingList,
                outgoingMessage: document.getElementById("outgoingTransfersError"),
                incomingTransfers: incomingList,
                incomingMessage: document.getElementById("incomingTransfersError"),
                transferForm: document.getElementById("transferForm")
            });

        };

        this.refresh = function (currentAccount) {
            accountList.reset();
            accountStatus.reset();
            accountList.show(function () {
                accountList.autoclick(currentAccount);
            });
        };
    }

})();
