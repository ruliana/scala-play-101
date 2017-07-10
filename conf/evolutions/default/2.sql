# Add createdAt

# --- !Ups

ALTER TABLE users ADD COLUMN created_at timestamp with time zone DEFAULT now();

# --- !Downs

ALTER TABLE users DROP COLUMN created_at;
