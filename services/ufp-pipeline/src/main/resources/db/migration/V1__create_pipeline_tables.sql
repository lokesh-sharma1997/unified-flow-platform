CREATE TABLE pipelines (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    status      VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by  VARCHAR(100) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE pipeline_sources (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pipeline_id UUID NOT NULL UNIQUE REFERENCES pipelines(id) ON DELETE CASCADE,
    type        VARCHAR(30) NOT NULL,
    config      JSONB NOT NULL
);

CREATE TABLE pipeline_sinks (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pipeline_id UUID NOT NULL UNIQUE REFERENCES pipelines(id) ON DELETE CASCADE,
    type        VARCHAR(30) NOT NULL,
    config      JSONB NOT NULL
);

CREATE TABLE pipeline_transforms (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pipeline_id     UUID NOT NULL REFERENCES pipelines(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(30) NOT NULL,
    transform_order INTEGER NOT NULL,
    config          JSONB,
    UNIQUE (pipeline_id, transform_order)
);

CREATE TABLE pipeline_runs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pipeline_id     UUID NOT NULL REFERENCES pipelines(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'STARTING',
    triggered_by    VARCHAR(100),
    records_in      BIGINT NOT NULL DEFAULT 0,
    records_out     BIGINT NOT NULL DEFAULT 0,
    records_failed  BIGINT NOT NULL DEFAULT 0,
    error_message   TEXT,
    started_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at    TIMESTAMPTZ
);

CREATE INDEX idx_pipeline_status      ON pipelines(status);
CREATE INDEX idx_pipeline_created_by  ON pipelines(created_by);
CREATE INDEX idx_run_pipeline_id      ON pipeline_runs(pipeline_id);
CREATE INDEX idx_run_status           ON pipeline_runs(status);
