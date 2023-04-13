CREATE DATABASE  IF NOT EXISTS `market` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `market`;
-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: market
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `ID` int NOT NULL,
  `Name` varchar(30) NOT NULL,
  `Phone` varchar(15) DEFAULT NULL,
  `Address` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Customer1','12345678910','Address - somewhere'),(2,'Customer2','12345678910','Address - somewhere'),(3,'Customer3','12345678910','Address - somewhere'),(4,'Customer4','12345678910','Address - somewhere'),(5,'Customer5','12345678910','Address - somewhere'),(6,'Customer6','12345678910','Address - somewhere'),(7,'Customer7','12345678910','Address - somewhere'),(8,'Customer8','12345678910','Address - somewhere'),(9,'Customer9','12345678910','Address - somewhere'),(10,'Customer10','12345678910','Address - somewhere'),(11,'Customer11','12345678910','Address - somewhere'),(12,'Customer12','12345678910','Address - somewhere'),(13,'Customer13','12345678910','Address - somewhere'),(14,'Customer14','12345678910','Address - somewhere'),(15,'Customer15','12345678910','Address - somewhere'),(16,'Customer16','12345678910','Address - somewhere');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `Product_ID` int NOT NULL,
  `Product_Name` varchar(30) NOT NULL,
  `Product_Purchasing_Price` float NOT NULL,
  `Product_Selling_Price` float NOT NULL,
  `Product_Supplier_ID` int DEFAULT NULL,
  `Product_Quantity` int DEFAULT NULL,
  PRIMARY KEY (`Product_ID`),
  UNIQUE KEY `Product_Name` (`Product_Name`),
  KEY `Product_Supplier_ID` (`Product_Supplier_ID`),
  CONSTRAINT `inventory_ibfk_1` FOREIGN KEY (`Product_Supplier_ID`) REFERENCES `suppliers` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,'tea',10,14,2,147),(2,'Pepsi',5,7,2,195),(3,'CocaCola',5,7,1,94),(4,'Rice',15,20,4,444),(5,'soap',6,8,5,550),(6,'Cheese',15,20,3,108),(7,'Water',4,5,4,293),(8,'Milk',8,10,1,209),(9,'Choclate',7,10,6,126),(10,'Sugar',12,15,3,198);
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
  `Order_ID` int NOT NULL,
  `Product_ID` int NOT NULL,
  `Price` float DEFAULT NULL,
  `Quantity` int DEFAULT NULL,
  PRIMARY KEY (`Product_ID`,`Order_ID`),
  KEY `order_details_ibfk_1` (`Order_ID`),
  CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`Order_ID`) REFERENCES `orders` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`Product_ID`) REFERENCES `inventory` (`Product_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
INSERT INTO `order_details` VALUES (1,1,14,3),(1,2,7,2),(4,2,7,3),(5,3,7,4),(7,3,7,2),(2,4,20,2),(3,4,20,1),(7,4,20,2),(8,4,20,1),(4,6,20,2),(3,7,5,2),(9,7,5,5),(5,8,10,5),(8,8,10,6),(3,9,10,5),(9,9,10,5),(6,10,15,2);
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `ID` int NOT NULL,
  `date` date DEFAULT NULL,
  `Customer_ID` int DEFAULT NULL,
  `Order_Total_Price` float DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `Customer_ID` (`Customer_ID`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`Customer_ID`) REFERENCES `customers` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2022-12-20',3,56),(2,'2022-12-20',6,40),(3,'2022-12-20',3,80),(4,'2022-12-20',8,61),(5,'2022-12-20',4,78),(6,'2022-12-20',7,30),(7,'2023-04-13',6,54),(8,'2023-04-13',3,80),(9,'2023-04-13',7,75);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchases`
--

DROP TABLE IF EXISTS `purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchases` (
  `id` int NOT NULL,
  `date` date DEFAULT NULL,
  `Product_ID` int DEFAULT NULL,
  `Product_Name` varchar(25) DEFAULT NULL,
  `Price` float DEFAULT NULL,
  `Supplier_ID` int DEFAULT NULL,
  `Quantity` int DEFAULT NULL,
  `Total_Price` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Product_ID` (`Product_ID`),
  KEY `Supplier_ID` (`Supplier_ID`),
  CONSTRAINT `purchases_ibfk_1` FOREIGN KEY (`Product_ID`) REFERENCES `inventory` (`Product_ID`),
  CONSTRAINT `purchases_ibfk_2` FOREIGN KEY (`Supplier_ID`) REFERENCES `suppliers` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchases`
--

LOCK TABLES `purchases` WRITE;
/*!40000 ALTER TABLE `purchases` DISABLE KEYS */;
INSERT INTO `purchases` VALUES (1,'2022-12-20',1,'tea',10,2,150,1500),(2,'2022-12-20',2,'Pepsi',5,2,200,1000),(3,'2022-12-20',3,'CocaCola',5,1,100,500),(4,'2022-12-20',4,'Rice',15,4,450,6750),(5,'2022-12-20',10,'Sugar',12,3,200,2400),(6,'2022-12-20',9,'Choclate',7,6,136,952),(7,'2022-12-20',8,'Milk',8,1,220,1760),(8,'2022-12-20',7,'Water',4,4,300,1200),(9,'2022-12-20',5,'soup',6,5,550,3300),(10,'2022-12-20',6,'Cheese',15,3,110,1650);
/*!40000 ALTER TABLE `purchases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suppliers` (
  `ID` int NOT NULL,
  `Name` varchar(30) NOT NULL,
  `Phone` varchar(15) DEFAULT NULL,
  `Address` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suppliers`
--

LOCK TABLES `suppliers` WRITE;
/*!40000 ALTER TABLE `suppliers` DISABLE KEYS */;
INSERT INTO `suppliers` VALUES (1,'Supplier1','12345678910','Address-somewhere'),(2,'Supplier2','12345678910','Address-somewhere'),(3,'Supplier3','12345678910','Address-somewhere'),(4,'Supplier4','12345678910','Address-somewhere'),(5,'Supplier5','12345678910','Address-somewhere'),(6,'Supplier6','12345678910','Address-somewhere'),(7,'Supplier7','12345678910','Address-somewhere'),(8,'Supplier8','12345678910','Address-somewhere');
/*!40000 ALTER TABLE `suppliers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'market'
--
/*!50003 DROP PROCEDURE IF EXISTS `update_purchase_price` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_purchase_price`(purchase_id int, new_price float)
begin 
update purchases set price = new_price where id = purchase_id;

set new_price = ((select quantity from purchases where ID = purchase_id) * new_price);
update purchases set total_price = new_price where id = purchase_id;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `update_purchase_product_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_purchase_product_id`(purchase_id int, old_product_id int, new_product_id int, quantity int)
begin 
update inventory set product_quantity = product_quantity - quantity where product_id = old_product_id;
update inventory set product_quantity = product_quantity + quantity where product_id = new_product_id;
update purchases set product_id = new_product_id where id = purchase_id;
update purchases set supplier_id = (select product_supplier_id from inventory where Product_ID = new_product_id) where id = purchase_id;
update purchases set Product_name = (select product_name from inventory where Product_ID = new_product_id) where id = purchase_id;
update purchases set price = (select Product_Purchasing_Price from inventory where Product_ID = new_product_id)where id = purchase_id;
update purchases set total_price = ((select Product_Purchasing_Price from inventory where Product_ID = new_product_id) * quantity) where id = purchase_id;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `update_purchase_quantity` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_purchase_quantity`(purchase_id int, new_quantity int)
begin 
 SET @update_product_id = (select product_id from purchases where id = purchase_id);
 
 SET @current_quantity = (select quantity from purchases where id = purchase_id);
 
update inventory set product_quantity = (new_quantity - (select @current_quantity)) + product_quantity where product_id = (select @update_product_id);

update purchases set quantity = new_quantity where id = purchase_id;
set @new_price = ((select price from purchases where ID = purchase_id) * new_quantity);

update purchases set total_price = (select @new_price) where id = purchase_id;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-04-13 14:13:40
