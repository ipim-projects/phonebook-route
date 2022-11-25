INSERT INTO fullbook2(first_name,last_name, work_phone, mobile_phone, email, birthdate, company, job_title, address)
VALUES (:#${body.firstName}, :#${body.lastName},:#${body.workPhone},:#${body.mobilePhone}, :#${body.email}, TO_DATE(:#${body.birthdate},'YYYY-MM-DD'), :#${body.company}, :#${body.jobTitle}, :#${body.address});
