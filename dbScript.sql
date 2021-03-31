CREATE SEQUENCE lis.citm_seq START WITH 1;

GRANT SELECT ON lis.citm_seq TO PUBLIC;

COMMENT ON SEQUENCE lis.citm_seq IS
  'Последовательность для генерации уникальных идентификаторов фактов использования прибора в Cobas IT Middleware.';

CREATE TABLE lis.citm (
  citm_id bigint NOT NULL DEFAULT nextval('lis.citm_seq'::regclass)
    constraint citm_pkey
    primary key,
  device_instance bigint
    constraint ref2device_instances
      references lis.rt_deviceinstances,
    constraint unique_device_instance
      unique (device_instance)
);

COMMENT ON TABLE lis.citm IS
  'Факт использования прибора в Cobas IT Middleware.';
COMMENT ON COLUMN lis.citm.citm_id IS
  'Уникальный идентификатор факта использования прибора в Cobas IT Middleware.';
COMMENT ON COLUMN lis.citm.device_instance IS
  'Идентификатор экземпляра прибора.';

ALTER TABLE lis.citm owner to gis;



CREATE SEQUENCE lis.citm_query_seq START WITH 1;

GRANT SELECT ON lis.citm_query_seq TO PUBLIC;

COMMENT ON SEQUENCE lis.citm_query_seq IS
  'Последовательность для генерации уникальных идентификаторов записей очереди CITM.';
--DROP TABLE lis.citm_query;
CREATE TABLE lis.citm_query (
  citm_query_id bigint NOT NULL DEFAULT nextval('lis.citm_query_seq'::regclass)
    constraint citm_query_pkey
    primary key,
  scheduled_container bigint NOT NULL
    CONSTRAINT ref2containers
      REFERENCES lis.scheduled_containers,
  added timestamp,
  processed timestamp,
  error_msg text
);

alter table lis.citm_query drop constraint ref2containers;
ALTER TABLE lis.citm_query owner to gis;


CREATE OR REPLACE FUNCTION lis.query_container_to_citm() RETURNS trigger AS $$
BEGIN
    -- Проверить, что указаны имя сотрудника и зарплата
    IF NEW.container_state = 'registered' AND NEW.barcode IS NOT NULL AND position('.' in NEW.barcode) = 0 THEN
        INSERT INTO lis.citm_query(scheduled_container, added) VALUES(NEW.scheduled_container, current_timestamp);
        IF NEW.resid IN (SELECT ) THEN
            INSERT INTO lis.device_query(scheduled_container, added) VALUES(NEW.scheduled_container, current_timestamp);
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

ALTER FUNCTION lis.query_container_to_citm() OWNER TO gis;

CREATE TRIGGER emp_stamp AFTER INSERT OR UPDATE ON lis.scheduled_containers
    FOR EACH ROW EXECUTE PROCEDURE lis.query_container_to_citm();

update lis.scheduled_containers set container_state = 'registered' where scheduled_container = 12778793;

select * from lis.citm_query astmOrder by citm_query_id;

-- тестовый пример
-- device_instance 51
-- code Immulite2000.2
-- barcode 19207484

select sc.barcode, si.device_instance from lis.scheduled_invests si
--join lis.citm c on si.device_instance = c.device_instance
join lis.scheduled_containers sc on sc.scheduled_sample = si.scheduled_sample and sc.barcode is not null
where si.invest_state = 'scheduled';


-- При регистрации образца добавляется запись в lis.citm_query. Сейчас все, не только для CITM предназначенные

-- в scheduled_invests есть investigation и technique
-- в rt_technique_positions для (investigation, technique) есть device_instance

-- select s1.* from lis.scheduled_containers s1
-- join lis.scheduled_containers s2 on s1.scheduled_sample = s2.scheduled_sample
--                                       and s1.scheduled_container != s2.scheduled_container
-- where s1.barcode is not null and s1.registration_time>

-- При регистрации образца происходит вызов ХП LIS.registerPrimaryContainer(?, ?, ?)

--drop FUNCTION lis.getTasks4CITM();

CREATE OR REPLACE FUNCTION lis.gettasks4citm(OUT task_id bigint, OUT sample_id character varying, OUT device_instance bigint,
                                             OUT p_device_code character varying, OUT dilution_factor real, OUT test bigint,
                                             OUT material character varying, OUT test_code character varying, OUT cartnum bigint,
                                             OUT fam character varying, OUT sex character varying, OUT birthday date,
                                             OUT scheduled_profile bigint, OUT p_scheduled_invest bigint, OUT is_aliquot boolean,
                                             OUT p_route bigint, OUT p_scheduled_container bigint, OUT p_manual_aliquot boolean)
RETURNS SETOF RECORD
    LANGUAGE plpgsql
AS
$$
DECLARE
    v_barcode VARCHAR;
    v_record2 RECORD;
    v_record  RECORD;
    v_has_results BOOLEAN;
    v_container_type BIGINT;
