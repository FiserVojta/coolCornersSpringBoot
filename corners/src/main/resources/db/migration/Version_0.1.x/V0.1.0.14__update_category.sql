alter table category
    add column if not exists category_type varchar(32);

alter table event
    add column if not exists category_id INTEGER;


INSERT INTO category ( name, main, title, category_type) VALUES ('Travel', TRUE, 'Travel opportunity', 'EVENT');
INSERT INTO category ( name, main, title, category_type) VALUES ( 'Presentation', TRUE, 'Trip presentation', 'EVENT');
INSERT INTO category ( name, main, title, category_type) VALUES ( 'Social', TRUE, 'Social gathering', 'EVENT');
INSERT INTO category (name, main, title, category_type) VALUES ( 'Volunteering', TRUE, 'Volunteering', 'EVENT');