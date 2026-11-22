-- =====================================================================
-- Migración: guardar el orden del sorteo en torneo_equipos
-- Ejecutar UNA VEZ en una base que ya tenía las tablas creadas.
-- Si todavía no tenés datos que te importen, es más simple borrar las
-- tablas y volver a correr crear-tablas.sql.
-- =====================================================================

BEGIN;

ALTER TABLE torneo_equipos ADD COLUMN IF NOT EXISTS orden integer;

-- Los torneos que ya existían no guardaron el orden del sorteo: se les asigna
-- uno por id de equipo. Las llaves de esos torneos pueden no coincidir con los
-- partidos ya cargados; se recomienda borrar esos torneos y sus partidos.
UPDATE torneo_equipos te
SET orden = x.pos
FROM (
    SELECT torneo_id, equipo_id,
           ROW_NUMBER() OVER (PARTITION BY torneo_id ORDER BY equipo_id) - 1 AS pos
    FROM torneo_equipos
) x
WHERE te.torneo_id = x.torneo_id AND te.equipo_id = x.equipo_id;

ALTER TABLE torneo_equipos ALTER COLUMN orden SET NOT NULL;
ALTER TABLE torneo_equipos DROP CONSTRAINT IF EXISTS torneo_equipos_pkey;
ALTER TABLE torneo_equipos ADD PRIMARY KEY (torneo_id, orden);
ALTER TABLE torneo_equipos ADD CONSTRAINT uq_torneo_equipos_equipo UNIQUE (torneo_id, equipo_id);

COMMIT;
