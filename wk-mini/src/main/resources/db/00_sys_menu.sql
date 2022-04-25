INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('fc348d93d9454d66be5e486dbef2d6f8', 'PLATFORM', '', '0001', '系统管理', 'sys.manage', 'menu', '', '', 'fa fa-sitemap', 1, 0, 'sys.manage', '系统管理', 0, 1, '', 1573701338351, '', 1574745621835, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('2bb7b66621c54b9798f99992de3eae08', 'PLATFORM', 'fc348d93d9454d66be5e486dbef2d6f8', '00010001', '单位管理', 'sys.manage.unit', 'menu', '/platform/sys/unit', '', NULL, 1, 0, 'sys.manage.unit', NULL, 1, 0, '', 1573701338362, '', 1573701338362, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('4644675d304c423490634592b3a3cd23', 'PLATFORM', '2bb7b66621c54b9798f99992de3eae08', '000100010001', '添加单位', 'sys.manage.unit.create', 'data', NULL, '', NULL, 0, 0, 'sys.manage.unit.create', NULL, 2, 0, '', 1573701338372, '', 1573701338372, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('89a670bdde05448db35fa0403ad02126', 'PLATFORM', '2bb7b66621c54b9798f99992de3eae08', '000100010002', '修改单位', 'sys.manage.unit.update', 'data', NULL, '', NULL, 0, 0, 'sys.manage.unit.update', NULL, 3, 0, '', 1573701338381, '', 1573701338381, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('0f588f26bc5a4f86a2afdd4fab941d82', 'PLATFORM', '2bb7b66621c54b9798f99992de3eae08', '000100010003', '删除单位', 'sys.manage.unit.delete', 'data', NULL, '', NULL, 0, 0, 'sys.manage.unit.delete', NULL, 4, 0, '', 1573701338395, '', 1573701338395, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('3c0a52058051471e991d6a0a73572200', 'PLATFORM', 'fc348d93d9454d66be5e486dbef2d6f8', '00010002', '用户管理', 'sys.manage.user', 'menu', '/platform/sys/user', '', NULL, 1, 0, 'sys.manage.user', NULL, 9, 0, '', 1573701338405, '', 1575967380960, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('3044cbbf64d34ad1a139a3cb3034f885', 'PLATFORM', '3c0a52058051471e991d6a0a73572200', '000100020001', '添加用户', 'sys.manage.user.create', 'data', NULL, '', NULL, 0, 0, 'sys.manage.user.create', NULL, 10, 0, '', 1573701338413, '', 1575967380969, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('451b85d1d84449e9aedd7fc7ab63c42b', 'PLATFORM', '3c0a52058051471e991d6a0a73572200', '000100020002', '修改用户', 'sys.manage.user.update', 'data', NULL, '', NULL, 0, 0, 'sys.manage.user.update', NULL, 11, 0, '', 1573701338438, '', 1575967380972, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('ed5f07e5d5154826b3381a75fe3b0747', 'PLATFORM', '3c0a52058051471e991d6a0a73572200', '000100020003', '删除用户', 'sys.manage.user.delete', 'data', NULL, '', NULL, 0, 0, 'sys.manage.user.delete', NULL, 12, 0, '', 1573701338452, '', 1575967380975, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('604cf1a4a6ef4bd2add23deb3ece8865', 'PLATFORM', '3c0a52058051471e991d6a0a73572200', '000100020004', '导出数据', 'sys.manage.user.export', 'data', NULL, '', NULL, 0, 0, 'sys.manage.user.export', NULL, 13, 0, '', 1575967380980, '', 1575967380980, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('d219119a90954fe383754f1f3718001b', 'PLATFORM', 'fc348d93d9454d66be5e486dbef2d6f8', '00010003', '角色管理', 'sys.manage.role', 'menu', '/platform/sys/role', '', NULL, 1, 0, 'sys.manage.role', NULL, 14, 0, '', 1573701338461, '', 1573701338461, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('696646f57ff943eaa761299559149c3d', 'PLATFORM', 'd219119a90954fe383754f1f3718001b', '000100030001', '添加角色', 'sys.manage.role.create', 'data', NULL, '', NULL, 0, 0, 'sys.manage.role.create', NULL, 15, 0, '', 1573701338471, '', 1573701338471, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('ca05bea226d546b498fa27c507945c24', 'PLATFORM', 'd219119a90954fe383754f1f3718001b', '000100030002', '修改角色', 'sys.manage.role.update', 'data', NULL, '', NULL, 0, 0, 'sys.manage.role.update', NULL, 16, 0, '', 1573701338482, '', 1573701338482, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('6ba639e643bd41d0ba1b230dc1c90cf9', 'PLATFORM', 'd219119a90954fe383754f1f3718001b', '000100030003', '删除角色', 'sys.manage.role.delete', 'data', NULL, '', NULL, 0, 0, 'sys.manage.role.delete', NULL, 17, 0, '', 1573701338491, '', 1573701338492, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('c6904b4a486c45ea95af39367f42dbde', 'PLATFORM', 'd219119a90954fe383754f1f3718001b', '000100030004', '分配菜单', 'sys.manage.role.menu', 'data', NULL, '', NULL, 0, 0, 'sys.manage.role.menu', NULL, 18, 0, '', 1573701338500, '', 1573701338500, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('aab41056fd934f7cbc29dcc7fc547243', 'PLATFORM', 'd219119a90954fe383754f1f3718001b', '000100030005', '分配用户', 'sys.manage.role.user', 'data', NULL, '', NULL, 0, 0, 'sys.manage.role.user', NULL, 19, 0, '', 1573701338509, '', 1573701338509, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('c560f4744b6a4294b807b55a220e8566', 'PLATFORM', 'fc348d93d9454d66be5e486dbef2d6f8', '00010004', '菜单管理', 'sys.manage.menu', 'menu', '/platform/sys/menu', '', NULL, 1, 0, 'sys.manage.menu', NULL, 24, 0, '', 1573701338518, '', 1573701338518, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('a906c01a6b6d49d1a5b37e759575bc30', 'PLATFORM', 'c560f4744b6a4294b807b55a220e8566', '000100040001', '添加菜单', 'sys.manage.menu.create', 'data', NULL, '', NULL, 0, 0, 'sys.manage.menu.create', NULL, 25, 0, '', 1573701338528, '', 1573701338528, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('d8d7626891c44d7e9162046ae3919067', 'PLATFORM', 'c560f4744b6a4294b807b55a220e8566', '000100040002', '修改菜单', 'sys.manage.menu.update', 'data', NULL, '', NULL, 0, 0, 'sys.manage.menu.update', NULL, 26, 0, '', 1573701338536, '', 1573701338536, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('92026d19f8774f7eaaafbe6f825e1694', 'PLATFORM', 'c560f4744b6a4294b807b55a220e8566', '000100040003', '删除菜单', 'sys.manage.menu.delete', 'data', NULL, '', NULL, 0, 0, 'sys.manage.menu.delete', NULL, 27, 0, '', 1573701338543, '', 1573701338543, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('c09a841513614389a5e69f58af7b8b57', 'PLATFORM', 'fc348d93d9454d66be5e486dbef2d6f8', '00010006', '日志管理', 'sys.manage.log', 'menu', '/platform/sys/log', '', NULL, 1, 0, 'sys.manage.log', NULL, 28, 0, '', 1573701338581, '', 1573701338581, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('891b0b71892c4bd89da8a6f02af72aab', 'PLATFORM', 'c09a841513614389a5e69f58af7b8b57', '000100060001', '删除日志', 'sys.manage.log.delete', 'data', NULL, '', NULL, 0, 0, 'sys.manage.log.delete', NULL, 29, 0, '', 1573701338589, '', 1573701338589, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('8bd74ff30cb248ff9531659e5d075328', 'PLATFORM', 'fc348d93d9454d66be5e486dbef2d6f8', '00010007', '定时任务', 'sys.manage.task', 'menu', '/platform/sys/task', '', NULL, 1, 0, 'sys.manage.task', NULL, 30, 0, '', 1573701338597, '', 1573701338597, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('d04faa935e5b4c9688cfb439cbd73194', 'PLATFORM', '8bd74ff30cb248ff9531659e5d075328', '000100070001', '添加任务', 'sys.manage.task.create', 'data', NULL, '', NULL, 0, 0, 'sys.manage.task.create', NULL, 31, 0, '', 1573701338604, '', 1573701338604, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('ab076b48a08b45a89d01f57637a4403b', 'PLATFORM', '8bd74ff30cb248ff9531659e5d075328', '000100070002', '修改任务', 'sys.manage.task.update', 'data', NULL, '', NULL, 0, 0, 'sys.manage.task.update', NULL, 32, 0, '', 1573701338611, '', 1573701338611, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('7ecdf86548224f91bca75e59e4904bde', 'PLATFORM', '8bd74ff30cb248ff9531659e5d075328', '000100070003', '删除任务', 'sys.manage.task.delete', 'data', NULL, '', NULL, 0, 0, 'sys.manage.task.delete', NULL, 33, 0, '', 1573701338618, '', 1573701338618, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('1040fae6e2b6423da20a700cb9452e29', 'PLATFORM', 'fc348d93d9454d66be5e486dbef2d6f8', '00010009', '消息管理', 'sys.manage.msg', 'menu', '/platform/sys/msg', '', NULL, 1, 0, 'sys.manage.msg', NULL, 34, 0, '', 1573701338762, '', 1573701338762, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('1dbf82e8ccf64bfdb14a212fe79dbbaf', 'PLATFORM', '1040fae6e2b6423da20a700cb9452e29', '000100090001', '添加消息', 'sys.manage.msg.create', 'data', NULL, '', NULL, 0, 0, 'sys.manage.msg.create', NULL, 35, 0, '', 1573701338768, '', 1573701338768, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('948a3945d74c431d8207b435bb05e02b', 'PLATFORM', '1040fae6e2b6423da20a700cb9452e29', '000100090002', '修改消息', 'sys.manage.msg.update', 'data', NULL, '', NULL, 0, 0, 'sys.manage.msg.update', NULL, 36, 0, '', 1573701338777, '', 1573701338777, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('00ff5b6d32e84f7a962c3c36e0cf8b33', 'PLATFORM', '1040fae6e2b6423da20a700cb9452e29', '000100090003', '删除消息', 'sys.manage.msg.delete', 'data', NULL, '', NULL, 0, 0, 'sys.manage.msg.delete', NULL, 37, 0, '', 1573701338784, '', 1573701338784, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('302d591b06ec46bb8a714e20cd49eefe', 'PLATFORM', 'fc348d93d9454d66be5e486dbef2d6f8', '00010010', '应用管理', 'sys.manage.app', 'menu', '/platform/sys/app', NULL, NULL, 1, 0, 'sys.manage.app', NULL, 20, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620372883873, NULL, 1620372883873, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('73f1525e615f4954809aa9a401be0e25', 'PLATFORM', '302d591b06ec46bb8a714e20cd49eefe', '000100100001', '新增应用', 'sys.manage.app.create', 'data', NULL, NULL, NULL, 0, 0, 'sys.manage.app.create', NULL, 21, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620372883880, NULL, 1620372883880, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('3545334cef5d403c9e2bc5de59eaee0d', 'PLATFORM', '302d591b06ec46bb8a714e20cd49eefe', '000100100002', '修改应用', 'sys.manage.app.update', 'data', NULL, NULL, NULL, 0, 0, 'sys.manage.app.update', NULL, 22, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620372883884, NULL, 1620372883884, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('106ae9335f4b42a4b224c151a8f1f9ff', 'PLATFORM', '302d591b06ec46bb8a714e20cd49eefe', '000100100003', '删除应用', 'sys.manage.app.delete', 'data', NULL, NULL, NULL, 0, 0, 'sys.manage.app.delete', NULL, 23, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620372883888, NULL, 1620372883888, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('267a19074a79492992398b753186cea3', 'PLATFORM', 'fc348d93d9454d66be5e486dbef2d6f8', '00010011', '职务管理', 'sys.manage.post', 'menu', '/platform/sys/post', NULL, NULL, 1, 0, 'sys.manage.post', NULL, 5, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620698812564, NULL, 1620698812564, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('885a92cfc197448ba09aee9c91f94369', 'PLATFORM', '267a19074a79492992398b753186cea3', '000100110001', '新增职务', 'sys.manage.post.create', 'data', NULL, NULL, NULL, 0, 0, 'sys.manage.post.create', NULL, 6, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620698812583, NULL, 1620698812583, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('0ee3617dfe8e4759a05d1c34b8008e57', 'PLATFORM', '267a19074a79492992398b753186cea3', '000100110002', '修改职务', 'sys.manage.post.update', 'data', NULL, NULL, NULL, 0, 0, 'sys.manage.post.update', NULL, 7, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620698812585, NULL, 1620698812585, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('961b412aeb3f43aea8b6f2afe5c135e6', 'PLATFORM', '267a19074a79492992398b753186cea3', '000100110003', '删除职务', 'sys.manage.post.delete', 'data', NULL, NULL, NULL, 0, 0, 'sys.manage.post.delete', NULL, 8, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620698812588, NULL, 1620698812588, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('85e792d12b124b39a5152b5be1951aef', 'PLATFORM', '', '0002', '系统配置', 'sys.config', 'menu', '', '', 'fa fa-cog', 1, 0, 'sys.config', NULL, 38, 1, '', 1573701338624, '', 1574745669277, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('13a954e244cf44f7b7395cd3f1b31fc8', 'PLATFORM', '85e792d12b124b39a5152b5be1951aef', '00020001', '数据字典', 'sys.config.dict', 'menu', '/platform/sys/dict', '', NULL, 1, 0, 'sys.config.dict', NULL, 43, 0, '', 1573701338630, '', 1573701338630, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('30726c1b3f024e2980a89dfe14a4db1c', 'PLATFORM', '13a954e244cf44f7b7395cd3f1b31fc8', '000200010001', '添加字典', 'sys.config.dict.create', 'data', NULL, '', NULL, 0, 0, 'sys.config.dict.create', NULL, 44, 0, '', 1573701338636, '', 1573701338636, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('83fc1608150e40dfb3e077130d0f2076', 'PLATFORM', '13a954e244cf44f7b7395cd3f1b31fc8', '000200010002', '修改字典', 'sys.config.dict.update', 'data', NULL, '', NULL, 0, 0, 'sys.config.dict.update', NULL, 45, 0, '', 1573701338642, '', 1573701338642, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('330514d0ac854b8ab5991285719250ec', 'PLATFORM', '13a954e244cf44f7b7395cd3f1b31fc8', '000200010003', '删除字典', 'sys.config.dict.delete', 'data', NULL, '', NULL, 0, 0, 'sys.config.dict.delete', NULL, 46, 0, '', 1573701338648, '', 1573701338648, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('d45c813c8f8e48818caaa43a24b113ec', 'PLATFORM', '85e792d12b124b39a5152b5be1951aef', '00020002', '密钥管理', 'sys.config.key', 'menu', '/platform/sys/key', '', NULL, 1, 0, 'sys.config.key', NULL, 47, 0, '', 1573701338656, '', 1573701338656, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('bef96377fc9e4913b473960ea858ca76', 'PLATFORM', 'd45c813c8f8e48818caaa43a24b113ec', '000200020001', '添加密钥', 'sys.config.key.create', 'data', NULL, '', NULL, 0, 0, 'sys.config.key.create', NULL, 48, 0, '', 1573701338664, '', 1573701338664, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('3f80c022afbe4f93a999698760b7e9bc', 'PLATFORM', 'd45c813c8f8e48818caaa43a24b113ec', '000200020002', '修改密钥', 'sys.config.key.update', 'data', NULL, '', NULL, 0, 0, 'sys.config.key.update', NULL, 49, 0, '', 1573701338672, '', 1573701338672, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('2f4652e5965b47ff9bf89e2e1e6d1d4a', 'PLATFORM', 'd45c813c8f8e48818caaa43a24b113ec', '000200020003', '删除密钥', 'sys.config.key.delete', 'data', NULL, '', NULL, 0, 0, 'sys.config.key.delete', NULL, 50, 0, '', 1573701338679, '', 1573701338679, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('ec780f54129a4addb1d1c083ab661c68', 'PLATFORM', '85e792d12b124b39a5152b5be1951aef', '00020003', '系统参数', 'sys.config.param', 'menu', '/platform/sys/param', '', NULL, 1, 0, 'sys.config.param', NULL, 39, 0, '', 1573701338687, '', 1573701338687, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('890e7a9112a249cda4432d03a722184d', 'PLATFORM', 'ec780f54129a4addb1d1c083ab661c68', '000200030001', '添加参数', 'sys.config.param.create', 'data', NULL, '', NULL, 0, 0, 'sys.config.param.create', NULL, 40, 0, '', 1573701338696, '', 1573701338696, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('cc4e4bb07fd242069678328bb8668d84', 'PLATFORM', 'ec780f54129a4addb1d1c083ab661c68', '000200030002', '修改参数', 'sys.config.param.update', 'data', NULL, '', NULL, 0, 0, 'sys.config.param.update', NULL, 41, 0, '', 1573701338706, '', 1573701338706, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('ef47359314b944399d529d7cbd664f78', 'PLATFORM', 'ec780f54129a4addb1d1c083ab661c68', '000200030003', '删除参数', 'sys.config.param.delete', 'data', NULL, '', NULL, 0, 0, 'sys.config.param.delete', NULL, 42, 0, '', 1573701338715, '', 1573701338715, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('307f0fd75bfc403888594cb413292bb2', 'PLATFORM', '', '0003', '运维管理', 'sys.server', 'menu', '', '', 'fa fa-wrench', 1, 0, 'sys.server', '运维中心', 51, 1, '', 1573701338791, '', 1574745693207, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('fbed76b870dc46efab60a93d148d1234', 'COMMON', '', '0004', '用户中心', 'home', 'menu', '', '', 'fa fa-home', 1, 0, 'home', '用户中心', 3, 1, '', 1573701338797, '', 1573701338797, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('fbed76b870dc46efab60a93d148d9136', 'COMMON', 'fbed76b870dc46efab60a93d148d1234', '00040001', '消息中心', 'home.msg', 'menu', '/home/notifications', '', '', 1, 0, 'home.msg', '消息中心', 4, 0, '', 1573701338727, '18e8f1c8168b4dc9a12156bc9f2e294b', 1621412661426, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('642808b159d94b2882ff79b672a89705', 'COMMON', 'fbed76b870dc46efab60a93d148d9136', '000400010003', '设置已读', 'home.msg.read', 'data', NULL, '', NULL, 0, 0, 'home.msg.read', NULL, 7, 0, '', 1577168644270, '', 1621412661429, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('4ed96aedb9d34e79a63ca90a5dbe8dd0', 'COMMON', 'fbed76b870dc46efab60a93d148d1234', '00040002', '个人资料', 'home.user', 'menu', '/home/user', NULL, NULL, 1, 0, 'home.user', NULL, 53, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620798989846, '18e8f1c8168b4dc9a12156bc9f2e294b', 1621412667978, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('0e59f8735a1646c3b415041da5f95f70', 'COMMON', '4ed96aedb9d34e79a63ca90a5dbe8dd0', '000400020001', '修改资料', 'home.user.update', 'data', NULL, NULL, NULL, 0, 0, 'home.user.update', NULL, 54, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620798989857, NULL, 1621412667983, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('d7cabaff19c84bed961275f96cb696a1', 'COMMON', '4ed96aedb9d34e79a63ca90a5dbe8dd0', '000400020002', '重置密码', 'home.user.resetPwd', 'data', NULL, NULL, NULL, 0, 0, 'home.user.resetPwd', NULL, 55, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1620798989860, NULL, 1621412667985, 0);
INSERT INTO sys_menu(id, appId, parentId, path, name, alias, type, href, target, icon, showit, disabled, permission, note, location, hasChildren, createdBy, createdAt, updatedBy, updatedAt, delFlag) VALUES ('006bd23d426149ffbf7d0ff52fb31f27', 'PLATFORM', '307f0fd75bfc403888594cb413292bb2', '00030002', '服务器监控', 'sys.server.server', 'menu', '/platform/sys/server', NULL, NULL, 1, 0, 'sys.server.server', NULL, 80, 0, '18e8f1c8168b4dc9a12156bc9f2e294b', 1621933019216, '18e8f1c8168b4dc9a12156bc9f2e294b', 1621936257133, 0);