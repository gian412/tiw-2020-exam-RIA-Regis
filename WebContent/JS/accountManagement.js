(function () {

    // Page components
    let userMessage, accountList, accountStatus, accountDetails, outgoingList, incomingList, transferSuccessful, transferError, dropDownMenu, autoClickAfterSuccess = false, pageOrchestrator = new PageOrchestrator();

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
            let that =  this;
            makeCall("GET", "GetAccounts", null, function (request) {
                if (request.readyState === 4) {
                    let message = request.responseText;
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
                    anchor.setAttribute("accountid", account.id); // Add account ID as attribute to anchor
                    anchor.addEventListener('click', (e) => { // Add event on anchor click
                        accountStatus.show(e.target.getAttribute("accountid"));
                    }, false);
                    anchor.href = "#"; // Add fake href
                    row.appendChild(linkCell); // Add link cell to row

                    // Row
                    that.listBody.appendChild(row); // Add row to table body
                });
                this.listContainer.style.visibility = "visible"; // Set div visible
            }
        };

        this.reset = function () {
            this.listContainer.style.visibility = "hidden";
            this.error.textContent = "";
        };

        this.autoClick = function (accountId) {
            let e = new Event('click');
            let selector = "a[accountid='" + accountId + "']";
            let anchorToClick = (accountId) ? document.querySelector(selector) : this.listBody.querySelectorAll("a")[0];
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
        this.transferSuccess = options['transferSuccess'];
        this.transferError = options['transferError'];

        this.registerEvent = function(orchestrator) {
            this.transferForm.querySelector("input[type='button']").addEventListener('click', (e) => {
                let form = e.target.closest("form");
                if (form.checkValidity()) {
                    let that = this, originAccount = form.querySelector("input[type='hidden']").value;
                    makeCall("POST", "MakeTransfer", form, function (request) {
                        if (request.readyState === XMLHttpRequest.DONE) {
                            let message = request.responseText;
                            switch (request.status) {
                                case 200: // Ok
                                    that.transferSuccess.update(message); // TODO: transferSuccessful
                                    autoClickAfterSuccess = true;
                                    that.transferForm.style.visibility = "hidden";
                                    dropDownMenu.update();
                                    orchestrator.refresh(originAccount);
                                    break;
                                case 400:
                                    switch (message) {
                                        case "Parameters can't be empty":
                                        case "Origin Account ID can't be empty":
                                        case "Origin Account ID must be an integer":
                                        case "Destination User ID can't be empty":
                                        case "Destination User ID must be an integer":
                                        case "Destination Account ID can't be empty":
                                        case "Destination Account ID must be an integer":
                                        case "Causal can't be empty":
                                        case "Causal can't be greater than 1024 characters":
                                        case "Amount can't be empty":
                                        case "Amount must be a double":
                                        case "Amount must be greater than 0":
                                            that.transferFormError.textContent = message;
                                            break;
                                        default :
                                            that.transferError.update(message);// TODO: transferError
                                            that.transferForm.style.visibility = "hidden";
                                            break;
                                    }
                                    break;
                                case 401:
                                    that.transferError.update(message);// TODO: transferError
                                    that.transferForm.style.visibility = "hidden";
                                    break;
                                case 500:
                                    that.transferFormError.textContent = message;
                                    break;
                            }
                        }
                    });
                } else {
                    form.reportValidity();
                }
            });
        };

        this.show = function(accountId) {
            let that = this;
            makeCall("GET", "AccountDetails?accountId=" + accountId, null,
                function(request) {
                    if (request.readyState === XMLHttpRequest.DONE) {
                        let message = request.responseText;
                        if (request.status === 200) { // Ok
                            let account = JSON.parse(message);
                            that.update(account);
                        } else { // BadRequest and InternalServerError
                            that.accountDetailsError.textContent = message;
                        }
                    }
                }
            );
            makeCall("GET", "OutgoingTransfers?accountId=" + accountId, null,
                function(request) {
                    if (request.readyState === XMLHttpRequest.DONE) {
                        let message = request.responseText;
                        if (request.status === 200) { // Ok
                            let transfers = JSON.parse(message);
                            that.outgoingTransfers.update(transfers);
                        } else { // Bad request, Unauthorized and InternalServerError
                            that.outgoingMessage.textContent = message;
                        }
                    }
                }
            );
            makeCall("GET", "IncomingTransfers?accountId=" + accountId, null,
                function(request) {
                    if (request.readyState === XMLHttpRequest.DONE) {
                        let message = request.responseText;
                        if (request.status === 200) { // Ok
                            let transfers = JSON.parse(message);
                            that.incomingTransfers.update(transfers);
                        } else { // Bad request, Unauthorized and InternalServerError
                            that.incomingMessage.textContent = message;
                        }
                    }
                }
            );
            this.transferForm.reset();
            this.transferFormError.textContent = "";
            this.transferError.reset();
            if (!autoClickAfterSuccess) {
                this.transferForm.style.visibility = "visible";
                this.transferSuccess.reset();
            } else {
                autoClickAfterSuccess = !autoClickAfterSuccess;
            }
        };

        this.reset = function() {
            this.accountDetails.reset();
            this.outgoingTransfers.reset(true);
            this.incomingTransfers.reset(false);
            this.transferForm.style.visibility = "hidden";
            this.accountDetailsError.textContent = "";
            this.outgoingMessage.textContent = "";
            this.incomingMessage.textContent = "";
            this.transferFormError.textContent = "";
        };

        this.update = function(account) {
            this.accountDetails.update(account);
            this.outgoingTransfers.update();
            this.incomingTransfers.update();
            this.transferForm.reset();
            this.transferForm.style.visibility = "visible";
            this.transferSuccess.reset();
            this.transferError.reset();
        };
    }

    // Element that controls account details' table
    function AccountDetails(_accountDetailsId, _accountDetailsBalance, _accountDetailsContainer) {

        this.accountId = _accountDetailsId;
        this.accountDetailsBalance = _accountDetailsBalance;
        this.accountDetailsContainer = _accountDetailsContainer;
        
        this.update = function(account) {
            this.accountId.textContent = account.id;
            this.accountDetailsBalance.textContent = account.balance;
            this.accountDetailsContainer.style.visibility = "visible";
            // Set origin account in the form
            let hiddenOrigin = document.getElementById("originAccountId");
            hiddenOrigin.setAttribute("value", account.id);
        };

        this.reset = function() {
            this.accountDetailsContainer.style.visibility = "hidden";
        };

    }

    // Element that control the outgoing transfer's list
    function TransferList(_transferContainer, _transferBody, _error, _outgoing) {

        this.transfersContainer = _transferContainer;
        this.transfersBody = _transferBody;
        this.error = _error;
        this.outgoing = _outgoing;

        this.update = function(arrayTransfers) {
            let length = arrayTransfers.length, row, causalCell, amountCell, dateCell, otherAccountCell;
            this.transfersBody.innerHTML = ""; // Empty table body
            this.error.textContent = "";
            if (length === 0) {
               if (this.outgoing) {
                   this.error.textContent = "No outgoing transfer yet";
               } else {
                   this.error.textContent = "No incoming transfer yet";
               }
           } else {
                let that = this;
               arrayTransfers.forEach(function (transfer) {
                   // Create row
                   row = document.createElement("tr");

                   causalCell = document.createElement("td"); // Create Causal cell
                   causalCell.textContent = transfer.causal; // Fill Causal cell
                   causalCell.setAttribute("class", (that.outgoing) ? "outgoing" : "incoming");
                   row.appendChild(causalCell); // Append Causal cell to row

                   amountCell = document.createElement("td"); // Create Amount cell
                   amountCell.textContent = transfer.amount; // Fill Amount cell
                   amountCell.setAttribute("class", (that.outgoing) ? "outgoing" : "incoming");
                   row.appendChild(amountCell); // Append Amount cell to row

                   dateCell = document.createElement("td"); // Create Date cell
                   dateCell.textContent = transfer.date; // Fill Date cell
                   dateCell.setAttribute("class", (that.outgoing) ? "outgoing" : "incoming");
                   row.appendChild(dateCell); // Append Date cell to row

                   otherAccountCell = document.createElement("td"); // Create Origin/Destination Account cell
                   otherAccountCell.textContent = (that.outgoing) ? transfer.destinationAccount : transfer.originAccount; // Fill Origin/Destination Account cell
                   otherAccountCell.setAttribute("class", (that.outgoing) ? "outgoing" : "incoming");
                   row.appendChild(otherAccountCell); // Append Origin/Destination Account to row

                   that.transfersBody.appendChild(row);
               });
               that.transfersContainer.style.visibility = "visible";
           }
        };

        this.reset = function(outgoing) {
            let container
            if (outgoing) {
                container = document.getElementById("outgoingContainer");
            } else {
                container = document.getElementById("incomingContainer");
            }
            container.style.visibility = "hidden";
        };
    }

    function TransferSuccessful(_successMessage) {

        this.successMessage = _successMessage;

        this.update = function(message) {
            this.successMessage.textContent = message;
        };

        this.reset = function () {
            this.successMessage.textContent = "";
        };

    }

    function TransferError(_errorMessage) {

        this.errorMessage = _errorMessage;

        this.update = function (message) {
            this.errorMessage.textContent = message;
        };

        this.reset = function () {
            this.errorMessage.textContent = "";
        };
    }

    function DropDownMenu(_dropDownBody) {

        this.dropDownBody = _dropDownBody;

        this.arrayAddress = [];

        this.update = function() {
            let that = this;
            makeCall("GET", "GetAddresses", null, function (request) {
                if (request.readyState === XMLHttpRequest.DONE) {
                    let message = request.responseText;
                    if (request.status === 200) {
                        let addresses = JSON.parse(request.responseText);
                        that.arrayAddress = addresses;
                        that.populate(addresses);
                    } else {
                        that.setMessage(message, true);
                    }
                }
            });
        }

        this.populate = function (arrayAddress) {

            let length = arrayAddress.length, anchor;
            if (length === 0) {
                this.setMessage("No address yet", false);
            } else {
                this.dropDownBody.innerHTML = "";
                let that = this
                arrayAddress.forEach(function (address) {
                    anchor = document.createElement("a");
                    anchor.textContent = address.identifier;
                    anchor.setAttribute("user", address.user);
                    anchor.setAttribute("account", address.account);
                    anchor.addEventListener('click', (e) => {
                        let target, identifier, userId, accountId;
                        // Get anchor from the event
                        target = e.target;
                        // Get elements from html page
                        identifier = document.getElementById("identifier");
                        userId = document.getElementById("userId");
                        accountId = document.getElementById("accountId");
                        // Fill elements with anchor parameters
                        identifier.setAttribute("value", target.textContent);
                        userId.setAttribute("value", target.getAttribute("user"));
                        accountId.setAttribute("value", target.getAttribute("account"));

                    }, false);
                    anchor.href = "#";
                    that.dropDownBody.appendChild(anchor);
                });
            }


        }

        this.setMessage = function (errorMessage, error) {

            let anchor;

            this.dropDownBody.innerHTML = ""; // Empty the list of addresses
            anchor = document.createElement("a");
            anchor.textContent = errorMessage;
            anchor.href = "#";
            if (error) {
                anchor.className = "error";
            }
            this.dropDownBody.appendChild(anchor);
        }

        this.registerEvent = function(_textField) {
            let textFiled = _textField;
            let that = this;
            textFiled.addEventListener('input', (e) => {
                let string, newArrayAddress = [];
                string = e.target.value;
                that.arrayAddress.forEach(function (address) {
                    if (string===address.identifier.substring(0, string.length)) {
                        newArrayAddress.push(address);
                    }
                });
                that.populate(newArrayAddress);


            });
        }

    }

    // Element that control the flow 0of the entire page
    function PageOrchestrator() {

        this.start = function () {

            // Initialize and show user id message in title
            userMessage = new UserMessage(sessionStorage.getItem("userId"), document.getElementById("user_id"));
            userMessage.show();

            dropDownMenu = new DropDownMenu(document.getElementById("dropdown-content"));

            dropDownMenu.registerEvent(document.getElementById("identifier"));

            // Initialize account list component
            accountList = new AccountList(
                document.getElementById("accountEmpty"),
                document.getElementById("accountContainer"),
                document.getElementById("accountBody")
            );



            // Initialize outgoing list component
            outgoingList = new TransferList(
                document.getElementById("outgoingContainer"),
                document.getElementById("outgoingBody"),
                document.getElementById("outgoingTransfersError"),
                true
            );

            // Initialize incoming list component
            incomingList = new TransferList(
                document.getElementById("incomingContainer"),
                document.getElementById("incomingBody"),
                document.getElementById("incomingTransfersError"),
                false
            );

            // Initialize transfer successful component
            transferSuccessful = new TransferSuccessful(document.getElementById("transferSuccessfulMessage"));

            // Initialize transfer error component
            transferError = new TransferError(document.getElementById("transferErrorMessage"));

            // Initialize new transfer form component
            accountDetails = new AccountDetails(
                document.getElementById("accountDetailsId"),
                document.getElementById("accountDetailsBalance"),
                document.getElementById("accountDetailsContainer")
            );

            // Initialize transfers lists component
            accountStatus = new AccountStatus({
                accountDetails: accountDetails,
                accountDetailsError: document.getElementById("accountDetailsError"),
                outgoingTransfers: outgoingList,
                outgoingMessage: document.getElementById("outgoingTransfersError"),
                incomingTransfers: incomingList,
                incomingMessage: document.getElementById("incomingTransfersError"),
                transferForm: document.getElementById("transferForm"),
                transferFormError: document.getElementById("transferFormError"),
                transferSuccess: transferSuccessful,
                transferError: transferError
            });

            // Register event for form component
            accountStatus.registerEvent(this);

        };

        this.refresh = function (currentAccount) {
            accountList.reset();
            accountStatus.reset();
            accountList.show(function () {
                accountList.autoClick(currentAccount);
            });
            dropDownMenu.update();
        };
    }

})();
