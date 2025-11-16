ALTER TABLE defection
    ADD COLUMN severity_level INT COMMENT '严重程度等级1-5',
ADD COLUMN repair_suggestion VARCHAR(500) COMMENT '修复建议';