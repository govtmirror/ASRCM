-- This script inserts the base static data for srcalc.

-- SPECIALTY table
INSERT INTO SPECIALTY (id, vista_id, name) VALUES (1, 50, 'General Surgery');
INSERT INTO SPECIALTY (id, vista_id, name) VALUES (2, 52, 'Neurosurgery');
INSERT INTO SPECIALTY (id, vista_id, name) VALUES (3, 54, 'Orthopedic');
INSERT INTO SPECIALTY (id, vista_id, name) VALUES (4, 58, 'Thoracic');
INSERT INTO SPECIALTY (id, vista_id, name) VALUES (5, 59, 'Urology');
INSERT INTO SPECIALTY (id, vista_id, name) VALUES (6, 62, 'Vascular');
INSERT INTO SPECIALTY (id, vista_id, name) VALUES (7, 48, 'Cardiac');
-- TODO: validate assumption that other -> General Surgery
INSERT INTO SPECIALTY (id, vista_id, name) VALUES (8, 50, 'Other Non-Cardiac Specialty');

-- *** Variables ***

-- Cardiac Gender
INSERT INTO VARIABLE (id, display_name) VALUES (1, 'Gender');
INSERT INTO MULTI_SELECT_VARIABLE (id, display_type) VALUES (1, 'Radio');
-- Intentionally reverse Female and Male to verify display order.
INSERT INTO MULTI_SELECT_OPTION (id, option_value) VALUES (1, 'Female');
INSERT INTO MULTI_SELECT_OPTION (id, option_value) VALUES (2, 'Male');
INSERT INTO MULTI_SELECT_VARIABLE_OPTION (variable_id, option_id, option_index) VALUES (1, 2, 0);
INSERT INTO MULTI_SELECT_VARIABLE_OPTION (variable_id, option_id, option_index) VALUES (1, 1, 1);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (7, 1, 0);

-- Non-Cardiac Procedure
INSERT INTO VARIABLE (id, display_name) VALUES (3, 'Procedure');
INSERT INTO PROCEDURE_VARIABLE (id) VALUES (3);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (1, 3, 0);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (2, 3, 0);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (3, 3, 0);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (4, 3, 0);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (5, 3, 0);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (6, 3, 0);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (8, 3, 0);

-- Non-Cardiac Age
INSERT INTO VARIABLE (id, display_name) VALUES (2, 'Age');
-- There is not really an upper limit on age, but specify an unrealistically high
-- one to have some idea of significant digits.
INSERT INTO NUMERICAL_VARIABLE (id, min_value, max_value) VALUES (2, 0, 999);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (1, 2, 1);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (2, 2, 1);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (3, 2, 1);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (4, 2, 1);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (5, 2, 1);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (6, 2, 1);
INSERT INTO SPECIALTY_VARIABLE (specialty_id, variable_id, display_order) VALUES (8, 2, 1);