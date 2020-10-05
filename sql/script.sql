CREATE DATABASE `beerhouse` /*!40100 DEFAULT CHARACTER SET latin1 */;


CREATE TABLE `tb_craftbeer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `ingredients` varchar(250) DEFAULT NULL,
  `alcohol_content` varchar(80) NOT NULL,
  `price` decimal(6,2) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;