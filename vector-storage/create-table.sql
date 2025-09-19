CREATE TABLE IF NOT EXISTS documents (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    filename VARCHAR NOT NULL,
    content VARCHAR NOT NULL,
    embedding vector(3072) NOT NULL
);