package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.MachineState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Repository
public interface MachineRepository extends SoftDeleteJpaRepository<Machine> {


    Machine findBySrNumberAndDeleted(String srNumber, boolean deleted);

    @Modifying
    @Query("update Machine m set m.deleted = true where m.prefix = ?1 ")
    void deleteByPrefix(String prefix);

    @Query("from Machine m where m.deleted=false and m.prefix=?1")
    Stream<Machine> findPrefix(String prefix);


    @Query(value = "select m.state as state,count(m.state) as number from machines m where m.inst_id= ?1 group by m.state",nativeQuery = true)
    List<Map<String,Object>> getStat(String instId);

    @Query(value = """
            with soft_data as (select mfo                           as mfo,
                                      max(bs.id)                    as id,
                                      coalesce(soft, 'NULL')        as soft,
                                      count(*)                      as count,
                                      max(bs.name) || ' ' || bs.mfo as name
                               from machines ms
                                        left join branches bs on ms.branch_id = bs.id
                               where ms.deleted = false
                                 and bs.deleted = false
                                 and inst_id = ?1
                               group by mfo, soft),
                 model_data as (select mfo                     as mfo,
                                       coalesce(model, 'NULL') as model,
                                       count(*)                as count
                                from machines ms
                                         left join branches bs on ms.branch_id = bs.id
                                where ms.deleted = false
                                  and bs.deleted = false
                                  and inst_id = ?1
                                group by mfo, model),
                 fix_count as (select branch_mfo, sum(fixed_count) as fix_count
                               from machines
                               where inst_id = ?1
                               group by branch_mfo),
                 auth_count as (select branch_mfo, sum(auth_count) as auth_count
                                from machines
                                where inst_id = ?1
                                group by branch_mfo),
                 counts as (select branch_mfo, count(auth_count) as countt
                           from machines
                           where inst_id = ?1
                           group by branch_mfo)
            select max(sd.name)                                                                         as name_and_mfo,
                   max(sd.id)                                                                           as id,
                   max(c.countt)                                                                        as count,
                   max(f.fix_count)                                                                     as fix_count,
                   max(a.auth_count)                                                                    as auth_count,
                   sd.mfo                                                                               as mfo,
                   (select string_agg(soft || '/' || count, ' | ') from soft_data where mfo = sd.mfo)   as soft,
                   (select string_agg(model || '/' || count, ' | ') from model_data where mfo = sd.mfo) as model
            from soft_data sd
                     inner join model_data md on sd.mfo = md.mfo
                     inner join auth_count a on a.branch_mfo = sd.mfo
                     inner join fix_count f on f.branch_mfo = sd.mfo
                     inner join counts c on c.branch_mfo = sd.mfo
            group by sd.mfo;
             """,nativeQuery = true)
    List<Map<String,String>> getByInstId(String instId);

    @Query(value = "select m.state as state,count(m.state) as number from machines m where m.inst_id= ?1 group by m.state", nativeQuery = true)
    List<Map<String, Object>> getState(String instId);

    List<Machine> findAllByState(MachineState state);

    @Query("select count(m) from Machine m where m.instId = ?1 and m.state <>2")
    Long getAllTerminal(String instId);
    @Query("select count(m) from Machine m where m.instId = ?1 and m.isContract")
    Long getAllTerminalHasContract(String instId);
    @Query("select count(m) from Machine m where m.instId = ?1 and (m.state=0 or m.state=3)")
    Long getAllWorkingTerminal(String instId);
}
