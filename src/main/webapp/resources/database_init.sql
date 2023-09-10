CREATE DATABASE IF NOT EXISTS library;

USE library;

CREATE TABLE `members` (
  `member_ID` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `address` varchar(45) NOT NULL,
  `phone_number` varchar(11) DEFAULT NULL,
  `id_card_number` varchar(8) NOT NULL,
  `status` varchar(8) NOT NULL DEFAULT 'active',
  PRIMARY KEY (`member_ID`),
  UNIQUE KEY `id_card_number_UNIQUE` (`id_card_number`),
  UNIQUE KEY `phone_number_UNIQUE` (`phone_number`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `stock` (
  `stock_ID` int NOT NULL AUTO_INCREMENT,
  `author` varchar(100) DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `category` varchar(20) NOT NULL,
  `number_of_copies` int NOT NULL,
  PRIMARY KEY (`stock_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `library`.`borrows` (
  `borrow_ID` INT NOT NULL,
  `member_ID_FK` INT NOT NULL,
  `stock_ID_FK` INT NOT NULL,
  `date_of_borrow` DATE NULL,
  `due_date` DATE NULL,
  `date_of_return` DATE NULL,
  PRIMARY KEY (`borrow_ID`),
  INDEX `stock_ID_FK_idx` (`stock_ID_FK` ASC) VISIBLE,
  INDEX `member_ID_FK_idx` (`member_ID_FK` ASC) VISIBLE,
  CONSTRAINT `stock_ID_FK`
    FOREIGN KEY (`stock_ID_FK`)
    REFERENCES `library`.`stock` (`stock_ID`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `member_ID_FK`
    FOREIGN KEY (`member_ID_FK`)
    REFERENCES `library`.`members` (`member_ID`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);


INSERT INTO library.members VALUES(1, 'Dale Clayton', '1004 Jarvis Street, Cheektowage', '5169258931', '056189TA', 'active');
INSERT INTO library.members VALUES(2, 'Cole Robinson', '2056 Drummond Street, Newton', '8622664037', '782936JF', 'active');
INSERT INTO library.members VALUES(3, 'Joanna Watson', '2520 Oakmound Drive, Burr Ridge', '8478043885', '749270MA', 'active');
INSERT INTO library.members VALUES(4, 'Willa Norris', '617 Parkway Drive, Bisbee', '6026285991', '997134SJ', 'active');
INSERT INTO library.members VALUES(5, 'Jim Morton', '3010 Gandy Street, Maryland Heights', '8162130385', '772801KM', 'active');
INSERT INTO library.members VALUES(6, 'Thomas Matis', '4171  Spring Street, Taylorville', '2178234447', '722910UJ', 'active');
INSERT INTO library.members VALUES(7, 'Ed Hewitt', '3284 Southside Lane, Irvine', '9495963181', '888111HJ', 'active');
INSERT INTO library.members VALUES(8, 'Fiona Grant', '4048 Creek Street, Pittsburg', '5558092144', '836293JU', 'active');
INSERT INTO library.members VALUES(9, 'Aubrey Neal', '953 Harvest Lane, Los Angeles', '3238069673', '638455AB', 'active');
INSERT INTO library.members VALUES(10, 'Amanda Garrett', '1473 Neville Street, New Albany', '8124038850', '173229NH', 'active');
INSERT INTO library.members VALUES(11, 'Pierce Franklin', '2393 Providence Lane, Tripoli', '9205485014', '732935MU', 'active');
INSERT INTO library.members VALUES(12, 'Serena Cross', '2740 Smith Road, Walnut Creek', '9092161645', '378826IU', 'active');
INSERT INTO library.members VALUES(13, 'Oliver Lucas', '3841 Lake Road, Lexington', '5026100477', '172652JA', 'active');
INSERT INTO library.members VALUES(14, 'Davy Quinn', '1252 Mayo Street, Georgetown', '5023169178', '146558BA', 'active');
INSERT INTO library.members VALUES(15, 'Morton Young', '2143 Colony Street, Bloomfield', '2032066292', '345092AT', 'active');


INSERT INTO library.stock VALUES(1, 'Pierce Brown', 'Dark Age', 'book', 10);
INSERT INTO library.stock VALUES(2, 'JR Ward', 'The Chosen', 'book', 6);
INSERT INTO library.stock VALUES(3, 'Christopher Nolan', 'The Dark Knight', 'movie', 3);
INSERT INTO library.stock VALUES(4, 'Stephen King', 'Sleep Doctor', 'book', 4);
INSERT INTO library.stock(stock_ID, title, category, number_of_copies) VALUES(5, 'Forbes', 'magazine', 1);
INSERT INTO library.stock(stock_ID, title, category, number_of_copies) VALUES(6, 'MS Office 2016', 'software', 3);
INSERT INTO library.stock VALUES(7, 'Andrzej Sapkowski', 'Season of Storms', 'book', 4);
INSERT INTO library.stock VALUES(8, 'Andy Weir', 'The Martian', 'book', 12);
INSERT INTO library.stock(stock_ID, title, category, number_of_copies) VALUES(9, 'PTC Creo', 'software', 10);
INSERT INTO library.stock(stock_ID, title, category, number_of_copies) VALUES(10, 'GameStar', 'magazine', 3);
INSERT INTO library.stock VALUES(11, 'Rick Riordan', 'The Lightning Thief', 'book', 7);
INSERT INTO library.stock VALUES(12, 'JK Rowling', 'Harry Potter and the Goblet of Fire', 'book', 21);
INSERT INTO library.stock VALUES(13, 'George RR Martin', 'The Winds of Winter', 'book', 7);


INSERT INTO library.borrows VALUES(1, 4, 2, '2020-01-03', '2020-02-03', '2020-02-09');
INSERT INTO library.borrows VALUES(2, 10, 7, '2020-02-14', '2020-03-14', '2020-04-01');
INSERT INTO library.borrows VALUES(3, 7, 1, '2020-04-09', '2020-05-09', '2020-04-14');
INSERT INTO library.borrows VALUES(4, 6, 9, '2020-01-30', '2020-02-28', '2020-03-08');
INSERT INTO library.borrows(borrow_ID, member_ID_FK, stock_ID_FK, date_of_borrow, due_date) VALUES(5, 14, 8, '2020-04-12', '2020-05-12');
INSERT INTO library.borrows(borrow_ID, member_ID_FK, stock_ID_FK, date_of_borrow, due_date) VALUES(6, 1, 12, '2020-04-11', '2020-05-11');
INSERT INTO library.borrows(borrow_ID, member_ID_FK, stock_ID_FK, date_of_borrow, due_date) VALUES(7, 4, 4, '2020-03-27', '2020-04-27');