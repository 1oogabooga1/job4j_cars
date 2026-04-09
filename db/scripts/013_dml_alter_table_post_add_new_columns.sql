ALTER TABLE auto_post ADD COLUMN photo_id INT REFERENCES photo(id);
ALTER TABLE auto_post ADD COLUMN car_id INT REFERENCES car(id);