BEGIN
    v_has_results = FALSE;
    SELECT cq.citm_query_id, sc.barcode, sc.container
    INTO task_id, v_barcode, v_container_type
    FROM lis.citm_query cq
             LEFT JOIN lis.scheduled_containers sc on sc.scheduled_container = cq.scheduled_container
    WHERE processed IS NULL and error_msg IS NULL
    ORDER BY citm_query_id
    LIMIT 1;

    IF FOUND AND v_barcode IS NOT NULL THEN
        BEGIN
          FOR v_record IN SELECT DISTINCT device_code, barcode, route, sample_container, scheduled_container, scheduled_invest FROM LIS.listContainerRoutes2(v_barcode)
              LOOP
                  FOR v_record2 IN
                      SELECT it.sample_id,
                             it.device_instance,
                             it.dilution_factor,
                             it.test,
                             it.material,
                             it.test_code,
                             it.cartnum,
                             it.fam,
                             it.sex,
                             it.birthday,
                             it.scheduled_profile,
                             it.scheduled_invest,
                             t.test_code as citm_test_code,
                             p.resid as resid
                      FROM lis.inquiry_tests_citm(v_record.device_code, v_record.barcode) it
                               JOIN lis.rt_tests t on t.test = it.test
                               JOIN lis.scheduled_profiles p on p.scheduled_profile = it.scheduled_profile
                      WHERE it.scheduled_invest = v_record.scheduled_invest
                      LOOP
                          sample_id = v_record2.sample_id;
                          device_instance = v_record2.device_instance;
                          p_device_code = v_record.device_code;
                          dilution_factor = v_record2.dilution_factor;
                          test = v_record2.test;
                          material = v_record2.material;
                          test_code = v_record2.citm_test_code;
                          cartnum = v_record2.cartnum;
                          fam = v_record2.fam;
                          sex = v_record2.sex;
                          birthday = v_record2.birthday;
                          scheduled_profile = v_record2.scheduled_profile;
                          p_scheduled_invest = v_record2.scheduled_invest;
                          is_aliquot = v_record.sample_container != 'primary_container';
                          p_route = v_record.route;
                          p_scheduled_container= v_record.scheduled_container;

                          IF (v_record2.resid = '2Ж6044') THEN
                              p_manual_aliquot = TRUE;
                          END IF;

                          v_has_results = TRUE;
                          RETURN NEXT;
                      END LOOP;
              END LOOP;
        EXCEPTION
            WHEN raise_exception THEN
                UPDATE lis.citm_query SET error_msg = SQLERRM WHERE citm_query_id = task_id;
                RETURN;
        END;
    END IF;

    IF v_has_results = FALSE THEN
        UPDATE lis.citm_query
        SET error_msg = 'Тесты не найдены'
        WHERE citm_query_id = task_id;
    END IF;
    RETURN;
END
$$;

alter function lis.getTasks4CITM() owner to gis;




CREATE OR REPLACE FUNCTION lis.add_raw_result_citm(
    p_device_code character varying, p_instance_module character varying,
    p_sample_id character varying, p_test_type character varying,
    p_test_id bigint, p_sample_type character varying,
    p_priority character varying, p_result character varying,
    p_dilution_factor character varying, p_normal_range_flag character varying,
    p_container_type character varying, p_unit character varying,
    p_result_status character varying, p_reagent_serial character varying,
    p_reagent_lot character varying, p_sequence_number character varying,
    p_carrier character varying, p_position character varying,
    p_test_started timestamp without time zone,
    p_test_completed timestamp without time zone, p_device_comment character varying,
    OUT o_raw_result bigint) returns bigint
    SECURITY DEFINER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    v_log_msg               VARCHAR;

    v_device                BIGINT;
    v_device_instance       BIGINT;
    v_scheduled_test        BIGINT;

    v_technique             BIGINT;
    v_technique_name        VARCHAR;

    v_material              BIGINT;
    v_scheduled_sample      BIGINT;
    v_scheduled_container   BIGINT;
    v_AtomId                BIGINT;
    v_raw_result_status     VARCHAR(1);
    v_operator              VARCHAR(10);
    v_sample_id             VARCHAR(100);

    v_err                   VARCHAR;
    v_record                RECORD;
    v_count                 BIGINT;
    v_instance_module       VARCHAR(32);
    v_sequence_number       VARCHAR(10);
    v_test_code             VARCHAR(35);

