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
                         b.code                                                                           as branch_mfo,
                         b.id                                                                             as branch_id,
                         m.status7003                                                                     as status_7003,
                         m.merchant_name                                                                  as merchant_name,
                         (case when length(b.bank_code) = 4 then '0' || b.bank_code else b.bank_code end) as inst_id
                  from (select *
                        from machine l
                        where l.state <> 2
                          and l.type_id = 6
                          and l.sync_date > current_date - interval '1 day' + interval '18 hour'
                          and l.model_id in (-2, -1, 840)
                        order by l.sync_date
                        offset ? limit 100) as m
                       
                           inner join branch b on m.branch_id = b.id
                      and bank_code not in ('9006', '9004', '9002')) as t;
                        """;
    String GET_DAILY_AUTH_CODE = """
            select a.sr_number, a.billing_type
            from auth_code a
                     inner join branch b on a.branch_id = b.id
                and bank_code not in ('9006', '9004', '9002')
            where a.creation_date > current_timestamp - interval '1 day'
            order by a.creation_date;
            """;

    String GET_DAILY_FIX = """
            select a.sr_number, a2.code as application_number
            from application_machine a
                     inner join application a2 on a2.id = a.application_id
                     inner join machine m on a.machine_id = m.id
                     inner join branch b on m.branch_id = b.id
                and bank_code not in ('9006', '9004', '9002')
            where m.type_id = 6
              and a.updated_date > current_stamp - interval '1 day'
              and a.machine_status like 'Transmitted';
            """;
}
