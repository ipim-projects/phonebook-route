------------------------------------------------------------------------------------------------------------------
-- Триггер для реализации:
-- "При загрузке в БД данных из файла различающиеся или пустые поля справочника переписываются значениями из файла."
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION trigger_fullbook2_before_insert () RETURNS trigger AS $fullbook2_before_insert$ 
DECLARE
   old_birthdate DATE; new_birthdate DATE;
   old_mobile_phone CHAR(13); new_mobile_phone CHAR(13);
   old_work_phone CHAR(13); new_work_phone CHAR(13);
   old_email VARCHAR(41); new_email VARCHAR(41);
   old_company VARCHAR(200); new_company VARCHAR(200);
   old_job_title VARCHAR(150); new_job_title VARCHAR(150);
   old_address VARCHAR(255); new_address VARCHAR(255);
BEGIN
        -- Проверить, что переданы непустые имя и фамилия сотрудника
        IF NEW.first_name IS NULL THEN
            RAISE EXCEPTION 'first_name cannot be null';
        END IF;
        IF NEW.last_name IS NULL THEN
            RAISE EXCEPTION 'last_name cannot be null';
        END IF;

        -- добавить новую строку в телефонный справочник, если записи о таком сотруднике ещё нет
        IF (select count(*) from fullbook2 where first_name = NEW.first_name and last_name = NEW.last_name) = 0 THEN
            RETURN NEW;
        END IF;

        -- Внести новые значения в существующую строку телефонного справочника: различающиеся или пустые поля справочника переписываются новыми значениями 
        select birthdate, mobile_phone, work_phone, email, company, job_title, address into old_birthdate, old_mobile_phone, old_work_phone, old_email, old_company, old_job_title, old_address from fullbook2 where  first_name = NEW.first_name and last_name = NEW.last_name;

        IF (new.birthdate is not null) THEN new_birthdate := NEW.birthdate; ELSE new_birthdate := old_birthdate; END IF;
        IF (new.mobile_phone is not null) THEN new_mobile_phone := NEW.mobile_phone; ELSE new_mobile_phone := old_mobile_phone; END IF;
        IF (new.work_phone is not null) THEN new_work_phone := NEW.work_phone; ELSE new_work_phone := old_work_phone; END IF;
        IF (new.email is not null) THEN new_email := NEW.email; ELSE new_email := old_email; END IF;
        IF (new.company is not null) THEN new_company := NEW.company; ELSE new_company := old_company; END IF;
        IF (new.job_title is not null) THEN new_job_title := NEW.job_title; ELSE new_job_title := old_job_title; END IF;
        IF (new.address is not null) THEN new_address := NEW.address; ELSE new_address := old_address; END IF;

        UPDATE fullbook2
                SET birthdate = new_birthdate,
                    mobile_phone = new_mobile_phone, 
                    work_phone = new_work_phone,
                    email = new_email, 
                    company = new_company, 
                    job_title = new_job_title, 
                    address = new_address
                WHERE first_name = NEW.first_name and last_name = NEW.last_name;

        RETURN NULL;
END;
$fullbook2_before_insert$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER fullbook2_before_insert BEFORE INSERT ON fullbook2 FOR EACH ROW EXECUTE PROCEDURE trigger_fullbook2_before_insert();
