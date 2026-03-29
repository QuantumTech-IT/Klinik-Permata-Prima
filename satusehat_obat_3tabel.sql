-- MariaDB dump 10.18  Distrib 10.4.17-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: sik_klinik_permata_restore
-- ------------------------------------------------------
-- Server version	10.4.17-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `satu_sehat_mapping_obat`
--

DROP TABLE IF EXISTS `satu_sehat_mapping_obat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `satu_sehat_mapping_obat` (
  `kode_brng` varchar(15) NOT NULL,
  `obat_code` varchar(15) DEFAULT NULL,
  `obat_system` varchar(100) NOT NULL,
  `obat_display` varchar(80) DEFAULT NULL,
  `form_code` varchar(30) DEFAULT NULL,
  `form_system` varchar(100) DEFAULT NULL,
  `form_display` varchar(80) DEFAULT NULL,
  `numerator_code` varchar(15) DEFAULT NULL,
  `numerator_system` varchar(80) DEFAULT NULL,
  `denominator_code` varchar(15) DEFAULT NULL,
  `denominator_system` varchar(80) DEFAULT NULL,
  `route_code` varchar(30) DEFAULT NULL,
  `route_system` varchar(100) DEFAULT NULL,
  `route_display` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`kode_brng`),
  CONSTRAINT `satu_sehat_mapping_obat_ibfk_1` FOREIGN KEY (`kode_brng`) REFERENCES `tokobarang` (`kode_brng`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `satu_sehat_mapping_obat`
--

LOCK TABLES `satu_sehat_mapping_obat` WRITE;
/*!40000 ALTER TABLE `satu_sehat_mapping_obat` DISABLE KEYS */;
INSERT INTO `satu_sehat_mapping_obat` VALUES ('OBT0668','93001088','http://sys-ids.kemkes.go.id/kfa','Aciclovir 200 mg Tablet (KIMIA FARMA)','BS066','http://terminology.kemkes.go.id/CodeSystem/medication-form','Tablet','mg','http://unitsofmeasure.org','TAB','http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm','O','http://www.whocc.no/atc','Oral'),('OBT1330','93008056','http://sys-ids.kemkes.go.id/kfa','Paracetamol 650 mg Tablet (SANMOL FORTE)','BS066','http://terminology.kemkes.go.id/CodeSystem/medication-form','Tablet','mg','http://unitsofmeasure.org','STR','http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm','O','http://www.whocc.no/atc','Oral'),('OBT1337','93008097','http://sys-ids.kemkes.go.id/kfa','Metamizole Sodium 500 mg Tablet (SANTAGESIK)','BS066','http://terminology.kemkes.go.id/CodeSystem/medication-form','Tablet','mg','http://unitsofmeasure.org','STR','http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm','O','http://www.whocc.no/atc','Oral'),('OBT3593','93006861','http://sys-ids.kemkes.go.id/kfa','Mecobalamin 500 mcg Kapsul (MECONEURO)','BS019','http://terminology.kemkes.go.id/CodeSystem/medication-form','Kapsul','ug','http://unitsofmeasure.org','STR','http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm','O','http://www.whocc.no/atc','Oral'),('OBT3734','93000605','http://sys-ids.kemkes.go.id/kfa','Metamizole Sodium 500 mg/mL Larutan Injeksi (SANTAGESIK)','BS034','http://terminology.kemkes.go.id/CodeSystem/medication-form','Larutan Injeksi','mL','http://unitsofmeasure.org','Amp','http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm','P','http://www.whocc.no/atc','Parenteral');
/*!40000 ALTER TABLE `satu_sehat_mapping_obat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `satu_sehat_medication`
--

DROP TABLE IF EXISTS `satu_sehat_medication`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `satu_sehat_medication` (
  `kode_brng` varchar(15) NOT NULL,
  `id_medication` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`kode_brng`),
  CONSTRAINT `satu_sehat_medication_ibfk_1` FOREIGN KEY (`kode_brng`) REFERENCES `tokobarang` (`kode_brng`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `satu_sehat_medication`
--

LOCK TABLES `satu_sehat_medication` WRITE;
/*!40000 ALTER TABLE `satu_sehat_medication` DISABLE KEYS */;
INSERT INTO `satu_sehat_medication` VALUES ('OBT0668','919191919191'),('OBT1330','a44b0f75-6fc3-4464-8fff-99c8c932180e'),('OBT1337','726cd830-7b43-467c-8e60-8fd964ffb850'),('OBT3593','23625071-c600-408c-ae41-813941407541');
/*!40000 ALTER TABLE `satu_sehat_medication` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `satu_sehat_medicationrequest`
--

DROP TABLE IF EXISTS `satu_sehat_medicationrequest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `satu_sehat_medicationrequest` (
  `no_resep` varchar(14) NOT NULL,
  `kode_brng` varchar(15) NOT NULL,
  `id_medicationrequest` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`no_resep`,`kode_brng`),
  KEY `kode_brng` (`kode_brng`),
  CONSTRAINT `satu_sehat_medicationrequest_ibfk_1` FOREIGN KEY (`no_resep`) REFERENCES `resep_obat` (`no_resep`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `satu_sehat_medicationrequest_ibfk_2` FOREIGN KEY (`kode_brng`) REFERENCES `tokobarang` (`kode_brng`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `satu_sehat_medicationrequest`
--

LOCK TABLES `satu_sehat_medicationrequest` WRITE;
/*!40000 ALTER TABLE `satu_sehat_medicationrequest` DISABLE KEYS */;
INSERT INTO `satu_sehat_medicationrequest` VALUES ('202601050002','OBT3593','fad496f8-2200-4c2b-8bc7-0ac408f16c59'),('202601160001','OBT3593','073cd761-e225-4fc1-9592-f9d06c6ea199'),('202601210002','OBT3593','7098cd89-0951-40b7-bb99-9a91cee675b3'),('202602100001','OBT3593','1694e0e8-8d6d-4c98-af62-23541826a60e');
/*!40000 ALTER TABLE `satu_sehat_medicationrequest` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-04  6:42:32
