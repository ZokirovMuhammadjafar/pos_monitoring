create function checker(inst_id character varying, sr_number character varying) returns boolean
    language plpgsql
as
$$
declare
prefixs     varchar;
    sr_numbers  varchar;
    sr          varchar;
first       int ;
second      int;
last        int;
    first_char  varchar ;
    second_char varchar;
    last_char   varchar;
    response    bool default false;
begin
    prefixs = (select left(sr_number, 3));
    last_char=(select substring(sr_number from 4 for 5));
    if is_numeric(last_char) is false then
        return response;
end if;
last = last_char::int;
select distinct ai.range
into sr_numbers
from contract c
         inner join branch b on c.id = b.contract_id
         inner join agreement a on a.contract_id = c.id
         inner join agreement_item ai on a.id = ai.agreement_id
where a.agr_id = (select max(ag.agr_id)
                  from agreement ag
                  where ag.contract_id = c.id
                    and ag.state = 4)
  and b.code like inst_id
  and ai.prefix = prefixs;
if sr_numbers is not null then

        for sr in SELECT unnest(string_to_array(sr_numbers, ';'))
                                loop
                         sr = (select trim(sr));
if sr like '%(%' then
                    first_char = (select substring(sr FROM 4 FOR 5));
                    second_char = (select substring(sr FROM 13 FOR 5));
                    if (is_numeric(first_char) and is_numeric(second_char) ) then
                        first = first_char::int;
second = second_char::int;
                        if first <= last and second >= last then
                            response = true;
end
                            if;
end if;
else
                    first_char = (select substring(sr FROM 4 FOR 5));
                    if (is_numeric(first_char)) then
                        first = first_char::int;
                        if first = last then
                            response = true;
end
                            if;
end if;
end if;
end loop;
return response;
else
        return false;
end if;
end ;
$$;

create function is_numeric(value text) returns boolean
    language plpgsql
as
$$
DECLARE
result boolean;
BEGIN
BEGIN
        result := (value ~ '^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$');
EXCEPTION
        WHEN others THEN
            result := false;
END;
RETURN result;
END;
$$;