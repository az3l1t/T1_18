--- Norair (az3l1t) file
--- Last change - 06.05.2025 (10:46 AM)
ALTER TABLE tasks
    ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'NEW';

UPDATE tasks SET status = 'NEW'
WHERE status IS NULL;
