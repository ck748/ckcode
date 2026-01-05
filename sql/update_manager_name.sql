-- 修复管理员姓名显示为 ???1 的问题
-- 将 manager 表中的 name 字段从 ???1 改为 admin

USE defect_detection;

-- 更新 manager 表
UPDATE manager SET name = 'admin' WHERE id = 1 AND account = 'admin1';

-- 验证更新结果
SELECT id, account, name, phone_number, email FROM manager WHERE id = 1;

-- 同时更新其他相关表的数据
UPDATE operator SET create_name = 'admin', name = 'admin' WHERE id = 1;
UPDATE api SET description = 'admin' WHERE id = 1;
UPDATE detect_log SET name = 'admin' WHERE id = 1;

-- 查看更新后的结果
SELECT '=== Manager 表 ===' AS table_name;
SELECT id, account, name FROM manager WHERE id = 1;

SELECT '=== Operator 表 ===' AS table_name;
SELECT id, name, create_name FROM operator WHERE id = 1;

SELECT '=== API 表 ===' AS table_name;
SELECT id, description FROM api WHERE id = 1;

SELECT '=== Detect_log 表 ===' AS table_name;
SELECT id, name FROM detect_log WHERE id = 1;
