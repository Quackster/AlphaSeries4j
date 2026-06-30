# Missing Strings

Source compared: `/opt/git/AlphaSeries4j-src/DECOMPILED/` against Java string literals under `/opt/git/AlphaSeries4j/src/main/java/`.

Total unique missing non-empty string literals: **1391**.

Comparison note: a decompiled string is treated as present if it exists as an exact Java literal or across consecutive Java string fragments, so wrapped Java SQL/text literals are not counted as missing.

## Summary By Decompiled File

| Decompiled file | Missing unique strings first seen here | Missing literal occurrences | Total literal occurrences |
| --- | ---: | ---: | ---: |
| `Boot.bas` | 91 | 152 | 429 |
| `DataManager.bas` | 5 | 6 | 24 |
| `Filesystems.bas` | 4 | 4 | 15 |
| `Functions.bas` | 75 | 327 | 408 |
| `Guardian.bas` | 3 | 3 | 3 |
| `Handling.bas` | 1046 | 4216 | 6435 |
| `Licence.bas` | 0 | 3 | 4 |
| `Main.frm` | 139 | 753 | 869 |
| `Mistake.frm` | 1 | 5 | 11 |
| `MySQL.bas` | 11 | 39 | 82 |
| `Updater.frm` | 11 | 71 | 95 |
| `privSockHTTP.frm` | 5 | 10 | 29 |

## Missing Strings And Placement

Place each string in the Java class matching the decompiled file name and the Java method matching the decompiled `Proc_*` function, unless the port intentionally renamed that method. Line numbers refer to the decompiled source.

### Boot.bas -> `com.alphaseries.Boot`

1. `SELECT message FROM staff_predefined_messages WHERE id_type='1'`
   - Place in: `Proc_1_10_6C7690` line 2318

2. `SELECT id,description FROM staff_predefined_categories WHERE id_parent='0'`
   - Place in: `Proc_1_10_6C7690` line 2335

3. `SELECT message FROM staff_predefined_messages WHERE id_type='2'`
   - Place in: `Proc_1_10_6C7690` line 2404

4. `SELECT COUNT(id) FROM catalog_pages WHERE id_parent='0' AND level_minrequired <= '`
   - Place in: `Proc_1_17_6CCDC0` line 3577

5. `SELECT id,name,ctlg_color,ctlg_icon,is_develop,is_visible FROM catalog_pages WHERE id_parent = '0' AND level_minrequired <= '`
   - Place in: `Proc_1_17_6CCDC0` line 3588
   - Length: 125 characters

6. `(DEV)`
   - Place in: `Proc_1_17_6CCDC0` line 3644

7. `SELECT COUNT(id) FROM catalog_pages WHERE id_parent = '`
   - Place in: `Proc_1_17_6CCDC0` line 3685

8. `SELECT id,name,ctlg_color,ctlg_icon,is_develop,is_visible FROM catalog_pages WHERE id_parent = '`
   - Place in: `Proc_1_17_6CCDC0` line 3754

9. `(Coming soon)`
   - Place in: `Proc_1_17_6CCDC0` line 3833

10. `(Develope)`
   - Place in: `Proc_1_17_6CCDC0` line 3874

11. `com.client.catalog.recommented.bestsellers.maxitems`
   - Place in: `Proc_1_1_6BB340` line 114

12. `com.client.catalog.recommented.bestsellers.ctlgpageid`
   - Place in: `Proc_1_1_6BB340` line 116

13. `DELETE FROM catalog_products WHERE ctlg_pageid='`
   - Place in: `Proc_1_1_6BB340` line 118

14. `SELECT catalog_products.id,catalog_products.sprite,catalog_products.name,catalog_products.description,catalog_products.id_product,catalog_products.type_secondary,catalog_products.price_credits,catalog_products.type_activitypoints,catalog_products.price_activitypoints,catalog_products.amount,catalog_products.receive_badge,catalog_products.allow_gifts,catalog_products.min_hc_level_required,catalog_products.replace_defaultsign FROM furnitures,catalog_products,catalog_pages WHERE catalog_products.id=furnitures.id_ctlgproduct AND catalog_pages.id=catalog_products.ctlg_pageid AND catalog_pages.level_minrequired='0' AND catalog_pages.hclevel_minrequired='0' AND catalog_pages.is_visible='1' AND catalog_pages.is_clickable='1' AND catalog_pages.is_develop='0' GROUP BY furnitures.id_ctlgproduct,catalog_products.sprite ORDER BY COUNT(furnitures.id_ctlgproduct) DESC LIMIT `
   - Place in: `Proc_1_1_6BB340` line 121
   - Length: 872 characters

15. `INSERT INTO catalog_products(id_order,sprite,name,description,id_product,type_secondary,price_credits,type_activitypoints,price_activitypoints,amount,receive_badge,allow_gifts,min_hc_level_required,replace_defaultsign,ctlg_pageid) VALUES('`
   - Place in: `Proc_1_1_6BB340` line 277
   - Length: 239 characters

16. `com.client.catalog.recommented.new.maxitems`
   - Place in: `Proc_1_1_6BB340` line 284

17. `com.client.catalog.recommented.new.ctlgpageid`
   - Place in: `Proc_1_1_6BB340` line 286

18. `SELECT catalog_products.id,catalog_products.sprite,catalog_products.name,catalog_products.description,catalog_products.id_product,catalog_products.type_secondary,catalog_products.price_credits,catalog_products.type_activitypoints,catalog_products.price_activitypoints,catalog_products.amount,catalog_products.receive_badge,catalog_products.allow_gifts,catalog_products.min_hc_level_required,catalog_products.replace_defaultsign FROM catalog_products,catalog_pages,products WHERE catalog_pages.id=catalog_products.ctlg_pageid AND catalog_pages.level_minrequired='0' AND catalog_pages.hclevel_minrequired='0' AND catalog_pages.is_visible='1' AND catalog_pages.is_clickable='1' AND catalog_pages.is_develop='0' AND products.id=catalog_products.id_product AND products.id_type !='6' GROUP BY catalog_products.sprite ORDER BY catalog_products.id DESC LIMIT `
   - Place in: `Proc_1_1_6BB340` line 292
   - Length: 852 characters

19. `SELECT id_widget,is_enabled,price_regular,price_now FROM settings_promo`
   - Place in: `Proc_1_1_6BB340` line 571

20. `SELECT sprite_default,sprite_replacement FROM products_campaign WHERE is_active='1'`
   - Place in: `Proc_1_1_6BB340` line 668

21. `UNION ALL SELECT rooms_recommented.id_type,rooms_recommented.id_style,rooms_recommented.icon,rooms_recommented.caption,rooms_recommented.caption_2,rooms_recommented.caption_3,NULL,rooms.id,rooms.name,NULL,rooms.status_door,rooms.visitors_now,rooms.visitors_max,rooms.description,rooms_categories.has_trading,NULL,rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,models.name,models.required_files,models.visitors_max,rooms_recommented.id_tree,rooms_recommented.id FROM models,rooms,rooms_categories,rooms_recommented WHERE id_tree='`
   - Place in: `Proc_1_2_6BE280` line 774
   - Length: 569 characters

22. `' AND rooms_recommented.id_type='3' AND rooms_recommented.id_room IS NOT NULL AND rooms.id=rooms_recommented.id_room AND models.id=rooms.id_model GROUP BY rooms_recommented.id`
   - Place in: `Proc_1_2_6BE280` line 776
   - Length: 175 characters

23. `SELECT rooms_recommented.id_type,rooms_recommented.id_style,rooms_recommented.icon,rooms_recommented.caption,rooms_recommented.caption_2,rooms_recommented.caption_3,NULL,rooms.id,rooms.name,users.name,rooms.status_door,rooms.visitors_now,rooms.visitors_max,rooms.description,rooms_categories.has_trading,NULL,rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,NULL,NULL,NULL,rooms_recommented.id_tree,rooms_recommented.id FROM users,rooms,rooms_categories,rooms_recommented WHERE rooms_recommented.id_type='2' AND rooms_recommented.id_room IS NOT NULL AND rooms.id=rooms_recommented.id_room AND users.id=rooms.id_owner AND id_tree='`
   - Place in: `Proc_1_2_6BE280` line 838
   - Length: 668 characters

24. `/config.ini`
   - Place in: `Proc_1_3_6BEBA0` line 864, `Form_Initialize` line 9419, `Form_Load` line 896

25. `taskkill /F /IM "`
   - Place in: `Proc_1_3_6BEBA0` line 887

26. `\\ERR.log`
   - Place in: `Proc_1_3_6BEBA0` line 894, `Proc_0_24_68EEF0` line 12285

27. `\\SLOW.log`
   - Place in: `Proc_1_3_6BEBA0` line 903, `Proc_6_239_7FC170` line 75070, `Proc_5_0_6D3CD0` line 16, `Proc_5_1_6D4110` line 44, `Proc_5_2_6D4690` line 76, `Proc_5_3_6D4CF0` line 120

28. `                                          #####  #           #####\xa0 \xa0#       #  #####`
   - Place in: `Proc_1_3_6BEBA0` line 954

29. `                                          #       #  #    \xa0 \xa0    #       #\xa0 \xa0#       #  #       #`
   - Place in: `Proc_1_3_6BEBA0` line 956

30. `                                          #####  #           #####  \xa0#####   #####`
   - Place in: `Proc_1_3_6BEBA0` line 958

31. `                                          #       #  #           #            #       #  #       #`
   - Place in: `Proc_1_3_6BEBA0` line 960

32. `                                          #       #  #####  #            #       #  #       #  Series \x99`
   - Place in: `Proc_1_3_6BEBA0` line 962

33. `is_admin`
   - Place in: `Proc_1_3_6BEBA0` line 1012

34. `&timestamp=`
   - Place in: `Proc_1_3_6BEBA0` line 1021

35. `http://www.alpha-series.com/view_message?productKey=`
   - Place in: `Proc_1_3_6BEBA0` line 1021

36. `dmYnhs`
   - Place in: `Proc_1_3_6BEBA0` line 1022, `DownloadFile_Timer` line 822

37. `BASIC`
   - Place in: `Proc_1_3_6BEBA0` line 1063

38. `/whitelist.set`
   - Place in: `Proc_1_3_6BEBA0` line 1076

39. `SHOW columns FROM users LIKE 'activitypoints_3'`
   - Place in: `Proc_1_3_6BEBA0` line 1189

40. `ALTER TABLE \`users\` ADD  \`activitypoints_3\` INT( 11 ) NOT NULL DEFAULT  '0' AFTER  \`activitypoints_2\`;`
   - Place in: `Proc_1_3_6BEBA0` line 1190

41. `ALTER TABLE \`users\` ADD INDEX (  \`activitypoints_3\` );`
   - Place in: `Proc_1_3_6BEBA0` line 1191

42. `INSERT IGNORE INTO \`settings\` (\`variable\`, \`value\`) VALUES ('com.client.navigator.staff_picked.category.id.default', '490'), ('com.client.navigator.staff_picked.category.icon.default', 'officialrooms_hq/nav_staffpicks.gif');`
   - Place in: `Proc_1_3_6BEBA0` line 1192
   - Length: 224 characters

43. `INSERT IGNORE INTO \`settings\` (\`variable\`, \`value\`) VALUES ('com.client.navigator.staff_picked.style.default', '1');`
   - Place in: `Proc_1_3_6BEBA0` line 1194

44. `ALTER TABLE \`rooms_official\` ADD  \`requires_level_in\` ENUM(  '0',  '1',  '2' ) NOT NULL DEFAULT  '0' COMMENT  'The room is only visible for these ranked users. If an user is in the room the room is visible to everyone.' AFTER  \`caption_3\``
   - Place in: `Proc_1_3_6BEBA0` line 1195
   - Length: 238 characters

45. `ALTER TABLE \`users\` ADD  \`activitypoints_4\` INT( 11 ) NOT NULL DEFAULT  '0' AFTER  \`activitypoints_3\`;`
   - Place in: `Proc_1_3_6BEBA0` line 1197

46. `ALTER TABLE \`users\` ADD INDEX (  \`activitypoints_4\` );`
   - Place in: `Proc_1_3_6BEBA0` line 1198

47. `ALTER TABLE \`catalog_products\` CHANGE  \`type_activitypoints\`  \`type_activitypoints\` ENUM(  '0',  '1',  '2',  '3',  '4' ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT  '0'`
   - Place in: `Proc_1_3_6BEBA0` line 1199
   - Length: 189 characters

48. `SHOW columns FROM users_bans LIKE 'ipaddress'`
   - Place in: `Proc_1_3_6BEBA0` line 1202

49. `ALTER TABLE  \`users_bans\` ADD  \`ipaddress\` VARCHAR( 60 ) NULL AFTER  \`id_user\``
   - Place in: `Proc_1_3_6BEBA0` line 1203

50. `ALTER TABLE  \`users_bans\` ADD INDEX (  \`ipaddress\` )`
   - Place in: `Proc_1_3_6BEBA0` line 1204

51. `SELECT id_pet FROM settings_petraces WHERE id_pet='9' LIMIT 1`
   - Place in: `Proc_1_3_6BEBA0` line 1206

52. `ALTER TABLE  \`users\` CHANGE  \`level\`  \`level\` INT( 20 )  NOT NULL DEFAULT  '0'`
   - Place in: `Proc_1_3_6BEBA0` line 1208

53. `ALTER TABLE  \`level_privileges\` CHANGE  \`min_level\`  \`min_level\` INT( 20 )  NOT NULL`
   - Place in: `Proc_1_3_6BEBA0` line 1209

54. `ALTER TABLE  \`rooms_official\` CHANGE  \`requires_level_in\`  \`requires_level_in\` INT( 20 )  NOT NULL DEFAULT  '0' COMMENT  'The room is only visible for these ranked users. If an user is in the room the room is visible to everyone.'`
   - Place in: `Proc_1_3_6BEBA0` line 1210
   - Length: 230 characters

55. `ALTER TABLE  \`catalog_pages\` CHANGE  \`level_minrequired\`  \`level_minrequired\` INT( 20 )  NOT NULL DEFAULT  '0'`
   - Place in: `Proc_1_3_6BEBA0` line 1212

56. `INSERT IGNORE INTO settings_petraces (id_pet,breed,min_rank,min_hcrank,name)VALUES ('9',  '11',  '9',  '0',  'Rare Albino Turtle');`
   - Place in: `Proc_1_3_6BEBA0` line 1213
   - Length: 131 characters

57. `INSERT IGNORE INTO settings_petraces (id_pet,breed,min_rank,min_hcrank,name)VALUES ('9',  '8',  '9',  '0',  'Desert Turtle');`
   - Place in: `Proc_1_3_6BEBA0` line 1215
   - Length: 125 characters

58. `INSERT IGNORE INTO settings_petraces (id_pet,breed,min_rank,min_hcrank,name)VALUES ('9',  '7',  '9',  '0',  'Sewer Turtle');`
   - Place in: `Proc_1_3_6BEBA0` line 1217
   - Length: 124 characters

59. `INSERT IGNORE INTO settings_petraces (id_pet,breed,min_rank,min_hcrank,name)VALUES ('9',  '6',  '9',  '0',  'Yertle Turtle');`
   - Place in: `Proc_1_3_6BEBA0` line 1219
   - Length: 125 characters

60. `INSERT IGNORE INTO settings_petraces (id_pet,breed,min_rank,min_hcrank,name)VALUES ('9',  '5',  '9',  '0',  'Spotted Turtle');`
   - Place in: `Proc_1_3_6BEBA0` line 1221
   - Length: 126 characters

61. `INSERT IGNORE INTO settings_petraces (id_pet,breed,min_rank,min_hcrank,name)VALUES ('9',  '4',  '9',  '0',  'Pond Turtle');`
   - Place in: `Proc_1_3_6BEBA0` line 1223
   - Length: 123 characters

62. `INSERT IGNORE INTO settings_petraces (id_pet,breed,min_rank,min_hcrank,name)VALUES ('9',  '3',  '9',  '0',  'Kooper Trooper');`
   - Place in: `Proc_1_3_6BEBA0` line 1225
   - Length: 126 characters

63. `INSERT IGNORE INTO settings_petraces (id_pet,breed,min_rank,min_hcrank,name)VALUES ('9',  '2',  '9',  '0',  'Sea Turtle');`
   - Place in: `Proc_1_3_6BEBA0` line 1227
   - Length: 122 characters

64. `INSERT IGNORE INTO settings_petraces (id_pet,breed,min_rank,min_hcrank,name)VALUES ('9',  '1',  '9',  '0',  'Diamondback Turtle');`
   - Place in: `Proc_1_3_6BEBA0` line 1229
   - Length: 130 characters

65. `INSERT IGNORE INTO settings_petraces (id_pet,breed,min_rank,min_hcrank,name)VALUES ('9',  '0',  '9',  '0',  'Snapping Turtle');`
   - Place in: `Proc_1_3_6BEBA0` line 1231
   - Length: 127 characters

66. `UPDATE settings_petraces SET product_pet='pet9' WHERE id_pet='9'`
   - Place in: `Proc_1_3_6BEBA0` line 1233

67. `INSERT IGNORE INTO \`settings\` (\`variable\`, \`value\`) VALUES ('com.client.bot.pet9.speech', '*krrrr*');`
   - Place in: `Proc_1_3_6BEBA0` line 1234

68. `INSERT IGNORE INTO \`products\` (\`id\`, \`id_type\`, \`id_counter\`, \`name\`, \`description\`, \`link\`, \`sprite\`, \`color\`, \`square_rotation\`, \`square_x\`, \`square_y\`, \`square_z\`, \`action\`, \`effect\`, \`wire\`, \`default_sign\`, \`status_max\`, \`status_walkon\`, \`status_walkoff\`, \`handitems\`, \`distance_allowed\`, \`is_tradeable\`, \`is_recycleable\`, \`is_marketofferable\`, \`is_signable\`, \`id_deco\`, \`is_iconstack\`, \`allow_gifts\`, \`min_roomrights\`, \`time_rent\`, \`receive_badge\`, \`has_charge\`, \`charge_price_credits\`, \`charge_price_activitypoints\`, \`charge_price_activitypoints_type\`, \`charge_size\`, \`is_badgeshop\`) VALUES ('9962006', '6', NULL, 'Schildkr\xf6ten', '', '', 'pet9', '', NULL, '0', '0', '', NULL, NULL, NULL, '', '0', '-1', '-1', NULL, '1', '', '', '0', '', '0', '1', '0', '0', '-1', NULL, '0', '0', '0', '0', '0', '0');`
   - Place in: `Proc_1_3_6BEBA0` line 1235
   - Length: 805 characters

69. `INSERT IGNORE INTO \`catalog_products\` (\`id\`, \`id_order\`, \`sprite\`, \`name\`, \`description\`, \`id_product\`, \`ctlg_pageid\`, \`type_secondary\`, \`price_credits\`, \`type_activitypoints\`, \`price_activitypoints\`, \`amount\`, \`receive_badge\`, \`allow_gifts\`, \`min_hc_level_required\`, \`replace_defaultsign\`) VALUES (NULL, '0', 'pet9', 'Schildkr\xf6ten', '', '9962006', '17020', 'products', '200', '0', '', '1', NULL, '0', '0', NULL)`
   - Place in: `Proc_1_3_6BEBA0` line 1237
   - Length: 412 characters

70. `INSERT IGNORE INTO \`catalog_pages\` (\`id\`, \`id_order\`, \`id_parent\`, \`name\`, \`level_minrequired\`, \`hclevel_minrequired\`, \`is_visible\`, \`is_develop\`, \`is_clickable\`, \`ctlg_template\`, \`ctlg_special_template\`, \`ctlg_icon\`, \`ctlg_color\`, \`ctlg_txt1\`, \`ctlg_txt2\`, \`ctlg_txt3\`, \`ctlg_txt4\`, \`ctlg_txt5\`, \`ctlg_txt6\`, \`ctlg_txt7\`, \`ctlg_txt8\`, \`ctlg_txt9\`, \`ctlg_txt10\`, \`ctlg_txt11\`, \`ctlg_link\`, \`ctlg_header_img\`, \`ctlg_special_img\`) VALUES ('17020', '0', '505', 'Schildkr\xf6te', '0', '0', '1', '0', '1', 'pets', 'NULL', '126', '0', 'Sehr laaaaaaaaaaaaaaangsam!', 'W\xe4hle einen Namen:', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 'catalog_pet_headline1', NULL);`
   - Place in: `Proc_1_3_6BEBA0` line 1239
   - Length: 668 characters

71. `INSERT IGNORE INTO \`catalog_pages\` (\`id\`, \`id_order\`, \`id_parent\`, \`name\`, \`level_minrequired\`, \`hclevel_minrequired\`, \`is_visible\`, \`is_develop\`, \`is_clickable\`, \`ctlg_template\`, \`ctlg_special_template\`, \`ctlg_icon\`, \`ctlg_color\`, \`ctlg_txt1\`, \`ctlg_txt2\`, \`ctlg_txt3\`, \`ctlg_txt4\`, \`ctlg_txt5\`, \`ctlg_txt6\`, \`ctlg_txt7\`, \`ctlg_txt8\`, \`ctlg_txt9\`, \`ctlg_txt10\`, \`ctlg_txt11\`, \`ctlg_link\`, \`ctlg_header_img\`, \`ctlg_special_img\`) VALUES(26001, 24, 0, 'Raumgrundriss', 0, 0, '1', '0', '1', 'default_3x3', 'NULL', 125, 10, 'Kreativit\xe4t keiner Grenze gesetzt. Baue deinen eigenen Raumgrundriss!', 'F\xfcr mehr Details einfach anklicken!  ', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 'catalog_rares_headline1_en', 'ctlg_limited_teaser1');`
   - Place in: `Proc_1_3_6BEBA0` line 1241
   - Length: 746 characters

72. `INSERT IGNORE INTO \`catalog_products\` (\`id\`, \`id_order\`, \`sprite\`, \`name\`, \`description\`, \`id_product\`, \`ctlg_pageid\`, \`type_secondary\`, \`price_credits\`, \`type_activitypoints\`, \`price_activitypoints\`, \`amount\`, \`receive_badge\`, \`allow_gifts\`, \`min_hc_level_required\`, \`replace_defaultsign\`) VALUES(9477509, 0, 'hole', 'Loch', 'Sei kreativ! Bau deinen eigenen Grundriss.', 9999237, 26001, 'products', 5, '0', 0, 1, NULL, '0', '0', NULL);`
   - Place in: `Proc_1_3_6BEBA0` line 1243
   - Length: 436 characters

73. `INSERT IGNORE INTO \`products\` (\`id\`, \`id_type\`, \`id_counter\`, \`name\`, \`description\`, \`link\`, \`sprite\`, \`color\`, \`square_rotation\`, \`square_x\`, \`square_y\`, \`square_z\`, \`action\`, \`effect\`, \`wire\`, \`default_sign\`, \`status_max\`, \`status_walkon\`, \`status_walkoff\`, \`handitems\`, \`distance_allowed\`, \`is_tradeable\`, \`is_recycleable\`, \`is_marketofferable\`, \`is_signable\`, \`id_deco\`, \`is_iconstack\`, \`allow_gifts\`, \`min_roomrights\`, \`time_rent\`, \`receive_badge\`, \`has_charge\`, \`charge_price_credits\`, \`charge_price_activitypoints\`, \`charge_price_activitypoints_type\`, \`charge_size\`, \`is_badgeshop\`) VALUES(9999237, '4', NULL, 'Loch', 'Erstelle deinen eigenen Raumgrundriss!', '', 'hole', '', 2, 2, 2, '0', NULL, NULL, NULL, '0', 1, -1, -1, NULL, '1', '0', '0', '0', '0', '0', '1', '0', 1, -1, NULL, '0', 0, 0, 0, 0, '0');`
   - Place in: `Proc_1_3_6BEBA0` line 1245
   - Length: 812 characters

74. `ALTER TABLE  \`vouchers\` CHANGE  \`contain_hearts\`  \`contain_shells\` INT( 11 ) NOT NULL`
   - Place in: `Proc_1_3_6BEBA0` line 1247

75. `SELECT id FROM catalog_pages WHERE id='917097' LIMIT 1`
   - Place in: `Proc_1_3_6BEBA0` line 1249

76. `INSERT IGNORE INTO \`catalog_pages\` (\`id\`, \`id_order\`, \`id_parent\`, \`name\`, \`level_minrequired\`, \`hclevel_minrequired\`, \`is_visible\`, \`is_develop\`, \`is_clickable\`, \`ctlg_template\`, \`ctlg_special_template\`, \`ctlg_icon\`, \`ctlg_color\`, \`ctlg_txt1\`, \`ctlg_txt2\`, \`ctlg_txt3\`, \`ctlg_txt4\`, \`ctlg_txt5\`, \`ctlg_txt6\`, \`ctlg_txt7\`, \`ctlg_txt8\`, \`ctlg_txt9\`, \`ctlg_txt10\`, \`ctlg_txt11\`, \`ctlg_link\`, \`ctlg_header_img\`, \`ctlg_special_img\`) VALUES ('917097', '0', '58', 'GAME: Freeze', '0', '0', '1', '0', '1', 'default_3x3', 'NULL', '87', '0', 'Liefere Schneeballschlachten mit deinen Freunden!', 'F\xfcr mehr Details einfach anklicken!', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 'catalog_freeze_en', 'ctlg_pic_snowflake_pilot');`
   - Place in: `Proc_1_3_6BEBA0` line 1251
   - Length: 732 characters

77. `' OR sprite LIKE 'es_box' OR sprite LIKE 'es_tile`
   - Place in: `Proc_1_3_6BEBA0` line 1253

78. `' OR sprite LIKE 'es_exit'`
   - Place in: `Proc_1_3_6BEBA0` line 1253

79. `' OR sprite LIKE 'es_tile' OR sprite LIKE 'es_counter' OR sprite LIKE 'es_score_`
   - Place in: `Proc_1_3_6BEBA0` line 1253

80. `UPDATE catalog_products SET ctlg_pageid='917097',price_credits='50',price_activitypoints='0' WHERE sprite LIKE 'es_gate_`
   - Place in: `Proc_1_3_6BEBA0` line 1253

81. `UPDATE catalog_products SET amount='10',ctlg_pageid='917097',price_credits='500',price_activitypoints='0' WHERE sprite LIKE 'es_tile'`
   - Place in: `Proc_1_3_6BEBA0` line 1255
   - Length: 133 characters

82. `\\figuredata.cache`
   - Place in: `Proc_1_3_6BEBA0` line 1268, `Proc_6_16_6E2320` line 2537, `Proc_6_17_6E48D0` line 2771, `Proc_6_163_7B3480` line 57539

83. `DEBUG, time:   `
   - Place in: `Proc_1_3_6BEBA0` line 1276

84. `UPDATE rooms SET id_slot=null, visitors_now='0' WHERE visitors_now != 0`
   - Place in: `Proc_1_3_6BEBA0` line 1278

85. `SELECT id_quest,id_badge,progress,reward_increase,level_total,score_increase,type_reward,id_category FROM settings_achievements WHERE is_enabled='1' LIMIT 100`
   - Place in: `Proc_1_3_6BEBA0` line 1296
   - Length: 158 characters

86. `SELECT COUNT(*) FROM bots_petaccessoires LIMIT 500`
   - Place in: `Proc_1_3_6BEBA0` line 1545

87. `SELECT id_product,id_type,receive FROM bots_petaccessoires LIMIT 500`
   - Place in: `Proc_1_3_6BEBA0` line 1548

88. `SELECT id FROM products WHERE sprite LIKE 'present_gen`
   - Place in: `Proc_1_3_6BEBA0` line 1641

89. `DEBUG, time:   0 ms`
   - Place in: `Proc_1_3_6BEBA0` line 1651

90. `MUS Server Protokollierer aktiviert`
   - Place in: `Proc_1_3_6BEBA0` line 1655

91. `SELECT max_energy,max_exp,max_nutrition FROM bots_petlevels WHERE id_level='`
   - Place in: `Proc_1_7_6C5E10` line 2075

### DataManager.bas -> `com.alphaseries.DataManager`

92. `G1!/`
   - Place in: `Proc_8_7_8051C0` line 97

93. `HF7Z!!_`
   - Place in: `Proc_8_7_8051C0` line 97

94. `_Q`
   - Place in: `Proc_8_7_8051C0` line 97

95. `http://www.alpha-series.com/check_product_sep11?local_time=`
   - Place in: `Proc_8_7_8051C0` line 105

