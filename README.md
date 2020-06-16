# Structural Design

***

## Data Structure

##### Schema creation:

```sql
CREATE SCHEMA `money_transfer`;
```

##### 

##### Users table creation:

```sql
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `firstname` varchar(45) NOT NULL,
  `lastname` varchar(45) NOT NULL,
  `username` varchar(64) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
);
```

##### 

##### Accounts table creation:

```sql
CREATE TABLE `account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `balance` decimal(9,2) unsigned NOT NULL DEFAULT ''0.00'',
  `owner` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ownership_idx` (`owner`),
  CONSTRAINT `ownership` 
    FOREIGN KEY (`owner`) 
    REFERENCES `user` (`id`) 
    ON DELETE CASCADE
    ON UPDATE NO ACTION
);
```

##### 

##### Transfers table creation:

```sql
CREATE TABLE `transfer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` decimal(9,2) unsigned NOT NULL,
  `origin` int NOT NULL,
  `destination` int NOT NULL,
  `causal` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `origin_of_transfer_idx` (`origin`),
  KEY `destination_of_transfer_idx` (`destination`),
  CONSTRAINT `destination_of_transfer` 
    FOREIGN KEY (`destination`) 
    REFERENCES `account` (`id`) 
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `origin_of_transfer` 
   FOREIGN KEY (`origin`) 
   REFERENCES `account` (`id`) 
   ON DELETE CASCADE
   ON UPDATE NO ACTION
);
```

##### 

##### Address table creation:

```sql
CREATE TABLE `address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `owner` int NOT NULL,
  `user` int NOT NULL,
  `account` int NOT NULL,
  `identifier` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ownership_idx` (`owner`),
  KEY `savedcontact_idx` (`user`),
  KEY `saved_account_idx` (`account`),
  CONSTRAINT `address_owner` 
    FOREIGN KEY (`owner`) 
    REFERENCES `user` (`id`) 
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `saved_account` 
    FOREIGN KEY (`account`) 
    REFERENCES `account` (`id`) 
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `saved_user` 
    FOREIGN KEY (`user`) 
    REFERENCES `user` (`id`) 
    ON DELETE CASCADE
    ON UPDATE NO ACTION
);
```

***

***

## Components

- ##### Model Objects (Beans)
  
  - User
  
  - Account
  
  - Transfer
  
  - Address

- ##### Data Access Objects (Classes)
  
  - AnonymousUserDAO
  
  - UserDAO
  
  - AccountDAO
  
  - TransferDAO
  
  - AddressDAO

- ##### Controllers (Servlet)
  
  - CheckLogin
  
  - CreateUser
  
  - GetAccounts
  
  - GetAccountDetails
  
  - GetIncomingTransfers
  
  - GetOutgoingTransfers
  
  - GetAddresses
  
  - MakeTransfer
  
  - Logout

- ##### Views (Templates) & component
  
  - welcome
  
  - home