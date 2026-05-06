CREATE TABLE workflows (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    version     INTEGER NOT NULL DEFAULT 1,
    status      VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by  VARCHAR(100) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE workflow_steps (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id     UUID NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(30) NOT NULL,
    step_order      INTEGER NOT NULL,
    config          JSONB,
    timeout_seconds INTEGER,
    retry_count     INTEGER NOT NULL DEFAULT 0,
    depends_on      VARCHAR(255),
    UNIQUE (workflow_id, step_order)
);

CREATE TABLE workflow_executions (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id   UUID NOT NULL REFERENCES workflows(id),
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    triggered_by  VARCHAR(100),
    input_params  JSONB,
    output_params JSONB,
    error_message TEXT,
    started_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at  TIMESTAMPTZ
);

CREATE TABLE workflow_execution_steps (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    execution_id   UUID NOT NULL REFERENCES workflow_executions(id) ON DELETE CASCADE,
    step_id        UUID NOT NULL REFERENCES workflow_steps(id),
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attempt_number INTEGER NOT NULL DEFAULT 1,
    output         JSONB,
    error_message  TEXT,
    started_at     TIMESTAMPTZ,
    completed_at   TIMESTAMPTZ
);

CREATE INDEX idx_workflow_status     ON workflows(status);
CREATE INDEX idx_workflow_created_by ON workflows(created_by);
CREATE INDEX idx_exec_workflow_id    ON workflow_executions(workflow_id);
CREATE INDEX idx_exec_status         ON workflow_executions(status);
CREATE INDEX idx_exec_step_exec_id   ON workflow_execution_steps(execution_id);
