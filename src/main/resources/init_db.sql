-- create sequence for user
CREATE SEQUENCE user_id_seq INCREMENT 1 START 1;

-- create table user
CREATE TABLE "user" (
    id BIGINT PRIMARY KEY DEFAULT nextval('user_id_seq'),
    firstname VARCHAR(40) NOT NULL,
    lastname VARCHAR(40),
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(70) NOT NULL,
    provider VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    is_letter_recipient BOOLEAN NOT NULL,
    is_email_verified BOOLEAN NOT NULL
);


-- create sequence for jwt token
CREATE SEQUENCE jwt_token_id_seq INCREMENT 1 START 1;

-- create table jwt_token
CREATE TABLE jwt_token (
    id BIGINT PRIMARY KEY DEFAULT nextval('jwt_token_id_seq'),
    token VARCHAR(255) NOT NULL,
    expired BOOLEAN NOT NULL ,
    user_id BIGINT NOT NULL ,
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);


-- create sequence for confirmation token
CREATE SEQUENCE confirmation_token_id_seq INCREMENT 1 START 1;

-- create table confirmation_token
CREATE TABLE confirmation_token (
   id INTEGER PRIMARY KEY DEFAULT nextval('confirmation_token_id_seq'),
   token VARCHAR(255) NOT NULL,
   created_at TIMESTAMP NOT NULL,
   used BOOLEAN NOT NULL ,
   valid_for_usage BOOLEAN NOT NULL ,
   user_id BIGINT NOT NULL ,
   FOREIGN KEY (user_id) REFERENCES "user" (id)
);


-- create sequence for newsletter_receiver_not_registered
CREATE SEQUENCE newsletter_receiver_not_registered_id_seq INCREMENT 1 START 1;

-- create table newsletter_receiver_not_registered
CREATE TABLE newsletter_receiver_not_registered (
    id BIGINT PRIMARY KEY DEFAULT nextval('newsletter_receiver_not_registered_id_seq'),
    email VARCHAR(60) NOT NULL,
    message VARCHAR(255) NOT NULL
);