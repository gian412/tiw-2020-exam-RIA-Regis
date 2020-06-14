# tiw-2020-exam-Regis

### RIA version project for Web Technologies course's exam





## Structural Design

---

### Data Structure

##### Schema creation:

```sql
CREATE SCHEMA `money_transfer` ;
```

##### Users table creation:

```sql
CREATE TABLE `money_transfer`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `firstname` VARCHAR(45) NOT NULL,
  `lastname` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));
```

##### Accounts table creation:

```sql
CREATE TABLE `money_transfer`.`account` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `balance` INT NOT NULL DEFAULT 0,
  `owner` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ownership_idx` (`owner` ASC) VISIBLE,
  CONSTRAINT `ownership`
    FOREIGN KEY (`owner`)
    REFERENCES `money_transfer`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
```

##### Transfers table creation:

```sql
CREATE TABLE `money_transfer`.`transfer` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` INT NOT NULL,
  `origin` INT NOT NULL,
  `destination` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `origin_of_transfer_idx` (`origin` ASC) VISIBLE,
  INDEX `destination_of_transfer_idx` (`destination` ASC) VISIBLE,
  CONSTRAINT `origin_of_transfer`
    FOREIGN KEY (`origin`)
    REFERENCES `money_transfer`.`account` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `destination_of_transfer`
    FOREIGN KEY (`destination`)
    REFERENCES `money_transfer`.`account` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
```

---

---

## Components

- ##### Model Objects (Bean)
  
  - User
  
  - Account
  
  - Transfer

- ##### Data Access Object (DAO)
  
  - AnonymousUserDAO
  
  - UserDAO
  
  - AccountDAO
  
  - TransferDAO