96. `rank=`
   - Place in: `Proc_8_7_8051C0` line 122

### Filesystems.bas -> `com.alphaseries.Filesystems`

97. `ecx+eax+0000014Ch`
   - Place in: `Proc_7_0_8034A0` line 56, `Proc_6_163_7B3480` line 56251

98. `ecx+eax+00000438h`
   - Place in: `Proc_7_0_8034A0` line 92, `Proc_6_243_7FFEB0` line 75935

99. `eax+edi+00000420h`
   - Place in: `Proc_7_0_8034A0` line 122

100. `ecx+eax+00000420h`
   - Place in: `Proc_7_0_8034A0` line 133, `Proc_10_2_8099D0` line 81, `Proc_10_6_809F10` line 107, `Proc_10_7_80A190` line 153, `Proc_6_6_6DC9D0` line 1052, `Proc_6_8_6DD790` line 1283, `Proc_6_50_7166B0` line 14996, `Proc_6_52_7172B0` line 15291; plus 4 more functions

### Functions.bas -> `com.alphaseries.Functions`

101. `SELECT id_product,id_owner,sign,id_secondary FROM furnitures WHERE id ='`
   - Place in: `Proc_10_14_80B010` line 291, `Proc_6_226_7F0B20` line 70622

102. `\\cache\\users\\`
   - Place in: `Proc_10_14_80B010` line 307, `Proc_10_15_80BA40` line 463, `Proc_6_23_6E9A90` line 6940, `Proc_6_69_723630` line 19168, `Proc_6_89_73EA10` line 27787, `Proc_6_128_756190` line 33591, `Proc_6_130_75B770` line 34806, `Proc_6_132_75D4A0` line 35138; plus 11 more functions

103. `ecx+eax+00000448h`
   - Place in: `Proc_10_14_80B010` line 381, `Proc_10_15_80BA40` line 507, `Proc_6_23_6E9A90` line 6905, `Proc_6_69_723630` line 19134, `Proc_6_89_73EA10` line 27235, `Proc_6_128_756190` line 34041, `Proc_6_130_75B770` line 34773, `Proc_6_132_75D4A0` line 35340; plus 10 more functions

104. `edx+eax+0000044Ch`
   - Place in: `Proc_10_14_80B010` line 392, `Proc_10_15_80BA40` line 518, `Proc_6_128_756190` line 33569, `Proc_6_132_75D4A0` line 35351

105. `SELECT id_product,id_owner,sign FROM furnitures WHERE id ='`
   - Place in: `Proc_10_15_80BA40` line 448

106. `SELECT credits FROM users WHERE id='`
   - Place in: `Proc_10_16_80C480` line 545

107. `ecx+eax+0000012Ch`
   - Place in: `Proc_10_16_80C480` line 566, `Proc_6_23_6E9A90` line 7413, `Proc_6_73_725540` line 19771, `Proc_6_128_756190` line 33419, `Proc_6_132_75D4A0` line 35224, `Proc_6_137_766470` line 36774, `Proc_6_150_777FA0` line 44034

108. `ecx+eax+00000074h`
   - Place in: `Proc_10_18_80C9E0` line 634, `Proc_10_19_80CCD0` line 757, `Proc_10_24_80E790` line 4356, `Proc_6_23_6E9A90` line 8457, `Proc_6_55_71A6E0` line 16242, `Proc_6_72_7250D0` line 19522, `Proc_6_81_730010` line 23005, `Proc_6_93_745D90` line 28821; plus 8 more functions

109. `edx+eax+00000438h`
   - Place in: `Proc_10_18_80C9E0` line 667, `Proc_6_4_6DAFB0` line 752, `Proc_6_53_718E00` line 15544, `Proc_6_239_7FC170` line 73769, `Proc_6_243_7FFEB0` line 75524, `Proc_0_24_68EEF0` line 12230

110. `edx+eax+00000028h`
   - Place in: `Proc_10_19_80CCD0` line 711, `Proc_6_87_73C120` line 26840, `Proc_6_150_777FA0` line 41300

111. `ecx+eax+000000D4h`
   - Place in: `Proc_10_19_80CCD0` line 722, `Proc_6_87_73C120` line 26863, `Proc_6_150_777FA0` line 41312

112. `SELECT id,id_socket,motto,figure,gender FROM users WHERE id='`
   - Place in: `Proc_10_22_80D460` line 814

113. `ecx+eax+000000C8h`
   - Place in: `Proc_10_22_80D460` line 921, `Proc_10_24_80E790` line 3802, `Proc_6_17_6E48D0` line 3016, `Proc_6_39_711650` line 13915, `Proc_6_163_7B3480` line 55740, `Proc_6_192_7D1B80` line 64317, `Proc_6_237_7F9ED0` line 73263

114. `edx+eax+000000C8h`
   - Place in: `Proc_10_22_80D460` line 990, `Proc_6_23_6E9A90` line 9583, `Proc_6_39_711650` line 14104, `Proc_6_192_7D1B80` line 64402, `Proc_6_230_7F3D20` line 71584

115. `eax+edi+000000D0h`
   - Place in: `Proc_10_22_80D460` line 991

116. `ecx+edx+000000CCh`
   - Place in: `Proc_10_22_80D460` line 991, `Proc_6_23_6E9A90` line 9693, `Proc_6_39_711650` line 14102, `Proc_6_167_7BECA0` line 58770, `Proc_6_230_7F3D20` line 71585

117. `edx+eax+000000BEh`
   - Place in: `Proc_10_22_80D460` line 1012, `Proc_10_24_80E790` line 1696, `Proc_6_23_6E9A90` line 5685, `Proc_6_26_7034C0` line 10928, `Proc_6_27_706920` line 11581, `Proc_6_28_709DA0` line 12232, `Proc_6_45_714B60` line 14541, `Proc_6_48_7151E0` line 14671; plus 42 more functions

118. `ecx+edx+000000C8h`
   - Place in: `Proc_10_22_80D460` line 1103, `Proc_6_17_6E48D0` line 3047, `Proc_6_23_6E9A90` line 9750

119. `eax+ecx+000000CCh`
   - Place in: `Proc_10_22_80D460` line 1104, `Proc_6_17_6E48D0` line 3048, `Proc_6_23_6E9A90` line 9746, `Proc_6_237_7F9ED0` line 73253

120. `edx+edi+000000D0h`
   - Place in: `Proc_10_22_80D460` line 1105

121. `ecx+ebx+000003D8h`
   - Place in: `Proc_10_22_80D460` line 1144

122. `ecx+edx+000003DCh`
   - Place in: `Proc_10_22_80D460` line 1166, `Proc_6_163_7B3480` line 57020

123. `ecx+edx+000003E0h`
   - Place in: `Proc_10_22_80D460` line 1188

124. `ecx+ebx+000000B0h`
   - Place in: `Proc_10_22_80D460` line 1201, `Proc_6_1_6D8B70` line 307, `Proc_6_32_70EAB0` line 13257, `Proc_6_128_756190` line 33356, `Proc_6_132_75D4A0` line 35972, `Proc_6_139_768100` line 37225, `Proc_6_170_7C1100` line 59240, `Proc_6_179_7C7790` line 61035; plus 2 more functions

125. `edx+eax+00000404h`
   - Place in: `Proc_10_24_80E790` line 1216, `Proc_6_80_72EB60` line 22810, `Proc_6_150_777FA0` line 42116, `Proc_6_155_795C90` line 49580, `Proc_6_159_79FCD0` line 50918, `Proc_6_198_7D4B70` line 65284, `tmrRollers_Timer` line 9716, `Proc_0_24_68EEF0` line 13219

126. `edx+eax+00000406h`
   - Place in: `Proc_10_24_80E790` line 1226, `Proc_6_23_6E9A90` line 10655, `Proc_6_150_777FA0` line 42091, `Proc_6_155_795C90` line 50140, `Proc_6_159_79FCD0` line 51830, `Proc_6_198_7D4B70` line 65264, `tmrSigner_Timer` line 5889, `tmrRollers_Timer` line 9631; plus 1 more functions

127. `edx+edi+00000404h`
   - Place in: `Proc_10_24_80E790` line 1245, `Proc_0_24_68EEF0` line 15431

128. `edx+edi+00000406h`
   - Place in: `Proc_10_24_80E790` line 1264, `Proc_6_149_775C10` line 40558

129. `ecx+eax+00000406h`
   - Place in: `Proc_10_24_80E790` line 1284, `Proc_6_78_7279A0` line 20562, `Proc_6_79_72A430` line 21818, `Proc_6_80_72EB60` line 22821, `Proc_6_96_747000` line 29306, `Proc_6_97_747640` line 29406, `Proc_6_150_777FA0` line 41659, `Proc_6_179_7C7790` line 60898; plus 1 more functions

130. `edi+edx+00000404h`
   - Place in: `Proc_10_24_80E790` line 1305

131. `edi+edx+00000406h`
   - Place in: `Proc_10_24_80E790` line 1324

132. `edx+eax+00000460h`
   - Place in: `Proc_10_24_80E790` line 1658, `Proc_6_55_71A6E0` line 17440, `Proc_6_85_73A8E0` line 26503, `Proc_6_197_7D43C0` line 65013, `Proc_6_198_7D4B70` line 65240, `tmrSigner_Timer` line 1505

133. `ecx+eax+00000444h`
   - Place in: `Proc_10_24_80E790` line 1668, `Proc_6_210_7E1DC0` line 68145

134. `ecx+eax+0000007Ch`
   - Place in: `Proc_10_24_80E790` line 1704, `Proc_6_23_6E9A90` line 10083, `Proc_6_80_72EB60` line 22856, `Proc_6_84_733600` line 24438, `Proc_6_86_73B0D0` line 26604, `Proc_6_87_73C120` line 26779, `Proc_6_149_775C10` line 40330, `Proc_6_150_777FA0` line 40988; plus 8 more functions

135. `ecx+eax+000000BEh`
   - Place in: `Proc_10_24_80E790` line 1742, `Proc_6_23_6E9A90` line 3664, `Proc_6_26_7034C0` line 11304, `Proc_6_27_706920` line 11969, `Proc_6_28_709DA0` line 12657, `Proc_6_38_70FD10` line 13469, `Proc_6_39_711650` line 13822, `Proc_6_49_715D30` line 14827; plus 44 more functions

136. `edx+eax+00000098h`
   - Place in: `Proc_10_24_80E790` line 1765, `Proc_6_17_6E48D0` line 2719, `Proc_6_26_7034C0` line 11312, `Proc_6_27_706920` line 11977, `Proc_6_28_709DA0` line 12665, `Proc_6_78_7279A0` line 21030, `Proc_6_79_72A430` line 22277, `Proc_6_150_777FA0` line 44452; plus 2 more functions

137. `edx+eax+0000007Ch`
   - Place in: `Proc_10_24_80E790` line 1818, `Proc_10_26_81E4E0` line 5197, `Proc_6_23_6E9A90` line 10119, `Proc_6_26_7034C0` line 11399, `Proc_6_27_706920` line 12064, `Proc_6_28_709DA0` line 12752, `Proc_6_79_72A430` line 22586, `Proc_6_80_72EB60` line 22698; plus 12 more functions

138. `edx+ecx+000000B4h`
   - Place in: `Proc_10_24_80E790` line 1868, `Proc_6_26_7034C0` line 11466, `Proc_6_27_706920` line 12131, `Proc_6_28_709DA0` line 12407, `Proc_6_150_777FA0` line 44670, `Proc_6_167_7BECA0` line 58770

139. `edx+eax+00000444h`
   - Place in: `Proc_10_24_80E790` line 1972

140. `eax+ecx+0000004Ch`
   - Place in: `Proc_10_24_80E790` line 2058, `Proc_6_84_733600` line 25138, `Proc_6_159_79FCD0` line 54877, `Proc_6_179_7C7790` line 61370

141. `edx+eax+00000034h`
   - Place in: `Proc_10_24_80E790` line 2310, `Proc_6_150_777FA0` line 44279, `tmrSigner_Timer` line 1084, `tmrRollers_Timer` line 10339

142. `edx+eax+0000009Ch`
   - Place in: `Proc_10_24_80E790` line 2321, `Proc_6_14_6E10C0` line 2127, `Proc_6_102_749C50` line 29886, `Proc_6_150_777FA0` line 47424, `Proc_6_159_79FCD0` line 54509, `Proc_6_206_7DA450` line 67175, `Proc_6_210_7E1DC0` line 68466, `Proc_6_220_7EBA50` line 69661

143. `ecx+eax+0000041Ch`
   - Place in: `Proc_10_24_80E790` line 2332

144. `edx+eax+0000009Eh`
   - Place in: `Proc_10_24_80E790` line 2560, `Proc_6_150_777FA0` line 41590

145. `ecx+edx+0000041Ch`
   - Place in: `Proc_10_24_80E790` line 2643, `Proc_6_150_777FA0` line 45053

146. `eax+edx+0000009Eh`
   - Place in: `Proc_10_24_80E790` line 2923

147. `edx+eax+000000A8h`
   - Place in: `Proc_10_24_80E790` line 2964, `Proc_6_150_777FA0` line 45164

148. `edx+ecx+000000A8h`
   - Place in: `Proc_10_24_80E790` line 2986

149. `edx+eax+000000AEh`
   - Place in: `Proc_10_24_80E790` line 3018

150. `ecx+eax+000000AEh`
   - Place in: `Proc_10_24_80E790` line 3056

151. `J}`
   - Place in: `Proc_10_24_80E790` line 3203, `Proc_6_144_76BE70` line 39958, `Proc_6_150_777FA0` line 45218

152. `edx+eax+000000A2h`
   - Place in: `Proc_10_24_80E790` line 3219, `tmrSigner_Timer` line 3871

153. `edx+ecx+0000041Ch`
   - Place in: `Proc_10_24_80E790` line 3251

154. `edx+eax+0000041Ch`
   - Place in: `Proc_10_24_80E790` line 3274, `Proc_6_55_71A6E0` line 17159, `Proc_6_82_731070` line 23338, `Proc_6_103_74A510` line 30097, `Proc_6_150_777FA0` line 45115, `Proc_6_159_79FCD0` line 53439, `Proc_6_192_7D1B80` line 64190, `tmrSigner_Timer` line 3900

155. `WALK_ON`
   - Place in: `Proc_10_24_80E790` line 3386

156. `ecx+eax+00000078h`
   - Place in: `Proc_10_24_80E790` line 3386, `Proc_6_13_6E0A80` line 2033, `Proc_6_14_6E10C0` line 2237, `Proc_6_17_6E48D0` line 2907, `Proc_6_23_6E9A90` line 3671, `Proc_6_26_7034C0` line 10850, `Proc_6_27_706920` line 11503, `Proc_6_28_709DA0` line 12154; plus 21 more functions

157. `ecx+eax+00000080h`
   - Place in: `Proc_10_24_80E790` line 3398, `Proc_6_150_777FA0` line 47773, `Proc_6_155_795C90` line 49952, `Proc_6_159_79FCD0` line 54041, `tmrSigner_Timer` line 6888

158. `ecx+edx+00000070h`
   - Place in: `Proc_10_24_80E790` line 3420, `Proc_6_70_724190` line 19293, `Proc_6_78_7279A0` line 21136, `Proc_6_150_777FA0` line 41228, `Proc_6_155_795C90` line 48369

159. `ecx+eax+00000040h`
   - Place in: `Proc_10_24_80E790` line 3457, `Proc_6_159_79FCD0` line 52069

160. `edx+eax+00000040h`
   - Place in: `Proc_10_24_80E790` line 3469, `Proc_6_183_7CABF0` line 61679, `Proc_6_197_7D43C0` line 64992, `Proc_6_198_7D4B70` line 65220

161. `edx+eax+00000088h`
   - Place in: `Proc_10_24_80E790` line 3541, `Proc_10_26_81E4E0` line 5307, `Proc_6_155_795C90` line 49121, `Proc_6_159_79FCD0` line 50438, `Proc_6_179_7C7790` line 61001, `Proc_6_188_7CF3C0` line 63508, `DataProcess_Timer` line 295, `tmrSigner_Timer` line 6002; plus 2 more functions

162. `edx+eax+00000408h`
   - Place in: `Proc_10_24_80E790` line 3666, `tmrRollers_Timer` line 10082, `Proc_0_24_68EEF0` line 14235

163. `edx+eax+0000041Eh`
   - Place in: `Proc_10_24_80E790` line 3729, `Proc_6_13_6E0A80` line 1966, `Proc_6_14_6E10C0` line 2178, `Proc_6_102_749C50` line 29907

164. `ecx+eax+00000038h`
   - Place in: `Proc_10_24_80E790` line 3837

165. `edx+eax+00000038h`
   - Place in: `Proc_10_24_80E790` line 3858, `Proc_6_150_777FA0` line 46844, `Proc_6_159_79FCD0` line 53374, `tmrWalking_Timer` line 11105, `Proc_0_24_68EEF0` line 12609

166. `eax+ecx+0000041Ch`
   - Place in: `Proc_10_24_80E790` line 4408, `Proc_6_144_76BE70` line 39300, `Proc_6_159_79FCD0` line 53700

167. `edx+eax+0000002Ch`
   - Place in: `Proc_10_26_81E4E0` line 4745, `Proc_6_155_795C90` line 49701, `Proc_6_159_79FCD0` line 51039, `Proc_6_179_7C7790` line 61257, `Proc_0_24_68EEF0` line 13378

168. `edx+eax+0000002Eh`
   - Place in: `Proc_10_26_81E4E0` line 4755, `Proc_6_179_7C7790` line 61079, `Proc_0_24_68EEF0` line 13399

169. `edx+edi+0000002Ch`
   - Place in: `Proc_10_26_81E4E0` line 4774

170. `edx+edi+0000002Eh`
   - Place in: `Proc_10_26_81E4E0` line 4793

171. `ecx+eax+0000002Eh`
   - Place in: `Proc_10_26_81E4E0` line 4813, `Proc_6_39_711650` line 14192, `Proc_6_188_7CF3C0` line 63488, `Proc_0_24_68EEF0` line 12652

172. `edi+edx+0000002Ch`
   - Place in: `Proc_10_26_81E4E0` line 4834

173. `edi+edx+0000002Eh`
   - Place in: `Proc_10_26_81E4E0` line 4853

174. `ecx+eax+00000006h`
   - Place in: `Proc_10_26_81E4E0` line 5190, `Proc_6_179_7C7790` line 61092, `Proc_6_188_7CF3C0` line 63501, `Proc_6_243_7FFEB0` line 76235, `tmrSigner_Timer` line 948, `tmrBots_Timer` line 8706, `Proc_0_24_68EEF0` line 12459

175. `ecx+eax+00000030h`
   - Place in: `Proc_10_26_81E4E0` line 5435, `Proc_6_39_711650` line 14202, `Proc_6_53_718E00` line 15828, `Proc_6_55_71A6E0` line 16221

### Guardian.bas -> `com.alphaseries.Guardian`

176. `Scripting.FileSystemObject`
   - Place in: `(module declarations)` line 12

177. `ecx+eax+0000008Ch`
   - Place in: `Proc_11_2_821390` line 33

178. `edx+eax+000000B0h`
   - Place in: `Proc_11_3_821440` line 62, `Proc_6_2_6D9880` line 412, `Proc_6_3_6DA490` line 558, `Proc_6_4_6DAFB0` line 681, `Proc_6_12_6DFE90` line 1911, `Proc_6_17_6E48D0` line 2738, `Proc_6_28_709DA0` line 12453, `Proc_6_32_70EAB0` line 13222; plus 18 more functions

### Handling.bas -> `com.alphaseries.Handling`

179. `ecx+eax+00000048h`
   - Place in: `Proc_6_0_6D7FF0` line 13, `Proc_6_1_6D8B70` line 197, `Proc_6_2_6D9880` line 329, `Proc_6_3_6DA490` line 449, `Proc_6_4_6DAFB0` line 599, `Proc_6_6_6DC9D0` line 988, `Proc_6_7_6DD0E0` line 1111, `Proc_6_8_6DD790` line 1218; plus 27 more functions

180. `ecx+eax+000003F8h`
   - Place in: `Proc_6_0_6D7FF0` line 32, `Proc_6_1_6D8B70` line 216, `Proc_6_2_6D9880` line 348, `Proc_6_3_6DA490` line 468, `Proc_6_4_6DAFB0` line 618, `Proc_6_6_6DC9D0` line 1007, `Proc_6_7_6DD0E0` line 1130, `Proc_6_8_6DD790` line 1237; plus 28 more functions

181. `edx+edi+000003D4h`
   - Place in: `Proc_6_0_6D7FF0` line 32, `Proc_6_3_6DA490` line 468, `Proc_6_6_6DC9D0` line 1037, `Proc_6_7_6DD0E0` line 1130, `Proc_6_8_6DD790` line 1237, `Proc_6_20_6E88E0` line 3390, `Proc_6_23_6E9A90` line 6377, `Proc_6_26_7034C0` line 11016; plus 12 more functions

182. `SELECT users.id,users.name,ROUND((UNIX_TIMESTAMP()-users.create_time)/60,0),ROUND((UNIX_TIMESTAMP()-users.lastonline_time)/60,0),users.id_socket FROM users WHERE users.id='`
   - Place in: `Proc_6_0_6D7FF0` line 34
   - Length: 172 characters

183. `SELECT COUNT(id) FROM staff_cfh WHERE id_user='`
   - Place in: `Proc_6_0_6D7FF0` line 142

184. `' AND id_closed='2'`
   - Place in: `Proc_6_0_6D7FF0` line 152

185. `SELECT COUNT(id) FROM users_cautions WHERE id_user='`
   - Place in: `Proc_6_0_6D7FF0` line 162

186. `SELECT COUNT(id) FROM users_bans WHERE id_user='`
   - Place in: `Proc_6_0_6D7FF0` line 176

187. `UPDATE furnitures_dimmerpresets SET id_state='1' WHERE id_furni='`
   - Place in: `Proc_6_100_748C80` line 29713

188. `',id_background='`
   - Place in: `Proc_6_100_748C80` line 29714

189. `UPDATE furnitures_dimmerpresets SET id_state='2',id_light='`
   - Place in: `Proc_6_100_748C80` line 29714

190. `' AND id_preset='`
   - Place in: `Proc_6_100_748C80` line 29715

191. `' WHERE id_furni='`
   - Place in: `Proc_6_100_748C80` line 29715

192. `',colour='`
   - Place in: `Proc_6_100_748C80` line 29715

193. `SELECT id_product,position_wall FROM furnitures WHERE id='`
   - Place in: `Proc_6_100_748C80` line 29718

194. `SELECT id_effect,time_rent,COUNT(id_effect),timestamp_expire,UNIX_TIMESTAMP() FROM users_effects WHERE id_user='`
   - Place in: `Proc_6_101_749540` line 29763

195. `' GROUP BY users_effects.id_effect LIMIT 50`
   - Place in: `Proc_6_101_749540` line 29764

196. `ecx+eax+0000009Eh`
   - Place in: `Proc_6_102_749C50` line 29896

197. `eax+ebx+00000418h`
   - Place in: `Proc_6_102_749C50` line 29946, `Proc_6_150_777FA0` line 41890

198. `SELECT id,time_rent,timestamp_expire FROM users_effects WHERE id_user='`
   - Place in: `Proc_6_102_749C50` line 29962

199. `' AND id_effect='`
   - Place in: `Proc_6_102_749C50` line 29963

200. `' ORDER BY timestamp_expire DESC LIMIT 1`
   - Place in: `Proc_6_102_749C50` line 29963

201. `UPDATE users_effects SET timestamp_expire=UNIX_TIMESTAMP()+time_rent WHERE id='`
   - Place in: `Proc_6_102_749C50` line 29993

202. `ecx+edi+000003F0h`
   - Place in: `Proc_6_104_74AB60` line 30195

203. `edx+eax+000003F0h`
   - Place in: `Proc_6_105_74AD50` line 30220

204. `' AND type='0' AND name='`
   - Place in: `Proc_6_105_74AD50` line 30237

205. `SELECT id,visitors_max FROM models WHERE create_min_level_hc <= '`
   - Place in: `Proc_6_105_74AD50` line 30237

206. `ecx+edx+000003F0h`
   - Place in: `Proc_6_105_74AD50` line 30271

207. `INSERT INTO rooms(id_owner,name,visitors_max,id_model,timestamp_created) VALUES('`
   - Place in: `Proc_6_105_74AD50` line 30300

208. `\\CACHE\\ROOMS\\`
   - Place in: `Proc_6_105_74AD50` line 30306

209. `\\CACHE\\PATHFINDER\\`
   - Place in: `Proc_6_105_74AD50` line 30310

210. `esi+eax+00000022h`
   - Place in: `Proc_6_107_74B7E0` line 30373

211. `DELETE FROM rooms_official WHERE id_parent='`
   - Place in: `Proc_6_107_74B7E0` line 30402

212. `INSERT INTO rooms_official(id_parent,id_room,id_style,id_type,icon) VALUES('`
   - Place in: `Proc_6_107_74B7E0` line 30433

213. `UPDATE rooms SET is_staff_picked='`
   - Place in: `Proc_6_107_74B7E0` line 30467

214. `edx+eax+00000022h`
   - Place in: `Proc_6_107_74B7E0` line 30467

215. `ebx+eax+00000022h`
   - Place in: `Proc_6_107_74B7E0` line 30510

216. `UPDATE users SET amount_staffpicked=amount_staffpicked+1 WHERE id='`
   - Place in: `Proc_6_107_74B7E0` line 30529

217. `ecx+eax+0000006Ch`
   - Place in: `Proc_6_107_74B7E0` line 30529, `Proc_6_164_7BC820` line 58039

218. `SELECT amount_staffpicked FROM users WHERE id='`
   - Place in: `Proc_6_107_74B7E0` line 30548

219. `ecx+edx+00000314h`
   - Place in: `Proc_6_107_74B7E0` line 30600

220. `DELETE FROM users_badges WHERE id_user='`
   - Place in: `Proc_6_107_74B7E0` line 30629, `Proc_6_203_7D7F80` line 66119

221. `eax+ecx+0000006Ch`
   - Place in: `Proc_6_107_74B7E0` line 30629

222. `ecx+ebx+00000014h`
   - Place in: `Proc_6_107_74B7E0` line 30629, `Proc_6_163_7B3480` line 56974, `Proc_6_203_7D7F80` line 66119

223. `INSERT INTO users_badges(id_user,id_badge) VALUES('`
   - Place in: `Proc_6_107_74B7E0` line 30658, `Proc_6_203_7D7F80` line 66154

224. `edx+eax+0000000Ah`
   - Place in: `Proc_6_107_74B7E0` line 30679, `Proc_6_203_7D7F80` line 66618, `tmrSigner_Timer` line 567

225. `ecx+eax+00000014h`
   - Place in: `Proc_6_107_74B7E0` line 30690, `Proc_6_203_7D7F80` line 66074

226. `edx+eax+00000314h`
   - Place in: `Proc_6_107_74B7E0` line 30721

227. `eax+ecx+00000010h`
   - Place in: `Proc_6_107_74B7E0` line 30723, `Proc_6_132_75D4A0` line 36063, `Proc_6_183_7CABF0` line 62286

228. `ecx+edx+00000194h`
   - Place in: `Proc_6_107_74B7E0` line 30809

229. `edx+eax+0000001Eh`
   - Place in: `Proc_6_107_74B7E0` line 30819, `Proc_6_132_75D4A0` line 35795, `Proc_6_203_7D7F80` line 66189

230. `eax+ecx+00000014h`
   - Place in: `Proc_6_107_74B7E0` line 30840, `Proc_6_132_75D4A0` line 35818, `Proc_6_203_7D7F80` line 66210

231. `edx+ecx+0000001Eh`
   - Place in: `Proc_6_107_74B7E0` line 30860

232. `eax+ecx+00000008h`
   - Place in: `Proc_6_107_74B7E0` line 30902, `Proc_6_132_75D4A0` line 35309, `Proc_6_203_7D7F80` line 66407

233. `ecx+ebx+0000006Ch`
   - Place in: `Proc_6_107_74B7E0` line 30903

234. `ecx+ebx+00000008h`
   - Place in: `Proc_6_107_74B7E0` line 30938, `Proc_6_136_765F10` line 36639

235. `edx+ecx+000003CCh`
   - Place in: `Proc_6_107_74B7E0` line 30959

