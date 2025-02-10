# Oracle 与 MySQL 对比及 MySQL 快速入门指南

## 目录
1. Oracle 与 MySQL 核心区别
2. 语法差异详解
3. MySQL 基本语法
4. MySQL 常用函数
5. 迁移注意事项
6. 总结

---

## 1. Oracle 与 MySQL 核心区别

| 特性                | Oracle                            | MySQL                          |
|---------------------|-----------------------------------|--------------------------------|
| 数据库架构          | 多租户架构 (CDB/PDB)              | 单实例多数据库架构             |
| 存储引擎            | 单一存储引擎                      | 多存储引擎 (InnoDB/MyISAM等)   |
| 事务提交模式        | 默认自动提交需显式控制            | 默认自动提交                   |
| 字符串空值处理      | NULL 与 '' 等价                   | NULL 与 '' 严格区分            |
| 日期类型            | DATE 包含日期时间                 | DATE 仅日期，时间需 DATETIME   |
| 序列生成            | SEQUENCE 对象                     | AUTO_INCREMENT 列属性          |
| 分页机制            | ROWNUM 伪列                       | LIMIT 子句                     |

---

## 2. 语法差异详解

### 2.1 数据类型差异
```sql
-- Oracle
CREATE TABLE users (
    id NUMBER(10) PRIMARY KEY,
    name VARCHAR2(50),
    salary NUMBER(10,2),
    reg_date DATE
);

-- MySQL
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    salary DECIMAL(10,2),
    reg_date DATETIME
);
```

### 2.2 字符串处理
```sql
-- Oracle
SELECT first_name || ' ' || last_name AS full_name FROM employees;

-- MySQL
SELECT CONCAT(first_name, ' ', last_name) AS full_name FROM employees;
-- 或使用空格操作符（5.7+）
SELECT first_name ' ' last_name AS full_name FROM employees;
```
### 2.3 日期处理
```sql
-- Oracle
SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') FROM dual;

-- MySQL
SELECT DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s');
```

### 2.4 分页查询
```sql
-- Oracle
SELECT * FROM (
    SELECT t.*, ROWNUM rn 
    FROM (SELECT * FROM employees ORDER BY id) t
) WHERE rn BETWEEN 11 AND 20;

-- MySQL
SELECT * FROM employees ORDER BY id LIMIT 10 OFFSET 10;
```

### 2.5 自增列处理
```sql
-- Oracle
CREATE SEQUENCE seq_user START WITH 1;
INSERT INTO users (id, name) VALUES (seq_user.NEXTVAL, 'Alice');

-- MySQL
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50)
);
INSERT INTO users (name) VALUES ('Bob');
```

## 3. MySQL 基本语法
### 3.1 数据库操作
```sql
-- 创建数据库
CREATE DATABASE mydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 选择数据库
USE mydb;
```

### 3.2 表操作
```sql
-- 创建表（InnoDB引擎）
CREATE TABLE employees (
    emp_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_name VARCHAR(50) NOT NULL,
    hire_date DATE,
    salary DECIMAL(10,2) DEFAULT 0.00,
    INDEX idx_name (emp_name)
) ENGINE=InnoDB;

-- 修改表结构
ALTER TABLE employees ADD COLUMN dept_id INT AFTER salary;
```

### 3.3 数据操作
```sql
-- 插入数据
INSERT INTO employees (emp_name, hire_date, salary)
VALUES ('张三', '2023-01-15', 8500.00);

-- 批量插入
INSERT INTO employees (emp_name, hire_date)
VALUES ('李四', CURDATE()), ('王五', '2024-02-01');

-- 更新数据
UPDATE employees SET salary = salary * 1.1 WHERE dept_id = 10;

-- 删除数据
DELETE FROM employees WHERE emp_id = 100;
```

### 3.4 查询语句
```sql
-- 基础查询
SELECT emp_id, emp_name, salary 
FROM employees 
WHERE salary > 5000 
ORDER BY hire_date DESC
LIMIT 5;

-- 连接查询
SELECT e.emp_name, d.dept_name
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id;

-- 分组统计
SELECT dept_id, AVG(salary) avg_sal, COUNT(*) emp_count
FROM employees
GROUP BY dept_id
HAVING avg_sal > 8000;
```
## 4. MySQL 常用函数
### 4.1 字符串函数
```sql
SELECT 
    CONCAT('Hello', ' ', 'World'),   -- 字符串连接
    LOWER(emp_name),                -- 转小写
    SUBSTRING(emp_name, 1, 3),      -- 截取子串
    REPLACE(phone, '-', '')         -- 替换字符
FROM employees;
```
### 4.2 日期函数
```sql
SELECT 
    NOW(),                          -- 当前日期时间
    CURDATE(),                      -- 当前日期
    DATE_ADD(hire_date, INTERVAL 1 YEAR),  -- 日期计算
    DATEDIFF(NOW(), hire_date)      -- 日期差值
FROM employees;
```

### 4.3 数值函数
```sql
SELECT 
    ROUND(123.4567, 2),             -- 四舍五入
    CEILING(123.1),                 -- 向上取整
    FLOOR(123.9),                   -- 向下取整
    FORMAT(1234567.89, 2)           -- 千分位格式化
FROM dual;
```
### 4.4 条件函数
```sql
SELECT 
    IF(salary > 10000, '高薪', '普通'),  -- 简单条件
    CASE dept_id
        WHEN 10 THEN '技术部'
        WHEN 20 THEN '市场部'
        ELSE '其他部门'
    END AS dept_name,
    COALESCE(address, '未知地址')     -- 空值处理
FROM employees;
```