BEGIN
    v_raw_result_status = 'S';
    v_scheduled_test  = NULL;
    v_technique    = NULL;
    v_test_code    = NULL;

    v_instance_module = p_instance_module;

    IF p_sequence_number IS NOT NULL THEN
        v_sequence_number = SUBSTRING(p_sequence_number, 1, 10);
    END IF;

    IF p_sample_id IS NULL THEN
        v_sample_id = 'UNKNOWN';
    ELSE
        v_sample_id = p_sample_id;
    END IF;

    IF p_result_status = 'F' THEN
        v_raw_result_status = 'S';
    ELSEIF p_result_status = 'X' THEN
        v_raw_result_status = 'F';
    ELSE
        v_raw_result_status = 'S';
    END IF;

    -- findDeviceInstance
    BEGIN
        SELECT * INTO v_device_instance, v_device
        FROM LIS.findDeviceInstance( p_device_code);
    EXCEPTION
        WHEN raise_exception THEN
            v_err = ERROR.EXTRACT_ERROR_ALIAS(SQLERRM);
            IF NOT v_err = 'invalid_argument' THEN
                RAISE EXCEPTION '%',SQLERRM;
            END IF;
    END;

    IF v_device_instance IS NOT NULL AND v_instance_module IS NOT NULL THEN
        --  проверить если такой модуль в справочниках
        PERFORM * FROM LIS.rt_instancemodules
        WHERE device_instance = v_device_instance AND instance_module = TRIM(v_instance_module);

        IF NOT FOUND THEN
            v_instance_module = NULL;
        END IF;
    END IF;

    IF v_device IS NOT NULL THEN

        BEGIN
            SELECT * INTO v_operator
            FROM LIS.findInstanceOperator(v_device_instance);
        EXCEPTION
            WHEN raise_exception THEN
                v_err = ERROR.EXTRACT_ERROR_ALIAS(SQLERRM);
                IF NOT v_err = 'invalid_argument' THEN
                    RAISE EXCEPTION '%',SQLERRM;
                END IF;
        END;

        v_count = 0;

        <<LOOP1>>
        FOR v_record IN
            SELECT technique, material, test_code
            FROM LIS.rt_technique_testcodes a
            WHERE device = v_device AND test = p_test_id
            LOOP
                v_technique := v_record.technique;
                v_material := v_record.material;
                v_test_code := v_record.test_code;

                v_count = v_count + 1;

                BEGIN
                    SELECT * INTO v_scheduled_sample
                    FROM LIS.findScheduledSample(v_sample_id, v_material);

                    IF v_scheduled_sample IS NOT NULL THEN
                        BEGIN

                            SELECT * INTO v_scheduled_test
                            FROM LIS.findScheduledTest(v_scheduled_sample, v_technique);

                            EXIT LOOP1;

                        EXCEPTION
                            WHEN raise_exception THEN
                                v_err = ERROR.EXTRACT_ERROR_ALIAS(SQLERRM);
                                IF NOT v_err = 'scheduled_test_notfound' THEN
                                    RAISE EXCEPTION '%',SQLERRM;
                                END IF;
                        END;

                    END IF;

                EXCEPTION
                    WHEN raise_exception THEN
                        v_err = ERROR.EXTRACT_ERROR_ALIAS(SQLERRM);
                        IF NOT v_err = 'barcode_notfound' THEN
                            RAISE EXCEPTION '%',SQLERRM;
                        END IF;
                END;

                IF v_count > 1 THEN
                    v_technique = NULL;
                END IF;

            END LOOP;

    END IF;

    --  insert

    INSERT INTO LIS.raw_results
    ( raw_result_state
    , raw_result_status
    , result_source

    , device_instance
    , instance_module

    , device_code

    , sample_id

    , test_type
    , test_code
    , sample_type
    , priority
    , result
    , normal_range_flag
    , unit
    , result_status
    , sequence_number
    , carrier
    , position
    , test_started
    , test_completed
    , device_comment
    , dilution_factor
    , container_type
    , reagent_serial
    , reagent_lot

    , scheduled_test
    , technique

    , operator

    )
    values
    ( 'unplanned'
    , v_raw_result_status
    , 'analyser'

    , v_device_instance
    , v_instance_module

    , p_device_code

    , v_sample_id

    , p_test_type
    , v_test_code
    , p_sample_type
    , p_priority
    , p_result
    , p_normal_range_flag
    , p_unit
    , p_result_status
    , v_sequence_number
    , p_carrier
    , p_position
    , p_test_started
    , p_test_completed
    , p_device_comment
    , p_dilution_factor
    , p_container_type
    , p_reagent_serial
    , p_reagent_lot

    , v_scheduled_test
    , v_technique

    , v_operator
    );

    o_raw_result = CURRVAL('LIS.raw_results_SEQ');

    SELECT * INTO v_scheduled_container
    FROM LIS.findScheduledContainer(v_sample_id, v_material);

    IF v_technique IS NOT NULL THEN
        select technique_name into v_technique_name
        from lis.all_techniques
        where technique = v_technique;
    END IF;

    IF v_scheduled_test IS NOT NULL THEN
        PERFORM * FROM LIS.completeScheduledTest(v_scheduled_test, o_raw_result);

        IF v_scheduled_container IS NOT NULL THEN
            v_log_msg = 'get result from device '                            || E'\n' ||
                        '  device_code:'   || p_device_code                  || E'\n' ||
                        '  sample_id:'     || v_sample_id                    || E'\n' ||
                        '  technique:'     || COALESCE(v_technique_name, '') || E'\n' ||
                        '  result:'        || COALESCE(p_result, '')         || E'\n' ||
                        '  result_status:' || COALESCE(p_result_status, '')  || E'\n';

            PERFORM LOGS.LogEvent('LIS.LABGATE.ADDRESULT',
                                  'info',
                                  NULL,
                                  v_log_msg,
                                  'LIS.CONTAINER',
                                  v_scheduled_container);
        END IF;
    ELSE
        UPDATE LIS.raw_results
        SET raw_result_state = 'unsupported'
        WHERE raw_result = o_raw_result;

        IF v_scheduled_test IS NOT NULL THEN
            v_log_msg = 'get result from device '                            || E'\n' ||
                        '  device_code:'   || p_device_code                  || E'\n' ||
                        '  sample_id:'     || v_sample_id                    || E'\n' ||
                        '  technique:'     || COALESCE(v_technique_name, '') || E'\n' ||
                        '  result:'        || COALESCE(p_result, '')         || E'\n' ||
                        '  result_status:' || COALESCE(p_result_status, '')  || E'\n';

            PERFORM LOGS.LogEvent('LIS.LABGATE.ADDRESULT',
                                  'info',
                                  NULL,
                                  v_log_msg,
                                  'LIS.CONTAINER',
                                  v_scheduled_container);
        END IF;
    END IF;

