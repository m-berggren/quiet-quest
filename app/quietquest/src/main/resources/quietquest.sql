CREATE TABLE IF NOT EXISTS "user"
(
    "id"           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    "username"     varchar NOT NULL,
    "password"     varchar NOT NULL,
    "created_at"   timestamp,
    "app_sound"    bool,
    "sensor_sound" bool,
    "desk_mode"    bool
);

CREATE TABLE IF NOT EXISTS "quest"
(
    "id"               integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    "user_id"          integer NOT NULL,
    "completion_state" bool,
    "created_at"       timestamp,
    "title"            varchar NOT NULL,
    "detail"           varchar,
    "start_time"       timestamp,
    "complete_time"    timestamp,
    "box_open_times"   int
);

CREATE TABLE IF NOT EXISTS "task"
(
    "id"               integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    "quest_id"         integer NOT NULL,
    "description"      varchar NOT NULL,
    "start_time"       timestamp,
    "end_time"         timestamp,
    "completion_state" bool
);

CREATE TABLE IF NOT EXISTS "box_open_record"
(
    "user_id"  integer NOT NULL,
    "quest_id" integer NOT NULL,
    "time"     timestamp NOT NULL,
    PRIMARY KEY ("user_id", "quest_id", "time")
);

CREATE TABLE IF NOT EXISTS "pomodoro_quest"
(
    "quest_id"   integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    "focus_time" integer NOT NULL,
    "break_time" integer NOT NULL,
    "interval"   integer NOT NULL
);
