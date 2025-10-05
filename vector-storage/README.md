# Vector-Storage

This scripts are provided as temporary solution while liquibase is under construction. They provide basic database setup.

## create-extension.sql

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```
Must be executed first to enable `vector` field support.

## create-table.sql

```sql
CREATE TABLE IF NOT EXISTS documents (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    filename VARCHAR NOT NULL,
    content VARCHAR NOT NULL,
    embedding vector(1152) NOT NULL
);
```

Here we set vector size to 1152. This size must be the same as embedding model provides.