END;
$$;

ALTER FUNCTION lis.add_raw_result_citm(varchar, varchar, varchar, varchar, bigint, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, timestamp, timestamp, varchar, out bigint) OWNER TO gis;




select * from lis.getTasks4CITM() as result;



SELECT DISTINCT di.code, sc.barcode FROM lis.scheduled_invests si
 JOIN lis.scheduled_samples ss ON ss.scheduled_sample = si.scheduled_sample
 JOIN lis.scheduled_containers sc ON sc.scheduled_sample = ss.scheduled_sample
 JOIN lis.rt_technique_positions tp ON tp.investigation = si.investigation AND tp.technique = si.technique
 JOIN lis.rt_deviceinstances di ON di.device_instance = tp.device_instance
 JOIN lis.citm c on c.device_instance = tp.device_instance
WHERE sc.scheduled_container = p_scheduled_container;




--LIS.registerSecondaryContainer()

-- карта 88362
select * from LIS.listContainerRoutes('002005012');
select * from LIS.listContainerRoutes('002005013');
-- Если поле sample_container = 'secondary_container' - надо сделать аликвоту

SELECT * FROM LIS.inquiry_routes('', '02005012');


SELECT * FROM LIS.inquiry_tests('Evolis1', '02005012');
SELECT * FROM LIS.inquiry_tests('Cobas8000.1', '02005012');


select * from LIS.listContainerRoutes2('02005012'); -- с аликвотой
select * from LIS.listContainerRoutes2('02005013');


select * from LIS.listContainerRoutes2('16123566');
select * from LIS.listContainerRoutes2('16123567');
select * from LIS.listContainerRoutes2('16123568');

select * from LIS.listContainerRoutes2('17721771');
SELECT * FROM LIS.inquiry_tests('COBAS_CCEE', '17721771');

select * from LIS.listContainerRoutes2('575757');

select * from lis.getTasks4CITM();





select t.*, tp.test_name, tp.test_desc from lis.getTasks4CITM() t
JOIN lis.rt_test_properties tp on tp.test = t.test;

CREATE OR REPLACE FUNCTION lis.listcontainerroutes2(
  p_barcode               character varying,
  OUT priority            bigint,
  OUT route               bigint,
  OUT scheduled_invest    bigint,
  OUT scheduled_container numeric,
  OUT inwork              character varying,
  OUT route_name          character varying,
  OUT invest_state        character varying,
  OUT technique_name      character varying,
  OUT device              bigint,
  OUT device_instance     bigint,
  OUT device_code         varchar,
  OUT sample_container    character varying,
  OUT container_state     character varying,
  OUT barcode             character varying,
  OUT cartnum             bigint,
  OUT labnum              character varying,
  OUT work_material       bigint,
  OUT registration_time   timestamp WITHOUT TIME ZONE,
  OUT sample_type         character varying,
  OUT stress              character varying,
  OUT second_container    bigint,
  OUT second_labnum       character varying,
  OUT second_barcode      character varying,
  OUT registrator         character varying,
  OUT profile_state       character varying,
  OUT cito                character varying,
  OUT tuberackid          bigint,
  OUT tuberackbarcode     character varying,
  OUT tuberackstate       character varying,
  OUT tuberackposition    character varying,
  OUT storageid           bigint,
  OUT storagename         character varying,
  OUT storagebarcode      character varying) RETURNS setof record
  SECURITY DEFINER
  LANGUAGE plpgsql
AS
$$
DECLARE
  v_sqlcmd              varchar(1000);
  v_scheduled_sample    bigint;
  v_scheduled_container bigint;
  v_device_instance     bigint;
  v_route               bigint;
  v_route_length        bigint;
  v_iroute              bigint;
  v_flag                boolean;
  v_container_type      varchar(10);
  v_task_id             bigint;
  v_record              record;
  v_role                varchar;
