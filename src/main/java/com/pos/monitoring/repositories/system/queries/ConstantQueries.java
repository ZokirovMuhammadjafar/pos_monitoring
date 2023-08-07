package com.pos.monitoring.repositories.system.queries;

public interface ConstantQueries {
    String GET_ALL_CHANGE_MACHINES = """
            select t.*,
                   (case
                        when checker(inst_id := t.inst_id, sr_number := t.sr_number) is false
                            then checker(inst_id := t.inst_id, sr_number := upper(t.sr_number))
                        else true end) as is_contract
            from (select m.sr_number                                                                      as sr_number,
                         m.terminal_id                                                                    as terminal_id,
                         m.merchant_id                                                                    as merchant_id,
                         m.soft                                                                           as soft,
                         coalesce(m.mfo2,b.code)                                                          as branch_mfo,
                         b.id                                                                             as branch_id,
                         m.status7003                                                                     as status,
                         m.merchant_name                                                                  as merchant_name,
                         m.mcc                                                                            as mcc,
                         ml.name                                                                          as model,
                         (case when length(b.bank_code) = 4 then '0' || b.bank_code else b.bank_code end) as inst_id
                  from (select *
                        from machine l
                        where l.state <> 2
                          and l.type_id = 6
                          and (l.updated_date > current_date - interval '1 day' + interval '18 hour' or l.synced)
                          and l.model_id in (-2, -1, 840)
                        order by l.machine_id
                        offset ? limit 100) as m
                           inner join branch b on m.branch_id = b.id
                           inner join model ml on ml.id = m.model_id
                      and bank_code not in ('9006', '9004', '9002')) as t;
                                                           """;

    String GET_TABLE_BY_MFOS = """
            with soft_data as (select mfo                                                  as mfo,
                                      max(bs.id)                                           as id,
                                      coalesce(soft, 'NULL')                               as soft,
                                      count(*)                                             as count,
                                      sum(transaction_count)                               as transactionCount,
                                      pg_catalog.floor(sum(transaction_debit) / 100000000) as transactionDebit,
                                      max(bs.name) || ' ' || bs.mfo                        as name
                               from machines ms
                                        left join branches bs on ms.branch_id = bs.id
                               where ms.deleted = false

                                 and bs.deleted = false
                                 and mfo in (?1)
                               group by mfo, soft),
                 model_data as (select mfo                     as mfo,
                                       coalesce(model, 'NULL') as model,
                                       count(*)                as count
                                from machines ms
                                         left join branches bs on ms.branch_id = bs.id
                                where ms.deleted = false
                                  and bs.deleted = false
                                  and mfo in (?1)
                                group by mfo, model),
                 counts as (select branch_mfo,
                                   count(*)                                                                  as countt,
                                   sum(case when machines.mcc in ('6010', '6012', '6050') then 1 else 0 end) as mcc_count
                            from machines
                            where machines.branch_mfo in (?1)
                            group by branch_mfo)
            select max(sd.name)                                                                         as name_and_mfo,
                   max(sd.id)                                                                           as id,
                   max(c.countt)                                                                        as count,
                   sd.mfo                                                                               as mfo,
                   sum(sd.transactionCount)                                                             as transactionCount,
                   sum(sd.transactionDebit)                                                             as transactionDebit,
                   max(c.mcc_count)                                                                     as cash_terminals,
                   (select string_agg(soft || '/' || count, ' | ') from soft_data where mfo = sd.mfo)   as soft,
                   (select string_agg(model || '/' || count, ' | ') from model_data where mfo = sd.mfo) as model
            from soft_data sd
                     inner join model_data md on sd.mfo = md.mfo
                     inner join counts c on c.branch_mfo = sd.mfo
            group by sd.mfo;
                           """;

    String GET_ALL_CHANGE_MACHINES_WITH_BANKS_CHOSEN = """
             select t.*,
                    (case
                         when checker(inst_id := t.branch_mfo, sr_number := t.sr_number) is false
                             then checker(inst_id := t.branch_mfo, sr_number := upper(t.sr_number))
                         else true end) as is_contract
             from (select m.sr_number                                                                      as sr_number,
                          m.terminal_id                                                                    as terminal_id,
                          m.merchant_id                                                                    as merchant_id,
                          m.soft                                                                           as soft,
                          coalesce(m.mfo2,b.code)                                                          as branch_mfo,
                          b.id                                                                             as branch_id,
                          m.status7003                                                                     as status,
                          m.merchant_name                                                                  as merchant_name,
                          m.mcc                                                                            as mcc,
                          ml.name                                                                          as model,
                          (case when length(b.bank_code) = 4 then '0' || b.bank_code else b.bank_code end) as inst_id
                   from (select *
                         from machine l
                         where l.state <> 2
                           and l.type_id = 6
                           and (l.updated_date > current_date - interval '1 day' + interval '18 hour' or l.synced)
                           and l.model_id in (-2, -1, 840)
                         order by l.machine_id
                         offset ? limit 100) as m
                            inner join branch b on m.branch_id = b.id
                            inner join model ml on ml.id = m.model_id
                       and bank_code in ('9006', '9004', '9002')) as t;
            """;

    String REPORT_QUERY_POS_MONITORING = """
            with m as (select m.branch_mfo as   mfo,
                              m.model,
                              m.sr_number,
                              m.soft,
                              m.terminal_id,
                              m.merchant_id,
                              m.merchant_name,
                              (case when m.mcc in ('6050', '6010', '6012') then 1 else 0 end) kassa,
                               (case when m.is_contract then 'HA' else 'YOQ'end ) as contract,
                              (case when m.status =  'A' then 'ALIVE' else 'DEAD'end ) as status
                       from machines m),
                 t as (select t.terminal_id, t.merchant_id, sum(t.amount) as transaction_sum, sum(t.total) as transaction_count
                       from transaction_infos t
                       group by t.terminal_id, t.merchant_id)
            select m.*, t.transaction_count as transaction_count, t.transaction_sum / 100 as transaction_sum
            from m
                     left join t on t.terminal_id = m.terminal_id and t.merchant_id = m.merchant_id
            where mfo = ?1
            """;

    String REPORT_QUERY_7005 = """
            with auth as (select ac.sr_number, max(ac.creation_date) as date, count(*) as count
                          from auth_code ac
                                   inner join branch b on b.id = ac.branch_id
                          where b.code = ?
                            and ac.creation_date between current_timestamp - interval '1 Year' and current_timestamp
                          group by ac.sr_number),
                 app as (select a.sr_number, max(a2.code) as application_number, count(*) as count_application
                         from application_machine a
                                  inner join application a2 on a.application_id = a2.id
                                  inner join branch b2 on b2.id = a2.branch_id
                         where b2.code = ?
                           and a.model_id in (840, -1, -2)
                           and a2.creation_date between current_timestamp - interval '1 Year' and current_timestamp
                         group by a.sr_number)
            select coalesce(ap.sr_number, au.sr_number) as sr_number,
                   ap.application_number,
                   ap.count_application                 as application_count,
                   au.date                              as auth_date,
                   au.count                             as auth_count
            from app ap
                     full join auth au on ap.sr_number = au.sr_number;
            """;

}
