CREATE TABLE `demo_order` (
  `version` int DEFAULT NULL,
  `id` bigint NOT NULL,
  `status` int DEFAULT NULL,
  `create_time` bigint DEFAULT NULL,
  `update_time` bigint DEFAULT NULL,
  `consignee_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `consignee_shipping_address` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `consignee_mobile` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL,
  `total_money` bigint DEFAULT NULL,
  `actual_pay_money` bigint DEFAULT NULL,
  `coupon_id` bigint DEFAULT NULL,
  `deduction_points` bigint DEFAULT NULL,
  `order_submit_user_id` bigint DEFAULT NULL,
  `is_delete` int DEFAULT NULL,
  `seller_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


CREATE TABLE `demo_order_item` (
  `id` bigint NOT NULL,
  `order_id` bigint DEFAULT NULL,
  `goods_id` bigint DEFAULT NULL,
  `goods_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `amount` int DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `update_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


INSERT INTO `demo_order` (`version`, `id`, `status`, `create_time`, `update_time`, `consignee_name`, `consignee_shipping_address`, `consignee_mobile`, `total_money`, `actual_pay_money`, `coupon_id`, `deduction_points`, `order_submit_user_id`, `is_delete`, `seller_id`) VALUES (38, 1, 8, 1703658608810, 111, '111', '1', '18050194863', 1, 1, 1, 1, 1, 0, 1);


INSERT INTO `demo_order_item` (`id`, `order_id`, `goods_id`, `goods_name`, `amount`, `price`, `update_time`) VALUES (1770327803288502272, 1, 1770333256328372225, '1', 1, 1, 1710915314194);
INSERT INTO `demo_order_item` (`id`, `order_id`, `goods_id`, `goods_name`, `amount`, `price`, `update_time`) VALUES (1770333256328372224, 1, 1, '1', 1, 1, 1710915314167);