236. `F{`
   - Place in: `Proc_6_107_74B7E0` line 30969, `Proc_6_163_7B3480` line 57861, `Proc_6_203_7D7F80` line 66473

237. `SELECT id_room FROM rooms_favourites WHERE id_user='`
   - Place in: `Proc_6_108_74D800` line 30989

238. `eax+esi+000000B0h`
   - Place in: `Proc_6_108_74D800` line 30989

239. `DELETE FROM rooms_favourites WHERE id_room='`
   - Place in: `Proc_6_109_74DBD0` line 31026

240. `SELECT models.type,rooms.id,rooms.name,logs_visitedrooms.timestamp_enter,logs_visitedrooms.timestamp_left FROM rooms, logs_visitedrooms, models WHERE logs_visitedrooms.id_user='`
   - Place in: `Proc_6_10_6DE1D0` line 1438
   - Length: 177 characters

241. `i'),users.id,users.name,logs_chat.description FROM logs_chat,rooms,users WHERE logs_chat.id_room='`
   - Place in: `Proc_6_10_6DE1D0` line 1534, `Proc_5_4_6D55E0` line 229, `Proc_5_5_6D64D0` line 439

242. ` AND users.id=logs_chat.id_user OR logs_chat.id_partner='`
   - Place in: `Proc_6_10_6DE1D0` line 1535

243. `' AND logs_chat.timestamp < `
   - Place in: `Proc_6_10_6DE1D0` line 1535, `Proc_5_4_6D55E0` line 231

244. `' AND logs_chat.id_room='`
   - Place in: `Proc_6_10_6DE1D0` line 1536

245. `' AND logs_chat.timestamp > UNIX_TIMESTAMP()-600 AND users.id=logs_chat.id_user GROUP BY logs_chat.id ORDER BY logs_chat.id DESC LIMIT 50`
   - Place in: `Proc_6_10_6DE1D0` line 1537
   - Length: 137 characters

246. `INSERT INTO rooms_favourites(id_user,id_room,timestamp) VALUES('`
   - Place in: `Proc_6_110_74DDA0` line 31046

247. `ecx+ebx+00000148h`
   - Place in: `Proc_6_115_751220` line 31853, `Proc_6_116_751550` line 31884, `Proc_6_127_755D30` line 33132

248. `i') FROM rooms, logs_visitedrooms, models WHERE logs_visitedrooms.timestamp_enter > UNIX_TIMESTAMP()-21600 AND logs_visitedrooms.id_user='`
   - Place in: `Proc_6_11_6DF4A0` line 1717
   - Length: 138 characters

249. `SELECT rooms_official.id_type,rooms_official.id_style,rooms_official.icon,rooms_official.caption,rooms_official.caption_2,rooms_official.caption_3,NULL,rooms.id,rooms.name,users.name,rooms.status_door,rooms.visitors_now,rooms.visitors_max,rooms.description,rooms_categories.has_trading,NULL,rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,NULL,NULL,NULL,rooms_official.id_parent,rooms_official.id,rooms_official.requires_level_in FROM users,rooms,rooms_categories,rooms_official WHERE rooms_official.id_type='2' AND rooms_official.id_room IS NOT NULL AND rooms.id=rooms_official.id_room AND users.id=rooms.id_owner GROUP BY rooms_official.id`
   - Place in: `Proc_6_123_754020` line 32665
   - Length: 680 characters

250. `UNION ALL SELECT rooms_official.id_type,rooms_official.id_style,rooms_official.icon,rooms_official.caption,rooms_official.caption_2,rooms_official.caption_3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,rooms_official.id_parent,rooms_official.id,rooms_official.requires_level_in FROM rooms_official WHERE rooms_official.id_type='1' GROUP BY rooms_official.id`
   - Place in: `Proc_6_123_754020` line 32666
   - Length: 407 characters

251. `UNION ALL SELECT rooms_official.id_type,rooms_official.id_style,rooms_official.icon,rooms_official.caption,rooms_official.caption_2,rooms_official.caption_3,NULL,rooms.id,rooms.name,NULL,rooms.status_door,rooms.visitors_now,rooms.visitors_max,rooms.description,rooms_categories.has_trading,NULL,rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,models.name,models.required_files,models.visitors_max,rooms_official.id_parent,rooms_official.id,rooms_official.requires_level_in FROM models,rooms,rooms_categories,rooms_official WHERE rooms_official.id_type='3' AND rooms_official.id_room IS NOT NULL AND rooms.id=rooms_official.id_room AND models.id=rooms.id_model GROUP BY rooms_official.id`
   - Place in: `Proc_6_123_754020` line 32667
   - Length: 725 characters

252. `UNION ALL SELECT rooms_official.id_type,rooms_official.id_style,rooms_official.icon,rooms_official.caption,rooms_official.caption_2,rooms_official.caption_3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,rooms_official.id_parent,rooms_official.id,rooms_official.requires_level_in FROM rooms_official WHERE rooms_official.id_type='4' GROUP BY rooms_official.id`
   - Place in: `Proc_6_123_754020` line 32668
   - Length: 407 characters

253. `ecx+ebx+000003F8h`
   - Place in: `Proc_6_123_754020` line 32727

254. `ecx+eax+00000144h`
   - Place in: `Proc_6_124_754D90` line 32912

255. `SELECT get_one, get_two FROM(SELECT SUM(rooms.visitors_now) as get_one, rooms.tag_1 as get_two FROM settings,rooms,users WHERE rooms.tag_1 != '' AND rooms.visitors_max > 0 AND users.id=rooms.id_owner `
   - Place in: `Proc_6_124_754D90` line 32951
   - Length: 200 characters

256. `eax+esi+00000148h`
   - Place in: `Proc_6_124_754D90` line 32951

257. ` OR rooms.tag_2 != '' AND rooms.visitors_max > 0 AND users.id=rooms.id_owner `
   - Place in: `Proc_6_124_754D90` line 32952

258. `eax+ecx+00000148h`
   - Place in: `Proc_6_124_754D90` line 32952

259. ` GROUP BY 2 UNION ALL SELECT SUM(rooms.visitors_now) as get_one, rooms.tag_2 as get_two FROM settings,rooms,users WHERE rooms.tag_2 != '' AND rooms.visitors_max > 0 AND users.id=rooms.id_owner `
   - Place in: `Proc_6_124_754D90` line 32953
   - Length: 193 characters

260. `ecx+edx+00000148h`
   - Place in: `Proc_6_124_754D90` line 32954, `Proc_6_125_755650` line 33056

261. `edx+ebx+00000148h`
   - Place in: `Proc_6_124_754D90` line 32955

262. `ecx+eax+00000140h`
   - Place in: `Proc_6_124_754D90` line 33027

263. `' AND rooms.id=rooms_events.id_room AND rooms_categories.id=rooms.id_category AND users.id=rooms.id_owner `
   - Place in: `Proc_6_125_755650` line 33055

264. `rooms_events,users,rooms,rooms_categories WHERE rooms_events.name_category='`
   - Place in: `Proc_6_125_755650` line 33055

265. `' AND rooms.id=rooms_events.id_room AND rooms_categories.id=rooms.id_category AND users.id=rooms.id_owner GROUP BY rooms_events.id ORDER BY rooms_events.id ASC LIMIT `
   - Place in: `Proc_6_125_755650` line 33057, `Proc_6_127_755D30` line 33134
   - Length: 166 characters

266. `eax+ebx+00000148h`
   - Place in: `Proc_6_125_755650` line 33057

267. `users,rooms,rooms_categories WHERE rooms.tag_1 = '`
   - Place in: `Proc_6_125_755650` line 33079

268. `ecx+edi+00000148h`
   - Place in: `Proc_6_126_755B40` line 33098

269. `' AND users.id=rooms.id_owner OR rooms.name LIKE '`
   - Place in: `Proc_6_127_755D30` line 33113

270. `' AND users.id=rooms.id_owner OR rooms.name = '`
   - Place in: `Proc_6_127_755D30` line 33117

271. `' AND rooms_events.id_user=users.id AND rooms.id=rooms_events.id_room AND rooms_categories.id=rooms.id_category OR rooms_events.name LIKE '`
   - Place in: `Proc_6_127_755D30` line 33133
   - Length: 139 characters

272. `rooms_events,users,rooms,rooms_categories WHERE users.name='`
   - Place in: `Proc_6_127_755D30` line 33133

273. `com.client.club.buyclubviadefaultpage.enabled`
   - Place in: `Proc_6_128_756190` line 33149

274. `edx+eax+000003D0h`
   - Place in: `Proc_6_128_756190` line 33314, `Proc_6_132_75D4A0` line 35696

275. `',hc_startperiod=UNIX_TIMESTAMP(),`
   - Place in: `Proc_6_128_756190` line 33355, `Proc_6_132_75D4A0` line 35741

276. `eax+ecx+000003D0h`
   - Place in: `Proc_6_128_756190` line 33355, `Proc_6_132_75D4A0` line 35741

277. `UPDATE users SET level_hc='`
   - Place in: `Proc_6_128_756190` line 33356, `Proc_6_132_75D4A0` line 35742

278. `Dh`
   - Place in: `Proc_6_128_756190` line 33436

279. `ecx+edi+000003D4h`
   - Place in: `Proc_6_128_756190` line 33458

280. `eax+edi+0000012Ch`
   - Place in: `Proc_6_128_756190` line 33518, `Proc_6_132_75D4A0` line 35049

281. `edx+eax+0000012Ch`
   - Place in: `Proc_6_128_756190` line 33529, `Proc_6_132_75D4A0` line 35060, `Proc_6_150_777FA0` line 43967, `Proc_6_163_7B3480` line 56437

282. `edx+eax+00000448h`
   - Place in: `Proc_6_128_756190` line 33558, `Proc_6_137_766470` line 36726, `Proc_6_141_76A670` line 37677, `Proc_6_155_795C90` line 48571, `Proc_6_159_79FCD0` line 53842, `Proc_6_202_7D6760` line 65951

283. `INSERT INTO logs_moderation(id_type,id_user,id_target,id_target_2,timestamp,message,id_session) VALUES('3','`
   - Place in: `Proc_6_12_6DFE90` line 1911

284. `ecx+eax+000003D8h`
   - Place in: `Proc_6_130_75B770` line 34659

285. `SELECT id FROM users WHERE name='`
   - Place in: `Proc_6_132_75D4A0` line 35112

286. `UPDATE furnitures SET id_secondary='`
   - Place in: `Proc_6_132_75D4A0` line 35134

287. `UPDATE users SET gifts_given=gifts_given+1 WHERE id='`
   - Place in: `Proc_6_132_75D4A0` line 35155

288. `ebx+eax+000000B0h`
   - Place in: `Proc_6_132_75D4A0` line 35165

289. `UPDATE users SET gifts_received=gifts_received+1 WHERE id='`
   - Place in: `Proc_6_132_75D4A0` line 35166

290. `ecx+ebx+00000018h`
   - Place in: `Proc_6_132_75D4A0` line 35328

291. `_active.cache`
   - Place in: `Proc_6_132_75D4A0` line 35382, `Proc_6_163_7B3480` line 57872, `Proc_6_243_7FFEB0` line 75588

292. `INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time,id_ctlgproduct) VALUES('`
   - Place in: `Proc_6_132_75D4A0` line 35566

293. `' AND id_badge='`
   - Place in: `Proc_6_132_75D4A0` line 35768, `Proc_6_163_7B3480` line 56974

294. `SELECT id_badge FROM users_badges WHERE id_user='`
   - Place in: `Proc_6_132_75D4A0` line 35768, `Proc_6_163_7B3480` line 56976

295. `INSERT INTO users_badges(id_user,id_slot,id_badge) VALUES('`
   - Place in: `Proc_6_132_75D4A0` line 35780

296. `ecx+edx+0000001Eh`
   - Place in: `Proc_6_132_75D4A0` line 35840, `Proc_6_203_7D7F80` line 66230

297. `INSERT INTO users_effects(id_user,id_effect,time_rent) VALUES('`
   - Place in: `Proc_6_132_75D4A0` line 35900

298. `FFFFFF`
   - Place in: `Proc_6_132_75D4A0` line 35926

299. `Pet`
   - Place in: `Proc_6_132_75D4A0` line 35959

300. `ecx+edx+0000001Ch`
   - Place in: `Proc_6_132_75D4A0` line 36083

301. `INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time) VALUES('`
   - Place in: `Proc_6_132_75D4A0` line 36188

302. `' ORDER BY id DESC LIMIT 2`
   - Place in: `Proc_6_132_75D4A0` line 36211

303. `UPDATE furnitures SET id_destination='`
   - Place in: `Proc_6_132_75D4A0` line 36235

304. `SONG_PREBURNED_`
   - Place in: `Proc_6_132_75D4A0` line 36336

305. `SELECT title,sequence,id FROM soundmachine_cds WHERE id='`
   - Place in: `Proc_6_132_75D4A0` line 36337

306. `','1','2'),('`
   - Place in: `Proc_6_132_75D4A0` line 36493

307. `','2','1'),('`
   - Place in: `Proc_6_132_75D4A0` line 36493

308. `','3','1')`
   - Place in: `Proc_6_132_75D4A0` line 36493

309. `INSERT INTO furnitures_dimmerpresets(id_furni,id_preset,id_state) VALUES('`
   - Place in: `Proc_6_132_75D4A0` line 36493

310. `UPDATE furnitures SET sign='1,1,1,#000000,166' WHERE id='`
   - Place in: `Proc_6_132_75D4A0` line 36503

311. `ebx+edi+000003F8h`
   - Place in: `Proc_6_136_765F10` line 36562

312. `ecx+eax+0000000Ah`
   - Place in: `Proc_6_136_765F10` line 36639

313. `SELECT contain_product,contain_credits,contain_shells FROM vouchers WHERE name='`
   - Place in: `Proc_6_137_766470` line 36666

314. `SELECT id_product FROM catalog_products WHERE sprite='`
   - Place in: `Proc_6_137_766470` line 36730

315. `DELETE FROM vouchers WHERE name='`
   - Place in: `Proc_6_137_766470` line 36851

316. `SELECT id,id_product,sign FROM furnitures WHERE id_owner='`
   - Place in: `Proc_6_139_768100` line 36970

317. `PLACE_FLOOR`
   - Place in: `Proc_6_139_768100` line 37034

318. `ebx+edx+00000070h`
   - Place in: `Proc_6_139_768100` line 37079

319. `ebx+ecx+00000070h`
   - Place in: `Proc_6_139_768100` line 37098

320. `PLACE_WALLPAPER`
   - Place in: `Proc_6_139_768100` line 37099

321. `eax+edi+00000448h`
   - Place in: `Proc_6_139_768100` line 37225, `Proc_6_140_769400` line 37282

322. `ecx+eax+0000009Ch`
   - Place in: `Proc_6_13_6E0A80` line 1955, `Proc_6_150_777FA0` line 47385, `Proc_6_206_7DA450` line 67201, `Proc_6_210_7E1DC0` line 68758

323. `edx+eax+00000418h`
   - Place in: `Proc_6_13_6E0A80` line 1976, `Proc_6_14_6E10C0` line 2138, `Proc_6_82_731070` line 23299, `Proc_6_102_749C50` line 29917, `Proc_6_150_777FA0` line 41858

324. `WAVE`
   - Place in: `Proc_6_13_6E0A80` line 2033

325. `ecx+eax+00000084h`
   - Place in: `Proc_6_13_6E0A80` line 2044, `Proc_6_14_6E10C0` line 2248, `Proc_6_17_6E48D0` line 2918, `Proc_6_26_7034C0` line 10861, `Proc_6_27_706920` line 11514, `Proc_6_28_709DA0` line 12165, `Proc_6_70_724190` line 19218, `Proc_6_76_726CE0` line 20112; plus 12 more functions

326. `edx+edi+00000070h`
   - Place in: `Proc_6_13_6E0A80` line 2076, `Proc_6_14_6E10C0` line 2280, `Proc_6_23_6E9A90` line 8291, `Proc_6_26_7034C0` line 10893, `Proc_6_27_706920` line 11546, `Proc_6_70_724190` line 19273, `Proc_6_76_726CE0` line 20144, `Proc_6_78_7279A0` line 21116; plus 4 more functions

327. `ecx+edi+00000070h`
   - Place in: `Proc_6_13_6E0A80` line 2095, `Proc_6_14_6E10C0` line 2299, `Proc_6_26_7034C0` line 10912, `Proc_6_27_706920` line 11565, `Proc_6_76_726CE0` line 20163, `Proc_6_174_7C3BC0` line 59980, `Proc_6_194_7D3180` line 64776, `Proc_6_230_7F3D20` line 71659

328. `' AND id_room IS NULL LIMIT 1000`
   - Place in: `Proc_6_140_769400` line 37267

329. `SELECT id,id_product,sign,id_secondary FROM furnitures WHERE id_owner='`
   - Place in: `Proc_6_140_769400` line 37267

330. `' AND id_room ='`
   - Place in: `Proc_6_141_76A670` line 37572

331. `MOVEITEM_PLACE`
   - Place in: `Proc_6_144_76BE70` line 38070

332. `ecx+edx+00000026h`
   - Place in: `Proc_6_144_76BE70` line 38716

333. `ecx+edx+000000A2h`
   - Place in: `Proc_6_144_76BE70` line 38814

334. `eax+ecx+000000A4h`
   - Place in: `Proc_6_144_76BE70` line 38838

335. `ecx+edx+000000BEh`
   - Place in: `Proc_6_144_76BE70` line 38938, `Proc_6_150_777FA0` line 42394, `Form_QueryUnload` line 9218

336. `eax+ecx+000000AEh`
   - Place in: `Proc_6_144_76BE70` line 38960

337. `eax+ecx+000000A8h`
   - Place in: `Proc_6_144_76BE70` line 39195, `Proc_6_150_777FA0` line 45220

338. `edx+eax+00000060h`
   - Place in: `Proc_6_144_76BE70` line 39373, `Proc_6_183_7CABF0` line 61638

339. `ecx+edx+00000006h`
   - Place in: `Proc_6_144_76BE70` line 40088, `Proc_6_183_7CABF0` line 62286, `Proc_6_229_7F3070` line 71491

340. `ecx+edx+0000007Ch`
   - Place in: `Proc_6_144_76BE70` line 40098, `Proc_6_159_79FCD0` line 52504, `tmrSigner_Timer` line 6658, `Proc_0_24_68EEF0` line 14690

341. `eax+ecx+00000006h`
   - Place in: `Proc_6_144_76BE70` line 40198

342. `\\cache\\items_charges\\`
   - Place in: `Proc_6_144_76BE70` line 40292, `Proc_6_150_777FA0` line 44094

343. `eax+edi+00000406h`
   - Place in: `Proc_6_149_775C10` line 40458, `Proc_6_155_795C90` line 50088, `Proc_6_159_79FCD0` line 51778

344. `ebx+ecx+00000406h`
   - Place in: `Proc_6_149_775C10` line 40474

345. `eax+edi+00000404h`
   - Place in: `Proc_6_149_775C10` line 40499, `Proc_6_155_795C90` line 50140, `Proc_6_159_79FCD0` line 51830

346. `ebx+ecx+00000404h`
   - Place in: `Proc_6_149_775C10` line 40577

347. `ecx+ebx+00000406h`
   - Place in: `Proc_6_149_775C10` line 40676

348. `Gb`
   - Place in: `Proc_6_14_6E10C0` line 2166, `Proc_6_23_6E9A90` line 3886, `Proc_6_82_731070` line 23327, `Proc_6_102_749C50` line 29946, `Proc_6_150_777FA0` line 41889

349. `ecx+edi+00000418h`
   - Place in: `Proc_6_14_6E10C0` line 2166

350. `eax+edi+0000041Ah`
   - Place in: `Proc_6_14_6E10C0` line 2227

351. `DANCE`
   - Place in: `Proc_6_14_6E10C0` line 2237

352. `SWITCH_ITEMSTATE`
   - Place in: `Proc_6_150_777FA0` line 41179

353. `eax+edx+0000006Ch`
   - Place in: `Proc_6_150_777FA0` line 41264

354. `email_verified`
   - Place in: `Proc_6_150_777FA0` line 41288

355. `edx+ebx+00000404h`
   - Place in: `Proc_6_150_777FA0` line 41411

356. `edx+ebx+00000406h`
   - Place in: `Proc_6_150_777FA0` line 41431, `Proc_0_24_68EEF0` line 15431

357. `edx+eax+000000A4h`
   - Place in: `Proc_6_150_777FA0` line 41600, `Proc_6_198_7D4B70` line 65230, `tmrSigner_Timer` line 4164

358. `edx+ebx+000000ACh`
   - Place in: `Proc_6_150_777FA0` line 41682

359. `ecx+edx+000000ACh`
   - Place in: `Proc_6_150_777FA0` line 41702

360. `edx+eax+000000A0h`
   - Place in: `Proc_6_150_777FA0` line 41742, `Proc_6_159_79FCD0` line 54595, `Proc_6_210_7E1DC0` line 67866, `Proc_6_222_7ED710` line 70030

361. `ecx+edx+000000BCh`
   - Place in: `Proc_6_150_777FA0` line 41824

362. `hockey_light`
   - Place in: `Proc_6_150_777FA0` line 41902

363. `com.client.catalog.teleporters.enabled`
   - Place in: `Proc_6_150_777FA0` line 41931

364. `SELECT id_destination FROM furnitures WHERE  id='`
   - Place in: `Proc_6_150_777FA0` line 41991

365. `SELECT rooms.id,rooms.id_slot,furnitures.position_x,furnitures.position_y,furnitures.position_z,furnitures.position_r FROM furnitures,rooms WHERE furnitures.id='`
   - Place in: `Proc_6_150_777FA0` line 41995
   - Length: 161 characters

366. `' AND rooms.id=furnitures.id_room LIMIT 1`
   - Place in: `Proc_6_150_777FA0` line 41997

367. `MMMMH`
   - Place in: `Proc_6_150_777FA0` line 42648, `Proc_6_159_79FCD0` line 54344, `Proc_6_226_7F0B20` line 70817, `Proc_6_229_7F3070` line 71279, `Proc_6_239_7FC170` line 73946

368. `ebx+eax+0000000Ch`
   - Place in: `Proc_6_150_777FA0` line 42685, `Proc_6_226_7F0B20` line 70959

369. `SELECT soundmachine_jb_playlist.id_destination,soundmachine_jb_playlist.id_cd,soundmachine_cds.sequence FROM soundmachine_jb_playlist,soundmachine_cds WHERE soundmachine_jb_playlist.id_jukebox='`
   - Place in: `Proc_6_150_777FA0` line 42768, `Proc_6_229_7F3070` line 71338
   - Length: 194 characters

370. `' AND soundmachine_jb_playlist.id_order='`
   - Place in: `Proc_6_150_777FA0` line 42769, `tmrSigner_Timer` line 4540

371. `' AND soundmachine_cds.id=soundmachine_jb_playlist.id_destination GROUP BY soundmachine_cds.id LIMIT 1`
   - Place in: `Proc_6_150_777FA0` line 42772, `tmrSigner_Timer` line 4540

372. `JK`
   - Place in: `Proc_6_150_777FA0` line 43641

373. `JL`
   - Place in: `Proc_6_150_777FA0` line 43899

374. `0;300;600;900;1200;1500;1800;2100;2400;2700;3000;3300;3600;3900;4200;4500;4800;5100;5400`
   - Place in: `Proc_6_150_777FA0` line 44368

375. `;0;-1`
   - Place in: `Proc_6_150_777FA0` line 44369

376. `;30;60;120;180;300;600;`
   - Place in: `Proc_6_150_777FA0` line 44415

377. `fball_score_`
   - Place in: `Proc_6_150_777FA0` line 45491, `Proc_6_210_7E1DC0` line 68868

378. `ecx+eax+0000004Ch`
   - Place in: `Proc_6_150_777FA0` line 46706

379. `edi+eax+000000BEh`
   - Place in: `Proc_6_150_777FA0` line 46718

380. `edx+edi+0000004Ch`
   - Place in: `Proc_6_150_777FA0` line 46743

381. `ebx+eax+0000041Ch`
   - Place in: `Proc_6_150_777FA0` line 46807

382. `ecx+ebx+0000041Ch`
   - Place in: `Proc_6_150_777FA0` line 46985, `tmrSigner_Timer` line 4331

383. `edx+edi+00000060h`
   - Place in: `Proc_6_150_777FA0` line 47047

384. `\\cache\\wired_snapshots\\`
   - Place in: `Proc_6_150_777FA0` line 47052, `Proc_6_206_7DA450` line 67494, `Proc_6_210_7E1DC0` line 68200, `Proc_6_221_7ED1E0` line 69742

385. `ecx+ebx+00000098h`
   - Place in: `Proc_6_150_777FA0` line 47083

386. `ecx+edx+00000098h`
   - Place in: `Proc_6_150_777FA0` line 47126, `Proc_6_219_7EA390` line 69238

387. `ecx+eax+00000098h`
   - Place in: `Proc_6_150_777FA0` line 47181, `Proc_6_159_79FCD0` line 52381, `tmrSigner_Timer` line 1172

388. `ecx+edx+0000009Ch`
   - Place in: `Proc_6_150_777FA0` line 47289, `Proc_6_220_7EBA50` line 69541

389. `edx+ebx+0000009Ch`
   - Place in: `Proc_6_150_777FA0` line 47364

390. `ecx+edx+000000A0h`
   - Place in: `Proc_6_150_777FA0` line 47493, `Proc_6_222_7ED710` line 69910

391. `edx+edi+000000A0h`
   - Place in: `Proc_6_150_777FA0` line 47567

392. `ecx+eax+000000A0h`
   - Place in: `Proc_6_150_777FA0` line 47588, `Proc_6_210_7E1DC0` line 67889

393. `ecx+eax+00000090h`
   - Place in: `Proc_6_150_777FA0` line 47714, `tmrRollers_Timer` line 9443

394. `',position_x='0',position_y='0',position_z='0',position_r='0'   WHERE id='`
   - Place in: `Proc_6_150_777FA0` line 47900

395. `UPDATE furnitures SET id_room=null`
   - Place in: `Proc_6_150_777FA0` line 47900

396. `eax+ebx+00000406h`
   - Place in: `Proc_6_150_777FA0` line 48038, `Proc_6_159_79FCD0` line 54693

397. `eax+ebx+00000404h`
   - Place in: `Proc_6_150_777FA0` line 48108, `Proc_6_159_79FCD0` line 54746

398. `MOVEITEM_PICKUP`
   - Place in: `Proc_6_155_795C90` line 48305

399. `SELECT id_product,id,sign,position_x,position_y,position_z,position_r,id_secondary FROM furnitures WHERE id='`
   - Place in: `Proc_6_155_795C90` line 48439

400. `' AND id_owner IS NULL LIMIT 1`
   - Place in: `Proc_6_155_795C90` line 48440

401. `' AND furnitures.position_wall IS NOT NULL AND products.id=furnitures.id_product AND products.id_type='9'`
   - Place in: `Proc_6_155_795C90` line 48727

402. `SELECT COUNT(*) FROM furnitures,products WHERE furnitures.id_room='`
   - Place in: `Proc_6_155_795C90` line 48727

403. `',id_owner=null WHERE id='`
   - Place in: `Proc_6_155_795C90` line 48774

404. `',id_room='`
   - Place in: `Proc_6_155_795C90` line 48774

405. `UPDATE furnitures SET position_wall='`
   - Place in: `Proc_6_155_795C90` line 48774

406. `eax+ecx+00000406h`
   - Place in: `Proc_6_155_795C90` line 49580, `Proc_6_159_79FCD0` line 50918, `Proc_6_210_7E1DC0` line 68607

407. `eax+ecx+0000002Eh`
   - Place in: `Proc_6_155_795C90` line 49701, `Proc_6_159_79FCD0` line 51039, `Proc_6_179_7C7790` line 61161, `Proc_0_24_68EEF0` line 12806

408. `UPDATE furnitures SET id_owner=null,id_room='`
   - Place in: `Proc_6_155_795C90` line 49905, `Proc_6_159_79FCD0` line 51273

409. `SELECT id_product,id,NULL,position_x,position_y,position_z,position_r FROM furnitures WHERE id='`
   - Place in: `Proc_6_159_79FCD0` line 50339

410. `' AND position_wall IS NULL`
   - Place in: `Proc_6_159_79FCD0` line 50340

411. `ROTATE_ITEM`
   - Place in: `Proc_6_159_79FCD0` line 51466

