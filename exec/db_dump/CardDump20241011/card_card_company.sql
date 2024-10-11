-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: j11a601.p.ssafy.io    Database: card
-- ------------------------------------------------------
-- Server version	9.0.1

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
-- Table structure for table `card_company`
--

DROP TABLE IF EXISTS `card_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_company` (
  `card_company_id` int unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `name` varchar(10) NOT NULL,
  PRIMARY KEY (`card_company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_company`
--

LOCK TABLES `card_company` WRITE;
/*!40000 ALTER TABLE `card_company` DISABLE KEYS */;
INSERT INTO `card_company` VALUES (1,'2024-10-02 03:56:15.288515','2024-10-02 03:56:15.288515','KB국민카드'),(2,'2024-10-02 03:56:15.601794','2024-10-02 03:56:15.601794','삼성카드'),(3,'2024-10-02 03:56:15.620038','2024-10-02 03:56:15.620038','롯데카드'),(4,'2024-10-02 03:56:15.647039','2024-10-02 03:56:15.647039','우리카드'),(5,'2024-10-02 03:56:15.669317','2024-10-02 03:56:15.669317','신한카드'),(6,'2024-10-02 03:56:15.694955','2024-10-02 03:56:15.694955','꿈나무카드'),(7,'2024-10-02 03:56:15.726073','2024-10-02 03:56:15.726073','현대카드'),(8,'2024-10-02 03:56:15.759967','2024-10-02 03:56:15.759967','BC카드'),(9,'2024-10-02 03:56:15.775146','2024-10-02 03:56:15.775146','NH농협카드'),(10,'2024-10-02 03:56:15.792451','2024-10-02 03:56:15.792451','하나카드');
/*!40000 ALTER TABLE `card_company` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-11  3:38:40