CREATE TABLE IF NOT EXISTS documents (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    content VARCHAR NOT NULL,
    embedding vector(1152) NOT NULL
);