412. `MOVEITEM_ITEM`
   - Place in: `Proc_6_159_79FCD0` line 51568

413. `MOVEITEM_STACK`
   - Place in: `Proc_6_159_79FCD0` line 51647

414. `ecx+eax+00000044h`
   - Place in: `Proc_6_159_79FCD0` line 52101

415. `ecx+eax+00000050h`
   - Place in: `Proc_6_159_79FCD0` line 52179

416. `ecx+eax+00000054h`
   - Place in: `Proc_6_159_79FCD0` line 52211, `Proc_6_163_7B3480` line 55460, `Proc_6_183_7CABF0` line 62211

417. `ecx+eax+00000058h`
   - Place in: `Proc_6_159_79FCD0` line 52243

418. `ecx+eax+0000005Ch`
   - Place in: `Proc_6_159_79FCD0` line 52275

419. `SELECT id_slot,figure,gender FROM users_wardrobe WHERE id_user='`
   - Place in: `Proc_6_15_6E1900` line 2322

420. `ecx+eax+0000013Ch`
   - Place in: `Proc_6_163_7B3480` line 55227

421. `SELECT group_name,group_description,id_badge,id_room FROM users_groups WHERE id='`
   - Place in: `Proc_6_163_7B3480` line 55460

422. `edx+eax+00000064h`
   - Place in: `Proc_6_163_7B3480` line 55518, `Proc_6_183_7CABF0` line 61731, `Form_QueryUnload` line 9352, `tmrWalking_Timer` line 11960

423. `SELECT name FROM rooms WHERE id='`
   - Place in: `Proc_6_163_7B3480` line 55537

424. `ecx+eax+00000068h`
   - Place in: `Proc_6_163_7B3480` line 55559, `Proc_6_186_7CD040` line 62799, `Proc_0_24_68EEF0` line 12883

425. `edx+eax+000003E0h`
   - Place in: `Proc_6_163_7B3480` line 56085

426. `eax+ebx+000003DCh`
   - Place in: `Proc_6_163_7B3480` line 56122

427. `none`
   - Place in: `Proc_6_163_7B3480` line 56251

428. `ecx+ebx+0000014Ch`
   - Place in: `Proc_6_163_7B3480` line 56252

429. ` AND users.language='`
   - Place in: `Proc_6_163_7B3480` line 56272

430. `edx+ebx+0000014Ch`
   - Place in: `Proc_6_163_7B3480` line 56272

431. `SELECT COUNT(id) FROM rooms WHERE id_owner='`
   - Place in: `Proc_6_163_7B3480` line 56356

432. `' WHERE id = '`
   - Place in: `Proc_6_163_7B3480` line 56405

433. `UPDATE users SET login_ticket=null,id_socket = '`
   - Place in: `Proc_6_163_7B3480` line 56405

434. `edx+esi+000000C0h`
   - Place in: `Proc_6_163_7B3480` line 56424, `Proc_6_165_7BE0B0` line 58518

435. `edx+eax+000003ECh`
   - Place in: `Proc_6_163_7B3480` line 56503

436. `edx+eax+000003D8h`
   - Place in: `Proc_6_163_7B3480` line 56527

437. `SELECT COUNT(*) FROM logs_visitedrooms WHERE logs_visitedrooms.id_user='`
   - Place in: `Proc_6_163_7B3480` line 56558

438. `eax+ebx+00000194h`
   - Place in: `Proc_6_163_7B3480` line 56887

439. `esi+edx+0000017Ch`
   - Place in: `Proc_6_163_7B3480` line 56917

440. `edx+eax+00000308h`
   - Place in: `Proc_6_163_7B3480` line 56927

441. `ecx+esi+00000308h`
   - Place in: `Proc_6_163_7B3480` line 56975

442. `eax+ebx+000003E0h`
   - Place in: `Proc_6_163_7B3480` line 57040

443. `ecx+edx+00000014h`
   - Place in: `Proc_6_163_7B3480` line 57196, `Proc_6_203_7D7F80` line 66154, `Proc_6_206_7DA450` line 66920

444. `UPDATE users SET respect_amount='5',scratch_amount='5',update_time=UNIX_TIMESTAMP() WHERE id='`
   - Place in: `Proc_6_163_7B3480` line 57327

445. `UPDATE users SET hc_startperiod='0' WHERE id='`
   - Place in: `Proc_6_163_7B3480` line 57402

446. `',level_hc='`
   - Place in: `Proc_6_163_7B3480` line 57414

447. `SELECT DATE_FORMAT(FROM_UNIXTIME(timestamp), '`
   - Place in: `Proc_6_163_7B3480` line 57502

448. `s') FROM logs_recycler WHERE id_user='`
   - Place in: `Proc_6_163_7B3480` line 57502

449. `hr-500-39.hd-600-7.ch-630-64.lg-695-73.sh-725-75`
   - Place in: `Proc_6_163_7B3480` line 57826

450. `edx+ebx+000000CCh`
   - Place in: `Proc_6_163_7B3480` line 57846, `Proc_6_167_7BECA0` line 58703, `Proc_6_176_7C4EE0` line 60468, `Proc_6_243_7FFEB0` line 75751

451. `ecx+edi+0000006Ch`
   - Place in: `Proc_6_164_7BC820` line 58054

452. `edx+ebx+00000072h`
   - Place in: `Proc_6_164_7BC820` line 58201

453. `ecx+ebx+00000074h`
   - Place in: `Proc_6_164_7BC820` line 58388, `Proc_6_243_7FFEB0` line 75652

454. `ecx+edi+0000007Ch`
   - Place in: `Proc_6_164_7BC820` line 58453

455. `edx+ebx+000000C4h`
   - Place in: `Proc_6_165_7BE0B0` line 58518, `Proc_6_167_7BECA0` line 58703, `Proc_6_176_7C4EE0` line 60468, `Proc_6_243_7FFEB0` line 75751

456. `ecx+eax+00000434h`
   - Place in: `Proc_6_167_7BECA0` line 58641, `Proc_6_175_7C4800` line 60002

457. `edx+ebx+000000D0h`
   - Place in: `Proc_6_167_7BECA0` line 58703, `Proc_6_176_7C4EE0` line 60468, `Proc_6_243_7FFEB0` line 75751

458. `ecx+edx+000000C4h`
   - Place in: `Proc_6_167_7BECA0` line 58770

459. `edx+eax+00000430h`
   - Place in: `Proc_6_167_7BECA0` line 58814, `Proc_6_171_7C1520` line 59454, `Proc_6_243_7FFEB0` line 75904

460. `SELECT name,nickname,figure,motto,DATE_FORMAT(FROM_UNIXTIME(lastonline_time), '`
   - Place in: `Proc_6_167_7BECA0` line 58863

461. `[0:`
   - Place in: `Proc_6_167_7BECA0` line 58956, `Proc_6_171_7C1520` line 59430, `Proc_6_176_7C4EE0` line 60402, `Proc_6_243_7FFEB0` line 75904, `Proc_9_5_807DF0` line 135

462. ` OR id_friend='`
   - Place in: `Proc_6_167_7BECA0` line 58992, `Proc_6_171_7C1520` line 59284

463. `' AND has_accept='0' OR id_friend='`
   - Place in: `Proc_6_167_7BECA0` line 58992

464. `' AND has_accept='0'`
   - Place in: `Proc_6_167_7BECA0` line 58993

465. `UPDATE friendships SET has_accept = '1' WHERE `
   - Place in: `Proc_6_167_7BECA0` line 59002

466. `lg;ha;wa;hr;ch;sh;cc;ea;he;ca;hd;fa;cp;`
   - Place in: `Proc_6_16_6E2320` line 2517

467. `<settype type="`
   - Place in: `Proc_6_16_6E2320` line 2538, `Proc_6_17_6E48D0` line 2772, `Proc_6_163_7B3480` line 57540

468. `_0="`
   - Place in: `Proc_6_16_6E2320` line 2539, `Proc_6_17_6E48D0` line 2772, `Proc_6_163_7B3480` line 57563

469. `_1="`
   - Place in: `Proc_6_16_6E2320` line 2549, `Proc_6_17_6E48D0` line 2781, `Proc_6_163_7B3480` line 57592

470. `club="`
   - Place in: `Proc_6_16_6E2320` line 2605, `Proc_6_17_6E48D0` line 2837, `Proc_6_163_7B3480` line 57712

471. `eax+ecx+000003D4h`
   - Place in: `Proc_6_16_6E2320` line 2618, `Proc_6_17_6E48D0` line 2850, `Proc_6_52_7172B0` line 15213

472. `selectable="`
   - Place in: `Proc_6_16_6E2320` line 2621, `Proc_6_17_6E48D0` line 2853, `Proc_6_163_7B3480` line 57727

473. `colorable="`
   - Place in: `Proc_6_16_6E2320` line 2625, `Proc_6_17_6E48D0` line 2857, `Proc_6_163_7B3480` line 57732

474. `' AND id_slot='`
   - Place in: `Proc_6_16_6E2320` line 2672

475. `DELETE FROM users_wardrobe WHERE id_user='`
   - Place in: `Proc_6_16_6E2320` line 2672

476. `INSERT INTO users_wardrobe(id_user,id_slot,figure,gender) VALUES('`
   - Place in: `Proc_6_16_6E2320` line 2683

477. `', '`
   - Place in: `Proc_6_16_6E2320` line 2684, `Proc_6_105_74AD50` line 30301

478. `' AND has_accept='0' LIMIT 75`
   - Place in: `Proc_6_170_7C1100` line 59213

479. `DELETE FROM friendships WHERE id_user='`
   - Place in: `Proc_6_170_7C1100` line 59213

480. ` OR id_user='`
   - Place in: `Proc_6_170_7C1100` line 59240

481. `' AND has_accept='1' OR id_user='`
   - Place in: `Proc_6_171_7C1520` line 59284

482. `' AND id_user=' `
   - Place in: `Proc_6_171_7C1520` line 59284

483. `' AND id_friend=' `
   - Place in: `Proc_6_171_7C1520` line 59285

484. `ebx+ecx+00000430h`
   - Place in: `Proc_6_171_7C1520` line 59318

485. `SELECT id,name,id_socket,figure,motto,nickname,DATE_FORMAT(FROM_UNIXTIME(lastonline_time), '`
   - Place in: `Proc_6_172_7C25B0` line 59486

486. `ecx+edx+00000430h`
   - Place in: `Proc_6_172_7C25B0` line 59652, `Proc_6_176_7C4EE0` line 60312

487. `',UNIX_TIMESTAMP(),'(Chat To:     `
   - Place in: `Proc_6_173_7C3430` line 59743

488. `','3','`
   - Place in: `Proc_6_173_7C3430` line 59744

489. `eax+ecx+00000434h`
   - Place in: `Proc_6_174_7C3BC0` line 59825

490. `SELECT id FROM users WHERE name = '`
   - Place in: `Proc_6_174_7C3BC0` line 59840

491. `SELECT accept_friends FROM users WHERE id='`
   - Place in: `Proc_6_174_7C3BC0` line 59842

492. `edx+eax+00000044h`
   - Place in: `Proc_6_174_7C3BC0` line 59857, `Proc_6_179_7C7790` line 61369

493. `INSERT IGNORE INTO friendships(id_user,id_friend) VALUES('`
   - Place in: `Proc_6_174_7C3BC0` line 59904

494. `REQUESTFRIEND`
   - Place in: `Proc_6_174_7C3BC0` line 59918

495. `SELECT users.id,users.name FROM users,friendships WHERE friendships.has_accept='0' AND friendships.id_user='`
   - Place in: `Proc_6_175_7C4800` line 60013

496. `' AND users.id=friendships.id_friend LIMIT 50`
   - Place in: `Proc_6_175_7C4800` line 60014

497. `edx+eax+00000434h`
   - Place in: `Proc_6_175_7C4800` line 60089

498. `SELECT users.id,users.name,users.id_socket,users.figure,users.motto,users.nickname,DATE_FORMAT(FROM_UNIXTIME(lastonline_time), '`
   - Place in: `Proc_6_176_7C4EE0` line 60128
   - Length: 128 characters

499. `') FROM friendships,users WHERE friendships.has_accept='1' AND friendships.id_user = '`
   - Place in: `Proc_6_176_7C4EE0` line 60129

500. `' AND users.id = friendships.id_friend  LIMIT `
   - Place in: `Proc_6_176_7C4EE0` line 60130

501. `eax+edi+000003F8h`
   - Place in: `Proc_6_177_7C6580` line 60551

502. `SELECT bots.id,bots.name,bots.figure,bots_petdata.scratches FROM bots,bots_petdata WHERE bots.id_user='`
   - Place in: `Proc_6_178_7C6E60` line 60625

503. `' AND bots.id_handle='3' AND bots.id_room IS NULL AND bots_petdata.id_bot=bots.id`
   - Place in: `Proc_6_178_7C6E60` line 60626

504. `ebx+eax+000000A6h`
   - Place in: `Proc_6_179_7C7790` line 60776

505. `SELECT bots.id,bots.name,bots.motto,bots.speech,bots.responses,bots.position_x,bots.position_y,bots.position_z,bots.position_r,bots.figure,NULL,bots.id_handle,bots.id_handleaction,NULL,bots.speech_submit,bots.allow_walk,bots.max_fields_away FROM bots,bots_petdata WHERE bots_petdata.id_bot='`
   - Place in: `Proc_6_179_7C7790` line 61034
   - Length: 291 characters

506. `' AND bots.id=bots_petdata.id_bot AND bots.id_user='`
   - Place in: `Proc_6_179_7C7790` line 61035

507. `' AND bots.id_room IS NULL LIMIT 1`
   - Place in: `Proc_6_179_7C7790` line 61035

508. `eax+ecx+00000034h`
   - Place in: `Proc_6_179_7C7790` line 61187

509. `UPDATE bots SET id_room='`
   - Place in: `Proc_6_179_7C7790` line 61257

510. `ecx+edx+00000030h`
   - Place in: `Proc_6_179_7C7790` line 61258

511. `eax+ecx+00000040h`
   - Place in: `Proc_6_179_7C7790` line 61368, `Proc_6_183_7CABF0` line 62285

512. `ecx+edx+00000048h`
   - Place in: `Proc_6_179_7C7790` line 61369

513. `ecx+edx+00000050h`
   - Place in: `Proc_6_179_7C7790` line 61370

514. `UPDATE bots SET id_room=null WHERE id='`
   - Place in: `Proc_6_179_7C7790` line 61382

515. `ebx+eax+00000006h`
   - Place in: `Proc_6_179_7C7790` line 61487

516. `ecx+edx+00000004h`
   - Place in: `Proc_6_179_7C7790` line 61513, `Proc_6_183_7CABF0` line 62286, `Proc_6_189_7D0630` line 63858, `tmrSigner_Timer` line 3539

517. `eax+ebx+00000078h`
   - Place in: `Proc_6_179_7C7790` line 61553

518. `UPDATE users SET tutorial_clothes='1' WHERE id='`
   - Place in: `Proc_6_17_6E48D0` line 2738

519. `lg;ha;wa;hr;ch;sh;cc;ea;he;ca;hd;fa;`
   - Place in: `Proc_6_17_6E48D0` line 2751, `Proc_6_163_7B3480` line 57523

520. `SET_FIGURE`
   - Place in: `Proc_6_17_6E48D0` line 2907

521. `ecx+eax+000000CCh`
   - Place in: `Proc_6_17_6E48D0` line 2941, `Proc_6_163_7B3480` line 57521

522. `edi+edx+00000070h`
   - Place in: `Proc_6_17_6E48D0` line 2960, `Proc_6_28_709DA0` line 12197, `Proc_6_159_79FCD0` line 51510

523. `edi+ecx+00000070h`
   - Place in: `Proc_6_17_6E48D0` line 2979, `Proc_6_28_709DA0` line 12216, `Proc_6_159_79FCD0` line 51531

524. `',figure='`
   - Place in: `Proc_6_17_6E48D0` line 2995

525. `UPDATE users SET gender='`
   - Place in: `Proc_6_17_6E48D0` line 2995

526. `edx+eax+000000D0h`
   - Place in: `Proc_6_17_6E48D0` line 3048

527. `eax+ecx+000000C8h`
   - Place in: `Proc_6_17_6E48D0` line 3094, `Proc_6_23_6E9A90` line 9625

528. `edx+eax+000000CCh`
   - Place in: `Proc_6_17_6E48D0` line 3095

529. `ecx+edx+000000D0h`
   - Place in: `Proc_6_17_6E48D0` line 3096, `Proc_6_23_6E9A90` line 9633, `Proc_6_167_7BECA0` line 58770

530. `abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`
   - Place in: `Proc_6_181_7CA920` line 61570

531. `ecx+esi+00000006h`
   - Place in: `Proc_6_183_7CABF0` line 61627

532. `edx+eax+0000004Ch`
   - Place in: `Proc_6_183_7CABF0` line 61656, `Proc_6_197_7D43C0` line 65023, `Proc_0_24_68EEF0` line 15596

533. `eax+ebx+00000048h`
   - Place in: `Proc_6_183_7CABF0` line 61687

534. `ecx+edx+00000010h`
   - Place in: `Proc_6_183_7CABF0` line 61950, `Proc_6_203_7D7F80` line 66330

535. `edx+eax+00000058h`
   - Place in: `Proc_6_183_7CABF0` line 61951

536. `edi+ecx+00000044h`
   - Place in: `Proc_6_183_7CABF0` line 62037

537. `edi+ecx+00000040h`
   - Place in: `Proc_6_183_7CABF0` line 62122

538. `edx+ebx+00000004h`
   - Place in: `Proc_6_183_7CABF0` line 62302

539. `ecx+eax+0000016Ah`
   - Place in: `Proc_6_186_7CD040` line 62320, `Proc_6_237_7F9ED0` line 73301

540. `edi+esi+00000006h`
   - Place in: `Proc_6_186_7CD040` line 62352

541. `ecx+edi+00000050h`
   - Place in: `Proc_6_186_7CD040` line 62371

542. `eax+edi+00000004h`
   - Place in: `Proc_6_186_7CD040` line 62439, `tmrSigner_Timer` line 7731

543. `edx+eax+00000010h`
   - Place in: `Proc_6_186_7CD040` line 62440, `tmrSigner_Timer` line 512

544. `UPDATE users SET scratch_amount=scratch_amount-1,scratch_given=scratch_given+1 WHERE id='`
   - Place in: `Proc_6_186_7CD040` line 62451

545. `SELECT ROUND((UNIX_TIMESTAMP()-bots_petdata.timestamp_buy)/60/60/24,0),bots_petdata.energy,bots_petdata.experience,bots_petdata.id_level,bots_petdata.nutrition,users.id,users.name,bots_petdata.scratches FROM bots_petdata,users WHERE bots_petdata.id_bot='`
   - Place in: `Proc_6_186_7CD040` line 62983
   - Length: 254 characters

546. `' AND users.id=bots_petdata.id_owner LIMIT 1`
   - Place in: `Proc_6_186_7CD040` line 62984

547. `edx+ebx+00000084h`
   - Place in: `Proc_6_186_7CD040` line 63273, `Proc_6_189_7D0630` line 63858

548. `edx+edi+00000078h`
   - Place in: `Proc_6_186_7CD040` line 63293

549. `edx+eax+0000009Ah`
   - Place in: `Proc_6_188_7CF3C0` line 63311

550. `UPDATE users SET tutorial_guide='1' WHERE id='`
   - Place in: `Proc_6_188_7CF3C0` line 63330

551. `SELECT id,name,motto,speech,responses,position_x,position_y,position_z,position_r,figure,NULL,id_handle,id_handleaction,cache_action,speech_submit,allow_walk,max_fields_away FROM bots WHERE id='`
   - Place in: `Proc_6_188_7CF3C0` line 63409
   - Length: 194 characters

552. `eax+ebx+00000034h`
   - Place in: `Proc_6_188_7CF3C0` line 63595

553. `ecx+eax+00000020h`
   - Place in: `Proc_6_188_7CF3C0` line 63619, `tmrSigner_Timer` line 7414

554. `esi+eax+000000BCh`
   - Place in: `Proc_6_189_7D0630` line 63675

555. `eax+ecx+00000078h`
   - Place in: `Proc_6_189_7D0630` line 63926

556. `ecx+eax+000003D4h`
   - Place in: `Proc_6_18_6E7480` line 3218, `Proc_6_20_6E88E0` line 3401, `Proc_6_23_6E9A90` line 5980, `Proc_6_31_70DE80` line 12974, `Proc_6_105_74AD50` line 30237, `Proc_6_111_74DF70` line 31076, `Proc_6_128_756190` line 33269, `Proc_6_132_75D4A0` line 35683; plus 3 more functions

557. `edx+eax+000003D4h`
   - Place in: `Proc_6_18_6E7480` line 3229, `Proc_6_19_6E8040` line 3295, `Proc_6_132_75D4A0` line 35617, `Proc_6_163_7B3480` line 56132, `Proc_6_167_7BECA0` line 58616

558. `edx+ebx+000000BEh`
   - Place in: `Proc_6_190_7D11D0` line 64020

559. `Dw`
   - Place in: `Proc_6_190_7D11D0` line 64070

560. `eax+ebx+00000058h`
   - Place in: `Proc_6_190_7D11D0` line 64070

561. `eax+ecx+0000005Ch`
   - Place in: `Proc_6_190_7D11D0` line 64070

562. `eax+ecx+00000068h`
   - Place in: `Proc_6_190_7D11D0` line 64071, `Proc_0_24_68EEF0` line 12705

563. `edx+esi+000000BEh`
   - Place in: `Proc_6_191_7D18B0` line 64115

564. `ecx+esi+00000454h`
   - Place in: `Proc_6_191_7D18B0` line 64125

565. `esi+edx+000000BEh`
   - Place in: `Proc_6_192_7D1B80` line 64169

566. `eax+esi+00000450h`
   - Place in: `Proc_6_192_7D1B80` line 64179

567. `edi+esi+00000406h`
   - Place in: `Proc_6_192_7D1B80` line 64264

568. `edx+esi+00000404h`
   - Place in: `Proc_6_192_7D1B80` line 64264

569. `' AND id_slot='0' LIMIT 1000`
   - Place in: `Proc_6_193_7D2BB0` line 64560

570. `SELECT id_badge,id_slot,id FROM users_badges WHERE id_user='`
   - Place in: `Proc_6_193_7D2BB0` line 64560

571. `ecx+ebx+00000450h`
   - Place in: `Proc_6_193_7D2BB0` line 64609

572. `UPDATE users_badges SET id_slot='0' WHERE id_user='`
   - Place in: `Proc_6_194_7D3180` line 64646

573. `' WHERE id_badge='`
   - Place in: `Proc_6_194_7D3180` line 64665

574. `UPDATE users_badges SET id_slot='`
   - Place in: `Proc_6_194_7D3180` line 64665

575. `edx+ebx+00000450h`
   - Place in: `Proc_6_194_7D3180` line 64702

576. `SET_BADGE`
   - Place in: `Proc_6_194_7D3180` line 64713

577. `' LIMIT 5`
   - Place in: `Proc_6_195_7D38D0` line 64806, `Proc_6_201_7D5AC0` line 65665

578. `SELECT id_badge,id_slot,id FROM users_badges WHERE id_slot != '0' AND id_user='`
   - Place in: `Proc_6_195_7D38D0` line 64806

579. `edx+ecx+00000450h`
   - Place in: `Proc_6_195_7D38D0` line 64861

580. `ecx+edx+00000450h`
   - Place in: `Proc_6_195_7D38D0` line 64885

581. `' LIMIT 30`
   - Place in: `Proc_6_196_7D3ED0` line 64910

582. `SELECT name FROM users_tags WHERE id_user='`
   - Place in: `Proc_6_196_7D3ED0` line 64910

583. `edx+eax+00000454h`
   - Place in: `Proc_6_196_7D3ED0` line 64945

584. `eax+edi+00000454h`
   - Place in: `Proc_6_196_7D3ED0` line 64967

585. `ecx+eax+000000A4h`
   - Place in: `Proc_6_197_7D43C0` line 65002

586. `edx+eax+00000136h`
   - Place in: `Proc_6_197_7D43C0` line 65064

587. `edx+eax+00000412h`
   - Place in: `Proc_6_197_7D43C0` line 65158

588. `edx+ebx+00000414h`
   - Place in: `Proc_6_197_7D43C0` line 65158

589. `ecx+eax+0000043Ch`
   - Place in: `Proc_6_198_7D4B70` line 65250, `Proc_0_24_68EEF0` line 14307

590. `SELECT id,description_title,description_thanks FROM poll WHERE id='`
   - Place in: `Proc_6_199_7D54E0` line 65526, `Proc_6_200_7D5770` line 65563, `Proc_6_201_7D5AC0` line 65603

591. `INSERT INTO poll_exit(id_user,id_poll) VALUES('`
   - Place in: `Proc_6_199_7D54E0` line 65538

592. `ecx+eax+000003D0h`
   - Place in: `Proc_6_19_6E8040` line 3275

593. `ebx+ecx+000003DCh`
   - Place in: `Proc_6_19_6E8040` line 3350

594. `ebx+edx+000003E0h`
   - Place in: `Proc_6_19_6E8040` line 3350

595. `@Ghabbo_club`
   - Place in: `Proc_6_19_6E8040` line 3351

596. `ecx+edx+000003D4h`
   - Place in: `Proc_6_19_6E8040` line 3352, `Proc_6_23_6E9A90` line 5760

597. `edx+ebx+000003D4h`
   - Place in: `Proc_6_1_6D8B70` line 216, `Proc_6_2_6D9880` line 348, `Proc_6_3_6DA490` line 498, `Proc_6_4_6DAFB0` line 618, `Proc_6_6_6DC9D0` line 1007, `Proc_6_7_6DD0E0` line 1160, `Proc_6_8_6DD790` line 1267, `Proc_6_10_6DE1D0` line 1392; plus 12 more functions

598. `INSERT INTO logs_moderation(id_type,id_user,id_target,id_target_2,timestamp,message,id_session) VALUES('4','`
   - Place in: `Proc_6_1_6D8B70` line 280

599. `eax+ecx+000000B0h`
   - Place in: `Proc_6_1_6D8B70` line 280, `Proc_6_4_6DAFB0` line 716, `Proc_6_23_6E9A90` line 7055, `Proc_6_28_709DA0` line 12843, `Proc_6_49_715D30` line 14918, `Proc_6_63_721050` line 18510, `Proc_6_73_725540` line 19734, `Proc_6_75_7269D0` line 19967; plus 13 more functions

600. `edx+eax+000000B4h`
   - Place in: `Proc_6_1_6D8B70` line 281, `Proc_6_4_6DAFB0` line 630, `Proc_6_9_6DDD70` line 1342, `Proc_6_23_6E9A90` line 5673, `Proc_6_32_70EAB0` line 13179, `Proc_6_38_70FD10` line 13644, `Proc_6_50_7166B0` line 14986, `Proc_6_60_720060` line 18123; plus 12 more functions

601. `ecx+edx+00000090h`
   - Place in: `Proc_6_1_6D8B70` line 282

602. `{LINK:`
   - Place in: `Proc_6_1_6D8B70` line 284, `Proc_6_2_6D9880` line 416, `Proc_6_4_6DAFB0` line 753, `Proc_6_12_6DFE90` line 1915

603. `INSERT INTO poll_results(id_poll,id_question,message_answer,id_user,timestamp) VALUES('`
   - Place in: `Proc_6_200_7D5770` line 65576

604. `' LIMIT 50`
   - Place in: `Proc_6_201_7D5AC0` line 65608, `Proc_6_234_7F75C0` line 72585

605. `SELECT id,description_question,id_type FROM poll_questions WHERE id_poll='`
   - Place in: `Proc_6_201_7D5AC0` line 65608

606. `SELECT id,id_question,caption FROM poll_answers WHERE id_question='`
   - Place in: `Proc_6_201_7D5AC0` line 65665

607. `ecx+eax+00000138h`
   - Place in: `Proc_6_202_7D6760` line 65759, `Proc_6_203_7D7F80` line 66039

608. `com.client.catalog.recycler.block.time`
   - Place in: `Proc_6_202_7D6760` line 65770, `Proc_6_203_7D7F80` line 66050

609. ` OR id_owner='`
   - Place in: `Proc_6_202_7D6760` line 65804

610. `' AND id_room IS NULL AND id='`
   - Place in: `Proc_6_202_7D6760` line 65804

