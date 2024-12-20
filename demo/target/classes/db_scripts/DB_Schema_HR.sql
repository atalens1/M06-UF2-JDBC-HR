CREATE DATABASE IF NOT EXISTS HR;

USE HR;

CREATE TABLE IF NOT EXISTS  DEPARTMENTS
   (DEPARTMENT_ID INT PRIMARY KEY,
    DEPARTMENT_NAME VARCHAR(30) NOT NULL,
    MANAGER_ID INT,
    LOCATION_ID INT
    );
 
CREATE TABLE IF NOT EXISTS JOBS
   (JOB_ID VARCHAR(10) PRIMARY KEY,
    JOB_TITLE VARCHAR(35) NOT NULL,
    MIN_SALARY INT,
    MAX_SALARY INT
   );
	
CREATE TABLE IF NOT EXISTS EMPLOYEES
   (EMPLOYEE_ID INT PRIMARY KEY,
    FIRST_NAME VARCHAR(20),
    LAST_NAME VARCHAR(25) NOT NULL,
    EMAIL VARCHAR(25) NOT NULL UNIQUE,
    PHONE_INT VARCHAR(20),
    HIRE_DATE DATE NOT NULL,
    JOB_ID VARCHAR(10) NOT NULL,
    SALARY FLOAT,
    COMMISSION_PCT FLOAT,
    MANAGER_ID INT,
    DEPARTMENT_ID INT,
	BONUS VARCHAR(5),
     CHECK (salary > 0)
  );
  
CREATE TABLE IF NOT EXISTS JOB_HISTORY
   (EMPLOYEE_ID INT NOT NULL,
    START_DATE DATE NOT NULL,
    END_DATE DATE,
    JOB_ID VARCHAR(10) NOT NULL,
    DEPARTMENT_ID INT,
     CHECK (end_date > start_date),
     PRIMARY KEY (EMPLOYEE_ID, START_DATE)
  );