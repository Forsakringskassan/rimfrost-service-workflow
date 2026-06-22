CREATE TABLE handlaggning_reply_topic(
    handlaggning_id     UUID NOT NULL PRIMARY KEY,
    reply_topic         VARCHAR(255) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL
);