611. `edx+ebx+00000008h`
   - Place in: `Proc_6_203_7D7F80` line 66442, `Proc_6_226_7F0B20` line 70903

612. `ecx+edx+000003CCh`
   - Place in: `Proc_6_203_7D7F80` line 66463

613. `NQ`
   - Place in: `Proc_6_203_7D7F80` line 66739

614. `edx+eax+0000000Ch`
   - Place in: `Proc_6_206_7DA450` line 66890, `tmrSigner_Timer` line 606

615. `edx+ebx+00000010h`
   - Place in: `Proc_6_206_7DA450` line 66984

616. `506`
   - Place in: `Proc_6_206_7DA450` line 67155

617. `505`
   - Place in: `Proc_6_206_7DA450` line 67468

618. `1004`
   - Place in: `Proc_6_210_7E1DC0` line 67847

619. `1001`
   - Place in: `Proc_6_210_7E1DC0` line 68082

620. `1003`
   - Place in: `Proc_6_210_7E1DC0` line 68174

621. `1002`
   - Place in: `Proc_6_210_7E1DC0` line 68389

622. `edx+esi+00000444h`
   - Place in: `Proc_6_210_7E1DC0` line 68433

623. `503`
   - Place in: `Proc_6_210_7E1DC0` line 68446

624. `eax+ecx+00000404h`
   - Place in: `Proc_6_210_7E1DC0` line 68607

625. `ecx+ebx+00000408h`
   - Place in: `Proc_6_210_7E1DC0` line 68609

626. `502`
   - Place in: `Proc_6_210_7E1DC0` line 68737

627. `501`
   - Place in: `Proc_6_210_7E1DC0` line 68932

628. `SELECT id,id_product FROM furnitures WHERE id ='`
   - Place in: `Proc_6_219_7EA390` line 69112, `Proc_6_220_7EBA50` line 69404, `Proc_6_222_7ED710` line 69784

629. ` OR id_room='`
   - Place in: `Proc_6_219_7EA390` line 69197, `Proc_6_220_7EBA50` line 69489, `Proc_6_222_7ED710` line 69869

630. `eax+ecx+00000098h`
   - Place in: `Proc_6_219_7EA390` line 69327

631. `SELECT id_product FROM furnitures WHERE id ='`
   - Place in: `Proc_6_221_7ED1E0` line 69707

632. `ecx+edi+000000B4h`
   - Place in: `Proc_6_221_7ED1E0` line 69707, `Proc_0_24_68EEF0` line 14520

633. `eax+ecx+000000A0h`
   - Place in: `Proc_6_222_7ED710` line 69999

634. `esi+eax+0000000Ch`
   - Place in: `Proc_6_224_7EF5A0` line 70178

635. `SELECT MAX(id_order) FROM soundmachine_jb_playlist WHERE id_jukebox='`
   - Place in: `Proc_6_225_7EFBD0` line 70375

636. `SELECT id_destination FROM furnitures WHERE id_owner='`
   - Place in: `Proc_6_225_7EFBD0` line 70394

637. `INSERT INTO soundmachine_jb_playlist(id_jukebox,id_cd,id_order,id_destination) VALUES('`
   - Place in: `Proc_6_225_7EFBD0` line 70430

638. `' AND id_order='`
   - Place in: `Proc_6_226_7F0B20` line 70564

639. `SELECT id_cd FROM soundmachine_jb_playlist WHERE id_jukebox='`
   - Place in: `Proc_6_226_7F0B20` line 70564

640. `' AND id_cd='`
   - Place in: `Proc_6_226_7F0B20` line 70600

641. `DELETE FROM soundmachine_jb_playlist WHERE id_jukebox='`
   - Place in: `Proc_6_226_7F0B20` line 70600

642. `',id_order=id_order-1 WHERE id_order>`
   - Place in: `Proc_6_226_7F0B20` line 70620

643. `UPDATE soundmachine_jb_playlist SET id_jukebox='`
   - Place in: `Proc_6_226_7F0B20` line 70620

644. `ebx+eax+00000004h`
   - Place in: `Proc_6_226_7F0B20` line 70797

645. `UPDATE furnitures SET sign='0' WHERE id='`
   - Place in: `Proc_6_226_7F0B20` line 70922

646. `esi+eax+00000008h`
   - Place in: `Proc_6_227_7F2400` line 71045, `Proc_6_229_7F3070` line 71319

647. `' ORDER BY id_order ASC LIMIT `
   - Place in: `Proc_6_227_7F2400` line 71082

648. `SELECT id_cd,id_destination FROM soundmachine_jb_playlist WHERE id_jukebox='`
   - Place in: `Proc_6_227_7F2400` line 71082

649. `eax+edi+00000008h`
   - Place in: `Proc_6_227_7F2400` line 71082

650. `eax+ebx+0000000Eh`
   - Place in: `Proc_6_227_7F2400` line 71083

651. `ecx+eax+0000000Eh`
   - Place in: `Proc_6_227_7F2400` line 71140

652. `SELECT id,id_destination FROM furnitures WHERE id_owner='`
   - Place in: `Proc_6_228_7F2AF0` line 71176

653. `' LIMIT 250`
   - Place in: `Proc_6_228_7F2AF0` line 71177, `Proc_6_236_7F8540` line 72821

654. `esi+eax+00000004h`
   - Place in: `Proc_6_229_7F3070` line 71242

655. `' AND soundmachine_jb_playlist.id_order='0' AND soundmachine_cds.id=soundmachine_jb_playlist.id_destination GROUP BY soundmachine_cds.id LIMIT 1`
   - Place in: `Proc_6_229_7F3070` line 71339
   - Length: 144 characters

656. `UPDATE users SET motto='`
   - Place in: `Proc_6_230_7F3D20` line 71529

657. `SET_MOTTO`
   - Place in: `Proc_6_230_7F3D20` line 71596

658. `SELECT id_quest FROM users_quests WHERE id_user='`
   - Place in: `Proc_6_232_7F45A0` line 71689, `Proc_6_233_7F5D60` line 72162

659. `UPDATE users_quests SET timestamp_accepted=NULL WHERE id_user='`
   - Place in: `Proc_6_232_7F45A0` line 71772, `Proc_6_233_7F5D60` line 72245

660. `SELECT id_level FROM users_quests WHERE id_user='`
   - Place in: `Proc_6_232_7F45A0` line 71797, `Proc_6_233_7F5D60` line 72270

661. `' WHERE id_user='`
   - Place in: `Proc_6_232_7F45A0` line 71831, `Proc_6_233_7F5D60` line 72304, `Proc_6_243_7FFEB0` line 75651

662. `UPDATE users_quests SET timestamp_done=NULL,timestamp_accepted=UNIX_TIMESTAMP(),id_numericquest='`
   - Place in: `Proc_6_232_7F45A0` line 71831, `Proc_6_233_7F5D60` line 72304

663. `INSERT INTO users_quests(id_user,id_quest,id_level,id_numericquest) VALUES('`
   - Place in: `Proc_6_232_7F45A0` line 71854, `Proc_6_233_7F5D60` line 72327

664. `SELECT progress FROM users_quests WHERE id_user='`
   - Place in: `Proc_6_232_7F45A0` line 71905, `Proc_6_233_7F5D60` line 72378

665. `edx+eax+0000006Ch`
   - Place in: `Proc_6_232_7F45A0` line 72020

666. `SELECT time_next FROM users_quests WHERE id_user='`
   - Place in: `Proc_6_232_7F45A0` line 72030, `Proc_6_233_7F5D60` line 72483

667. `UPDATE users_quests SET time_next='`
   - Place in: `Proc_6_232_7F45A0` line 72064, `Proc_6_233_7F5D60` line 72517

668. `eax+edi+0000007Ch`
   - Place in: `Proc_6_233_7F5D60` line 72150

669. `' AND id_campaign='`
   - Place in: `Proc_6_233_7F5D60` line 72151

670. `SELECT id FROM quests WHERE level='`
   - Place in: `Proc_6_233_7F5D60` line 72151

671. `eax+ebx+00000074h`
   - Place in: `Proc_6_233_7F5D60` line 72151

672. `UPDATE users_quests SET timestamp_done=NULL,timestamp_accepted=NULL WHERE id_user='`
   - Place in: `Proc_6_234_7F75C0` line 72584

673. `edx+eax+0000007Eh`
   - Place in: `Proc_6_235_7F77E0` line 72630

674. `SELECT id_quest,id_numericquest,progress,id_level,time_next FROM users_quests WHERE id_user='`
   - Place in: `Proc_6_235_7F77E0` line 72640

675. `ecx+edx+0000006Ch`
   - Place in: `Proc_6_235_7F77E0` line 72699

676. `SELECT id_quest,id_level,timestamp_done,timestamp_accepted,time_next FROM users_quests WHERE id_user='`
   - Place in: `Proc_6_236_7F8540` line 72820

677. `MXLVL`
   - Place in: `Proc_6_236_7F8540` line 72857

678. `ecx+edx+0000042Ch`
   - Place in: `Proc_6_236_7F8540` line 72927

679. `edx+eax+0000042Ch`
   - Place in: `Proc_6_236_7F8540` line 72955

680. `ecx+eax+00000168h`
   - Place in: `Proc_6_237_7F9ED0` line 73291

681. `com.server.socket.game.activitypoints_0.interval`
   - Place in: `Proc_6_238_7FA670` line 73309

682. `com.server.socket.game.activitypoints_0.max`
   - Place in: `Proc_6_238_7FA670` line 73327

683. `com.server.socket.game.activitypoints_0.amount`
   - Place in: `Proc_6_238_7FA670` line 73342

684. `com.server.socket.game.activitypoints_1.interval`
   - Place in: `Proc_6_238_7FA670` line 73392

685. `com.server.socket.game.activitypoints_1.max`
   - Place in: `Proc_6_238_7FA670` line 73409

686. `com.server.socket.game.activitypoints_1.amount`
   - Place in: `Proc_6_238_7FA670` line 73424

687. `com.server.socket.game.activitypoints_2.interval`
   - Place in: `Proc_6_238_7FA670` line 73474

688. `com.server.socket.game.activitypoints_2.max`
   - Place in: `Proc_6_238_7FA670` line 73492

689. `com.server.socket.game.activitypoints_2.amount`
   - Place in: `Proc_6_238_7FA670` line 73507

690. `UPDATE users SET activitypoints_2=activitypoints_2+`
   - Place in: `Proc_6_238_7FA670` line 73518

691. `com.server.socket.game.activitypoints_3.interval`
   - Place in: `Proc_6_238_7FA670` line 73557

692. `com.server.socket.game.activitypoints_3.max`
   - Place in: `Proc_6_238_7FA670` line 73575

693. `com.server.socket.game.activitypoints_3.amount`
   - Place in: `Proc_6_238_7FA670` line 73590

694. `com.server.socket.game.activitypoints_4.interval`
   - Place in: `Proc_6_238_7FA670` line 73640

695. `com.server.socket.game.activitypoints_4.max`
   - Place in: `Proc_6_238_7FA670` line 73659

696. `com.server.socket.game.activitypoints_4.amount`
   - Place in: `Proc_6_238_7FA670` line 73674

697. `ecx+edx+00000420h`
   - Place in: `Proc_6_239_7FC170` line 73809

698. `edx+edi+00000420h`
   - Place in: `Proc_6_239_7FC170` line 73837

699. `UPDATE users SET settings_sound='`
   - Place in: `Proc_6_239_7FC170` line 73904

700. `edi+eax+000000C4h`
   - Place in: `Proc_6_239_7FC170` line 74763

701. `GAME -- index: `
   - Place in: `Proc_6_239_7FC170` line 75066

702. `[selectData_LoggedIn :: `
   - Place in: `Proc_6_239_7FC170` line 75069

703. `s > `
   - Place in: `Proc_6_239_7FC170` line 75069, `Proc_5_0_6D3CD0` line 16, `Proc_5_1_6D4110` line 44, `Proc_5_2_6D4690` line 76, `Proc_5_3_6D4CF0` line 120

704. `eax+esi+00000406h`
   - Place in: `Proc_6_23_6E9A90` line 3769

705. `edx+ecx+00000404h`
   - Place in: `Proc_6_23_6E9A90` line 3769, `tmrSigner_Timer` line 2081

706. `edx+edi+00000026h`
   - Place in: `Proc_6_23_6E9A90` line 3770

707. `edx+eax+0000001Ch`
   - Place in: `Proc_6_23_6E9A90` line 3809, `Proc_6_132_75D4A0` line 36042, `tmrSigner_Timer` line 5068

708. `ecx+eax+0000001Ch`
   - Place in: `Proc_6_23_6E9A90` line 3829

709. `{SERVE_`
   - Place in: `Proc_6_23_6E9A90` line 3864

710. `edx+eax+00000004h`
   - Place in: `Proc_6_23_6E9A90` line 3918, `Proc_6_107_74B7E0` line 30669, `Proc_6_128_756190` line 34431, `Proc_6_150_777FA0` line 42628, `Proc_6_189_7D0630` line 63782, `Proc_6_203_7D7F80` line 66594, `Proc_6_206_7DA450` line 66853, `tmrSigner_Timer` line 723

711. `edx+eax+0000003Ah`
   - Place in: `Proc_6_23_6E9A90` line 3938, `Proc_6_82_731070` line 23691, `Proc_6_183_7CABF0` line 61608, `Proc_6_186_7CD040` line 62333, `tmrBots_Timer` line 8968, `Proc_0_24_68EEF0` line 13658

712. `edx+esi+00000054h`
   - Place in: `Proc_6_23_6E9A90` line 3966

713. `edx+eax+00000008h`
   - Place in: `Proc_6_23_6E9A90` line 3986, `Proc_6_107_74B7E0` line 30743, `Proc_6_183_7CABF0` line 62285, `Proc_6_203_7D7F80` line 66350, `Proc_6_226_7F0B20` line 70600, `Proc_6_228_7F2AF0` line 71166, `tmrSigner_Timer` line 4539

714. `esi+edi+00000002h`
   - Place in: `Proc_6_23_6E9A90` line 4035

715. `ecx+eax+00000004h`
   - Place in: `Proc_6_23_6E9A90` line 4047, `Proc_6_39_711650` line 14172, `Proc_6_179_7C7790` line 61392, `Proc_6_183_7CABF0` line 62156, `tmrSigner_Timer` line 8000, `tmrBots_Timer` line 9127

716. `ecx+eax+00000002h`
   - Place in: `Proc_6_23_6E9A90` line 4080, `Proc_6_84_733600` line 25681

717. `edx+esi+00000048h`
   - Place in: `Proc_6_23_6E9A90` line 4080

718. `edx+eax+0000003Eh`
   - Place in: `Proc_6_23_6E9A90` line 4192

719. `ecx+eax+00000008h`
   - Place in: `Proc_6_23_6E9A90` line 4278, `Proc_6_39_711650` line 14139, `Proc_6_107_74B7E0` line 30766, `Proc_6_150_777FA0` line 42767, `Proc_6_203_7D7F80` line 66373, `Proc_6_225_7EFBD0` line 70334, `Proc_6_226_7F0B20` line 70543, `Proc_6_229_7F3070` line 71338

720. `ecx+edi+00000008h`
   - Place in: `Proc_6_23_6E9A90` line 4279, `Proc_6_136_765F10` line 36639

721. ` 0.0`
   - Place in: `Proc_6_23_6E9A90` line 4317, `Proc_0_24_68EEF0` line 13698

722. `ecx+edi+00000068h`
   - Place in: `Proc_6_23_6E9A90` line 4317

723. `edx+edi+00000008h`
   - Place in: `Proc_6_23_6E9A90` line 4356

724. `_spk`
   - Place in: `Proc_6_23_6E9A90` line 4369

725. `edx+eax+00000018h`
   - Place in: `Proc_6_23_6E9A90` line 4407, `Proc_6_132_75D4A0` line 35288, `Proc_6_186_7CD040` line 62973

726. `ecx+eax+00000018h`
   - Place in: `Proc_6_23_6E9A90` line 4427, `tmrBots_Timer` line 9092

727. `_here`
   - Place in: `Proc_6_23_6E9A90` line 4478

728. `edx+eax+00000036h`
   - Place in: `Proc_6_23_6E9A90` line 4581, `tmrBots_Timer` line 8694, `tmrRollers_Timer` line 10358

729. `edx+eax+00000006h`
   - Place in: `Proc_6_23_6E9A90` line 4594, `Proc_6_179_7C7790` line 61545, `Proc_6_188_7CF3C0` line 63435, `Proc_6_189_7D0630` line 63824, `Proc_6_226_7F0B20` line 70778, `Proc_6_243_7FFEB0` line 76223, `tmrSigner_Timer` line 1005, `tmrRollers_Timer` line 10737; plus 1 more functions

730. `ecx+eax+00000088h`
   - Place in: `Proc_6_23_6E9A90` line 4601, `Proc_6_155_795C90` line 49098, `Proc_6_159_79FCD0` line 52630, `tmrSigner_Timer` line 6035, `tmrRollers_Timer` line 9880, `Proc_0_24_68EEF0` line 14258

731. `ecx+eax+00000034h`
   - Place in: `Proc_6_23_6E9A90` line 4621, `Proc_6_150_777FA0` line 41619, `tmrBots_Timer` line 8723, `Proc_0_24_68EEF0` line 13283

732. `edi+ecx+00000034h`
   - Place in: `Proc_6_23_6E9A90` line 4660, `tmrBots_Timer` line 8744

733. `edi+ecx+00000036h`
   - Place in: `Proc_6_23_6E9A90` line 4879

734. `gst sad`
   - Place in: `Proc_6_23_6E9A90` line 5232

735. `com.client.bot.pet.reject_command.speech`
   - Place in: `Proc_6_23_6E9A90` line 5252

736. `edx+eax+00000078h`
   - Place in: `Proc_6_23_6E9A90` line 5317, `tmrBots_Timer` line 9050, `Proc_0_24_68EEF0` line 12597

737. `fuse_cmd_statistics`
   - Place in: `Proc_6_23_6E9A90` line 5499

738. `SELECT (UNIX_TIMESTAMP()-value) FROM settings WHERE variable='com.server.socket.listen.time' LIMIT 1`
   - Place in: `Proc_6_23_6E9A90` line 5532

739. `minutes`
   - Place in: `Proc_6_23_6E9A90` line 5544

740. `seconds`
   - Place in: `Proc_6_23_6E9A90` line 5546

741. `SELECT COUNT(*) FROM users WHERE id_socket IS NOT NULL`
   - Place in: `Proc_6_23_6E9A90` line 5548

742. `Server is running for `
   - Place in: `Proc_6_23_6E9A90` line 5548

743. ` sockets are listened.`
   - Place in: `Proc_6_23_6E9A90` line 5549

744. ` users are online. / `
   - Place in: `Proc_6_23_6E9A90` line 5549

745. `\xb6 Please note that the server sometimes would be restarted for updates, which has not to-do with the stability of the Emulator.`
   - Place in: `Proc_6_23_6E9A90` line 5550
   - Length: 127 characters

746. `fuse_cmd_drink`
   - Place in: `Proc_6_23_6E9A90` line 5554

747. `edx+eax+000003F8h`
   - Place in: `Proc_6_23_6E9A90` line 5590, `Proc_6_28_709DA0` line 12491, `Proc_6_31_70DE80` line 12962, `Proc_6_48_7151E0` line 14741, `Proc_6_49_715D30` line 14878, `Proc_6_52_7172B0` line 15213, `Proc_6_111_74DF70` line 31064, `Proc_6_123_754020` line 32862; plus 2 more functions

748. `edx+ecx+000003D4h`
   - Place in: `Proc_6_23_6E9A90` line 5590, `Proc_6_28_709DA0` line 12491, `Proc_6_48_7151E0` line 14741, `Proc_6_49_715D30` line 14878, `Proc_6_52_7172B0` line 15260

749. `fuse_cmd_follow`
   - Place in: `Proc_6_23_6E9A90` line 5620

750. `ecx+ebx+00000070h`
   - Place in: `Proc_6_23_6E9A90` line 5701, `Proc_6_144_76BE70` line 38132, `Proc_6_169_7C0DC0` line 59192

751. `" is on hotelview, unable to perform action.`
   - Place in: `Proc_6_23_6E9A90` line 5708

752. `The user "`
   - Place in: `Proc_6_23_6E9A90` line 5708

753. `\xb6 The target user must be in a room, please re-try again if s(he) is in a room!`
   - Place in: `Proc_6_23_6E9A90` line 5709

754. `" is not online or does not exist, unable to perform action.`
   - Place in: `Proc_6_23_6E9A90` line 5717

755. `\xb6 The target user must be online, please re-try again if s(he) is online!`
   - Place in: `Proc_6_23_6E9A90` line 5720

756. `edx+ebx+00000070h`
   - Place in: `Proc_6_23_6E9A90` line 5795, `Proc_6_144_76BE70` line 38113, `Proc_6_155_795C90` line 48349

757. `:disconnect`
   - Place in: `Proc_6_23_6E9A90` line 5819

758. `fuse_cmd_disconnect`
   - Place in: `Proc_6_23_6E9A90` line 5820

759. `ecx+eax+00000478h`
   - Place in: `Proc_6_23_6E9A90` line 5936, `Proc_6_26_7034C0` line 11071, `Proc_6_27_706920` line 11724, `Proc_6_28_709DA0` line 12364, `Proc_6_81_730010` line 23270, `Proc_6_173_7C3430` line 59677

760. `fuse_cmd_`
   - Place in: `Proc_6_23_6E9A90` line 5999

761. `\xb6 Please note that some commands require additional syntax, which hasn't been listed up here!`
   - Place in: `Proc_6_23_6E9A90` line 6053

762. `fuse_cmd_whosonline`
   - Place in: `Proc_6_23_6E9A90` line 6058

763. `ecx+eax+000000C0h`
   - Place in: `Proc_6_23_6E9A90` line 6121, `Proc_6_38_70FD10` line 13514, `Proc_6_39_711650` line 13870, `Proc_6_55_71A6E0` line 17988, `Proc_6_79_72A430` line 22560, `Proc_6_128_756190` line 33771, `Proc_6_132_75D4A0` line 35110, `tmrSigner_Timer` line 3589; plus 2 more functions

764. `:ping`
   - Place in: `Proc_6_23_6E9A90` line 6128

765. `fuse_cmd_ping`
   - Place in: `Proc_6_23_6E9A90` line 6129

766. `The session of the user '`
   - Place in: `Proc_6_23_6E9A90` line 6187

767. ` ms.`
   - Place in: `Proc_6_23_6E9A90` line 6188

768. `' responsed in `
   - Place in: `Proc_6_23_6E9A90` line 6188

769. `:ping_all`
   - Place in: `Proc_6_23_6E9A90` line 6190

770. `fuse_cmd_ping_all`
   - Place in: `Proc_6_23_6E9A90` line 6191

771. ` ms)`
   - Place in: `Proc_6_23_6E9A90` line 6261

772. `:location`
   - Place in: `Proc_6_23_6E9A90` line 6268

773. `:position`
   - Place in: `Proc_6_23_6E9A90` line 6270

774. `:effect`
   - Place in: `Proc_6_23_6E9A90` line 6272

775. `fuse_cmd_effect`
   - Place in: `Proc_6_23_6E9A90` line 6273

776. `eax+edi+0000041Ch`
   - Place in: `Proc_6_23_6E9A90` line 6344

777. `:debug_log`
   - Place in: `Proc_6_23_6E9A90` line 6347

778. `:reset_log`
   - Place in: `Proc_6_23_6E9A90` line 6384

779. `:asd`
   - Place in: `Proc_6_23_6E9A90` line 6422

780. `:ha`
   - Place in: `Proc_6_23_6E9A90` line 6424

781. `:hotelalert`
   - Place in: `Proc_6_23_6E9A90` line 6426

782. `:ra`
   - Place in: `Proc_6_23_6E9A90` line 6428

783. `:rankalert`
   - Place in: `Proc_6_23_6E9A90` line 6430

784. `:ranktransfer`
   - Place in: `Proc_6_23_6E9A90` line 6432

785. `fuse_cmd_ranktransfer`
   - Place in: `Proc_6_23_6E9A90` line 6433

786. `:rankdisconnect`
   - Place in: `Proc_6_23_6E9A90` line 6614

787. `fuse_cmd_rankdisconnect`
   - Place in: `Proc_6_23_6E9A90` line 6616

788. `:voucher`
   - Place in: `Proc_6_23_6E9A90` line 6761

789. `fuse_cmd_voucher`
   - Place in: `Proc_6_23_6E9A90` line 6763

790. `INSERT INTO vouchers(contain_credits,name) VALUES('`
   - Place in: `Proc_6_23_6E9A90` line 6808

791. `INSERT INTO vouchers(contain_product,name) VALUES('`
   - Place in: `Proc_6_23_6E9A90` line 6814

792. `Your voucher code has been generated, this is the voucher code:                                   `
   - Place in: `Proc_6_23_6E9A90` line 6821

793. `\xb6 Please note that vouchers never expire and can not be read again, so don't lose this voucher code!`
   - Place in: `Proc_6_23_6E9A90` line 6826

794. `:buy`
   - Place in: `Proc_6_23_6E9A90` line 6829

795. `fuse_cmd_buy`
   - Place in: `Proc_6_23_6E9A90` line 6831

796. `ecx+eax+0000044Ch`
   - Place in: `Proc_6_23_6E9A90` line 6916, `Proc_6_69_723630` line 19145, `Proc_6_130_75B770` line 34784, `Proc_6_137_766470` line 36698, `Proc_6_141_76A670` line 37613, `Proc_6_155_795C90` line 48495, `Proc_6_159_79FCD0` line 53773, `Proc_6_202_7D6760` line 65923; plus 2 more functions

797. `edx+esi+000000B0h`
   - Place in: `Proc_6_23_6E9A90` line 6941, `Proc_6_228_7F2AF0` line 71176

798. `eax+ecx+00000448h`
   - Place in: `Proc_6_23_6E9A90` line 6944, `Proc_6_69_723630` line 19168, `Proc_6_89_73EA10` line 27412, `Proc_6_128_756190` line 34087, `Proc_6_130_75B770` line 34806, `Proc_6_139_768100` line 37204, `Proc_6_140_769400` line 37511, `Proc_6_141_76A670` line 37703; plus 3 more functions

799. `:setimage`
   - Place in: `Proc_6_23_6E9A90` line 6946

800. `:set_image`
   - Place in: `Proc_6_23_6E9A90` line 6949

801. `:giveshells`
   - Place in: `Proc_6_23_6E9A90` line 6952

802. `fuse_cmd_giveshells`
   - Place in: `Proc_6_23_6E9A90` line 6955

803. `You reached the maximum of shells.`
   - Place in: `Proc_6_23_6E9A90` line 7022

804. `\xb6 The maximum is 99999 shells!`
   - Place in: `Proc_6_23_6E9A90` line 7032

805. `UPDATE users SET activitypoints_4=activitypoints_4+`
   - Place in: `Proc_6_23_6E9A90` line 7051, `Proc_6_238_7FA670` line 73685

806. `ecx+edx+000000E8h`
   - Place in: `Proc_6_23_6E9A90` line 7084

807. `:givepixels`
   - Place in: `Proc_6_23_6E9A90` line 7123

808. `fuse_cmd_givepixels`
   - Place in: `Proc_6_23_6E9A90` line 7126

809. `You reached the maximum of pixels.`
   - Place in: `Proc_6_23_6E9A90` line 7193

810. `\xb6 The maximum is 99999 pixels!`
   - Place in: `Proc_6_23_6E9A90` line 7205

811. `UPDATE users SET activitypoints_0=activitypoints_0+`
   - Place in: `Proc_6_23_6E9A90` line 7220, `Proc_6_238_7FA670` line 73352

812. `edx+ecx+000000D8h`
   - Place in: `Proc_6_23_6E9A90` line 7253

813. `:givecredits`
   - Place in: `Proc_6_23_6E9A90` line 7274

814. `fuse_cmd_givecredits`
   - Place in: `Proc_6_23_6E9A90` line 7277

815. `You reached the maximum of credits.`
   - Place in: `Proc_6_23_6E9A90` line 7343

816. `\xb6 The maximum is 99999 credits!`
   - Place in: `Proc_6_23_6E9A90` line 7355

817. `ecx+edx+0000012Ch`
   - Place in: `Proc_6_23_6E9A90` line 7403

818. `:givesnowflakes`
   - Place in: `Proc_6_23_6E9A90` line 7427

