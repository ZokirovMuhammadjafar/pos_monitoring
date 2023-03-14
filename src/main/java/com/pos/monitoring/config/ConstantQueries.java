package com.pos.monitoring.config;

public class ConstantQueries {
    public static final String GET_ALL_CHANGE_MACHINES = """
            select *
            from (select m.sr_number                                               as sr_number,
                         m.terminal_id                                             as terminal_id,
                         m.merchant_id                                             as merchant_id,
                         b.code                                                    as branch_mfo,
                         b.id                                                      as branch_id,
                         '0' || b.bank_code as inst_id,
                         checker(inst_id := '0'||b.bank_code, sr_number := m.sr_number) as is_contract
                  from machine m
                           inner join branch b on m.branch_id = b.id
                  where m.state <> 2
                    and m.type_id = 6
                    and bank_code not in ('9006', '9004', '9002')
                    and (m.updated_date > current_timestamp - interval '2 month' or
                         m.creation_date > current_timestamp - interval '2 month')) as t;
            """;

    public static final String GET_ALL_MACHINES_FIRST = """
               select t.*, checker(inst_id :=  t.inst_id, sr_number := t.sr_number) as is_contract
                                                       from (select m.sr_number        as sr_number,
                                                                    m.terminal_id      as terminal_id,
                                                                    m.merchant_id      as merchant_id,
                                                                    b.code             as branch_mfo,
                                                                    b.id               as branch_id,
                                                                    '0' || b.bank_code as inst_id
                                                             from machine m
                                                                      inner join branch b on m.branch_id = b.id
                                                             where m.state <> 2
                                                               and m.type_id = 6
                                                               and bank_code not in ('9006', '9004', '9002')
                                                             offset 200000 limit 1000) as t;
                                                       
            """;
}