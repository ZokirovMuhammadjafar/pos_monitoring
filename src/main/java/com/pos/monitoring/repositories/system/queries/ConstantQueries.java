package com.pos.monitoring.repositories.system.queries;

public interface ConstantQueries {
    String GET_ALL_CHANGE_MACHINES = """
            select t.*, checker(inst_id := '0' || t.inst_id, sr_number := t.sr_number) as is_contract
            from (select m.sr_number        as sr_number,
                         m.terminal_id      as terminal_id,
                         m.merchant_id      as merchant_id,
                         m.soft      as soft,
                         b.code             as branch_mfo,
                         b.id               as branch_id,
                         '0' || b.bank_code as inst_id
                  from machine m
                           inner join branch b on m.branch_id = b.id
                  where m.state <> 2
                    and m.type_id = 6
                    and bank_code not in ('9006', '9004', '9002')
                    and (m.updated_date > current_timestamp - interval '1 day' or
                         m.creation_date > current_timestamp - interval '1 day')) as t;
            """;

    String GET_ALL_MACHINES_FIRST = """
            select t.*,
                   checker(inst_id := t.inst_id, sr_number := t.sr_number)                           as is_contract,
                   (select count(a) from auth_code a where a.sr_number = t.sr_number)::int           as auth_count,
                   (select count(a) from application_machine a where a.sr_number = t.sr_number)::int as fixed_count
                      
            from (select m.sr_number        as sr_number,
                         m.terminal_id      as terminal_id,
                         m.merchant_id      as merchant_id,
                         m.soft             as soft,
                         b.code             as branch_mfo,
                         b.id               as branch_id,
                         '0' || b.bank_code as inst_id
                  from (select *
                        from machine l
                        where l.state <> 2
                          and l.type_id = 6
                        offset ? limit 100) as m
                           inner join branch b on m.branch_id = b.id
                      and bank_code not in ('9006', '9004', '9002')) as t;
            """;
}