819. `fuse_cmd_givesnowflakes`
   - Place in: `Proc_6_23_6E9A90` line 7430

820. `You reached the maximum of snowflakes.`
   - Place in: `Proc_6_23_6E9A90` line 7497

821. `\xb6 The maximum is 99999 snowflakes!`
   - Place in: `Proc_6_23_6E9A90` line 7509

822. `UPDATE users SET activitypoints_1=activitypoints_1+`
   - Place in: `Proc_6_23_6E9A90` line 7524, `Proc_6_238_7FA670` line 73435

823. `ecx+edx+000000DCh`
   - Place in: `Proc_6_23_6E9A90` line 7557

824. `:givemoney`
   - Place in: `Proc_6_23_6E9A90` line 7578

825. `fuse_cmd_givemoney`
   - Place in: `Proc_6_23_6E9A90` line 7581

826. `You reached the maximum of mad money.`
   - Place in: `Proc_6_23_6E9A90` line 7648

827. `\xb6 The maximum is 99999 mad money!`
   - Place in: `Proc_6_23_6E9A90` line 7660

828. `UPDATE users SET activitypoints_3=activitypoints_3+`
   - Place in: `Proc_6_23_6E9A90` line 7675, `Proc_6_238_7FA670` line 73601

829. `ecx+edx+000000E4h`
   - Place in: `Proc_6_23_6E9A90` line 7708

830. `:givebadge`
   - Place in: `Proc_6_23_6E9A90` line 7739

831. `fuse_cmd_givebadge`
   - Place in: `Proc_6_23_6E9A90` line 7742

832. `INSERT INTO users_badges(id_badge,id_user,id_slot) VALUES('`
   - Place in: `Proc_6_23_6E9A90` line 7819

833. `ecx+edx+000000B0h`
   - Place in: `Proc_6_23_6E9A90` line 7825, `Proc_6_32_70EAB0` line 13256, `Proc_6_38_70FD10` line 13576, `Proc_6_64_721650` line 18614, `Proc_6_105_74AD50` line 30300, `Proc_6_128_756190` line 33678, `Proc_6_132_75D4A0` line 35742, `Proc_6_137_766470` line 36726; plus 13 more functions

834. `ecx+eax+0000001Eh`
   - Place in: `Proc_6_23_6E9A90` line 7848

835. `SELECT id FROM users_badges WHERE id_badge='`
   - Place in: `Proc_6_23_6E9A90` line 7849, `Proc_6_107_74B7E0` line 30690, `Proc_6_132_75D4A0` line 35784, `Proc_6_203_7D7F80` line 66178

836. `edx+eax+00000014h`
   - Place in: `Proc_6_23_6E9A90` line 7883, `Proc_6_26_7034C0` line 10935, `Proc_6_27_706920` line 11588, `Proc_6_28_709DA0` line 12239, `Proc_6_107_74B7E0` line 30559, `tmrSigner_Timer` line 5776

837. `ebx+ecx+0000001Eh`
   - Place in: `Proc_6_23_6E9A90` line 7906

838. `:takebadge`
   - Place in: `Proc_6_23_6E9A90` line 7912

839. `fuse_cmd_takebadge`
   - Place in: `Proc_6_23_6E9A90` line 7915

840. `DELETE FROM users_badges WHERE id_badge='`
   - Place in: `Proc_6_23_6E9A90` line 7992

841. `' AND id_user='`
   - Place in: `Proc_6_23_6E9A90` line 7996, `Proc_6_109_74DBD0` line 31026, `Proc_6_167_7BECA0` line 58992, `Proc_6_194_7D3180` line 64665

842. `:set_rank`
   - Place in: `Proc_6_23_6E9A90` line 8012

843. `fuse_cmd_set_rank`
   - Place in: `Proc_6_23_6E9A90` line 8015

844. `,name='MOD-`
   - Place in: `Proc_6_23_6E9A90` line 8097

845. `edx+eax+000000C0h`
   - Place in: `Proc_6_23_6E9A90` line 8097, `Proc_6_32_70EAB0` line 13331, `Proc_6_38_70FD10` line 13632, `Proc_6_39_711650` line 14101, `Proc_6_167_7BECA0` line 58860, `Proc_6_168_7C05F0` line 59125, `Proc_6_174_7C3BC0` line 59890, `Proc_6_176_7C4EE0` line 60401; plus 3 more functions

846. `UPDATE users SET level='`
   - Place in: `Proc_6_23_6E9A90` line 8117

847. `:invisible`
   - Place in: `Proc_6_23_6E9A90` line 8169

848. `fuse_cmd_invisible`
   - Place in: `Proc_6_23_6E9A90` line 8172

849. `:teleport`
   - Place in: `Proc_6_23_6E9A90` line 8305

850. `fuse_cmd_teleport`
   - Place in: `Proc_6_23_6E9A90` line 8308

851. `:roomalert`
   - Place in: `Proc_6_23_6E9A90` line 8380

852. `fuse_cmd_roomalert`
   - Place in: `Proc_6_23_6E9A90` line 8383

853. `:roomkick`
   - Place in: `Proc_6_23_6E9A90` line 8493

854. `fuse_cmd_roomkick`
   - Place in: `Proc_6_23_6E9A90` line 8496

855. `:recache_room`
   - Place in: `Proc_6_23_6E9A90` line 8636

856. `fuse_cmd_recache_room`
   - Place in: `Proc_6_23_6E9A90` line 8639

857. `ecx+eax+000000B4h`
   - Place in: `Proc_6_23_6E9A90` line 8728, `Proc_6_43_713680` line 14257, `Proc_6_45_714B60` line 14551, `Proc_6_46_714D50` line 14572, `Proc_6_48_7151E0` line 14681, `Proc_6_49_715D30` line 14931, `Proc_6_50_7166B0` line 15015, `Proc_6_60_720060` line 18162; plus 18 more functions

858. `\\CACHE\\rooms\\`
   - Place in: `Proc_6_23_6E9A90` line 8749

859. `\\CACHE\\pathfinder\\`
   - Place in: `Proc_6_23_6E9A90` line 8760

860. `:alert`
   - Place in: `Proc_6_23_6E9A90` line 8770

861. `fuse_cmd_alert`
   - Place in: `Proc_6_23_6E9A90` line 8773

862. `:kick`
   - Place in: `Proc_6_23_6E9A90` line 8858

863. `fuse_cmd_kick`
   - Place in: `Proc_6_23_6E9A90` line 8861

864. `:roomshutup`
   - Place in: `Proc_6_23_6E9A90` line 8950

865. `fuse_cmd_roomshutup`
   - Place in: `Proc_6_23_6E9A90` line 8953, `Proc_6_26_7034C0` line 10987, `Proc_6_27_706920` line 11640, `Proc_6_28_709DA0` line 12291

866. `:roomunmute`
   - Place in: `Proc_6_23_6E9A90` line 9010

867. `:shutup`
   - Place in: `Proc_6_23_6E9A90` line 9070

868. `fuse_cmd_shutup`
   - Place in: `Proc_6_23_6E9A90` line 9073, `Proc_6_26_7034C0` line 11017, `Proc_6_27_706920` line 11670, `Proc_6_28_709DA0` line 12321

869. `:setname`
   - Place in: `Proc_6_23_6E9A90` line 9140

870. `:set_name`
   - Place in: `Proc_6_23_6E9A90` line 9143

871. `:unmute`
   - Place in: `Proc_6_23_6E9A90` line 9146

872. `:clean_inventory`
   - Place in: `Proc_6_23_6E9A90` line 9216

873. `fuse_cmd_clean_inventory`
   - Place in: `Proc_6_23_6E9A90` line 9219

874. `SELECT furnitures.id FROM furnitures,products WHERE furnitures.id_owner='`
   - Place in: `Proc_6_23_6E9A90` line 9268

875. `' AND furnitures.id_owner > 0 AND products.id=furnitures.id_product AND products.is_tradeable='0' GROUP BY furnitures.id LIMIT 250`
   - Place in: `Proc_6_23_6E9A90` line 9270
   - Length: 130 characters

876. `:clean_inventory_all`
   - Place in: `Proc_6_23_6E9A90` line 9346

877. `' AND furnitures.id_owner > 0 AND products.id=furnitures.id_product GROUP BY furnitures.id LIMIT 250`
   - Place in: `Proc_6_23_6E9A90` line 9400

878. `edx+edi+000000B0h`
   - Place in: `Proc_6_23_6E9A90` line 9480

879. `:copyfigure`
   - Place in: `Proc_6_23_6E9A90` line 9489

880. `fuse_cmd_copyfigure`
   - Place in: `Proc_6_23_6E9A90` line 9492

881. `ecx+esi+000000CCh`
   - Place in: `Proc_6_23_6E9A90` line 9562

882. `eax+esi+000000C8h`
   - Place in: `Proc_6_23_6E9A90` line 9585

883. `eax+ebx+000000CCh`
   - Place in: `Proc_6_23_6E9A90` line 9619

884. `eax+ecx+000000D0h`
   - Place in: `Proc_6_23_6E9A90` line 9707, `Proc_6_39_711650` line 14101, `Proc_6_230_7F3D20` line 71586

885. `UPDATE users SET figure='`
   - Place in: `Proc_6_23_6E9A90` line 9746, `Proc_6_163_7B3480` line 57846

886. `',gender='`
   - Place in: `Proc_6_23_6E9A90` line 9748

887. `:recache_catalog`
   - Place in: `Proc_6_23_6E9A90` line 9787

888. `:recache_catelogue`
   - Place in: `Proc_6_23_6E9A90` line 9790

889. `:recache_settings`
   - Place in: `Proc_6_23_6E9A90` line 9794

890. `:recache_pets`
   - Place in: `Proc_6_23_6E9A90` line 9797

891. `:recache_navigator`
   - Place in: `Proc_6_23_6E9A90` line 9800

892. `:reache_faq`
   - Place in: `Proc_6_23_6E9A90` line 9804

893. `fuse_cmd_recache_settings`
   - Place in: `Proc_6_23_6E9A90` line 9810

894. `fuse_cmd_recache_catalog`
   - Place in: `Proc_6_23_6E9A90` line 9851

895. `fuse_cmd_setname`
   - Place in: `Proc_6_23_6E9A90` line 9892

896. `" will receive a name set request up on next sign in. If you'd like the user to change the name unfortunately, please use the disconnect speech-command.`
   - Place in: `Proc_6_23_6E9A90` line 9968
   - Length: 152 characters

897. `UPDATE users SET merge_name='1',tutorial_name='1' WHERE name='`
   - Place in: `Proc_6_23_6E9A90` line 9983

898. `fuse_cmd_setimage`
   - Place in: `Proc_6_23_6E9A90` line 9992

899. `ads_mpu_730`
   - Place in: `Proc_6_23_6E9A90` line 10198

900. `ads_background`
   - Place in: `Proc_6_23_6E9A90` line 10200

901. `imageUrl=`
   - Place in: `Proc_6_23_6E9A90` line 10202

902. `fuse_cmd_rankalert`
   - Place in: `Proc_6_23_6E9A90` line 10248

903. `fuse_cmd_ha`
   - Place in: `Proc_6_23_6E9A90` line 10456

904. `fuse_cmd_position`
   - Place in: `Proc_6_23_6E9A90` line 10533

905. `Your current position details:`
   - Place in: `Proc_6_23_6E9A90` line 10633

906. `positionX:                                                                                             `
   - Place in: `Proc_6_23_6E9A90` line 10643

907. `ecx+edx+00000404h`
   - Place in: `Proc_6_23_6E9A90` line 10645, `Proc_6_39_711650` line 14102, `Proc_6_97_747640` line 29406, `Proc_6_150_777FA0` line 41660, `Proc_0_24_68EEF0` line 15012

908. `positionY:                                                                                             `
   - Place in: `Proc_6_23_6E9A90` line 10653

909. `positionZ:                                                                                             `
   - Place in: `Proc_6_23_6E9A90` line 10663

910. `eax+ecx+00000408h`
   - Place in: `Proc_6_23_6E9A90` line 10665

911. `rotationX:                                                                                             `
   - Place in: `Proc_6_23_6E9A90` line 10671

912. `ecx+edx+00000400h`
   - Place in: `Proc_6_23_6E9A90` line 10673

913. `rotationY:                                                                                             `
   - Place in: `Proc_6_23_6E9A90` line 10681

914. `edx+eax+00000402h`
   - Place in: `Proc_6_23_6E9A90` line 10683

915. `roomID:                                                                                             `
   - Place in: `Proc_6_23_6E9A90` line 10691

916. `UPDATE users SET id_socket=null WHERE id = '`
   - Place in: `Proc_6_242_7FF0D0` line 75124

917. `UPDATE users SET lastonline_time=UNIX_TIMESTAMP(),online_time=online_time+`
   - Place in: `Proc_6_243_7FFEB0` line 75609

918. `eax+ecx+000003F4h`
   - Place in: `Proc_6_243_7FFEB0` line 75609

919. `UPDATE users_quests SET progress='`
   - Place in: `Proc_6_243_7FFEB0` line 75651

920. `edx+ebx+00000438h`
   - Place in: `Proc_6_243_7FFEB0` line 76039

921. `CHATWITHSOMEONE`
   - Place in: `Proc_6_26_7034C0` line 10850, `Proc_6_27_706920` line 11503, `Proc_6_28_709DA0` line 12154

922. `edx+eax+000000BCh`
   - Place in: `Proc_6_26_7034C0` line 10945, `Proc_6_27_706920` line 11598, `Proc_6_28_709DA0` line 12249, `Proc_6_43_713680` line 14237, `Proc_6_44_7145E0` line 14469, `Proc_6_45_714B60` line 14531, `Proc_6_47_714F60` line 14622, `Proc_6_48_7151E0` line 14661; plus 21 more functions

923. `edx+eax+00000020h`
   - Place in: `Proc_6_26_7034C0` line 10975, `Proc_6_27_706920` line 11628, `Proc_6_28_709DA0` line 12279, `tmrSigner_Timer` line 3390

924. `ecx+eax+0000003Ch`
   - Place in: `Proc_6_26_7034C0` line 10985, `Proc_6_27_706920` line 11638, `Proc_6_28_709DA0` line 12289, `Proc_6_159_79FCD0` line 52037

925. `edx+eax+00000478h`
   - Place in: `Proc_6_26_7034C0` line 11060, `Proc_6_27_706920` line 11713

926. `','0','`
   - Place in: `Proc_6_26_7034C0` line 11109, `Proc_6_132_75D4A0` line 35780, `Proc_6_232_7F45A0` line 71855, `Proc_6_233_7F5D60` line 72328

927. `{ `
   - Place in: `Proc_6_26_7034C0` line 11156, `Proc_6_27_706920` line 11809, `Proc_6_28_709DA0` line 12508, `Proc_6_210_7E1DC0` line 68974

928. `ecx+edx+00000470h`
   - Place in: `Proc_6_26_7034C0` line 11214, `Proc_6_27_706920` line 11879

929. `ecx+eax+00000474h`
   - Place in: `Proc_6_26_7034C0` line 11226, `Proc_6_27_706920` line 11891, `Proc_6_28_709DA0` line 12579

930. `ecx+eax+00000470h`
   - Place in: `Proc_6_26_7034C0` line 11260, `Proc_6_27_706920` line 11925, `Proc_6_28_709DA0` line 12613

931. `com.client.chat.typetofast_block.interval`
   - Place in: `Proc_6_26_7034C0` line 11262, `Proc_6_27_706920` line 11927, `Proc_6_28_709DA0` line 12615

932. `ebx+eax+000000BEh`
   - Place in: `Proc_6_26_7034C0` line 11354, `Proc_6_27_706920` line 12019, `Proc_6_28_709DA0` line 12707, `Proc_6_32_70EAB0` line 13272, `Proc_6_55_71A6E0` line 16195, `Proc_6_84_733600` line 25162, `Proc_6_107_74B7E0` line 30503, `Proc_6_144_76BE70` line 38661; plus 6 more functions

933. `edx+ebx+0000006Ch`
   - Place in: `Proc_6_26_7034C0` line 11371, `Proc_6_27_706920` line 12036, `Proc_6_28_709DA0` line 12724, `Proc_6_86_73B0D0` line 26583, `Proc_6_142_76B310` line 37821, `Proc_6_179_7C7790` line 60805

934. `','1','`
   - Place in: `Proc_6_27_706920` line 11762

935. `',UNIX_TIMESTAMP(),'(To:   `
   - Place in: `Proc_6_28_709DA0` line 12454

936. `ecx+edx+000000C0h`
   - Place in: `Proc_6_28_709DA0` line 12454, `Proc_6_38_70FD10` line 13679, `Proc_6_167_7BECA0` line 58770, `Proc_6_171_7C1520` line 59406, `Proc_6_173_7C3430` line 59743

937. `','2','`
   - Place in: `Proc_6_28_709DA0` line 12455, `Proc_6_107_74B7E0` line 30435

938. `edx+ecx+00000470h`
   - Place in: `Proc_6_28_709DA0` line 12567

939. `edi+ecx+00000164h`
   - Place in: `Proc_6_28_709DA0` line 12843

940. `INSERT INTO logs_moderation(id_type,id_user,id_target,id_target_2,timestamp,message,id_session) VALUES('5','`
   - Place in: `Proc_6_2_6D9880` line 412

941. `ecx+edx+000000B4h`
   - Place in: `Proc_6_2_6D9880` line 413, `Proc_6_3_6DA490` line 532, `Proc_6_4_6DAFB0` line 717, `Proc_6_12_6DFE90` line 1912, `Proc_6_63_721050` line 18536, `Proc_6_73_725540` line 19700, `Proc_6_84_733600` line 24208, `Proc_6_150_777FA0` line 47257; plus 5 more functions

942. `eax+ecx+00000090h`
   - Place in: `Proc_6_2_6D9880` line 414, `Proc_6_3_6DA490` line 533, `Proc_6_12_6DFE90` line 1913, `Proc_6_28_709DA0` line 12455, `Proc_6_73_725540` line 19700, `Proc_6_84_733600` line 25645, `Proc_6_155_795C90` line 48292, `Proc_6_159_79FCD0` line 54829

943. `' AND id_closed='0' AND timestamp_sent > UNIX_TIMESTAMP()-600 ORDER BY id DESC LIMIT 1`
   - Place in: `Proc_6_30_70DC90` line 12910

944. `SELECT id FROM staff_cfh WHERE id_user='`
   - Place in: `Proc_6_30_70DC90` line 12910

945. `DELETE FROM staff_cfh WHERE id='`
   - Place in: `Proc_6_30_70DC90` line 12914

946. `SELECT staff_cfh.id,staff_cfh.id_tab,users.id,users.name,staff_cfh.id_partner,staff_cfh.id_room,staff_cfh.id_category,staff_cfh.description,rooms.id,rooms.name,staff_cfh.id_picker FROM staff_cfh,users,rooms WHERE id_closed != '3' AND staff_cfh.timestamp_sent > UNIX_TIMESTAMP()-43200 AND users.id_socket IS NOT NULL AND rooms.id=staff_cfh.id_room LIMIT 1000`
   - Place in: `Proc_6_31_70DE80` line 12979
   - Length: 357 characters

947. `SELECT name FROM users WHERE id='`
   - Place in: `Proc_6_31_70DE80` line 13051

948. `SELECT id_closed FROM staff_cfh WHERE id_user='`
   - Place in: `Proc_6_32_70EAB0` line 13189

949. `INSERT INTO staff_cfh(id_user,id_room,id_category,id_partner,description,timestamp_sent) VALUES('`
   - Place in: `Proc_6_32_70EAB0` line 13256

950. `eax+ecx+000000C0h`
   - Place in: `Proc_6_32_70EAB0` line 13331, `Proc_6_167_7BECA0` line 58814, `Proc_6_171_7C1520` line 59361, `Proc_6_176_7C4EE0` line 60402, `Proc_6_239_7FC170` line 74863, `Proc_6_243_7FFEB0` line 75904

951. `ecx+edx+00000068h`
   - Place in: `Proc_6_32_70EAB0` line 13333, `Proc_6_55_71A6E0` line 16736

952. `SELECT id,name FROM faq WHERE name LIKE '`
   - Place in: `Proc_6_36_70F7B0` line 13392

953. `UPDATE users SET tutorial_name='1',merge_name='0' WHERE id='`
   - Place in: `Proc_6_38_70FD10` line 13503

954. `eax+ebx+000000C0h`
   - Place in: `Proc_6_38_70FD10` line 13611

955. `UPDATE users SET name='`
   - Place in: `Proc_6_38_70FD10` line 13632

956. `INSERT INTO logs_identity(previous_identity,new_identity,timestamp,id_session) VALUES('`
   - Place in: `Proc_6_38_70FD10` line 13679

957. `edx+ebx+00000090h`
   - Place in: `Proc_6_38_70FD10` line 13680, `Proc_6_84_733600` line 25597, `Proc_6_202_7D6760` line 66004

958. `ecx+eax+00000430h`
   - Place in: `Proc_6_38_70FD10` line 13691, `Proc_6_165_7BE0B0` line 58530, `Proc_6_167_7BECA0` line 58630, `Proc_6_168_7C05F0` line 59049, `Proc_6_169_7C0DC0` line 59150, `Proc_6_171_7C1520` line 59295, `Proc_6_173_7C3430` line 59691, `Proc_6_174_7C3BC0` line 59789; plus 3 more functions

959. `eax+ecx+00000430h`
   - Place in: `Proc_6_38_70FD10` line 13797, `Proc_6_167_7BECA0` line 58860, `Proc_6_171_7C1520` line 59362

960. `edx+eax+00000096h`
   - Place in: `Proc_6_39_711650` line 13843, `Proc_6_79_72A430` line 22277

961. `ecx+eax+00000094h`
   - Place in: `Proc_6_39_711650` line 13853, `Proc_6_84_733600` line 24489, `Proc_6_150_777FA0` line 45848

962. `SELECT COUNT(*) FROM users WHERE name='`
   - Place in: `Proc_6_39_711650` line 13899

963. `<Battle><Vip><Greek><Star><Idol><Geek><Emo><Driver><Epic><Astro><Maniac><Agent><Mega><Dude><Simpson><Tired><Sad><Tall><Joker><Jock><Emo><Fan><Super><Gamer><Slayer><Smart><Skater><Big><Aqua><Stealthy><Magi><Lover><Alien><Hyper><Uber><Rocker><Captain><Happy><Official><Spy>`
   - Place in: `Proc_6_39_711650` line 13902
   - Length: 271 characters

964. `<Rat><King><Master><Boy><Man><Punk><Lord><Chief>`
   - Place in: `Proc_6_39_711650` line 13903

965. `<Queen><Girl><Bee><Chick><Angel><Babe><Baby><Lady>`
   - Place in: `Proc_6_39_711650` line 13904

966. `ecx+edx+00000406h`
   - Place in: `Proc_6_39_711650` line 14103, `Proc_6_144_76BE70` line 38790, `Proc_0_24_68EEF0` line 15013

967. `ecx+edx+00000408h`
   - Place in: `Proc_6_39_711650` line 14103, `Proc_6_210_7E1DC0` line 68609, `tmrRollers_Timer` line 10144, `Proc_0_24_68EEF0` line 15013

968. `ecx+eax+0000003Ah`
   - Place in: `Proc_6_39_711650` line 14125

969. `ecx+eax+0000000Ch`
   - Place in: `Proc_6_39_711650` line 14150, `Proc_6_150_777FA0` line 42704, `Proc_6_224_7EF5A0` line 70197, `Proc_6_226_7F0B20` line 70978

970. `ecx+eax+00000010h`
   - Place in: `Proc_6_39_711650` line 14161, `Proc_6_179_7C7790` line 61404, `Proc_6_186_7CD040` line 62950

971. `ecx+eax+0000002Ch`
   - Place in: `Proc_6_39_711650` line 14182, `Proc_6_179_7C7790` line 61109, `Proc_6_188_7CF3C0` line 63518, `Proc_0_24_68EEF0` line 12640

972. `INSERT INTO logs_moderation(id_type,id_user,id_target,id_target_2,timestamp,message,id_session) VALUES('6','`
   - Place in: `Proc_6_3_6DA490` line 531

973. `eax+ebx+000000B0h`
   - Place in: `Proc_6_3_6DA490` line 531, `Proc_6_17_6E48D0` line 2995, `Proc_6_26_7034C0` line 11107, `Proc_6_27_706920` line 11760, `Proc_6_32_70EAB0` line 13331, `Proc_6_38_70FD10` line 13632, `Proc_6_64_721650` line 18613, `Proc_6_79_72A430` line 22424; plus 9 more functions

974. `INSERT INTO users_bans(id_user,id_partner,message,timestamp_expire,timestamp_submit,ipaddress) VALUES('`
   - Place in: `Proc_6_3_6DA490` line 557

975. `',UNIX_TIMESTAMP()+`
   - Place in: `Proc_6_3_6DA490` line 558

976. `ecx+edx+0000002Ch`
   - Place in: `Proc_6_3_6DA490` line 559, `Proc_6_179_7C7790` line 61140

977. `INSERT INTO users_bans(id_user,id_partner,message,timestamp_expire,timestamp_submit) VALUES('`
   - Place in: `Proc_6_3_6DA490` line 572

978. `UPDATE users SET login_session=NULL WHERE id='`
   - Place in: `Proc_6_3_6DA490` line 575

979. `SELECT rooms.id,rooms.name,rooms.description,rooms.status_door,rooms.id_category,rooms.visitors_max,models.visitors_max,tag_1,tag_2,NULL,rooms.allow_otherspets,rooms.allow_feedpets,rooms.allow_walkthrough,rooms.disable_walls FROM rooms,models WHERE rooms.id='`
   - Place in: `Proc_6_43_713680` line 14257
   - Length: 259 characters

980. `' AND models.id=rooms.id_model LIMIT 1`
   - Place in: `Proc_6_43_713680` line 14258, `Proc_6_78_7279A0` line 20249, `Proc_5_5_6D64D0` line 425

981. `SELECT users.id,users.name FROM rooms_rights,users WHERE rooms_rights.id_room='`
   - Place in: `Proc_6_43_713680` line 14295

982. `' AND users.id=rooms_rights.id_user LIMIT 250`
   - Place in: `Proc_6_43_713680` line 14296

983. `UPDATE rooms SET icon='`
   - Place in: `Proc_6_44_7145E0` line 14502

984. `SELECT status_door FROM rooms WHERE id='`
   - Place in: `Proc_6_46_714D50` line 14572, `Proc_6_48_7151E0` line 14681

985. `ecx+eax+000000BCh`
   - Place in: `Proc_6_46_714D50` line 14587, `Proc_6_50_7166B0` line 14953, `Proc_6_61_720490` line 18239, `Proc_6_62_7209F0` line 18369, `Proc_6_65_721A10` line 18633, `Proc_6_66_721D60` line 18707, `Proc_6_68_723170` line 18990, `Proc_6_69_723630` line 19011; plus 6 more functions

986. `UPDATE users SET homeroom='`
   - Place in: `Proc_6_47_714F60` line 14640

987. `,null`
   - Place in: `Proc_6_48_7151E0` line 14755

988. `INSERT INTO rooms_events(id_room,id_user,name,description,id_category,tag_1,tag_2,timestamp,name_category) VALUES('`
   - Place in: `Proc_6_48_7151E0` line 14779

989. `ecx+edi+000000B0h`
   - Place in: `Proc_6_48_7151E0` line 14780, `Proc_6_65_721A10` line 18675, `Proc_6_73_725540` line 19699, `Proc_6_117_751880` line 31908, `Proc_6_118_751A80` line 31927, `Proc_6_119_751C80` line 31945, `Proc_6_120_751E80` line 31963, `Proc_6_121_752080` line 31982; plus 2 more functions

990. `',name='`
   - Place in: `Proc_6_49_715D30` line 14918, `Proc_6_52_7172B0` line 15443

991. `UPDATE rooms_events SET id_user='`
   - Place in: `Proc_6_49_715D30` line 14918

992. `',description='`
   - Place in: `Proc_6_49_715D30` line 14919, `Proc_6_52_7172B0` line 15444

993. `ecx+ebx+000000B4h`
   - Place in: `Proc_6_49_715D30` line 14919, `Proc_6_63_721050` line 18510, `Proc_6_65_721A10` line 18675, `Proc_6_82_731070` line 24085, `Proc_6_84_733600` line 24147, `Proc_6_107_74B7E0` line 30403, `Proc_6_150_777FA0` line 47158, `Proc_6_159_79FCD0` line 54168; plus 3 more functions

