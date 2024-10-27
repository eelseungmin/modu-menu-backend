-- 모든 제약 조건 비활성화
SET REFERENTIAL_INTEGRITY FALSE;

truncate table user_tb;
truncate table vote_tb;
truncate table choice_tb;
truncate table vote_item_tb;
truncate table place_tb;
truncate table review_tb;
truncate table review_vibe_tb;
truncate table vibe_tb;
truncate table place_vibe_tb;
truncate table place_food_tb;
truncate table food_tb;
truncate table participant_tb;

-- 모든 제약 조건 활성화
SET REFERENTIAL_INTEGRITY TRUE;