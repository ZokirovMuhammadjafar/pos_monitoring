package com.pos.monitoring.repositories.system.specifications;

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
                          (case when length(coalesce(m.mfo2, b.code)) = 4 then '0' || coalesce(m.mfo2, b.code) else coalesce(m.mfo2, b.code) end)                                                         as branch_mfo,
                         b.id                                                                             as branch_id,
                         m.status7003                                                                     as status,
                         m.merchant_name                                                                  as merchant_name,
                         m.mcc                                                                            as mcc,
                         ml.name                                                                          as model,
                         (case when m.state == 2 then true else false end)                                as deleted,
                         (case when length(b.bank_code) = 4 then '0' || b.bank_code else b.bank_code end) as inst_id
                  from (select *
                        from machine l
                        where l.state <> 2
                          and l.type_id = 6
                          and (l.updated_date > current_date - interval '1 day' + interval '18 hour' or l.synced)
                          and l.model_id in (-2, -1, 840)
                        order by l.id
                        offset ? limit 100) as m
                           inner join branch b on m.branch_id = b.id
                           inner join model ml on ml.id = m.model_id
                      and bank_code not in ('9006', '9004', '9002')) as t;
                                                                       """;
    String GET_TABLE_BY_MFOS = """
            with general_stat as (select ms.branch_mfo                                        as mfo,
                                         count(*)                                             as count,
                                         sum(transaction_count)                               as transactionCount,
                                         pg_catalog.floor(sum(transaction_debit) / 100000000) as transactionDebit,
                                         max(b.name) || ' ' || ms.branch_mfo                  as name,
                                         max(b.id)                                            as id
                                  from machines ms
                                           inner join branches b on b.id = ms.branch_id
                                  where ms.branch_mfo in (?1)
                                  group by branch_mfo),
                 kassa_stat as (select ms.branch_mfo                                        as mfo,
                                       count(*)                                             as count,
                                       sum(transaction_count)                               as transactionCount,
                                       pg_catalog.floor(sum(transaction_debit) / 100000000) as transactionDebit
                                from machines ms
                                where ms.branch_mfo in (?1)
                                  and ms.mcc in ('6010', '6012', '6050')
                                group by branch_mfo)
                        
            select g.name                                                 as name_and_mfo,
                   g.id                                                   as id,
                   g.mfo                                                  as mfo,
                   array_to_string(
                           ARRAY(
                                   SELECT coalesce(m.soft ,'NULL')|| '/' || count(m)
                                   FROM machines m
                                   WHERE m.branch_mfo = g.mfo
                                   GROUP BY m.soft
                           ),
                           ' | '
                   )                                                      as soft,
                   array_to_string(
                           ARRAY(
                                   SELECT m.model || '/' || count(m)
                                   FROM machines m
                                   WHERE m.branch_mfo = g.mfo
                                   GROUP BY m.model
                           ),
                           ' | '
                   )                                                      as model,
                   g.count                                                as count,
                   g.transactionCount                                     as transactionCount,
                   g.transactionDebit                                     as transactionDebit,
                   k.count                                                as cash_terminals,
                   k.transactionDebit                                     as c_transaction_sum,
                   k.transactionCount                                     as c_transaction_count,
                   (g.count - coalesce(k.count, 0))                       as m_count,
                   (g.transactionCount - coalesce(k.transactionCount, 0)) as m_transaction_count,
                   (g.transactionDebit - coalesce(k.transactionDebit, 0)) as m_transaction_sum
                        
            from general_stat g
                     left join kassa_stat k on k.mfo = g.mfo;
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
                         (case when length(coalesce(m.mfo2, b.code)) = 4 then '0' || coalesce(m.mfo2, b.code) else coalesce(m.mfo2, b.code) end)                                                         as branch_mfo,
                         b.id                                                                             as branch_id,
                         m.status7003                                                                     as status,
                         m.merchant_name                                                                  as merchant_name,
                         m.mcc                                                                            as mcc,
                         ml.name                                                                          as model,
                         (case when m.state == 2 then true else false end)                                as deleted,
                         (case when length(b.bank_code) = 4 then '0' || b.bank_code else b.bank_code end) as inst_id
                  from (select *
                        from machine l
                        where l.state <> 2
                          and l.type_id = 6
                          and (l.updated_date > current_date - interval '1 day' + interval '18 hour' or l.synced)
                          and l.model_id in (-2, -1, 840)
                        order by l.id
                        offset ? limit 100) as m
                           inner join branch b on m.branch_id = b.id
                           inner join model ml on ml.id = m.model_id
                      and bank_code in ('9006', '9004', '9002')) as t;
            """;
    String REPORT_QUERY_POS_MONITORING = """
            with m as (select m.branch_mfo                                            as     mfo,
                              m.model,
                              m.sr_number,
                              m.soft,
                              m.terminal_id,
                              m.merchant_id,
                              m.merchant_name,
                              (case when m.mcc in ('6050', '6010', '6012') then 'kassa' end) as  kassa,
                              (case when m.is_contract then 'BOR' else 'YOQ' end)     as     contract,
                              (case when m.status = 'A' then 'ALIVE' else 'DEAD' end) as     status
                       from machines m),
                 t as (select t.terminal_id, t.merchant_id, sum(t.amount) as transaction_sum, sum(t.total) as transaction_count
                       from transaction_infos t
                       where t.transactions_day between current_date - interval '1 month' and current_date
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
