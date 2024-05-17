-- Insert a user if not exists
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM "user" WHERE username = 'jim') THEN
            INSERT INTO "user" (username, password, app_sound, sensor_sound, desk_mode)
            VALUES ('jim', 'ef51306214d9a6361ee1d5b452e6d2bb70dc7ebb85bf9e02c3d4747fb57d6bec', true, true, false);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM "user" WHERE username = 'linda') THEN
            INSERT INTO "user" (username, password, app_sound, sensor_sound, desk_mode)
            VALUES ('linda', 'ef51306214d9a6361ee1d5b452e6d2bb70dc7ebb85bf9e02c3d4747fb57d6bec', true, true, false);
        END IF;
    END
$$;

-- Get Jim's user ID and insert quests
DO $$
    DECLARE
        jim_user_id INTEGER;
    BEGIN
        SELECT id INTO jim_user_id FROM "user" WHERE username = 'jim';

        -- Check and insert quests
        IF NOT EXISTS (SELECT 1 FROM "quest" WHERE user_id = jim_user_id AND title = 'Complete Software Engineering Assignment') THEN
            INSERT INTO "quest" (user_id, completion_state, title, detail, start_time, complete_time)
            VALUES
                (jim_user_id, true, 'Complete Software Engineering Assignment', 'Work on the software engineering group project.', '2024-03-25 08:00:00', '2024-03-26 18:00:00'),
                (jim_user_id, true, 'Prepare for Math Exam', 'Study chapters 4 to 7 for the upcoming math exam.', '2024-03-27 09:00:00', '2024-03-30 15:00:00'),
                (jim_user_id, true, 'Morning Yoga Session', 'Attend the weekly morning yoga class.', '2024-03-31 06:00:00', '2024-03-31 07:00:00'),
                (jim_user_id, true, 'Meditation Routine', 'Follow a guided meditation for 30 minutes.', '2024-04-01 07:30:00', '2024-04-01 08:00:00'),
                (jim_user_id, true, 'Outdoor Hiking Trip', 'Plan and go for a hiking trip this weekend.', '2024-04-02 10:00:00', '2024-04-02 14:00:00'),
                (jim_user_id, true, 'Complete UI Design Task', 'Work on the UI design for the new app feature.', '2024-04-03 11:00:00', '2024-04-05 17:00:00'),
                (jim_user_id, true, 'Read Software Engineering Book', 'Read the assigned chapters from the software engineering book.', '2024-04-06 18:00:00', '2024-04-10 20:00:00'),
                (jim_user_id, true, 'Pomodoro Study Session 1', 'Use Pomodoro technique to study for software engineering.', '2024-04-11 09:00:00', '2024-04-11 12:00:00'),
                (jim_user_id, true, 'Pomodoro Study Session 2', 'Use Pomodoro technique to prepare for math exam.', '2024-04-12 13:00:00', '2024-04-12 13:30:00'),
                (jim_user_id, false, 'Pomodoro Study Session 3', 'Use Pomodoro technique to complete UI design task.', NULL, NULL),
                (jim_user_id, false, 'Group Project Meeting', 'Attend and contribute to the group project meeting.', NULL, NULL),
                (jim_user_id, true, 'Complete Data Structures Assignment', 'Finish the data structures homework.', '2024-04-13 08:00:00', '2024-04-13 12:00:00'),
                (jim_user_id, false, 'Prepare Presentation', 'Prepare slides for the upcoming presentation.', NULL, NULL),
                (jim_user_id, true, 'Attend Workshop', 'Participate in the software engineering workshop.', '2024-04-14 09:00:00', '2024-04-14 17:00:00'),
                (jim_user_id, false, 'Complete Algorithm Design Task', 'Work on algorithm design problems.', NULL, NULL),
                (jim_user_id, true, 'Attend Yoga Retreat', 'Join the weekend yoga retreat.', '2024-04-15 08:00:00', '2024-04-17 17:00:00'),
                (jim_user_id, false, 'Practice Coding Problems', 'Solve various coding problems online.', NULL, NULL),
                (jim_user_id, true, 'Finish Reading Novel', 'Complete reading the novel for literature class.', '2024-05-13 18:00:00', '2024-05-14 20:00:00'),
                (jim_user_id, true, 'Weekly Team Sync', 'Participate in the weekly team sync meeting.', '2024-04-22 10:00:00', '2024-04-22 11:00:00'),
                (jim_user_id, true, 'Submit Tax Documents', 'Prepare and submit the necessary tax documents.', '2024-04-27 09:00:00', '2024-04-27 17:00:00'),
                (jim_user_id, true, 'Organize Workspace', 'Clean and organize the home office workspace.', '2024-05-02 14:00:00', '2024-05-02 16:00:00'),
                (jim_user_id, true, 'Grocery Shopping', 'Do the weekly grocery shopping.', '2024-05-10 10:00:00', '2024-05-10 11:30:00');
        END IF;

        -- Get the IDs of the quests for adding tasks and Pomodoro timers
        DECLARE
            quest1_id INTEGER;
            quest2_id INTEGER;
            quest3_id INTEGER;
            quest4_id INTEGER;
            quest5_id INTEGER;
            quest6_id INTEGER;
            quest7_id INTEGER;
            quest8_id INTEGER;
            quest9_id INTEGER;
            quest10_id INTEGER;
            quest11_id INTEGER;
            pomodoro_quest1_id INTEGER;
            pomodoro_quest2_id INTEGER;
            pomodoro_quest3_id INTEGER;
        BEGIN
            SELECT id INTO quest1_id FROM "quest" WHERE title = 'Complete Software Engineering Assignment' AND user_id = jim_user_id;
            SELECT id INTO quest2_id FROM "quest" WHERE title = 'Prepare for Math Exam' AND user_id = jim_user_id;
            SELECT id INTO quest3_id FROM "quest" WHERE title = 'Complete UI Design Task' AND user_id = jim_user_id;
            SELECT id INTO quest4_id FROM "quest" WHERE title = 'Read Software Engineering Book' AND user_id = jim_user_id;
            SELECT id INTO quest5_id FROM "quest" WHERE title = 'Group Project Meeting' AND user_id = jim_user_id;
            SELECT id INTO quest6_id FROM "quest" WHERE title = 'Complete Data Structures Assignment' AND user_id = jim_user_id;
            SELECT id INTO quest7_id FROM "quest" WHERE title = 'Prepare Presentation' AND user_id = jim_user_id;
            SELECT id INTO quest8_id FROM "quest" WHERE title = 'Attend Workshop' AND user_id = jim_user_id;
            SELECT id INTO quest9_id FROM "quest" WHERE title = 'Complete Algorithm Design Task' AND user_id = jim_user_id;
            SELECT id INTO quest10_id FROM "quest" WHERE title = 'Attend Yoga Retreat' AND user_id = jim_user_id;
            SELECT id INTO quest11_id FROM "quest" WHERE title = 'Practice Coding Problems' AND user_id = jim_user_id;
            SELECT id INTO pomodoro_quest1_id FROM "quest" WHERE title = 'Pomodoro Study Session 1' AND user_id = jim_user_id;
            SELECT id INTO pomodoro_quest2_id FROM "quest" WHERE title = 'Pomodoro Study Session 2' AND user_id = jim_user_id;
            SELECT id INTO pomodoro_quest3_id FROM "quest" WHERE title = 'Pomodoro Study Session 3' AND user_id = jim_user_id;

            -- Check and insert tasks for the quests
            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest1_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest1_id, 'Research project topics', '2024-03-25 08:00:00', '2024-03-25 18:00:00', true),
                    (quest1_id, 'Draft project proposal', '2024-03-26 09:00:00', '2024-03-26 13:00:00', true),
                    (quest1_id, 'Develop project outline', '2024-03-26 14:00:00', '2024-03-26 18:00:00', true);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest2_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest2_id, 'Review chapter 4', '2024-03-27 09:00:00', '2024-03-27 11:00:00', true),
                    (quest2_id, 'Solve practice problems', '2024-03-28 14:00:00', '2024-03-28 16:00:00', true),
                    (quest2_id, 'Take chapter 5 quiz', '2024-03-30 10:00:00', '2024-03-30 15:00:00', true);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest3_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest3_id, 'Sketch initial design', '2024-04-03 11:00:00', '2024-04-03 13:00:00', false),
                    (quest3_id, 'Create wireframes', '2024-04-04 14:00:00', '2024-04-05 17:00:00', false);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest4_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest4_id, 'Read chapter 1', '2024-04-06 18:00:00', '2024-04-07 20:00:00', true),
                    (quest4_id, 'Summarize key points', '2024-04-08 09:00:00', '2024-04-10 20:00:00', true);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest5_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest5_id, 'Prepare meeting agenda', '2024-04-09 08:00:00', '2024-04-09 10:00:00', false),
                    (quest5_id, 'Discuss project milestones', '2024-04-10 11:00:00', '2024-04-10 13:00:00', false);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest6_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest6_id, 'Complete coding exercises', '2024-04-13 08:00:00', '2024-04-13 10:00:00', true),
                    (quest6_id, 'Review data structures', '2024-04-13 10:30:00', '2024-04-13 12:00:00', true);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest7_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest7_id, 'Research presentation topic', '2024-04-14 09:00:00', '2024-04-14 11:00:00', false),
                    (quest7_id, 'Create slides', '2024-04-14 13:00:00', '2024-04-14 15:00:00', false);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest8_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest8_id, 'Attend sessions', '2024-04-14 09:00:00', '2024-04-14 12:00:00', true),
                    (quest8_id, 'Participate in activities', '2024-04-14 13:00:00', '2024-04-14 17:00:00', true);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest9_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest9_id, 'Solve algorithm problems', '2024-04-16 08:00:00', '2024-04-16 10:00:00', false),
                    (quest9_id, 'Optimize algorithms', '2024-04-16 11:00:00', '2024-04-16 13:00:00', false);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest10_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest10_id, 'Join yoga sessions', '2024-04-15 08:00:00', '2024-04-15 10:00:00', true),
                    (quest10_id, 'Meditate daily', '2024-04-16 07:00:00', '2024-04-17 07:30:00', true);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "task" WHERE quest_id = quest11_id) THEN
                INSERT INTO "task" (quest_id, description, start_time, end_time, completion_state)
                VALUES
                    (quest11_id, 'Solve coding challenges', '2024-04-18 18:00:00', '2024-04-18 20:00:00', false),
                    (quest11_id, 'Participate in coding competitions', '2024-04-19 09:00:00', '2024-04-19 11:00:00', false);
            END IF;

            -- Check and insert Pomodoro timers for the Pomodoro quests
            IF NOT EXISTS (SELECT 1 FROM "pomodoro_timer" WHERE quest_id = pomodoro_quest1_id) THEN
                INSERT INTO "pomodoro_timer" (quest_id, focus_time, break_time, interval)
                VALUES (pomodoro_quest1_id, 25, 5, 4);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "pomodoro_timer" WHERE quest_id = pomodoro_quest2_id) THEN
                INSERT INTO "pomodoro_timer" (quest_id, focus_time, break_time, interval)
                VALUES (pomodoro_quest2_id, 25, 5, 4);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "pomodoro_timer" WHERE quest_id = pomodoro_quest3_id) THEN
                INSERT INTO "pomodoro_timer" (quest_id, focus_time, break_time, interval)
                VALUES (pomodoro_quest3_id, 25, 5, 4);
            END IF;

            -- Check and insert box open records during quest running
            IF NOT EXISTS (SELECT 1 FROM "box_open_record" WHERE user_id = jim_user_id AND quest_id = quest1_id) THEN
                INSERT INTO "box_open_record" (user_id, quest_id, time)
                VALUES
                    (jim_user_id, quest1_id, '2024-03-25 09:00:00'),
                    (jim_user_id, quest1_id, '2024-03-25 11:00:00'),
                    (jim_user_id, quest1_id, '2024-03-25 13:00:00');
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "box_open_record" WHERE user_id = jim_user_id AND quest_id = quest2_id) THEN
                INSERT INTO "box_open_record" (user_id, quest_id, time)
                VALUES
                    (jim_user_id, quest2_id, '2024-03-27 10:00:00'),
                    (jim_user_id, quest2_id, '2024-03-28 09:00:00'),
                    (jim_user_id, quest2_id, '2024-03-28 14:00:00');
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "box_open_record" WHERE user_id = jim_user_id AND quest_id = quest3_id) THEN
                INSERT INTO "box_open_record" (user_id, quest_id, time)
                VALUES
                    (jim_user_id, quest3_id, '2024-04-03 12:00:00'),
                    (jim_user_id, quest3_id, '2024-04-04 11:00:00'),
                    (jim_user_id, quest3_id, '2024-04-05 15:00:00');
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "box_open_record" WHERE user_id = jim_user_id AND quest_id = quest4_id) THEN
                INSERT INTO "box_open_record" (user_id, quest_id, time)
                VALUES
                    (jim_user_id, quest4_id, '2024-04-06 19:00:00'),
                    (jim_user_id, quest4_id, '2024-04-08 18:00:00'),
                    (jim_user_id, quest4_id, '2024-04-09 20:00:00');
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "box_open_record" WHERE user_id = jim_user_id AND quest_id = quest5_id) THEN
                INSERT INTO "box_open_record" (user_id, quest_id, time)
                VALUES
                    (jim_user_id, quest5_id, '2024-04-09 08:30:00'),
                    (jim_user_id, quest5_id, '2024-04-09 09:30:00'),
                    (jim_user_id, quest5_id, '2024-04-10 10:30:00');
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "box_open_record" WHERE user_id = jim_user_id AND quest_id = pomodoro_quest1_id) THEN
                INSERT INTO "box_open_record" (user_id, quest_id, time)
                VALUES
                    (jim_user_id, pomodoro_quest1_id, '2024-04-11 09:25:00'),
                    (jim_user_id, pomodoro_quest1_id, '2024-04-11 10:25:00');
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "box_open_record" WHERE user_id = jim_user_id AND quest_id = pomodoro_quest2_id) THEN
                INSERT INTO "box_open_record" (user_id, quest_id, time)
                VALUES
                    (jim_user_id, pomodoro_quest2_id, '2024-04-12 13:30:00'),
                    (jim_user_id, pomodoro_quest2_id, '2024-04-12 14:30:00'),
                    (jim_user_id, pomodoro_quest2_id, '2024-04-12 15:30:00');
            END IF;

            IF NOT EXISTS (SELECT 1 FROM "box_open_record" WHERE user_id = jim_user_id AND quest_id = pomodoro_quest3_id) THEN
                INSERT INTO "box_open_record" (user_id, quest_id, time)
                VALUES
                    (jim_user_id, pomodoro_quest3_id, '2024-04-18 09:00:00'),
                    (jim_user_id, pomodoro_quest3_id, '2024-04-18 10:00:00'),
                    (jim_user_id, pomodoro_quest3_id, '2024-04-18 11:00:00');
            END IF;
        END;
    END
$$;
