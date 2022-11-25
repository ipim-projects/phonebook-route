------------------------------------------------------------------------------------------------------------------
-- Пересоздание БД "phonebook2"
-- Из flyWayDB пересоздание БД не работает, требуется запускать вручную из консоли SQL Shell
-- 
-- DROP DATABASE IF EXISTS phonebook2;
-- CREATE DATABASE phonebook2 WITH OWNER = postgres  ENCODING = 'UTF8' LC_COLLATE = 'Russian_Russia.1251' LC_CTYPE = 'Russian_Russia.1251' TABLESPACE = pg_default CONNECTION LIMIT = -1 IS_TEMPLATE = False;
------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------
-- Создание таблицы телефонного справочника
------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS public.fullbook2;

CREATE TABLE public.fullbook2 (first_name VARCHAR(10) NOT NULL, last_name VARCHAR(20) NOT NULL, birthdate date, mobile_phone CHAR(13) NOT NULL, work_phone CHAR(13), email VARCHAR(41)  NOT NULL, company VARCHAR(200), job_title VARCHAR(150), address VARCHAR(255));

ALTER TABLE IF EXISTS public.fullbook2 OWNER to postgres;
