/* Stocks datable must be created previously */
USE `stocks`;

CREATE TABLE IF NOT EXISTS `financial_product` (
    `id` BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `symbol` varchar(255) NOT NULL,
    `isin` varchar(255) NOT NULL,
    `name` varchar(255) NOT NULL,
    `data_source` varchar(255) NOT NULL,
    `data_source_symbol` varchar(255) NOT NULL,

    UNIQUE(`symbol`)
);

CREATE SEQUENCE IF NOT EXISTS `eod_seq` increment by 50;

CREATE TABLE IF NOT EXISTS `eod`(
    `id` BIGINT NOT NULL DEFAULT nextval(`stocks`.`eod_seq`),
    `day` date NOT NULL,
    `open_price` decimal(7,3) NOT NULL,
    `close_price` decimal(7,3) NOT NULL,
    `high_price` decimal(7,3) NOT NULL,
    `low_price` decimal(7,3) NOT NULL,
    `dividend_per_share` decimal(7,3) NOT NULL,
    `volume` int(11) DEFAULT NULL,
    `financial_product_id` BIGINT NOT NULL,

    PRIMARY KEY (`id`),
    UNIQUE(`financial_product_id`,`day`),

    CONSTRAINT `fk_financial_product` FOREIGN KEY (`financial_product_id`) REFERENCES `financial_product` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);