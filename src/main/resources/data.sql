-- Default categories
INSERT IGNORE INTO category (id, name, parent_id, sort) VALUES (1, '电子产品', NULL, 1);
INSERT IGNORE INTO category (id, name, parent_id, sort) VALUES (2, '服装', NULL, 2);
INSERT IGNORE INTO category (id, name, parent_id, sort) VALUES (3, '食品', NULL, 3);
INSERT IGNORE INTO category (id, name, parent_id, sort) VALUES (4, '手机', 1, 1);
INSERT IGNORE INTO category (id, name, parent_id, sort) VALUES (5, '电脑', 1, 2);
INSERT IGNORE INTO category (id, name, parent_id, sort) VALUES (6, '男装', 2, 1);
INSERT IGNORE INTO category (id, name, parent_id, sort) VALUES (7, '女装', 2, 2);

-- Default currency rates (base: CNY)
INSERT IGNORE INTO currency_rate (base_currency, target_currency, rate) VALUES ('CNY', 'USD', 0.140000);
INSERT IGNORE INTO currency_rate (base_currency, target_currency, rate) VALUES ('CNY', 'EUR', 0.130000);
INSERT IGNORE INTO currency_rate (base_currency, target_currency, rate) VALUES ('CNY', 'JPY', 21.500000);
INSERT IGNORE INTO currency_rate (base_currency, target_currency, rate) VALUES ('CNY', 'GBP', 0.110000);
INSERT IGNORE INTO currency_rate (base_currency, target_currency, rate) VALUES ('USD', 'CNY', 7.140000);
INSERT IGNORE INTO currency_rate (base_currency, target_currency, rate) VALUES ('EUR', 'CNY', 7.690000);