BEGIN

  SELECT approle
  INTO v_role
  FROM utils3.getuserroles2()
  WHERE approle = 'role-laboratory';

  /*ищем barcode, если не найден то ищем лабораторный номер,
    если не найдем ошибка*/
  SELECT a.scheduled_container, a.container_type, b.scheduled_sample
  INTO v_scheduled_container, v_container_type, v_scheduled_sample
  FROM lis.scheduled_containers a
         JOIN lis.scheduled_samples b ON a.scheduled_sample = b.scheduled_sample
  WHERE a.barcode = p_barcode;

  IF NOT found
  THEN
    SELECT a.scheduled_container, a.container_type, b.scheduled_sample
    INTO v_scheduled_container, v_container_type, v_scheduled_sample
    FROM lis.scheduled_containers a
           JOIN lis.scheduled_samples b
    ON a.scheduled_sample = b.scheduled_sample
    WHERE a.labnum = p_barcode AND a.scheduled_date <= now()::date AND
        a.scheduled_date >= now()::date - '30 days'::interval;

    IF NOT found
    THEN
      PERFORM error.raise_error('barcode_notfound', p_barcode::varchar);
    ELSE
      IF v_role IS NULL
      THEN
        PERFORM logs.logevent('LIS.SAMPLES.SAMPLESORTING.GETCONTAINER', 'info',
                              NULL, 'Get container labnum - ' || p_barcode,
                              'LIS.CONTAINER', v_scheduled_container);
      ELSE
        PERFORM logs.logevent('LIS.SAMPLES.SAMPLESORTING.VIEWCONTAINER', 'info',
                              NULL, 'Get container labnum - ' || p_barcode,
                              'LIS.CONTAINER', v_scheduled_container);
      END IF;
    END IF;
  ELSE
    IF v_role IS NULL
    THEN
      PERFORM logs.logevent('LIS.SAMPLES.SAMPLESORTING.GETCONTAINER', 'info',
                            NULL, 'Get container barcode - ' || p_barcode,
                            'LIS.CONTAINER', v_scheduled_container);
    ELSE
      PERFORM logs.logevent('LIS.SAMPLES.SAMPLESORTING.VIEWCONTAINER', 'info',
                            NULL, 'Get container barcode - ' || p_barcode,
                            'LIS.CONTAINER', v_scheduled_container);
    END IF;
  END IF;

  /*
    если есть невыполненная задача по поиску, то отмечаем ее
  */
  SELECT ct.task_id
  INTO v_task_id
  FROM lis.container_tasks ct
  WHERE ct.scheduled_container = v_scheduled_container AND
    ct.task_status IN ('scheduled', 'inprogress');
  IF found
  THEN
    IF v_role IS NULL
    THEN
      /* добавить проверку что это сортировка */
      PERFORM lis.marktask(v_task_id, 'completed');
    END IF;
  END IF;

  DROP TABLE IF EXISTS temp_defvice_instances;
  CREATE TEMP TABLE temp_defvice_instances AS
  SELECT DISTINCT i.device_instance
  FROM lis.scheduled_invests i
  WHERE i.scheduled_sample = v_scheduled_sample AND
      i.technique NOT IN (SELECT ir.technique
                          FROM lis.sr_instancesofroute ir
                          WHERE ir.technique IS NOT NULL);

  DROP TABLE IF EXISTS temp_routes;
  EXECUTE '
    CREATE TEMP TABLE temp_routes
    (
      device_instance   BIGINT,
      route             BIGINT,
      technique         BIGINT,
      route_length      BIGINT,
      active            BIGINT
    )';

  IF v_container_type = 'first'
  THEN
    EXECUTE '
      INSERT INTO temp_routes(device_instance, route, route_length, active)
        SELECT a.device_instance, b.route , c.route_length, e.active
        FROM temp_defvice_instances a
          JOIN LIS.sr_instancesofroute b ON a.device_instance = b.device_instance
          JOIN LIS.sr_routes e ON b.route = e.route
          JOIN (SELECT b.route, count(*) AS route_length
                FROM temp_defvice_instances a
                  JOIN LIS.sr_instancesofroute b ON a.device_instance = b.device_instance
                GROUP BY b.route
               ) d ON b.route = d.route
          JOIN (SELECT b.route, count(*) AS route_length
                FROM LIS.sr_instancesofroute b
                GROUP BY b.route
               ) c ON b.route = c.route
                    and c.route_length = (case when e.strict = 1 then d.route_length else c.route_length end)
        WHERE e.active in (1, 2)';
    DROP TABLE IF EXISTS temp_defvice_instances;

    v_flag = TRUE;
    WHILE v_flag
      LOOP
        v_iroute = NULL;

        v_flag = FALSE;
        FOR v_record IN
          SELECT a.device_instance, a.route, a.route_length
          FROM temp_routes a
          ORDER BY a.route_length DESC, a.route
          LOOP
            v_flag = FALSE;

            v_device_instance = v_record.device_instance;
            v_route = v_record.route;
            v_route_length = v_record.route_length;

            IF v_iroute IS NULL
            THEN
              v_iroute = v_route;
            END IF;

            IF v_iroute <> v_route
            THEN
              v_flag = TRUE;
              EXIT;
            END IF;

            DELETE
            FROM temp_routes a
            WHERE a.device_instance = v_device_instance AND a.route <> v_route;

            IF NOT found
            THEN
              v_iroute = NULL;
            END IF;
          END LOOP;
      END LOOP;

    /*маршруты основаные на методиках*/
    EXECUTE '
      INSERT INTO temp_routes (technique, route)
        SELECT b.technique, b.route
        FROM LIS.scheduled_invests a
          JOIN LIS.sr_instancesofroute b ON a.technique = b.technique
        WHERE a.scheduled_sample =  ' || v_scheduled_sample;

  ELSEIF v_container_type = 'second'
  THEN

    EXECUTE '
      INSERT INTO temp_routes (route, device_instance, technique)
        SELECT a.route, a.device_instance, a.technique
        FROM LIS.sr_instancesofroute a
          JOIN LIS.sr_containersonroute b ON b.route = a.route
        WHERE b.scheduled_container = ' || v_scheduled_container || '
        UNION
        SELECT ir.route, ir.device_instance, ir.technique
        FROM LIS.scheduled_invests si
          join LIS.sr_instancesofroute ir on ir.device_instance = si.device_instance or ir.technique = si.technique
          join LIS.sr_routes r on r.route = ir.route and r.type = ''aliquot''
        WHERE si.aliquot_container = ' || v_scheduled_container;

  END IF;

  DROP TABLE IF EXISTS temp_invests;

  EXECUTE '
    CREATE TEMP TABLE temp_invests AS
      SELECT scheduled_invest, invest_state, ' || v_scheduled_sample || '::bigint as scheduled_sample,
             technique, device_instance, scheduled_profile, aliquot_container
      FROM LIS.scheduled_invests
      WHERE scheduled_sample = ' || v_scheduled_sample;

  --ANALYZE temp_routes;
  --ANALYZE temp_invests;
  FOR v_record IN
    SELECT c.priority, c.route,
      a.scheduled_invest, v_scheduled_container AS scheduled_container_,
      (CASE
        WHEN v_scheduled_container = e.scheduled_container AND c.route = e.route
          THEN 'containerinwork'
        WHEN v_scheduled_container <> e.scheduled_container AND
             c.route = e.route
          THEN 'sampleinwork'
        ELSE NULL
        END) AS inwork_,
      c.name AS route_name_,
      (CASE WHEN a.invest_state = 'completed'
              THEN a.invest_state
            ELSE (CASE WHEN b.active = 2
                         THEN 'busy'
                       ELSE a.invest_state END)
        END) AS invest_state_,
      d.technique_name,
      w.device,
      a.device_instance,
      v.code as device_code,
      (CASE WHEN d.sample_container IS NULL
              THEN w.sample_container
            ELSE d.sample_container END) AS sample_container_,
      dd.container_state, dd.barcode,
      ww.cartnum, ww.labnum,
      vv.work_material,
      CASE
        WHEN ww.scheduled_date > vv.registration_time::date
          THEN ww.scheduled_date
        ELSE vv.registration_time
        END AS registration_time,
      (CASE WHEN ii.cabid = 'привоз'
              THEN 'external'
            ELSE 'internal' END) AS sample_type_,
      lis.stresstostring(CASE WHEN gg.sample IS NULL
                                THEN vv.stress
                              ELSE gg.stress END) AS stress_,
      ff.scheduled_container AS second_container_, ff.labnum AS second_labnum_,
      ff.barcode AS second_barcode_,
      doc.name AS registrator_, aa.profile_state, ii.cito,
      tuberacks.id AS tuberackid_, tuberacks.barcode AS tuberack_,
      tuberacks.state AS tuberackstate_,
      lis.getposition(tc.position, tuberacks.width) AS tuberackposition_,
      storages.id AS storageid_, storages.name AS storagename_,
      storages.barcode AS storagebarcode_
    FROM lis.scheduled_containers dd
           JOIN lis.scheduled_samples vv
    ON dd.scheduled_sample = vv.scheduled_sample
           JOIN lis.medorders ww ON vv.lisorder = ww.lisorder
           JOIN temp_invests a ON vv.scheduled_sample = a.scheduled_sample
           JOIN lis.scheduled_profiles aa
    ON a.scheduled_profile = aa.scheduled_profile
           JOIN gis.invest ii ON aa.counter = ii.counter
           JOIN lis.rt_deviceinstances v
    ON a.device_instance = v.device_instance
           JOIN lis.rt_devices w ON v.device = w.device
           JOIN temp_routes b ON a.device_instance = b.device_instance
           JOIN lis.sr_routes c ON b.route = c.route
           JOIN lis.all_techniques d ON a.technique = d.technique
           LEFT JOIN gis.doctors doc ON dd.registrar = doc.id
           LEFT JOIN lis.sr_containersonroute e
    ON a.scheduled_sample = e.scheduled_sample AND c.route = e.route
           LEFT JOIN lis.rt_profile_samples gg ON vv.sample = gg.sample
           LEFT JOIN lis.scheduled_containers ff
    ON a.aliquot_container = ff.scheduled_container
           LEFT JOIN lis.tuberack_containers tc
    ON dd.scheduled_container = tc.scheduled_container
           LEFT JOIN lis.rt_tuberacks tuberacks ON tc.tuberackid = tuberacks.id
           LEFT JOIN lis.storage_tuberacks st ON tc.tuberackid = st.tuberackid
           LEFT JOIN lis.rt_storages storages ON st.storageid = storages.id
    WHERE dd.scheduled_container = v_scheduled_container AND
      vv.scheduled_sample = v_scheduled_sample AND
      aa.profile_state NOT IN ('failure') AND
        a.technique NOT IN (SELECT technique
                            FROM lis.sr_instancesofroute
                            WHERE technique IS NOT NULL)
    UNION ALL
    SELECT c.priority, c.route,
      a.scheduled_invest, v_scheduled_container AS scheduled_container_,
      (CASE
        WHEN v_scheduled_container = e.scheduled_container AND c.route = e.route
          THEN 'containerinwork'
        WHEN v_scheduled_container <> e.scheduled_container AND
             c.route = e.route
          THEN 'sampleinwork'
        ELSE NULL
        END) AS inwork_,
      c.name AS route_name_,
      (CASE
        WHEN a.invest_state = 'completed'
          THEN a.invest_state
        ELSE (CASE WHEN b.active = 2
                     THEN 'busy'
                   ELSE a.invest_state END)
        END) AS invest_state_,
      d.technique_name,
      w.device,
      a.device_instance,
      v.code as device_code,
      (CASE WHEN d.sample_container IS NULL
              THEN w.sample_container
            ELSE d.sample_container END) AS sample_container_,
      dd.container_state, dd.barcode,
      ww.cartnum, ww.labnum,
      vv.work_material, vv.registration_time,
      (CASE WHEN ii.cabid = 'привоз'
              THEN 'external'
            ELSE 'internal' END) AS sample_type_,
      lis.stresstostring(CASE WHEN gg.sample IS NULL
                                THEN vv.stress
                              ELSE gg.stress END) AS stress_,
      ff.scheduled_container AS second_container_, ff.labnum AS second_labnum_,
      ff.barcode AS second_barcode_,
      doc.name AS registrator_, aa.profile_state, ii.cito,
      tuberacks.id AS tuberackid_, tuberacks.barcode AS tuberack_,
      tuberacks.state AS tuberackstate_,
      lis.getposition(tc.position, tuberacks.width) AS tuberackposition_,
      storages.id AS storageid_, storages.name AS storagename_,
      storages.barcode AS storagebarcode_
    FROM lis.scheduled_containers dd
           JOIN lis.scheduled_samples vv
    ON dd.scheduled_sample = vv.scheduled_sample
           JOIN lis.medorders ww ON vv.lisorder = ww.lisorder
           JOIN temp_invests a ON vv.scheduled_sample = a.scheduled_sample
           JOIN lis.scheduled_profiles aa
    ON a.scheduled_profile = aa.scheduled_profile
           JOIN gis.invest ii ON aa.counter = ii.counter
           JOIN lis.rt_deviceinstances v
    ON a.device_instance = v.device_instance
           JOIN lis.rt_devices w ON v.device = w.device
           JOIN temp_routes b ON a.technique = b.technique
           JOIN lis.sr_routes c ON b.route = c.route
           JOIN lis.all_techniques d ON a.technique = d.technique
           LEFT JOIN gis.doctors doc ON dd.registrar = doc.id
           LEFT JOIN lis.sr_containersonroute e
    ON a.scheduled_sample = e.scheduled_sample AND c.route = e.route
           LEFT JOIN lis.rt_profile_samples gg ON vv.sample = gg.sample
           LEFT JOIN lis.scheduled_containers ff
    ON a.aliquot_container = ff.scheduled_container
           LEFT JOIN lis.tuberack_containers tc
    ON dd.scheduled_container = tc.scheduled_container
           LEFT JOIN lis.rt_tuberacks tuberacks ON tc.tuberackid = tuberacks.id
           LEFT JOIN lis.storage_tuberacks st ON tc.tuberackid = st.tuberackid
           LEFT JOIN lis.rt_storages storages ON st.storageid = storages.id
    WHERE dd.scheduled_container = v_scheduled_container AND
      vv.scheduled_sample = v_scheduled_sample AND
      aa.profile_state NOT IN ('failure')
    ORDER BY 10, 1 DESC, 2, 3
    LOOP
      priority := v_record.priority;
      route := v_record.route;
      scheduled_invest := v_record.scheduled_invest;
      scheduled_container := v_record.scheduled_container_;
      inwork := v_record.inwork_;
      route_name := v_record.route_name_;
      invest_state := v_record.invest_state_;
      technique_name := v_record.technique_name;
      device := v_record.device;
      device_instance := v_record.device_instance;
      device_code := v_record.device_code;
      sample_container := v_record.sample_container_;
      container_state := v_record.container_state;
      barcode := v_record.barcode;
      cartnum := v_record.cartnum;
      labnum := v_record.labnum;
      work_material := v_record.work_material;
      registration_time := v_record.registration_time;
      sample_type := v_record.sample_type_;
      stress := v_record.stress_;
      second_container := v_record.second_container_;
      second_labnum := v_record.second_labnum_;
      second_barcode := v_record.second_barcode_;
      registrator := v_record.registrator_;
      profile_state := v_record.profile_state;
      cito := v_record.cito;
      tuberackid := v_record.tuberackid_;
      tuberackbarcode := v_record.tuberack_;
      tuberackstate := v_record.tuberackstate_;
      tuberackposition := v_record.tuberackposition_;
      storageid := v_record.storageid_;
      storagename := v_record.storagename_;
      storagebarcode := v_record.storagebarcode_;
      RETURN NEXT;
    END LOOP;

  DROP TABLE IF EXISTS temp_defvice_instances;
  DROP TABLE IF EXISTS temp_invests;
  RETURN;