994. `SELECT id_slot,id_owner FROM rooms WHERE id='`
   - Place in: `Proc_6_4_6DAFB0` line 633

995. `INSERT INTO logs_moderation(id_type,id_user,id_target,timestamp,message,id_session) VALUES('1','`
   - Place in: `Proc_6_4_6DAFB0` line 681

996. `eax+ecx+000000B4h`
   - Place in: `Proc_6_4_6DAFB0` line 682, `Proc_6_23_6E9A90` line 10693, `Proc_6_26_7034C0` line 11108, `Proc_6_27_706920` line 11761, `Proc_6_28_709DA0` line 12454, `Proc_6_48_7151E0` line 14779, `Proc_6_82_731070` line 24098, `Proc_6_89_73EA10` line 27869; plus 8 more functions

997. `edx+eax+00000090h`
   - Place in: `Proc_6_4_6DAFB0` line 683, `Proc_6_26_7034C0` line 11109, `Proc_6_27_706920` line 11762, `Proc_6_53_718E00` line 15805, `Proc_6_150_777FA0` line 47752

998. `INSERT INTO logs_moderation(id_type,id_user,id_target,timestamp,message,id_session) VALUES('2','`
   - Place in: `Proc_6_4_6DAFB0` line 716

999. `eax+ebx+00000090h`
   - Place in: `Proc_6_4_6DAFB0` line 718, `Proc_6_173_7C3430` line 59744

1000. `edx+eax+00000074h`
   - Place in: `Proc_6_4_6DAFB0` line 730, `Proc_6_55_71A6E0` line 16172, `Proc_6_82_731070` line 23422, `Proc_6_144_76BE70` line 38736, `Proc_6_155_795C90` line 49534, `Proc_6_159_79FCD0` line 50872, `Proc_6_164_7BC820` line 58094, `Proc_6_210_7E1DC0` line 68124; plus 2 more functions

1001. `DELETE FROM rooms_events WHERE id_room='`
   - Place in: `Proc_6_4_6DAFB0` line 820, `Proc_6_45_714B60` line 14551, `Proc_6_55_71A6E0` line 16967

1002. `edx+ebx+000000B0h`
   - Place in: `Proc_6_4_6DAFB0` line 845, `Proc_6_23_6E9A90` line 9756, `Proc_6_47_714F60` line 14640, `Proc_6_69_723630` line 19168, `Proc_6_78_7279A0` line 21176, `Proc_6_89_73EA10` line 27787, `Proc_6_109_74DBD0` line 31026, `Proc_6_130_75B770` line 34806; plus 9 more functions

1003. ` (Room caution of room id:   `
   - Place in: `Proc_6_4_6DAFB0` line 846

1004. `)',UNIX_TIMESTAMP())`
   - Place in: `Proc_6_4_6DAFB0` line 846

1005. `SELECT staff_cfh.id,users.id,users.name,staff_cfh.id_partner,staff_cfh.id_room,staff_cfh.id_category,staff_cfh.description,rooms.id,rooms.name FROM staff_cfh,users,rooms WHERE staff_cfh.id='`
   - Place in: `Proc_6_4_6DAFB0` line 857
   - Length: 190 characters

1006. `' AND staff_cfh.id_closed='0' AND users.id=staff_cfh.id_user AND rooms.id=staff_cfh.id_room LIMIT 1`
   - Place in: `Proc_6_4_6DAFB0` line 858

1007. `SELECT id,name FROM users WHERE id='`
   - Place in: `Proc_6_4_6DAFB0` line 888, `Proc_6_31_70DE80` line 13012

1008. `edx+edi+000000B8h`
   - Place in: `Proc_6_50_7166B0` line 14976

1009. `SELECT users.id,users.name,rooms_events.id_room,rooms_events.id_category,rooms_events.name,rooms_events.description,DATE_FORMAT(FROM_UNIXTIME(rooms_events.timestamp), '`
   - Place in: `Proc_6_51_716AC0` line 15039
   - Length: 168 characters

1010. `'),rooms_events.tag_1,rooms_events.tag_2 FROM rooms_events,users WHERE rooms_events.id_room='`
   - Place in: `Proc_6_51_716AC0` line 15040

1011. `' AND users.id=rooms_events.id_user LIMIT 1`
   - Place in: `Proc_6_51_716AC0` line 15041

1012. `SELECT id FROM rooms_categories WHERE id='`
   - Place in: `Proc_6_52_7172B0` line 15212

1013. `',thickness_wallpaper='`
   - Place in: `Proc_6_52_7172B0` line 15443

1014. `UPDATE rooms SET thickness_floor='`
   - Place in: `Proc_6_52_7172B0` line 15443

1015. `',password='`
   - Place in: `Proc_6_52_7172B0` line 15444

1016. `',status_door='`
   - Place in: `Proc_6_52_7172B0` line 15444

1017. `',allow_feedpets='`
   - Place in: `Proc_6_52_7172B0` line 15445

1018. `',id_category='`
   - Place in: `Proc_6_52_7172B0` line 15445

1019. `,allow_otherspets='`
   - Place in: `Proc_6_52_7172B0` line 15445

1020. `',allow_walkthrough='`
   - Place in: `Proc_6_52_7172B0` line 15446

1021. `',disable_walls='`
   - Place in: `Proc_6_52_7172B0` line 15446

1022. `',visitors_max='`
   - Place in: `Proc_6_52_7172B0` line 15446

1023. `edx+eax+00000084h`
   - Place in: `Proc_6_53_718E00` line 15691, `Proc_6_55_71A6E0` line 17532, `Proc_6_179_7C7790` line 60826

1024. `edx+eax+000003FCh`
   - Place in: `Proc_6_53_718E00` line 15714, `Proc_6_93_745D90` line 28874

1025. `eax+ecx+000003FCh`
   - Place in: `Proc_6_53_718E00` line 15754, `Proc_6_144_76BE70` line 40148

1026. `ecx+edx+00000084h`
   - Place in: `Proc_6_53_718E00` line 15754

1027. `INSERT INTO logs_visitedrooms(id_user,id_room,timestamp_enter,id_session) VALUES('`
   - Place in: `Proc_6_53_718E00` line 15804

1028. `eax+edi+000000B0h`
   - Place in: `Proc_6_53_718E00` line 15804, `Proc_6_63_721050` line 18485, `Proc_6_239_7FC170` line 73904

1029. `',visitors_now=visitors_now+1 WHERE id='`
   - Place in: `Proc_6_53_718E00` line 15806

1030. `UPDATE rooms SET id_slot='`
   - Place in: `Proc_6_53_718E00` line 15806

1031. `edx+eax+00000030h`
   - Place in: `Proc_6_53_718E00` line 15817, `Proc_6_55_71A6E0` line 16735, `tmrRollers_Timer` line 10585

1032. `UPDATE rooms SET visitors_now=visitors_now+1 WHERE id='`
   - Place in: `Proc_6_53_718E00` line 15828

1033. `ecx+edx+00000074h`
   - Place in: `Proc_6_53_718E00` line 15849

1034. `edx+edi+00000130h`
   - Place in: `Proc_6_53_718E00` line 15869

1035. `edx+eax+00000134h`
   - Place in: `Proc_6_53_718E00` line 15879

1036. `LOAD_INTO`
   - Place in: `Proc_6_53_718E00` line 15980, `Proc_6_84_733600` line 24668

1037. `esi+edi+00000064h`
   - Place in: `Proc_6_55_71A6E0` line 16021

1038. `edx+eax+0000015Ch`
   - Place in: `Proc_6_55_71A6E0` line 16071, `Proc_6_89_73EA10` line 27128, `Proc_6_90_742E80` line 27921, `Proc_6_91_743480` line 28054, `Proc_6_92_744870` line 28415, `Proc_6_93_745D90` line 28896, `Proc_6_94_746990` line 29129

1039. `edx+eax+000003FAh`
   - Place in: `Proc_6_55_71A6E0` line 16083, `Proc_6_179_7C7790` line 60726

1040. `ecx+eax+000003FAh`
   - Place in: `Proc_6_55_71A6E0` line 16093, `Proc_6_179_7C7790` line 60736

1041. `eax+ebx+00000064h`
   - Place in: `Proc_6_55_71A6E0` line 16124

1042. `UPDATE rooms SET visitors_now=visitors_now-1 WHERE id_slot='`
   - Place in: `Proc_6_55_71A6E0` line 16183

1043. `ebx+eax+00000030h`
   - Place in: `Proc_6_55_71A6E0` line 16202

1044. `UPDATE rooms SET visitors_now=visitors_now-1 WHERE id='`
   - Place in: `Proc_6_55_71A6E0` line 16221

1045. `\\cache\\rooms\\`
   - Place in: `Proc_6_55_71A6E0` line 16483, `Proc_6_84_733600` line 24470, `Form_QueryUnload` line 9298

1046. `eax+ecx+00000094h`
   - Place in: `Proc_6_55_71A6E0` line 16483, `Proc_6_84_733600` line 26028, `Proc_6_144_76BE70` line 38688, `Proc_6_155_795C90` line 50023, `Proc_6_159_79FCD0` line 51387

1047. `edx+ebx+00000064h`
   - Place in: `Proc_6_55_71A6E0` line 16483

1048. `\\cache\\pathfinder\\`
   - Place in: `Proc_6_55_71A6E0` line 16522, `Proc_6_84_733600` line 24388, `Form_QueryUnload` line 9352

1049. `eax+ecx+0000007Ch`
   - Place in: `Proc_6_55_71A6E0` line 16522, `Proc_6_82_731070` line 24064, `Proc_6_84_733600` line 26258, `Proc_6_144_76BE70` line 38510, `Proc_6_150_777FA0` line 45831, `Proc_6_155_795C90` line 49886, `Proc_6_159_79FCD0` line 51253, `Proc_6_164_7BC820` line 58094; plus 1 more functions

1050. `UPDATE bots SET position_x='`
   - Place in: `Proc_6_55_71A6E0` line 16734

1051. `eax+ecx+0000002Ch`
   - Place in: `Proc_6_55_71A6E0` line 16734, `Proc_0_24_68EEF0` line 12805

1052. `ecx+edx+0000002Eh`
   - Place in: `Proc_6_55_71A6E0` line 16734

1053. `eax+ecx+0000002Ah`
   - Place in: `Proc_6_55_71A6E0` line 16735

1054. `',cache_action='`
   - Place in: `Proc_6_55_71A6E0` line 16736

1055. `edx+eax+00000054h`
   - Place in: `Proc_6_55_71A6E0` line 16756, `Proc_6_81_730010` line 23125, `Proc_6_163_7B3480` line 55450, `Proc_6_190_7D11D0` line 63988

1056. `',energy='`
   - Place in: `Proc_6_55_71A6E0` line 16877, `Proc_6_179_7C7790` line 61368

1057. `UPDATE bots_petdata SET id_level='`
   - Place in: `Proc_6_55_71A6E0` line 16877, `Proc_6_179_7C7790` line 61368

1058. `ecx+edx+00000040h`
   - Place in: `Proc_6_55_71A6E0` line 16877

1059. `',experience='`
   - Place in: `Proc_6_55_71A6E0` line 16878, `Proc_6_179_7C7790` line 61369

1060. `',nutrition='`
   - Place in: `Proc_6_55_71A6E0` line 16878, `Proc_6_179_7C7790` line 61369

1061. `eax+ecx+00000044h`
   - Place in: `Proc_6_55_71A6E0` line 16878

1062. `ecx+edx+0000004Ch`
   - Place in: `Proc_6_55_71A6E0` line 16878

1063. `' WHERE id_bot='`
   - Place in: `Proc_6_55_71A6E0` line 16879, `Proc_6_179_7C7790` line 61370

1064. `',scratches='`
   - Place in: `Proc_6_55_71A6E0` line 16879, `Proc_6_179_7C7790` line 61370

1065. `edx+eax+00000050h`
   - Place in: `Proc_6_55_71A6E0` line 16879

1066. `ecx+eax+00000064h`
   - Place in: `Proc_6_55_71A6E0` line 16967, `Proc_6_163_7B3480` line 55537, `Proc_6_206_7DA450` line 67023

1067. `eax+ecx+000000BEh`
   - Place in: `Proc_6_55_71A6E0` line 16988, `Proc_6_144_76BE70` line 38726, `Form_QueryUnload` line 9231

1068. `ecx+ebx+000000BEh`
   - Place in: `Proc_6_55_71A6E0` line 16988, `tmrSigner_Timer` line 1356

1069. `UPDATE rooms SET id_slot=null WHERE id_slot='`
   - Place in: `Proc_6_55_71A6E0` line 16999

1070. `ebx+eax+00000038h`
   - Place in: `Proc_6_55_71A6E0` line 17100, `Proc_6_84_733600` line 25169, `Proc_6_150_777FA0` line 46785

1071. `eax+ebx+0000041Ch`
   - Place in: `Proc_6_55_71A6E0` line 17398

1072. `ecx+eax+00000460h`
   - Place in: `Proc_6_55_71A6E0` line 17450, `Proc_6_150_777FA0` line 41943

1073. `ecx+eax+000003FCh`
   - Place in: `Proc_6_55_71A6E0` line 17463, `Proc_6_150_777FA0` line 41713

1074. `' AND timestamp_left IS NULL`
   - Place in: `Proc_6_55_71A6E0` line 17483

1075. `UPDATE logs_visitedrooms SET timestamp_left=UNIX_TIMESTAMP() WHERE id_user='`
   - Place in: `Proc_6_55_71A6E0` line 17483

1076. `ecx+edx+000003FCh`
   - Place in: `Proc_6_55_71A6E0` line 17532

1077. `SELECT visitors_now,visitors_max,status_door,password,id_slot,id_owner FROM rooms WHERE rooms.id='`
   - Place in: `Proc_6_55_71A6E0` line 17590

1078. `SELECT id_user FROM rooms_bans WHERE id_user='`
   - Place in: `Proc_6_55_71A6E0` line 17690

1079. `edx+eax+00000070h`
   - Place in: `Proc_6_60_720060` line 18152, `Proc_6_84_733600` line 24711, `Proc_6_85_73A8E0` line 26412, `Proc_6_164_7BC820` line 58201, `Proc_6_243_7FFEB0` line 75651

1080. `ecx+edi+000003F8h`
   - Place in: `Proc_6_61_720490` line 18288, `Proc_6_62_7209F0` line 18418, `Proc_6_123_754020` line 32826

1081. `INSERT IGNORE INTO rooms_bans(id_room,id_user,timestamp_expire) VALUES('`
   - Place in: `Proc_6_62_7209F0` line 18431

1082. `',UNIX_TIMESTAMP()+900)`
   - Place in: `Proc_6_62_7209F0` line 18432

1083. `esi+eax+000000B4h`
   - Place in: `Proc_6_63_721050` line 18454

1084. `SELECT id_user FROM rooms_rates WHERE id_user='`
   - Place in: `Proc_6_63_721050` line 18486, `Proc_6_79_72A430` line 21429

1085. `eax+ebx+000000B4h`
   - Place in: `Proc_6_63_721050` line 18486, `Proc_6_79_72A430` line 21429, `Proc_6_84_733600` line 24159, `Proc_6_107_74B7E0` line 30467, `Proc_6_150_777FA0` line 47567

1086. `INSERT INTO rooms_rates(id_user,id_room,timestamp) VALUES('`
   - Place in: `Proc_6_63_721050` line 18510

1087. `SELECT rate FROM rooms WHERE id='`
   - Place in: `Proc_6_63_721050` line 18521

1088. `UPDATE rooms SET rate='`
   - Place in: `Proc_6_63_721050` line 18536

1089. `edx+ebx+000000B4h`
   - Place in: `Proc_6_64_721650` line 18573, `Proc_6_74_7265B0` line 19889, `Proc_6_75_7269D0` line 19937, `Proc_6_95_746CD0` line 29201, `Proc_6_155_795C90` line 48440, `Proc_6_167_7BECA0` line 58703, `Proc_6_176_7C4EE0` line 60468, `Proc_0_24_68EEF0` line 12421

1090. `eax+ecx+00000164h`
   - Place in: `Proc_6_64_721650` line 18613, `Proc_6_243_7FFEB0` line 76037

1091. `edx+edi+000000B4h`
   - Place in: `Proc_6_65_721A10` line 18656, `Proc_6_71_724CF0` line 19449, `Proc_6_159_79FCD0` line 50340

1092. `INSERT INTO rooms_rights(id_user,id_room) VALUES('`
   - Place in: `Proc_6_65_721A10` line 18675

1093. `SELECT id,id_product,sign,caption,position_wall FROM furnitures WHERE id='`
   - Place in: `Proc_6_66_721D60` line 18728

1094. `',caption='`
   - Place in: `Proc_6_66_721D60` line 18788

1095. `SELECT id,id_product,sign,caption FROM furnitures WHERE id='`
   - Place in: `Proc_6_67_722940` line 18836, `Proc_6_68_723170` line 18958

1096. `DELETE FROM furnitures WHERE id='`
   - Place in: `Proc_6_68_723170` line 18991, `Proc_6_69_723630` line 19113, `Proc_6_73_725540` line 19805, `Proc_6_87_73C120` line 27019, `Proc_6_139_768100` line 37235

1097. `SELECT id,id_product,id_destination,sign_extra FROM furnitures WHERE id='`
   - Place in: `Proc_6_69_723630` line 19024

1098. `ecx+eax+000000B0h`
   - Place in: `Proc_6_6_6DC9D0` line 1089, `Proc_6_7_6DD0E0` line 1198, `Proc_6_15_6E1900` line 2322, `Proc_6_16_6E2320` line 2672, `Proc_6_23_6E9A90` line 9266, `Proc_6_30_70DC90` line 12910, `Proc_6_32_70EAB0` line 13189, `Proc_6_38_70FD10` line 13502; plus 34 more functions

1099. `edx+ebx+000000C0h`
   - Place in: `Proc_6_6_6DC9D0` line 1092, `Proc_6_23_6E9A90` line 6187, `Proc_6_38_70FD10` line 13576, `Proc_6_167_7BECA0` line 58703, `Proc_6_176_7C4EE0` line 60468, `Proc_6_239_7FC170` line 74810, `Proc_6_243_7FFEB0` line 75751

1100. `SELECT id,id_product,sign,position_wall FROM furnitures WHERE id='`
   - Place in: `Proc_6_70_724190` line 19195

1101. `' AND position_wall IS NOT NULL LIMIT 1`
   - Place in: `Proc_6_70_724190` line 19196

1102. `CLICK_ON`
   - Place in: `Proc_6_70_724190` line 19207, `Proc_6_150_777FA0` line 41081

1103. `edx+edi+000000BCh`
   - Place in: `Proc_6_70_724190` line 19331

1104. `SELECT users.id_socket FROM rooms_rights,users WHERE rooms_rights.id_room='' AND users.id=rooms_rights.id_user AND users.id_socket IS NOT NULL`
   - Place in: `Proc_6_71_724CF0` line 19407
   - Length: 142 characters

1105. `DELETE FROM rooms_rights WHERE id_room='`
   - Place in: `Proc_6_71_724CF0` line 19419

1106. `DELETE FROM rooms WHERE id='`
   - Place in: `Proc_6_72_7250D0` line 19558

1107. `edx+edi+0000006Ch`
   - Place in: `Proc_6_73_725540` line 19640, `Proc_6_144_76BE70` line 38025, `Proc_6_155_795C90` line 48230

1108. `esi+edi+0000006Ch`
   - Place in: `Proc_6_73_725540` line 19669, `Proc_6_155_795C90` line 48261

1109. `' AND id='`
   - Place in: `Proc_6_73_725540` line 19714, `Proc_6_139_768100` line 36970, `Proc_6_219_7EA390` line 69197, `Proc_6_220_7EBA50` line 69489, `Proc_6_222_7ED710` line 69869, `Proc_6_225_7EFBD0` line 70394

1110. `SELECT id_product FROM furnitures WHERE id_room='`
   - Place in: `Proc_6_73_725540` line 19714

1111. `SELECT id_product,id,sign,position_x,position_y,position_z,position_r FROM furnitures WHERE id='`
   - Place in: `Proc_6_73_725540` line 19783

1112. `edx+eax+00000420h`
   - Place in: `Proc_6_74_7265B0` line 19855, `Proc_6_87_73C120` line 27061, `Proc_6_128_756190` line 34630, `Proc_6_167_7BECA0` line 58605

1113. `DELETE FROM rooms_rights WHERE id_user='`
   - Place in: `Proc_6_74_7265B0` line 19866

1114. `eax+edi+000000B4h`
   - Place in: `Proc_6_74_7265B0` line 19866, `Proc_6_96_747000` line 29243, `Proc_0_24_68EEF0` line 14485

1115. `ecx+edx+00000164h`
   - Place in: `Proc_6_75_7269D0` line 19967

1116. `edx+eax+00000168h`
   - Place in: `Proc_6_76_726CE0` line 20005

1117. `edx+edi+000000BEh`
   - Place in: `Proc_6_76_726CE0` line 20024

1118. `UPDATE users SET respect_amount=respect_amount-1,respect_given=respect_given+1 WHERE id='`
   - Place in: `Proc_6_76_726CE0` line 20036

1119. `UPDATE users SET respect_received=respect_received+1 WHERE id='`
   - Place in: `Proc_6_76_726CE0` line 20048

1120. `SELECT respect_received FROM users WHERE id='`
   - Place in: `Proc_6_76_726CE0` line 20088

1121. `GIVERESPECT`
   - Place in: `Proc_6_76_726CE0` line 20101

1122. `SELECT rooms.id,rooms_official.id,models.required_files,rooms_official.caption FROM rooms_official,rooms,models WHERE rooms.id='`
   - Place in: `Proc_6_77_727590` line 20205
   - Length: 128 characters

1123. `' AND rooms_official.id_room=rooms.id AND models.id=rooms.id_model AND models.type='1'`
   - Place in: `Proc_6_77_727590` line 20206

1124. `SELECT rooms.id,rooms.id_slot,NULL,models.name,models.id,rooms.id_floor,rooms.id_wallpaper,rooms.id_landscape,rooms.rate,models.map,models.position_x,models.position_y,NULL,rooms.name,rooms.disable_walls,rooms.allow_otherspets,rooms.allow_walkthrough,rooms.allow_feedpets,models.type,rooms.visitors_primaryid FROM rooms, models WHERE rooms.id='`
   - Place in: `Proc_6_78_7279A0` line 20248
   - Length: 344 characters

1125. `ebx+edx+00000130h`
   - Place in: `Proc_6_78_7279A0` line 20303, `Proc_6_79_72A430` line 21301

1126. `ecx+eax+00000134h`
   - Place in: `Proc_6_78_7279A0` line 20313, `Proc_6_79_72A430` line 21311

1127. `C~`
   - Place in: `Proc_6_78_7279A0` line 20314, `Proc_6_79_72A430` line 21312

1128. `edi+edx+00000410h`
   - Place in: `Proc_6_78_7279A0` line 20440

1129. `edx+eax+00000410h`
   - Place in: `Proc_6_78_7279A0` line 20450

1130. `ecx+eax+00000404h`
   - Place in: `Proc_6_78_7279A0` line 20582, `Proc_6_79_72A430` line 21838, `Proc_6_150_777FA0` line 41974, `Proc_6_179_7C7790` line 60888, `Proc_0_24_68EEF0` line 14069

1131. `edx+eax+00000094h`
   - Place in: `Proc_6_78_7279A0` line 20968, `Proc_6_79_72A430` line 22216, `Proc_6_84_733600` line 26307, `Proc_6_144_76BE70` line 38537, `tmrSigner_Timer` line 6732

1132. `H}`
   - Place in: `Proc_6_78_7279A0` line 20969, `Proc_6_79_72A430` line 22217

1133. `edi+ebx+0000006Ch`
   - Place in: `Proc_6_78_7279A0` line 21000, `Proc_6_79_72A430` line 22343, `Proc_6_179_7C7790` line 60878

1134. `edx+edi+00000096h`
   - Place in: `Proc_6_78_7279A0` line 21030

1135. `ecx+ebx+0000009Ah`
   - Place in: `Proc_6_78_7279A0` line 21031

1136. `ENTEROTHERSROOM`
   - Place in: `Proc_6_78_7279A0` line 21043, `Proc_6_79_72A430` line 22290

1137. `ebx+edi+0000006Ch`
   - Place in: `Proc_6_78_7279A0` line 21096, `Proc_6_79_72A430` line 22247

1138. `' AND timestamp_hide>UNIX_TIMESTAMP() LIMIT 1`
   - Place in: `Proc_6_78_7279A0` line 21151, `Proc_6_79_72A430` line 22399

1139. `SELECT id,description_title FROM poll WHERE id_room='`
   - Place in: `Proc_6_78_7279A0` line 21151, `Proc_6_79_72A430` line 22399

1140. `' AND id_poll='`
   - Place in: `Proc_6_78_7279A0` line 21177, `Proc_6_79_72A430` line 22424

1141. `SELECT id_user FROM poll_exit WHERE id_user='`
   - Place in: `Proc_6_78_7279A0` line 21177, `Proc_6_79_72A430` line 22424

1142. `SELECT id FROM poll_results WHERE id_user='`
   - Place in: `Proc_6_78_7279A0` line 21211, `Proc_6_79_72A430` line 22458

1143. `SELECT id_user FROM rooms_rights WHERE id_user='`
   - Place in: `Proc_6_79_72A430` line 21547

1144. `edx+edi+00000468h`
   - Place in: `Proc_6_79_72A430` line 21713

1145. `edx+ebx+0000009Ah`
   - Place in: `Proc_6_79_72A430` line 22278

1146. `edx+ecx+00000070h`
   - Place in: `Proc_6_79_72A430` line 22383, `Proc_6_150_777FA0` line 41141, `Proc_6_159_79FCD0` line 51632, `Proc_6_164_7BC820` line 58223

1147. `SELECT id_user FROM staff_cfh WHERE id='`
   - Place in: `Proc_6_7_6DD0E0` line 1172

1148. `',id_tab='0' WHERE id='`
   - Place in: `Proc_6_7_6DD0E0` line 1198

1149. `UPDATE staff_cfh SET id_closed='`
   - Place in: `Proc_6_7_6DD0E0` line 1198

1150. `edx+edi+000000C0h`
   - Place in: `Proc_6_7_6DD0E0` line 1200

1151. `edx+ebx+00000468h`
   - Place in: `Proc_6_80_72EB60` line 22657

1152. `eax+esi+00000464h`
   - Place in: `Proc_6_80_72EB60` line 22679

1153. `ecx+edx+00000464h`
   - Place in: `Proc_6_80_72EB60` line 22835

1154. `ebx+ecx+00000130h`
   - Place in: `Proc_6_81_730010` line 23054

1155. `eax+ebx+00000054h`
   - Place in: `Proc_6_81_730010` line 23125

1156. `ecx+ebx+00000060h`
   - Place in: `Proc_6_81_730010` line 23165

1157. `ecx+esi+00000418h`
   - Place in: `Proc_6_82_731070` line 23327

1158. `ecx+esi+0000041Ch`
   - Place in: `Proc_6_82_731070` line 23384, `Proc_6_192_7D1B80` line 64515

1159. `edx+edi+0000041Ch`
   - Place in: `Proc_6_82_731070` line 23511, `tmrSigner_Timer` line 4077

1160. `edx+eax+0000041Ah`
   - Place in: `Proc_6_82_731070` line 23531

1161. `edx+edi+0000041Ah`
   - Place in: `Proc_6_82_731070` line 23569

1162. `edx+edi+00000418h`
   - Place in: `Proc_6_82_731070` line 23627

1163. `edx+eax+0000003Ch`
   - Place in: `Proc_6_82_731070` line 23710

1164. `ecx+ebx+00000004h`
   - Place in: `Proc_6_82_731070` line 23747, `Proc_6_188_7CF3C0` line 63651, `tmrRollers_Timer` line 10679

1165. `eax+esi+0000003Ch`
   - Place in: `Proc_6_82_731070` line 23748

1166. `esi+eax+000000BEh`
   - Place in: `Proc_6_82_731070` line 23862, `Proc_6_107_74B7E0` line 30366, `Proc_6_224_7EF5A0` line 70171, `Proc_6_225_7EFBD0` line 70349, `Proc_6_227_7F2400` line 71038, `Proc_6_229_7F3070` line 71235

