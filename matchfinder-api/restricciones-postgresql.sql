-- Ejecutar UNA VEZ, después de que Hibernate haya creado las tablas (primer arranque de la app).
-- Garantizan a nivel de base de datos que no puede haber dos owners
-- ni dos torneos activos, aunque lleguen dos peticiones al mismo tiempo.

CREATE UNIQUE INDEX IF NOT EXISTS uq_un_solo_owner
    ON usuarios ((1)) WHERE rol = 'OWNER';

CREATE UNIQUE INDEX IF NOT EXISTS uq_un_torneo_activo
    ON torneos ((1)) WHERE estado = 'ACTIVO';