END;
$$;

ALTER FUNCTION lis.listcontainerroutes2(varchar, OUT bigint, OUT bigint, OUT bigint, OUT numeric, OUT varchar, OUT varchar, OUT varchar, OUT varchar, OUT bigint, OUT varchar, OUT varchar, OUT varchar, OUT bigint, OUT varchar, OUT bigint, OUT timestamp, OUT varchar, OUT varchar, OUT bigint, OUT varchar, OUT varchar, OUT varchar, OUT varchar, OUT varchar, OUT bigint, OUT varchar, OUT varchar, OUT varchar, OUT bigint, OUT varchar, OUT varchar) OWNER TO krypton;





INSERT INTO lis.raw_results (raw_result, raw_result_state, raw_result_status, result_source, test_type, sample_id, test_code, sample_type, priority, result, result_status, normal_range_flag, unit, sequence_number, carrier, position, test_started, test_completed, dilution_factor, container_type, reagent_serial, reagent_lot, device_error, device_comment, histogram, operator, device_code, device_instance, instance_module, scheduled_test, technique, started_time, completed_time, cause_of_repeat, inner_comment, result_type, value_type, outofrange, er_result, er_unit, comment, reference_range, reference_flag) VALUES (59328754, 'unsupported', 'S', 'analyser', 'SAMPLE', '5654', '74', 'SAMPLE', 'R', '140.800', 'F', 'N', 'mmol/l', null, null, null, null, '2020-05-16 00:00:00.000000', '1.0', null, null, null, null, null, 0, null, 'COBAS_CCEE', 89, null, null, null, null, '2020-05-16 14:50:59.909115', null, null, null, null, null, null, null, null, null, null);
INSERT INTO lis.raw_results (raw_result, raw_result_state, raw_result_status, result_source, test_type, sample_id, test_code, sample_type, priority, result, result_status, normal_range_flag, unit, sequence_number, carrier, position, test_started, test_completed, dilution_factor, container_type, reagent_serial, reagent_lot, device_error, device_comment, histogram, operator, device_code, device_instance, instance_module, scheduled_test, technique, started_time, completed_time, cause_of_repeat, inner_comment, result_type, value_type, outofrange, er_result, er_unit, comment, reference_range, reference_flag) VALUES (59328753, 'unsupported', 'S', 'analyser', 'SAMPLE', '5654', '108', 'SAMPLE', 'R', '4.620', 'F', 'N', 'mmol/l', null, null, null, null, '2020-05-16 00:00:00.000000', '1.0', null, null, null, null, null, 0, null, 'COBAS_CCEE', 89, null, null, null, null, '2020-05-16 14:50:59.906155', null, null, null, null, null, null, null, null, null, null);
INSERT INTO lis.raw_results (raw_result, raw_result_state, raw_result_status, result_source, test_type, sample_id, test_code, sample_type, priority, result, result_status, normal_range_flag, unit, sequence_number, carrier, position, test_started, test_completed, dilution_factor, container_type, reagent_serial, reagent_lot, device_error, device_comment, histogram, operator, device_code, device_instance, instance_module, scheduled_test, technique, started_time, completed_time, cause_of_repeat, inner_comment, result_type, value_type, outofrange, er_result, er_unit, comment, reference_range, reference_flag) VALUES (59328752, 'unsupported', 'S', 'analyser', 'SAMPLE', '5654', '40', 'SAMPLE', 'R', '100.200', 'F', 'N', 'mmol/l', null, null, null, null, '2020-05-16 00:00:00.000000', '1.0', null, null, null, null, null, 0, null, 'COBAS_CCEE', 89, null, null, null, null, '2020-05-16 14:50:59.901709', null, null, null, null, null, null, null, null, null, null);
INSERT INTO lis.raw_results (raw_result, raw_result_state, raw_result_status, result_source, test_type, sample_id, test_code, sample_type, priority, result, result_status, normal_range_flag, unit, sequence_number, carrier, position, test_started, test_completed, dilution_factor, container_type, reagent_serial, reagent_lot, device_error, device_comment, histogram, operator, device_code, device_instance, instance_module, scheduled_test, technique, started_time, completed_time, cause_of_repeat, inner_comment, result_type, value_type, outofrange, er_result, er_unit, comment, reference_range, reference_flag) VALUES (59328751, 'unsupported', 'S', 'analyser', 'SAMPLE', '5653', '44', 'SAMPLE', 'R', '49.000', 'C', 'N', 'umol/l', null, null, null, null, '2020-05-16 00:00:00.000000', '1.0', null, null, null, null, null, 0, null, 'COBAS_CCEE', 89, null, null, null, null, '2020-05-16 14:50:48.754982', null, null, null, null, null, null, null, null, null, null);
INSERT INTO lis.raw_results (raw_result, raw_result_state, raw_result_status, result_source, test_type, sample_id, test_code, sample_type, priority, result, result_status, normal_range_flag, unit, sequence_number, carrier, position, test_started, test_completed, dilution_factor, container_type, reagent_serial, reagent_lot, device_error, device_comment, histogram, operator, device_code, device_instance, instance_module, scheduled_test, technique, started_time, completed_time, cause_of_repeat, inner_comment, result_type, value_type, outofrange, er_result, er_unit, comment, reference_range, reference_flag) VALUES (59328750, 'unsupported', 'S', 'analyser', 'SAMPLE', '5651', '44', 'SAMPLE', 'R', '49.000', 'C', 'N', 'umol/l', null, null, null, null, '2020-05-16 00:00:00.000000', '1.0', null, null, null, null, null, 0, null, 'COBAS_CCEE', 89, null, null, null, null, '2020-05-16 14:50:40.152351', null, null, null, null, null, null, null, null, null, null);
INSERT INTO lis.raw_results (raw_result, raw_result_state, raw_result_status, result_source, test_type, sample_id, test_code, sample_type, priority, result, result_status, normal_range_flag, unit, sequence_number, carrier, position, test_started, test_completed, dilution_factor, container_type, reagent_serial, reagent_lot, device_error, device_comment, histogram, operator, device_code, device_instance, instance_module, scheduled_test, technique, started_time, completed_time, cause_of_repeat, inner_comment, result_type, value_type, outofrange, er_result, er_unit, comment, reference_range, reference_flag) VALUES (59328749, 'completed', 'S', 'analyser', 'SAMPLE', '5649', 'CRE', 'SAMPLE', 'R', '49.000', 'F', 'N', 'umol/l', null, null, null, null, '2020-05-16 00:00:00.000000', '1.0', null, null, null, null, null, 0, null, 'COBAS_CCEE', 89, null, null, null, null, '2020-05-16 14:49:46.667494', null, null, null, null, null, null, null, null, null, null);