1167. `esi+eax+00000070h`
   - Place in: `Proc_6_82_731070` line 23869

1168. `SELECT id_type,id_source,id_sprite,position_x,position_y,position_z,action,action_rotation,action_height FROM models_furnitures WHERE id_model='`
   - Place in: `Proc_6_82_731070` line 23888
   - Length: 144 characters

1169. `' LIMIT 500`
   - Place in: `Proc_6_82_731070` line 23889

1170. `\\cache\\wired_trigger\\`
   - Place in: `Proc_6_84_733600` line 24147, `Proc_6_150_777FA0` line 47158, `Proc_6_159_79FCD0` line 54460, `Proc_6_219_7EA390` line 69358

1171. `\\cache\\wired_action\\`
   - Place in: `Proc_6_84_733600` line 24222, `Proc_6_150_777FA0` line 47364, `Proc_6_159_79FCD0` line 54546, `Proc_6_220_7EBA50` line 69661

1172. `\\cache\\wired_condition\\`
   - Place in: `Proc_6_84_733600` line 24296, `Proc_6_150_777FA0` line 47567, `Proc_6_159_79FCD0` line 54626, `Proc_6_222_7ED710` line 70030

1173. `\\cache\\rooms\\destination_`
   - Place in: `Proc_6_84_733600` line 24418, `Proc_6_150_777FA0` line 47888, `Proc_6_159_79FCD0` line 54019

1174. `ecx+eax+00000024h`
   - Place in: `Proc_6_84_733600` line 24668

1175. `SELECT id,id_product,position_x,position_y,position_r,position_z,sign,id_secondary FROM furnitures WHERE id_room='`
   - Place in: `Proc_6_84_733600` line 24723

1176. `' AND id_owner IS NULL AND position_wall IS NULL LIMIT 1000`
   - Place in: `Proc_6_84_733600` line 24724

1177. `ecx+edx+00000094h`
   - Place in: `Proc_6_84_733600` line 24749, `Proc_6_144_76BE70` line 38584, `Proc_6_150_777FA0` line 45889, `Proc_6_159_79FCD0` line 53912, `tmrSigner_Timer` line 6791, `Form_QueryUnload` line 9298

1178. `eax+ecx+00000080h`
   - Place in: `Proc_6_84_733600` line 24892, `Proc_6_159_79FCD0` line 53981

1179. `eax+ecx+00000060h`
   - Place in: `Proc_6_84_733600` line 25078, `Proc_6_144_76BE70` line 39337, `Proc_6_159_79FCD0` line 53341

1180. `edx+ebx+00000080h`
   - Place in: `Proc_6_84_733600` line 25794

1181. `edx+eax+00000080h`
   - Place in: `Proc_6_84_733600` line 25823, `Proc_6_150_777FA0` line 47793, `Proc_6_159_79FCD0` line 51296

1182. `@\``
   - Place in: `Proc_6_84_733600` line 26385

1183. `' AND id_owner IS NULL AND position_wall IS NOT NULL LIMIT 100`
   - Place in: `Proc_6_85_73A8E0` line 26422

1184. `SELECT id,id_product,position_wall,sign FROM furnitures WHERE id_room='`
   - Place in: `Proc_6_85_73A8E0` line 26422

1185. `ecx+eax+00000028h`
   - Place in: `Proc_6_86_73B0D0` line 26653

1186. `edx+esi+0000006Ch`
   - Place in: `Proc_6_87_73C120` line 26752

1187. `INSERT INTO bots(id_user,figure,name,id_handle) VALUES('`
   - Place in: `Proc_6_87_73C120` line 26959, `Proc_6_132_75D4A0` line 35972

1188. `','4')`
   - Place in: `Proc_6_87_73C120` line 26960, `Proc_6_132_75D4A0` line 35973

1189. `SELECT MAX(id) FROM bots WHERE id_user='`
   - Place in: `Proc_6_87_73C120` line 26971, `Proc_6_132_75D4A0` line 35984

1190. `INSERT INTO bots_petdata(id_bot,timestamp_buy,id_owner,energy,nutrition) VALUES('`
   - Place in: `Proc_6_87_73C120` line 27004, `Proc_6_132_75D4A0` line 36017

1191. `eax+ecx+00000004h`
   - Place in: `Proc_6_87_73C120` line 27005, `Proc_6_132_75D4A0` line 36019, `Proc_0_24_68EEF0` line 12805

1192. `ecx+edx+00000008h`
   - Place in: `Proc_6_87_73C120` line 27005, `Proc_6_107_74B7E0` line 30902, `Proc_6_132_75D4A0` line 36019, `Proc_6_179_7C7790` line 61448, `Proc_6_183_7CABF0` line 61947, `Proc_6_186_7CD040` line 62440, `Proc_6_203_7D7F80` line 66407, `Proc_6_206_7DA450` line 66983

1193. `edx+eax+00000160h`
   - Place in: `Proc_6_89_73EA10` line 27138, `Proc_6_90_742E80` line 27931

1194. `ecx+eax+00000158h`
   - Place in: `Proc_6_89_73EA10` line 27181, `Proc_6_91_743480` line 28101, `Proc_6_92_744870` line 28476

1195. `ecx+edx+00000448h`
   - Place in: `Proc_6_89_73EA10` line 27324, `Proc_6_128_756190` line 34351, `Proc_6_140_769400` line 37474, `Proc_6_155_795C90` line 48873, `Proc_6_159_79FCD0` line 53814, `Proc_6_225_7EFBD0` line 70491, `Proc_6_226_7F0B20` line 70702

1196. `ecx+eax+0000015Ch`
   - Place in: `Proc_6_89_73EA10` line 27428, `Proc_6_90_742E80` line 27980, `Proc_6_91_743480` line 28321, `Proc_6_92_744870` line 28717, `Proc_6_94_746990` line 29149, `Proc_6_141_76A670` line 37556, `Proc_6_142_76B310` line 37763, `Proc_6_144_76BE70` line 37967; plus 1 more functions

1197. `edx+eax+00000158h`
   - Place in: `Proc_6_89_73EA10` line 27437, `Proc_6_92_744870` line 28507

1198. `UPDATE furnitures SET id_owner='`
   - Place in: `Proc_6_89_73EA10` line 27713, `Proc_6_226_7F0B20` line 70578

1199. `INSERT INTO logs_trading(id_user,id_partner,items_user,items_partner,id_room,timestamp,id_session) VALUES('`
   - Place in: `Proc_6_89_73EA10` line 27868

1200. `ecx+ebx+00000090h`
   - Place in: `Proc_6_89_73EA10` line 27870, `Proc_6_168_7C05F0` line 59126

1201. `UPDATE staff_cfh SET id_tab='1',id_picker=0,timestamp_picked=null WHERE `
   - Place in: `Proc_6_8_6DD790` line 1289

1202. `eax+edi+00000160h`
   - Place in: `Proc_6_90_742E80` line 27970

1203. `edx+edi+00000160h`
   - Place in: `Proc_6_90_742E80` line 28001

1204. `ecx+eax+00000160h`
   - Place in: `Proc_6_90_742E80` line 28024, `Proc_6_91_743480` line 28064, `Proc_6_92_744870` line 28425

1205. `SELECT id,id_product,sign FROM furnitures WHERE id='`
   - Place in: `Proc_6_91_743480` line 28077, `Proc_6_92_744870` line 28438

1206. `ecx+edx+00000158h`
   - Place in: `Proc_6_91_743480` line 28132

1207. `eax+ecx+00000150h`
   - Place in: `Proc_6_91_743480` line 28199

1208. `ecx+edx+00000154h`
   - Place in: `Proc_6_91_743480` line 28309, `Proc_6_92_744870` line 28705

1209. `ecx+edx+00000150h`
   - Place in: `Proc_6_91_743480` line 28310, `Proc_6_92_744870` line 28568

1210. `ecx+ebx+00000150h`
   - Place in: `Proc_6_91_743480` line 28311, `Proc_6_92_744870` line 28707

1211. `edx+eax+00000154h`
   - Place in: `Proc_6_91_743480` line 28391, `Proc_6_92_744870` line 28787

1212. `edx+eax+00000150h`
   - Place in: `Proc_6_91_743480` line 28392, `Proc_6_92_744870` line 28593

1213. `edx+ebx+00000150h`
   - Place in: `Proc_6_91_743480` line 28393, `Proc_6_92_744870` line 28789

1214. `edx+ecx+00000154h`
   - Place in: `Proc_6_92_744870` line 28614

1215. `ecx+ebx+00000404h`
   - Place in: `Proc_6_96_747000` line 29306, `Proc_6_149_775C10` line 40657, `tmrSigner_Timer` line 2366

1216. `SELECT id_light,id_preset,id_background,colour,id_state FROM furnitures_dimmerpresets WHERE id_furni='`
   - Place in: `Proc_6_98_747D80` line 29455

1217. `' LIMIT 3`
   - Place in: `Proc_6_98_747D80` line 29456

1218. `SELECT furnitures_dimmerpresets.id_light,furnitures_dimmerpresets.id_preset,furnitures_dimmerpresets.id_background,furnitures_dimmerpresets.colour,furnitures.id_product,furnitures.position_wall,furnitures.sign FROM furnitures_dimmerpresets,furnitures WHERE furnitures_dimmerpresets.id_furni='`
   - Place in: `Proc_6_99_748460` line 29567
   - Length: 292 characters

1219. `' AND furnitures_dimmerpresets.id_state='2' AND furnitures.id=furnitures_dimmerpresets.id_furni LIMIT 1`
   - Place in: `Proc_6_99_748460` line 29568

1220. `edx+eax+00000048h`
   - Place in: `Proc_6_9_6DDD70` line 1311, `Proc_6_23_6E9A90` line 5189, `Proc_6_55_71A6E0` line 16877, `Proc_6_249_802F10` line 76325, `tmrBots_Timer` line 8980, `Proc_5_6_6D7090` line 551

1221. `edx+esi+000003D4h`
   - Place in: `Proc_6_9_6DDD70` line 1330, `Proc_6_23_6E9A90` line 5531, `Proc_6_249_802F10` line 76345

1222. ` name='Inappropriate to hotel management',`
   - Place in: `Proc_6_9_6DDD70` line 1350

1223. ` status_door='1',`
   - Place in: `Proc_6_9_6DDD70` line 1350

1224. `  WHERE id='`
   - Place in: `Proc_6_9_6DDD70` line 1352

### Main.frm -> `com.alphaseries.Main`

1225. `C:\\Windows\\SysWow64\\MSWINSCK.OCX`
   - Place in: `(module declarations)` line 2, `(module declarations)` line 3

1226. `{248DD890-BB45-11CF-9ABC0080C7E7B78D}#1.0#0`
   - Place in: `(module declarations)` line 2, `(module declarations)` line 3

1227. `C:\\Windows\\SysWow64\\RICHTX32.OCX`
   - Place in: `(module declarations)` line 3

1228. `{3B7C8863-D78F-101B-B9B504021C009402}#1.2#0`
   - Place in: `(module declarations)` line 3

1229. `Main.frx`
   - Place in: `(module declarations)` line 10

1230. `Form1`
   - Place in: `(module declarations)` line 11, `(module declarations)` line 10, `(module declarations)` line 10

1231. `Frame1`
   - Place in: `(module declarations)` line 18, `(module declarations)` line 41

1232. `Trebuchet MS`
   - Place in: `(module declarations)` line 39, `(module declarations)` line 65

1233. `MS Sans Serif`
   - Place in: `(module declarations)` line 189

1234. `Verdana`
   - Place in: `(module declarations)` line 262, `(module declarations)` line 30

1235. `Main`
   - Place in: `(module declarations)` line 273

1236. `urlmon`
   - Place in: `(module declarations)` line 276

1237. `winmm`
   - Place in: `(module declarations)` line 278

1238. `/CACHE/ROOMS`
   - Place in: `Form_Initialize` line 9395

1239. `/CACHE/PATHFINDER`
   - Place in: `Form_Initialize` line 9401

1240. `/CACHE/USERS`
   - Place in: `Form_Initialize` line 9407

1241. `eax+ecx+00000064h`
   - Place in: `Form_QueryUnload` line 9298

1242. `UPDATE rooms SET id_slot=null,visitors_now='0' WHERE id_slot IS NOT NULL OR visitors_now!='0'`
   - Place in: `Form_QueryUnload` line 9362

1243. `ERROR `
   - Place in: `Proc_0_24_68EEF0` line 12253

1244. ` - ERROR`
   - Place in: `Proc_0_24_68EEF0` line 12256

1245. `ecx+eax+00000424h`
   - Place in: `Proc_0_24_68EEF0` line 12284

1246. `ecx+ebx+000000B8h`
   - Place in: `Proc_0_24_68EEF0` line 12384

1247. `eax+ebx+000000ACh`
   - Place in: `Proc_0_24_68EEF0` line 12466

1248. `ecx+eax+000000ACh`
   - Place in: `Proc_0_24_68EEF0` line 12576

1249. `ecx+esi+0000002Ch`
   - Place in: `Proc_0_24_68EEF0` line 12705

1250. `edx+esi+0000002Eh`
   - Place in: `Proc_0_24_68EEF0` line 12705

1251. `eax+ecx+00000030h`
   - Place in: `Proc_0_24_68EEF0` line 12806

1252. `eax+esi+0000002Ah`
   - Place in: `Proc_0_24_68EEF0` line 12807

1253. `ecx+edi+00000028h`
   - Place in: `Proc_0_24_68EEF0` line 12807

1254. `ebx+edx+00000034h`
   - Place in: `Proc_0_24_68EEF0` line 12902

1255. `edx+ebx+00000036h`
   - Place in: `Proc_0_24_68EEF0` line 12921

1256. `eax+esi+0000002Ch`
   - Place in: `Proc_0_24_68EEF0` line 13063

1257. `ecx+esi+0000002Eh`
   - Place in: `Proc_0_24_68EEF0` line 13063

1258. `eax+esi+00000068h`
   - Place in: `Proc_0_24_68EEF0` line 13064

1259. `ecx+eax+000000A8h`
   - Place in: `Proc_0_24_68EEF0` line 13253

1260. `ecx+eax+00000036h`
   - Place in: `Proc_0_24_68EEF0` line 13295

1261. `eax+edi+00000030h`
   - Place in: `Proc_0_24_68EEF0` line 13499

1262. `eax+edi+0000002Eh`
   - Place in: `Proc_0_24_68EEF0` line 13566

1263. `mv `
   - Place in: `Proc_0_24_68EEF0` line 13566

1264. `eax+esi+00000030h`
   - Place in: `Proc_0_24_68EEF0` line 13567

1265. `edx+eax+00000076h`
   - Place in: `Proc_0_24_68EEF0` line 13819

1266. `eax+edi+00000076h`
   - Place in: `Proc_0_24_68EEF0` line 13848

1267. `ecx+eax+00000076h`
   - Place in: `Proc_0_24_68EEF0` line 13858

1268. `eax+ecx+0000043Ch`
   - Place in: `Proc_0_24_68EEF0` line 14133

1269. `esi+ecx+00000404h`
   - Place in: `Proc_0_24_68EEF0` line 14133

1270. `esi+edx+00000406h`
   - Place in: `Proc_0_24_68EEF0` line 14133

1271. `eax+edi+00000400h`
   - Place in: `Proc_0_24_68EEF0` line 14235

1272. `edx+esi+00000402h`
   - Place in: `Proc_0_24_68EEF0` line 14236

1273. `edx+esi+00000412h`
   - Place in: `Proc_0_24_68EEF0` line 14326

1274. `edx+esi+00000414h`
   - Place in: `Proc_0_24_68EEF0` line 14345

1275. `ecx+eax+00000070h`
   - Place in: `Proc_0_24_68EEF0` line 14417

1276. `eax+esi+000000B4h`
   - Place in: `Proc_0_24_68EEF0` line 14449

1277. `esi+eax+00000404h`
   - Place in: `Proc_0_24_68EEF0` line 14913

1278. `esi+ecx+00000406h`
   - Place in: `Proc_0_24_68EEF0` line 14913

1279. `esi+eax+0000043Ch`
   - Place in: `Proc_0_24_68EEF0` line 14914

1280. `ecx+esi+00000402h`
   - Place in: `Proc_0_24_68EEF0` line 15014

1281. `edx+edi+00000400h`
   - Place in: `Proc_0_24_68EEF0` line 15014

1282. `ecx+eax+00000412h`
   - Place in: `Proc_0_24_68EEF0` line 15144

1283. `ecx+eax+00000414h`
   - Place in: `Proc_0_24_68EEF0` line 15156

1284. `eax+edi+00000408h`
   - Place in: `Proc_0_24_68EEF0` line 15343

1285. `edx+eax+0000045Ch`
   - Place in: `Proc_0_24_68EEF0` line 15353

1286. `edx+esi+00000408h`
   - Place in: `Proc_0_24_68EEF0` line 15432

1287. `flatctrl useradmin/`
   - Place in: `Proc_0_24_68EEF0` line 15572

1288. `esi+eax+00000416h`
   - Place in: `Proc_0_24_68EEF0` line 15583

1289. `ecx+edx+00000088h`
   - Place in: `gameServer_C_q]<lkamWk&_uo_lLfj`j` line 446

1290. `ecx+edi+00000036h`
   - Place in: `tmrBots_Timer` line 8864

1291. `com.client.bot.pet.happy.speech`
   - Place in: `tmrBots_Timer` line 9011

1292. `ecx+edi+00000428h`
   - Place in: `tmrPing_Timer` line 10789

1293. `ecx+edi+000003F4h`
   - Place in: `tmrPing_Timer` line 10808

1294. `ecx+edi+00000024h`
   - Place in: `tmrPing_Timer` line 10827

1295. `com.client.check.inactive_idle_kick.minutes`
   - Place in: `tmrPing_Timer` line 10828

1296. `com.client.check.inactive_idle_disconnect.minutes`
   - Place in: `tmrPing_Timer` line 10855

1297. `edx+eax+00000428h`
   - Place in: `tmrPing_Timer` line 10894

1298. `ecx+eax+00000428h`
   - Place in: `tmrPing_Timer` line 10905

1299. `Dieses Hotel nutzt Alpha-Series, weitere Informationen \xfcber das Kommando ":about" oder \xfcber die Homepage von Alpha-Series.`
   - Place in: `tmrPing_Timer` line 10935
   - Length: 122 characters

1300. `ecx+edi+00000458h`
   - Place in: `tmrPing_Timer` line 10975

1301. `@r`
   - Place in: `tmrPing_Timer` line 10976

1302. `' WHERE variable='com.server.socket.mostactive'`
   - Place in: `tmrPing_Timer` line 11000

1303. `UPDATE settings SET value='`
   - Place in: `tmrPing_Timer` line 11000

1304. `ebx+eax+00000412h`
   - Place in: `tmrRollers_Timer` line 9858

1305. `ebx+eax+00000414h`
   - Place in: `tmrRollers_Timer` line 9858

1306. `ebx+edx+00000416h`
   - Place in: `tmrRollers_Timer` line 9858

1307. `edx+ebx+00000416h`
   - Place in: `tmrRollers_Timer` line 9858

1308. `edx+eax+0000043Ch`
   - Place in: `tmrRollers_Timer` line 10205

1309. `ecx+ebx+00000030h`
   - Place in: `tmrRollers_Timer` line 10641

1310. `ecx+edi+00000030h`
   - Place in: `tmrRollers_Timer` line 10680

1311. `edx+eax+00000068h`
   - Place in: `tmrRollers_Timer` line 10699, `Proc_0_24_68EEF0` line 13063

1312. `edx+eax+00000026h`
   - Place in: `tmrSigner_Timer` line 774

1313. `edx+ecx+00000012h`
   - Place in: `tmrSigner_Timer` line 774

1314. `edx+ecx+00000014h`
   - Place in: `tmrSigner_Timer` line 781

1315. `edx+ecx+0000001Ch`
   - Place in: `tmrSigner_Timer` line 781

1316. `edx+eax+0000000Eh`
   - Place in: `tmrSigner_Timer` line 794

1317. `ecx+ebx+00000024h`
   - Place in: `tmrSigner_Timer` line 831

1318. `edx+eax+00000024h`
   - Place in: `tmrSigner_Timer` line 850, `tmrBots_Timer` line 8677

1319. `ecx+edx+0000000Ah`
   - Place in: `tmrSigner_Timer` line 948

1320. `ecx+edi+0000000Ah`
   - Place in: `tmrSigner_Timer` line 995

1321. `edi+ebx+000000BEh`
   - Place in: `tmrSigner_Timer` line 1131

1322. `ecx+eax+00000026h`
   - Place in: `tmrSigner_Timer` line 1322

1323. `ecx+ebx+0000000Ah`
   - Place in: `tmrSigner_Timer` line 1428

1324. `edi+eax+00000460h`
   - Place in: `tmrSigner_Timer` line 1554

1325. `ebx+edi+00000468h`
   - Place in: `tmrSigner_Timer` line 1610

1326. `edx+eax+00000464h`
   - Place in: `tmrSigner_Timer` line 1639

1327. `edx+ecx+00000464h`
   - Place in: `tmrSigner_Timer` line 1757

1328. `edx+ecx+00000406h`
   - Place in: `tmrSigner_Timer` line 2195

1329. `edx+ecx+00000018h`
   - Place in: `tmrSigner_Timer` line 2702

1330. `ecx+edi+00000004h`
   - Place in: `tmrSigner_Timer` line 3438

1331. `AP`
   - Place in: `tmrSigner_Timer` line 3726

1332. `Powered by Hebbo`
   - Place in: `tmrSigner_Timer` line 3726

1333. `ecx+ebx+000000BCh`
   - Place in: `tmrSigner_Timer` line 3726

1334. `SELECT soundmachine_jb_playlist.id_destination,soundmachine_cds.sequence FROM soundmachine_jb_playlist,soundmachine_cds WHERE soundmachine_jb_playlist.id_jukebox='`
   - Place in: `tmrSigner_Timer` line 4539
   - Length: 163 characters

1335. `11000`
   - Place in: `tmrSigner_Timer` line 4886

1336. `edx+eax+00000012h`
   - Place in: `tmrSigner_Timer` line 5757

1337. `ecx+edx+00000012h`
   - Place in: `tmrSigner_Timer` line 6655

1338. `eax+ecx+00000018h`
   - Place in: `tmrSigner_Timer` line 6656

1339. `eax+ecx+00000020h`
   - Place in: `tmrSigner_Timer` line 6657

1340. `edx+edi+00000080h`
   - Place in: `tmrSigner_Timer` line 6838

1341. `eax+ecx+0000001Ch`
   - Place in: `tmrSigner_Timer` line 7337

1342. `edi+eax+00000004h`
   - Place in: `tmrSigner_Timer` line 7676

1343. `edx+edi+00000004h`
   - Place in: `tmrSigner_Timer` line 8271

1344. `edx+eax+000000B8h`
   - Place in: `tmrWalking_Timer` line 11024

1345. `edx+eax+00000416h`
   - Place in: `tmrWalking_Timer` line 11056, `Proc_0_24_68EEF0` line 14038

1346. `eax+edi+000000B8h`
   - Place in: `tmrWalking_Timer` line 11204

1347. `edx+esi+000000B4h`
   - Place in: `tmrWalking_Timer` line 11204

1348. `CONFIRMED_EMAIL`
   - Place in: `tmrWalking_Timer` line 11291

1349. `BROADCAST_MESSAGE`
   - Place in: `tmrWalking_Timer` line 11304

1350. `BROADCAST_MESSAGE_LANG`
   - Place in: `tmrWalking_Timer` line 11329

1351. `MOD_KICK_USER`
   - Place in: `tmrWalking_Timer` line 11375

1352. `MOD_KICK_ROOM`
   - Place in: `tmrWalking_Timer` line 11388

1353. `REMOVE_ITEM_INVENTORY`
   - Place in: `tmrWalking_Timer` line 11485

1354. `ADD_ITEM_INVENTORY`
   - Place in: `tmrWalking_Timer` line 11498

1355. `REFRESH_CREDITS`
   - Place in: `tmrWalking_Timer` line 11511

1356. `REFRESH_PIXELS`
   - Place in: `tmrWalking_Timer` line 11524

1357. `REFRESH_BADGES`
   - Place in: `tmrWalking_Timer` line 11537

1358. `MOD_MESSAGE_USER`
   - Place in: `tmrWalking_Timer` line 11564

1359. `SEND_BUS_POLL`
   - Place in: `tmrWalking_Timer` line 11684

1360. `SET_ROOMOWNER_MUTE`
   - Place in: `tmrWalking_Timer` line 11684

1361. `RECACHE_SETTINGS`
   - Place in: `tmrWalking_Timer` line 11913

1362. `RECACHE_CATALOG`
   - Place in: `tmrWalking_Timer` line 11917

1363. `REFRESH_APEARANCE`
   - Place in: `tmrWalking_Timer` line 11972

### Mistake.frm -> `com.alphaseries.Mistake`

1364. `Mistake.frx`
   - Place in: `(module declarations)` line 7

### MySQL.bas -> `com.alphaseries.MySQL`

1365. `[runQuery :: `
   - Place in: `Proc_5_0_6D3CD0` line 16

1366. `[runQuery_NoError :: `
   - Place in: `Proc_5_1_6D4110` line 44

1367. `[runRead :: `
   - Place in: `Proc_5_2_6D4690` line 76

1368. `[runRead_NoError :: `
   - Place in: `Proc_5_3_6D4CF0` line 120

1369. `SELECT rooms.id,rooms.name,models.type,staff_cfh.id_user,staff_cfh.id_partner,staff_cfh.timestamp_sent FROM rooms,models,staff_cfh WHERE staff_cfh.id='`
   - Place in: `Proc_5_4_6D55E0` line 195
   - Length: 151 characters

1370. `' AND rooms.id=staff_cfh.id_room AND models.id=rooms.id_model LIMIT 1`
   - Place in: `Proc_5_4_6D55E0` line 196

1371. `SELECT rooms.id,rooms.name,models.type FROM rooms,models WHERE rooms.id='`
   - Place in: `Proc_5_5_6D64D0` line 425

1372. `' AND logs_chat.timestamp > UNIX_TIMESTAMP()-600 AND users.id=logs_chat.id_user GROUP BY logs_chat.id ORDER BY logs_chat.id DESC LIMIT 100`
   - Place in: `Proc_5_5_6D64D0` line 440
   - Length: 138 characters

1373. `SELECT rooms.id,rooms.visitors_now,users.id,users.name,rooms.name,rooms.description,rooms.tag_1,rooms.tag_2 FROM rooms,users WHERE rooms.id='`
   - Place in: `Proc_5_6_6D7090` line 574
   - Length: 141 characters

1374. `' AND users.id=rooms.id_owner LIMIT 1`
   - Place in: `Proc_5_6_6D7090` line 575

1375. `SELECT name,description,tag_1,tag_2 FROM rooms_events WHERE id_room='`
   - Place in: `Proc_5_6_6D7090` line 643

### privSockHTTP.frm -> `com.alphaseries.PrivSockHTTP`

1376. `C:\\Windows\\SysWow64\\MSINET.OCX`
   - Place in: `(module declarations)` line 2

1377. `{48E59290-9880-11CF-975400AA00C00908}#1.0#0`
   - Place in: `(module declarations)` line 2

1378. `privSockHTTP`
   - Place in: `(module declarations)` line 5

1379. `privSockHTTP.frx`
   - Place in: `(module declarations)` line 11

1380. `Form2`
   - Place in: `(module declarations)` line 12

### Updater.frm -> `com.alphaseries.Updater`

1381. `Updater.frx`
   - Place in: `(module declarations)` line 9

1382. `asd`
   - Place in: `(module declarations)` line 111

1383. `Lorem ipsum dolor sit amet, consetetur sadipscing elitr`
   - Place in: `(module declarations)` line 151

1384. `Updater`
   - Place in: `(module declarations)` line 672

1385. `\xc3\x84`
   - Place in: `Form_Load` line 979

1386. `\xc3\xb6`
   - Place in: `Form_Load` line 979

1387. `\xc3\x9f`
   - Place in: `Form_Load` line 980

1388. `\xc3\xa4`
   - Place in: `Form_Load` line 980

1389. `\xc3\xbc`
   - Place in: `Form_Load` line 980

1390. `\xc3\x9c`
   - Place in: `Form_Load` line 981

1391. `\xc3\xa9`
   - Place in: `Form_Load` line 981
